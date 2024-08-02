package com.test.sku.jdbc;

import java.util.List;

public class PageItem {
	
	private List<EmpVO> list;
	private int totalPages;
	private int currPage;
	
	public PageItem() {}

	public List<EmpVO> getList() {
		return list;
	}

	public void setList(List<EmpVO> list) {
		this.list = list;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	
}
