package model;

import java.util.List;

public interface ISchoolDAO {

	void downloadFile(String dlFrom, String tableName);
	
	void createTable(String fileLocation);
	
	void insertDatas(SchoolBean data, String fileLocation);

	List<SchoolBean> searchItem(String queryStr, String condition);

	void updateAreaNameByCode(String tableName, SchoolBean data);

	void delect(String queryStr, String condition);
	
	boolean isTableExist(String tableName);

//	void exportJson(List<DataObject> outputCSV, String jsonName);



}