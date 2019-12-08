package com.eqarus.sentalyze.twitter.configs;

import twitter4j.Place;

public class TweetData {

	private String tweet;
	private String place;
	private String user;
	private String hashtags;
	private String language;
	private Place location;
	public TweetData() {
		// TODO Auto-generated constructor stub
	}
	

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Place getLocation() {
		return location;
	}


	public void setLocation(Place location) {
		this.location = location;
	}
	
	
}
