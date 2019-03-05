package cn.itcast.core.pojo.entity;

import java.io.Serializable;

public class Result implements Serializable{

	private static final long serialVersionUID = -8946453797496982517L;

	//true代表执行成功, false代表执行失败
	private boolean success;
	//成功或者是失败消息
	private String message;
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
}
