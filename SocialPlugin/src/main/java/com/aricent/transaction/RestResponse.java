package com.aricent.transaction;

public class RestResponse {

	private Object object;
	private int code;
	private String message;

	public RestResponse() {

	}

	public RestResponse(Object object, int resultCode, String msg) {
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
