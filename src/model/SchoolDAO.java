package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
//import com.google.gson.Gson;
import util.ConnectionFactory;

public class SchoolDAO implements ISchoolDAO {

	private Connection conn;

	public SchoolDAO() {
		this.conn = ConnectionFactory.getConnection();
	}

	@Override
	public void downloadFile(String dlFrom, String tableName) {
		try (InputStream is = new URL(dlFrom).openStream();
				InputStreamReader isr = new InputStreamReader(is, "BIG5");
				BufferedReader br = new BufferedReader(isr);) {
			String line;
			int lineCount = 1;
			while ((line = br.readLine()) != null) {
				if (lineCount == 1) {
					lineCount++;
					continue;
				}
				SchoolBean csvData = new SchoolBean();
				String[] columns = line.split(",");
				csvData.setYear(Integer.valueOf(columns[0]));
				csvData.setCode(columns[1]);
				csvData.setName(columns[2]);
				csvData.setGrade(columns[3]);
				csvData.setArea(Integer.valueOf(columns[4]));
				this.insertDatas(csvData, tableName);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void createTable(String tableName) {
		try(Statement state = conn.createStatement();) {
			String sqlStr = "CREATE TABLE " + tableName + "(ID int primary key Identity(1, 1), "
					+ "Year int, Code nvarchar(7)," + "[Name] nvarchar(50)," + "Grade nvarchar(4)," + "Area int)";
			state.execute(sqlStr);
			state.close();
			System.out.println("[[新增資料表成功]]");
		} catch (SQLException e) {
			if (e.getMessage().contains("資料庫中已經有一個名為")) {
				System.out.println("[[資料庫中已經有一個名為" + tableName + "的資料表]]");
			} else if (Integer.valueOf(e.getMessage().substring(4, 5)) >= 0) {
				System.out.println("[[資料表名稱只可用英文開始]]");
			}
		}
	}

	@Override
	public void insertDatas(SchoolBean data, String tableName) {
		System.out.println(tableName);
		try {
			String sqlStr = "INSERT INTO " + tableName
					+ "(Year, Code, [Name], Grade, Area) values ((? + 1911), ?, ?, ?, ?)";
			PreparedStatement preState = conn.prepareStatement(sqlStr);
			preState.setInt(1, data.getYear());
			if (data.getCode().substring(0, 1).equals("*")) {
				preState.setString(2, data.getCode().substring(1, data.getCode().length()));
			} else {
				preState.setString(2, data.getCode());
			}
			preState.setString(3, data.getName());
			preState.setString(4, data.getGrade());
			preState.setInt(5, data.getArea());
			preState.execute();
			preState.close();
		} catch (SQLException e) {
			if (e.getMessage().contains("無效的物件名稱") || e.getMessage().contains("接近")) {
				System.out.println("沒有名為" + tableName + "的資料表");
			}
		} catch (InputMismatchException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("[[資料新增成功]]");
	}

	@Override
	public List<SchoolBean> searchItem(String queryStr, String conditionStr) {
		List<SchoolBean> list = new ArrayList<>();
		try {
			PreparedStatement preState = conn.prepareStatement(queryStr);
			if (!conditionStr.equals("")) {
				preState.setString(1, conditionStr);
			}
			ResultSet rs = preState.executeQuery();
			System.out.println("Year\tCode\tName\tGrade\tArea");
			while (rs.next()) {
				SchoolBean data = new SchoolBean();
				data.setYear(rs.getInt("Year"));
				data.setCode(rs.getString("Code"));
				data.setName(rs.getString("Name"));
				data.setGrade(rs.getString("Grade"));
				data.setArea(rs.getInt("Area"));
				list.add(data);
				System.out.println(rs.getInt("Year") + "\t" + rs.getString("Code") + "\t" + rs.getString("Name") + "\t"
						+ rs.getString("Grade") + "\t" + rs.getInt("Area"));
			}
			rs.close();
			preState.close();
		} catch (SQLException e) {
			if (e.getMessage().contains("轉換成資料類型 int 時")) {
				System.out.println("[[搜尋條件錯誤]]");
			} else {
				System.out.println("[[搜尋條件無效]]");
				System.out.println(e.getMessage());
			}
		}
		if (list.size() == 0) {
			System.out.println("[[沒有資料]]");
		}
		return list;
	}

	@Override
	public void updateAreaNameByCode(String tableName, SchoolBean data) {
		try {
			PreparedStatement preState = conn.prepareStatement("UPDATE " + tableName + " SET Area = ? WHERE Code = ?");
			preState.setInt(1, data.getArea());
			preState.setString(2, data.getCode());
			preState.execute();
			preState.close();
		} catch (SQLException e) {
			System.out.println("[[輸入無效]]");
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void delect(String queryStr, String condition) {
		try {
			PreparedStatement preState = conn.prepareStatement(queryStr);
			if (!condition.equals("")) {
				preState.setString(1, condition);
			}
			preState.execute();
			preState.close();
			System.out.println("[[資料表刪除成功]]");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

//	public void exportJson(List<DataObject> outputCSV, String jsonName) {
//		Gson gson = new Gson();
//		String json = gson.toJson(outputCSV);
//		FileOutputStream fos;
//		try {
//			fos = new FileOutputStream("C:/Users/Student/Desktop/" + jsonName + ".Json");
//			OutputStreamWriter ow = new OutputStreamWriter(fos, "UTF-8");
//			BufferedWriter bw = new BufferedWriter(ow);
//			bw.write(json);
//			bw.close();
//			System.out.println("[[檔案已輸出到桌面]]");
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
//	}

	public boolean isTableExist(String tableName) {
		PreparedStatement preState;
		try {
			preState = conn.prepareStatement("SELECT * FROM " + tableName);
			ResultSet rs = preState.executeQuery();
			rs.close();
			preState.close();
			return true;
		} catch (SQLException e) {
			if (e.getMessage().contains("無效的物件名稱") || e.getMessage().contains("接近")) {
				System.out.println("[[沒有名為" + tableName + "的資料表]]");
			} else {
				System.out.println("[[輸入無效]]");
				System.out.println(e.getMessage());
			}
			return false;
		}
	}

}