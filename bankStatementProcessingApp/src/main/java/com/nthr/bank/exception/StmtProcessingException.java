package com.nthr.bank.exception;

import java.io.Serializable;

public class StmtProcessingException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;
	public String errorCode;
	public StmtProcessingException(String errorCode,String msg){
		super(msg);
		this.errorCode = errorCode;
	}
}
