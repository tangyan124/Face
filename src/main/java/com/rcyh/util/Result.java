package com.rcyh.util;

import java.io.Serializable;

/**
 * JSON数据结果封装
 * 
 * @author TY
 *
 */
@SuppressWarnings("serial")
public class Result implements Serializable {

	private int code;
	private String msg;
	private Object data;

	public Result() {
		super();
	}

	public Result(int code, String msg, Object data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
