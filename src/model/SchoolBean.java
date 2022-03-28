package model;

import java.io.Serializable;

public class SchoolBean implements Serializable {
	
	private static final long serialVersionUID = 1L;	//•i•H§£ºg, •Œ≥~???
	
	private int year;
	private String code;
	private String name;
	private String grade;
	private int area;
	
	public SchoolBean() {}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

}