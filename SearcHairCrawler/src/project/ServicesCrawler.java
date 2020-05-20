package project;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ServicesCrawler {

	static int count = 0;

	public static void main(String[] args) throws IOException {
		String TARGET_URL = "https://style-map.com/api/userservice/query";

		ArrayList<Integer> id = new ArrayList<Integer>();
		ArrayList<Integer> stylist = new ArrayList<Integer>();
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<Integer> min_price = new ArrayList<Integer>();
		ArrayList<Integer> max_price = new ArrayList<Integer>();
		ArrayList<Integer> service_time = new ArrayList<Integer>();
		ArrayList<String> description = new ArrayList<String>();

		ArrayList<Integer> all_stylist_id = new ArrayList<Integer>();
		all_stylist_id = getStylistId();

		jdbcMysql insert = new jdbcMysql();

		for (int stylist_id : all_stylist_id) {
			Connection con = Jsoup.connect(TARGET_URL).ignoreContentType(true);
			int start = 0;
			int end = 50;
			con.data("user", String.valueOf(stylist_id));
			con.data("start", String.valueOf(start));
			con.data("end", String.valueOf(end));
			Document doc = con.post();
			JSONObject jsonObj = new JSONObject(doc.text());

			int len = jsonObj.getJSONObject("data").getJSONArray("userservices").length();
			for (int j = 0; j < len; j++) {
				try {
					JSONObject service = jsonObj.getJSONObject("data").getJSONArray("userservices").getJSONObject(j);

					stylist.add(stylist_id);

					if (service.getJSONObject("service").isNull("name"))
						name.add("NULL");
					else
						name.add(service.getJSONObject("service").getString("name"));
					if (service.isNull("min_price"))
						min_price.add(-1);
					else
						min_price.add(service.getInt("min_price"));
					if (service.isNull("max_price"))
						max_price.add(-1);
					else
						max_price.add(service.getInt("max_price"));
					if (service.isNull("service_time"))
						service_time.add(-1);
					else
						service_time.add(service.getInt("service_time"));
					if (service.isNull("description") || service.getString("description").isEmpty())
						description.add("NULL");
					else
						description.add(service.getString("description")); // 可能為空
																			// 做處理？
					count++;
				} catch (Exception e) {
					break;
				}
			}
		}
		insert.insertService(stylist, name, min_price, max_price, service_time, description, count);

	}

	public static ArrayList<Integer> getStylistId() throws IOException {
		String TARGET_URL = "https://style-map.com/api/user/filter";

		ArrayList<Integer> all_stylist_id = new ArrayList<Integer>();
		for (int i = 0; i <= 2177; i += 100) {
			int start = i;
			int end = i + 100;
			Connection con = Jsoup.connect(TARGET_URL).ignoreContentType(true);
			con.data("start", String.valueOf(start));
			con.data("end", String.valueOf(end));
			Document doc = con.post();
			JSONObject jsonObj = new JSONObject(doc.text());

			int len = jsonObj.getJSONObject("data").getJSONArray("userlist").length();

			for (int j = 0; j < len; j++) {
				try {
					JSONObject stylist = jsonObj.getJSONObject("data").getJSONArray("userlist").getJSONObject(j);
					all_stylist_id.add(stylist.getInt("id"));
				} catch (Exception e) {
					break;
				}
			}
		}

		return all_stylist_id;
	}

}
