package action;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.SchoolBean;
import model.SchoolDAO;
import model.ISchoolDAO;

public class SchoolAction {
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		ISchoolDAO dao = new SchoolDAO();
		String tableName = ""; // 接收input資料表的名稱
		String inputStr = ""; // 接收input的資料內容
		String optionStr = ""; // 接收input的指令選擇
		String conditionStr = ""; // 接收input的sql搜尋條件
		String queryStr = ""; // 接收input的sql搜尋
		String jsonName = "";
		breakEnd: while (true) {
			System.out.println("選擇指令: ");
			System.out.println("1:搜尋\t2:新增\t3:更改\t4:刪除\t5關閉");
			optionStr = scanner.nextLine();
			breakStart: while (true) {
				switch (optionStr) {
				// 搜尋
				case "1":
					List<SchoolBean> outputCSV = new ArrayList<>();
					System.out.println("輸入搜尋的資料表名稱: ");
					tableName = scanner.nextLine();
					if (!dao.isTableExist(tableName)) {
						break breakStart;
					}
					while (true) {
						System.out.println("選擇搜尋條件: ");
						System.out.println("1:學校名稱\t2:學校代號\t3:教育級別\t4:校地面積\t5:全部資料\t0:返回目錄");
						optionStr = scanner.nextLine();
						queryStr = ""; // 接收input的sql搜尋
						switch (optionStr) {
						// 搜尋 > 學校名稱
						case "1":
							System.out.println("輸入學校名稱:");
							conditionStr = scanner.nextLine();
							queryStr = "SELECT * FROM " + tableName + " WHERE Name = ?";
							jsonName = tableName + "-Name=" + conditionStr;
							break;
						// 搜尋 > 學校名稱
						case "2":
							System.out.println("輸入學校代號:");
							conditionStr = scanner.nextLine();
							queryStr = "SELECT * FROM " + tableName + " WHERE Code = ?";
							jsonName = tableName + "-Code=" + conditionStr;
							break;
						// 搜尋 > 教育級別
						case "3":
							System.out.println("輸入教育級別:");
							conditionStr = scanner.nextLine();
							queryStr = "SELECT * FROM " + tableName + " WHERE Grade = ?";
							jsonName = tableName + "-Grade=" + conditionStr;
							break;
						// 搜尋 > 校地面積
						case "4":
							System.out.println("輸入校地面積:");
							conditionStr = scanner.nextLine();
							queryStr = "SELECT * FROM " + tableName + " WHERE Area = ?";
							// 處理 > < = 條件
							if (conditionStr.substring(0, 1).equals("<")) {
								jsonName = tableName + "-Area小於" + conditionStr.substring(1, conditionStr.length());
								queryStr = queryStr.replace("=", conditionStr.substring(0, 1));
								conditionStr = conditionStr.substring(1, conditionStr.length());
							} else if (conditionStr.substring(0, 1).equals(">")) {
								jsonName = tableName + "-Area大於" + conditionStr.substring(1, conditionStr.length());
								queryStr = queryStr.replace("=", conditionStr.substring(0, 1));
								conditionStr = conditionStr.substring(1, conditionStr.length());
							} else {
								jsonName = tableName + "-Area=" + conditionStr;
							}
							break;
						// 搜尋 > 整個資料表
						case "5":
							queryStr = "SELECT * FROM " + tableName;
							conditionStr = "";
							jsonName = tableName + "-全部";
							break;
						case "0":
							break breakStart;
						default:
							System.out.println("[[指令錯誤]]");
							System.out.println("[[請重新輸入]]");
							continue;
						}
						outputCSV = dao.searchItem(queryStr, conditionStr);
						break;
					}
					// 要不要輸出Json
					if(outputCSV.size() > 0) {
						System.out.println("需要輸出Json檔嗎?\t Y / N");
						optionStr = scanner.nextLine();
						if (optionStr.equals("Y") || optionStr.equals("y")) {
							System.out.println("[[功能未開放]]");
//							dao.exportJson(outputCSV, jsonName);
						}						
					}
					break breakStart;
				// 新增
				case "2":
					breakInsert: while (true) {
						System.out.println("選擇新增類別:");
						System.out.println("1:資料表\t2:資料\t0:返回目錄");
						optionStr = scanner.nextLine();
						switch (optionStr) {
						// 新增 > 資料表
						case "1":
							System.out.println("輸入資料表名稱:");
							tableName = scanner.nextLine();
							dao.createTable(tableName);
							break breakStart;
						// 新增 > 資料
						case "2":
							while (true) {
								System.out.println("選擇新增方法: ");
								System.out.println("1:網上下載csv\t2:手動輸入\t0:返回目錄");
								optionStr = scanner.nextLine();
								switch (optionStr) {
								// 新增 > 資料 > 網上下載csv
								case "1":
									System.out.println("輸入要讀入資料的資料表名稱: ");
									tableName = scanner.nextLine();
									if (!dao.isTableExist(tableName)) {
										break breakStart;
									}
									dao.downloadFile("https://stats.moe.gov.tw/files/others/opendata/109area.csv", tableName);
									break breakStart;
								// 新增 > 資料 > 手動輸入
								case "2":
									SchoolBean data = new SchoolBean();
									String dataString = "";
									System.out.println("輸入資料表名稱:");
									tableName = scanner.nextLine();
									if (!dao.isTableExist(tableName)) {
										break breakStart;
									}
									System.out.println("輸入學校代號:");
									data.setCode(scanner.nextLine());
									System.out.println("輸入學校名稱:");
									data.setName(scanner.nextLine());
									System.out.println("輸入學校教育級別:");
									data.setGrade(scanner.nextLine());
									try {
										System.out.println("輸入學年度(民國):");
										dataString = scanner.nextLine();
										data.setYear(Integer.valueOf(dataString));
										System.out.println("輸入學校總面積:");
										dataString = scanner.nextLine();
										data.setArea(Integer.valueOf(dataString));
									} catch (NumberFormatException e) {
										System.out.println("[[請輸入數字]]");
										continue;
									}
									dao.insertDatas(data, tableName);
									break breakStart;
								case "0":
									break breakStart;
								default:
									System.out.println("[[指令錯誤]]");
									System.out.println("[[請重新輸入]]");
									continue;
								}
							}
						case "0":
							break breakStart;
						default:
							System.out.println("[[指令錯誤]]");
							System.out.println("[[請重新輸入]]");
							continue;
						}
					}
				// 更改
				case "3":
					System.out.println("輸入資料表名稱:");
					tableName = scanner.nextLine();
					if (!dao.isTableExist(tableName)) {
						break breakStart;
					}
					SchoolBean item = new SchoolBean();
					System.out.println("輸入要更改的學校代號:");
					inputStr = scanner.nextLine();
					item.setCode(inputStr);
					System.out.println("輸入要新的面積:");
					inputStr = scanner.nextLine();
					item.setArea(Integer.parseInt(inputStr));
					dao.updateAreaNameByCode(tableName, item);
					dao.searchItem("SELECT * FROM " + tableName + " WHERE Code = ?", item.getCode());
					break breakStart;
				// 刪除
				case "4":
					while (true) {
						System.out.println("選擇刪除類別: ");
						System.out.println("1:資料表\t2:資料\t0:返回目錄");
						optionStr = scanner.nextLine();
						switch (optionStr) {
						// 刪除 > 資料表
						case "1":
							System.out.println("輸入資料表名稱:");
							tableName = scanner.nextLine();
							if (!dao.isTableExist(tableName)) {
								break breakStart;
							}
							queryStr = "drop table " + tableName;
							dao.delect(queryStr, inputStr);
							break breakStart;
						// 刪除 > 資料
						case "2":
							System.out.println("輸入資料表名稱:");
							tableName = scanner.nextLine();
							if (!dao.isTableExist(tableName)) {
								break breakStart;
							}
							System.out.println("輸入要刪除的:");
							System.out.println("1:學校名稱\t2:學校代號\t3:教育級別\t0:返回目錄");
							optionStr = scanner.nextLine();
							switch (optionStr) {
							case "1":
								System.out.println("輸入要刪除學校名稱:");
								inputStr = scanner.nextLine();
								queryStr = "DELETE FROM " + tableName + " WHERE Name = ?";
								break;
							case "2":
								System.out.println("輸入要刪除學校代號:");
								inputStr = scanner.nextLine();
								queryStr = "DELETE FROM " + tableName + " WHERE Code = ?";
								break;
							case "3":
								System.out.println("輸入要刪除教育級別:");
								inputStr = scanner.nextLine();
								queryStr = "DELETE FROM " + tableName + " WHERE Grade = ?";
								break;
							case "0":
								break breakStart;
							default:
								System.out.println("[[指令錯誤]]");
								System.out.println("[[請重新輸入]]");
								continue;
							}
							dao.delect(queryStr, inputStr);
						case "0":
							break breakStart;
						default:
							System.out.println("[[指令錯誤]]");
							System.out.println("[[請重新輸入]]");
							continue;
						}
					}
				case "5":
					scanner.close();
					System.out.println("已關閉");
					break breakEnd;
				default:
					System.out.println("[[指令錯誤]]");
					System.out.println("[[請重新輸入]]");
					break breakStart;
				}
			}
		}
	}
}
