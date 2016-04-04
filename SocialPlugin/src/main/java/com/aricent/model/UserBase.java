package com.aricent.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.mapping.Table;

@Table
public class UserBase {
	private String userBaseId;
	private String name;
	private List<String> emailIdList;
	private String password;
	private String age;
	private String gender;
	private String dob;
	private Date userRegisteredDate;
	private String nameOfFile;
	private String yearOfBirth;
	private int recUserId;
	

	
	public int getRecUserId() {
		return recUserId;
	}

	public void setRecUserId(int recUserId) {
		this.recUserId = recUserId;
	}

	public String getNameOfFile() {
		return nameOfFile;
	}

	public void setNameOfFile(String nameOfFile) {
		this.nameOfFile = nameOfFile;
	}

	public String getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public Date getUserRegisteredDate() {
		return userRegisteredDate;
	}

	public void setUserRegisteredDate(Date userRegisteredDate) {
		this.userRegisteredDate = userRegisteredDate;
	}

	public String getUserBaseId() {
		return userBaseId;
	}

	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getEmailIdList() {
		return emailIdList;
	}

	public void setEmailIdList(List<String> emailIdList) {
		this.emailIdList = emailIdList;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	

	
}
