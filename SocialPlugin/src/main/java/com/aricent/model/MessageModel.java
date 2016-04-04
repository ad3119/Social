package com.aricent.model;

public class MessageModel {
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsgStr() {
		return msgStr;
	}

	public void setMsgStr(String msgStr) {
		this.msgStr = msgStr;
	}

	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}





	private String emailId;
	private String passwd;
	private String name;
	private String msgStr;
	private int src;
	
	//
	// public void setMsgStr() {
	// MsgStr = "Hi" + name + "Welcome to Aricent....Your new emailid ="
	// + MailId + "Password = " + Pswd;
	// }
}
