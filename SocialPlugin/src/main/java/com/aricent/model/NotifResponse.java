package com.aricent.model;

public class NotifResponse {

	private Object object;
	private int code;
	private String message;

	public NotifResponse() {

	}

	public NotifResponse(Object object, int resultCode, String msg) {
		this.object = object;
		this.code = resultCode;
		this.message = msg;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

