package project;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.json.JSONObject;
import java.util.ArrayList;

public class SalonsCrawler {

	public static void main(String[] args) throws IOException {
		String TARGET_URL = "https://style-map.com/api/workingplace/filter";
		String NEXT_URL = "https://style-map.com/store/";
		ArrayList<Integer> id = new ArrayList<Integer>();
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<String> address = new ArrayList<String>();
		ArrayList<String> phone = new ArrayList<String>();
		ArrayList<String> businessTime = new ArrayList<String>();
		ArrayList<String> picture = new ArrayList<String>();

		jdbcMysql insert = new jdbcMysql();

		for (int i = 0; i <= 1290; i += 10) {
			Connection con = Jsoup.connect(TARGET_URL).ignoreContentType(true);
			int start = i;
			int end = i + 100;
			con.data("start", String.valueOf(start));
			con.data("end", String.valueOf(end));
			Document doc = con.post();
			JSONObject jsonObj = new JSONObject(doc.text());

			int len = jsonObj.getJSONObject("data").getJSONArray("salonlist").length();

			for (int j = 0; j < len; j++) {
				try {
					JSONObject salonlist = jsonObj.getJSONObject("data").getJSONArray("salonlist").getJSONObject(j);
					if (salonlist.isNull("id"))
						id.add(-1);
					else
						id.add(salonlist.getInt("id"));
					if (salonlist.isNull("name"))
						name.add("NULL");
					else
						name.add(salonlist.getString("name"));
					if (salonlist.isNull("address"))
						address.add("NULL");
					else
						address.add(salonlist.getString("address"));
					String pictureUrl;
					if (salonlist.isNull("logo"))
						pictureUrl = "NULL";
					else
						pictureUrl = "https://cdn.style-map.com/store/logo/large/"
								+ String.valueOf(salonlist.getInt("id")) + "_"
								+ String.valueOf(salonlist.getInt("logo")) + ".jpg";
					picture.add(pictureUrl);

					Document nextDoc = Jsoup.connect(NEXT_URL + String.valueOf(salonlist.getInt("id"))).get();
					Elements nextElements = nextDoc.select("script[type=\'application/ld+json\']");
					JSONObject nextJsonObj = new JSONObject(nextElements.get(0).data());
					if (nextJsonObj.isNull("telephone"))
						phone.add("NULL");
					else
						phone.add(nextJsonObj.getString("telephone"));
					if (nextJsonObj.isNull("openingHours"))
						businessTime.add("NULL");
					else
						businessTime.add(nextJsonObj.getString("openingHours"));
				} catch (Exception e) {
					break;
				}
			}

		}

		insert.insertSalon(id, name, address, phone, businessTime, picture);
	}

}
