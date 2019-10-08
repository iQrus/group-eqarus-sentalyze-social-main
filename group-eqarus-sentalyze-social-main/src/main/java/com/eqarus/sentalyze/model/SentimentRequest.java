package com.eqarus.sentalyze.model;

public class SentimentRequest {

	private  String keyword;
	private  long id;
	
	public SentimentRequest() {
		// TODO Auto-generated constructor stub
	}
	
	
	public SentimentRequest(long id, String keyword) {
		super();
		this.keyword = keyword;
		this.id = id;
	}


	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
}
