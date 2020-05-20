package project;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.json.JSONObject;
import java.util.ArrayList;

public class StylstsCrawler {

	public static void main(String[] args) throws IOException {
		String TARGET_URL = "https://style-map.com/api/user/filter";

		ArrayList<Integer> id = new ArrayList<Integer>();
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<String> job_title = new ArrayList<String>();
		ArrayList<Integer> salon = new ArrayList<Integer>();
		ArrayList<String> picture = new ArrayList<String>();

		jdbcMysql insert = new jdbcMysql();

		for (int i = 0; i <= 2177; i += 100) {
			Connection con = Jsoup.connect(TARGET_URL).ignoreContentType(true);
			int start = i;
			int end = i + 100;
			con.data("start", String.valueOf(start));
			con.data("end", String.valueOf(end));
			Document doc = con.post();
			JSONObject jsonObj = new JSONObject(doc.text());
			int len = jsonObj.getJSONObject("data").getJSONArray("userlist").length();

			for (int j = 0; j < len; j++) {
				try {
					JSONObject stylist = jsonObj.getJSONObject("data").getJSONArray("userlist").getJSONObject(j);
					if (stylist.isNull("id"))
						id.add(-1);
					else
						id.add(stylist.getInt("id"));
					if (stylist.isNull("name"))
						name.add("NULL");
					else
						name.add(stylist.getString("name"));
					if (stylist.getJSONObject("professionInfo").isNull("head"))
						job_title.add("NULL");
					else
						job_title.add(stylist.getJSONObject("professionInfo").getString("pname"));
					if (stylist.getJSONObject("wpInfo").isNull("wp_id"))
						salon.add(0);
					else
						salon.add(stylist.getJSONObject("wpInfo").getInt("wp_id"));
					String pictureUrl;
					if (stylist.isNull("head"))
						pictureUrl = "NULL";
					else
						pictureUrl = "https://cdn.style-map.com/user/head/small/" + String.valueOf(stylist.getInt("id"))
								+ "_" + String.valueOf(stylist.getInt("head")) + ".jpg";
					picture.add(pictureUrl);
				} catch (Exception e) {
					break;
				}
			}

		}

		insert.insertStylist(id, name, job_title, salon, picture);

	}

}
