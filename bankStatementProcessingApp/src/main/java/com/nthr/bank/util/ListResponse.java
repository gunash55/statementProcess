package com.nthr.bank.util;

import java.util.List;

public class ListResponse extends BaseResponse {
	private List<?> list;

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
}
