package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class jdbcMysql {
	public Connection con = null; // Database objects
	private Statement stat = null;
	private ResultSet rs = null;
	private ResultSet rs1 = null;
	private int rsInt = 0;
	private PreparedStatement pst = null;

	private String selectSQL = "select * from salon";

	public jdbcMysql() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/searchair?useUnicode=yes&characterEncoding=utf-8", "root", "12345678");

		} catch (ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
		} catch (SQLException x) {
			System.out.println("Exception :" + x.toString());
		}
	}

	private void Close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stat != null) {
				stat.close();
				stat = null;
			}
			if (pst != null) {
				pst.close();
				pst = null;
			}
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}

	public void SearchHastag(String hashtag) {
		try {

			String mysql = " where hashtag like '%" + hashtag + "%'";
			stat = con.createStatement();

			rs = stat.executeQuery(selectSQL + mysql);
			while (rs.next()) {
				System.out.println(rs.getString("picture"));
			}
		} catch (SQLException e) {
			System.out.println("DropDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}

	public void insertSalon(ArrayList<Integer> id, ArrayList<String> name, ArrayList<String> address,
			ArrayList<String> phone, ArrayList<String> businessTime, ArrayList<String> picture) throws IOException {

		try {
			for (int i = 0; i <= 32670; i++) {
				if (id.get(i) == 7653) {
					continue;
				}

				String insertdbSQL = "insert into salon VALUES(" + id.get(i) + ", \"" + name.get(i) + "\", \""
						+ address.get(i) + "\", \"" + phone.get(i) + "\", \"" + businessTime.get(i) + "\",\""
						+ picture.get(i) + "\" )";
				System.out.println(i + insertdbSQL);
				stat = con.createStatement();
				rsInt = stat.executeUpdate(insertdbSQL);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
	}

	public void insertStylist(ArrayList<Integer> id, ArrayList<String> name, ArrayList<String> job_title,
			ArrayList<Integer> salon, ArrayList<String> picture) {
		System.out.println(job_title);
		ArrayList<Integer> number = new ArrayList<Integer>();
		try {
			for (int i = 0; i <= 2186; i++) {
				String check = "SELECT id FROM `stylist` WHERE id = " + id.get(i);
				stat = con.createStatement();
				rs = stat.executeQuery(check);
				String check1 = "SELECT id FROM `salon` WHERE id = " + salon.get(i);
				stat = con.createStatement();
				rs1 = stat.executeQuery(check1);
				if (rs.next()) {
					continue;
				}
				if (!rs1.next()) {
					number.add(salon.get(i));
					continue;
				} else {
					String insertdbSQL = "insert into stylist VALUES(" + id.get(i) + ", '" + name.get(i) + "', '"
							+ job_title.get(i) + "', " + salon.get(i) + ",'" + picture.get(i) + "' )";
					System.out.println(i + insertdbSQL);
					stat = con.createStatement();
					rsInt = stat.executeUpdate(insertdbSQL);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}

		System.out.println(number);
	}

	public boolean checkUser(int account) {
		try {
			String checkDBUser = " where account = " + account;
			rs = stat.executeQuery(selectSQL + checkDBUser);
			if (rs.next()) {
				String existAccount = rs.getString("account");
				if (existAccount.equals(account)) { // compare password
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	public void insertHairstyles(ArrayList<Integer> id, ArrayList<Integer> stylist, ArrayList<String> description,
			ArrayList<String> hashtag, ArrayList<String> picture, int len) throws IOException {
		ArrayList<Integer> number = new ArrayList<Integer>();
		try {
			System.out.println(len);
			for (int i = 0; i < len; i++) {
				String check = "SELECT id FROM `stylist_works` WHERE id = " + id.get(i);
				stat = con.createStatement();
				rs = stat.executeQuery(check);
				String check1 = "SELECT id FROM `stylist` WHERE id = " + stylist.get(i);
				stat = con.createStatement();
				rs1 = stat.executeQuery(check1);
				if (rs.next()) {
					continue;
				}
				if (!rs1.next()) {
					number.add(stylist.get(i));
					continue;
				}
				PreparedStatement pstmt = con.prepareStatement("INSERT INTO stylist_works VALUES (?, ?, ?, ?, ?)");
				pstmt.setInt(1, id.get(i));
				pstmt.setInt(2, stylist.get(i));
				pstmt.setString(3, description.get(i));
				pstmt.setString(4, hashtag.get(i));
				pstmt.setString(5, picture.get(i));
				rsInt = pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		System.out.println(number.size());
		System.out.println(number);
	}

	public void insertService(ArrayList<Integer> stylist, ArrayList<String> name, ArrayList<Integer> min_price,
			ArrayList<Integer> max_price, ArrayList<Integer> service_time, ArrayList<String> description, int len)
			throws IOException {

		try {
			for (int i = 0; i <= 2177; i++) {
				PreparedStatement pstmt = con.prepareStatement("INSERT INTO service VALUES (?, ?, ?, ?, ?, ?, ?)");
				pstmt.setInt(1, i + 1);
				pstmt.setInt(2, stylist.get(i));
				pstmt.setString(3, name.get(i));
				pstmt.setInt(4, max_price.get(i));
				pstmt.setInt(5, min_price.get(i));
				pstmt.setString(6, description.get(i));
				pstmt.setInt(7, service_time.get(i));
				rsInt = pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
	}

}
