package project;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HairstylesCrawler {

	static int count = 0;

	public static void main(String[] args) throws IOException {
		String TARGET_URL = "https://style-map.com/api/post/popular";

		ArrayList<Integer> id = new ArrayList<Integer>();
		ArrayList<Integer> stylist = new ArrayList<Integer>();
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> hashtag = new ArrayList<String>();
		ArrayList<String> picture = new ArrayList<String>();

		ArrayList<Integer> all_kind_id = new ArrayList<Integer>();
		all_kind_id = getKindId();

		jdbcMysql insert = new jdbcMysql();

		for (int kind_id : all_kind_id) {
			Connection con = Jsoup.connect(TARGET_URL).ignoreContentType(true);
			int start = 0;
			int end = 100;
			con.data("tag_id", String.valueOf(kind_id));
			con.data("start", String.valueOf(start));
			con.data("end", String.valueOf(end));
			Document doc = con.post();
			JSONObject jsonObj = new JSONObject(doc.text());
			int len = jsonObj.getJSONObject("data").getJSONArray("postlist").length();

			for (int j = 0; j < len; j++) {
				try {
					JSONObject hairstyle = jsonObj.getJSONObject("data").getJSONArray("postlist").getJSONObject(j);
					if (hairstyle.isNull("id"))
						id.add(-1);
					else
						id.add(hairstyle.getInt("id"));
					if (hairstyle.isNull("user_id"))
						stylist.add(-1);
					else
						stylist.add(hairstyle.getInt("user_id"));
					if (hairstyle.isNull("description"))
						description.add("NULL");
					else
						description.add(hairstyle.getString("description"));

					int hashtag_len = hairstyle.getJSONArray("taglist").length();
					String hashtag_String = "[";
					for (int k = 0; k < hashtag_len; k++) {
						JSONObject hashtaglist = hairstyle.getJSONArray("taglist").getJSONObject(k);
						String hashtag_name = hashtaglist.getString("name");
						hashtag_String += ("\"" + hashtag_name + "\"");
						if (k != hashtag_len - 1)
							hashtag_String += ",";
					}
					hashtag_String += "]";
					hashtag.add(hashtag_String);
					int picture_id;
					String pictureUrl;
					if (hairstyle.isNull("photos"))
						pictureUrl = "NULL";
					else {
						picture_id = hairstyle.getJSONArray("photos").getJSONObject(0).getInt("id");
						pictureUrl = "https://cdn.style-map.com/post/photo/normal/" + String.valueOf(picture_id)
								+ ".jpg";
					}
					picture.add(pictureUrl);
					count++;
				} catch (Exception e) {
					break;
				}
			}
		}
		insert.insertHairstyles(id, stylist, description, hashtag, picture, count);
	}

	public static ArrayList<Integer> getKindId() throws IOException {
		String TARGET_URL = "https://style-map.com/api/post/tag_group";

		ArrayList<Integer> all_kind_id = new ArrayList<Integer>();
		for (int i = 0; i <= 4219; i += 100) {
			int start = i;
			int end = i + 100;
			Connection con = Jsoup.connect(TARGET_URL).ignoreContentType(true);
			con.data("start", String.valueOf(start));
			con.data("end", String.valueOf(end));
			Document doc = con.post();
			JSONObject jsonObj = new JSONObject(doc.text());

			int len = jsonObj.getJSONObject("data").getJSONArray("grouplist").length();
			for (int j = 0; j < len; j++) {
				try {
					JSONObject hairstyleKind = jsonObj.getJSONObject("data").getJSONArray("grouplist").getJSONObject(j);
					all_kind_id.add(hairstyleKind.getInt("id"));
				} catch (Exception e) {
					break;
				}
			}
		}
		return all_kind_id;
	}
}
