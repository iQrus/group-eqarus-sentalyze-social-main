package com.eqarus.sentalyze.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.eqarus.sentalyze.model.RedditData;
import com.eqarus.sentalyze.request.TwitterRequestBean;
import com.eqarus.sentalyze.twitter.MainApplication;
import com.eqarus.sentalyze.twitter.configs.TweetData;
import com.equarus.sentalyze.response.SentimentResponseBean;
import com.equarus.sentalyze.response.SentimentWrapperResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import twitter4j.GeoLocation;

@RestController
public class SocialDataController {
	@Autowired
	private RestTemplate restTemplate;

	private final MainApplication appService = new MainApplication();

	@CrossOrigin(origins = "*")
	@RequestMapping("/getSentiments")
	public SentimentWrapperResponse getSentiments(
			@RequestParam(value = "keyword", defaultValue = "Article370") String keyword) {
		// System.out.println(readArticles(keyword));
		List<RedditData> redditList = appService.redditPostExtraction(keyword,restTemplate);
		List<TweetData> response = null;
		List<String> list = new ArrayList<String>();
		LinkedHashMap<String, String> tweetLocation = new LinkedHashMap<>();
		LinkedHashMap<String, String> sentitmentList = new LinkedHashMap<>();
		// LinkedHashMap<String, RedditData> redditDataList = new LinkedHashMap<>();
		try {
			response = appService.getSocialNetDetails(keyword);
		} catch (InterruptedException e) {
			// TODO The developer has to define global exceptions and log it here
			e.printStackTrace();
		} catch (IOException e) {
			// TODO to be done by Nandan
			e.printStackTrace();
		}
		System.out.println("Response :" + response.toString());
		for (TweetData tweet : response) {
			sentitmentList.put(tweet.getTweet(), "1");
			list.add(tweet.getTweet());
			// tweetDataList.put(tweet.getTweet(),tweet.getTweet());
			if (tweet.getLocation() != null && tweet.getLocation().getBoundingBoxCoordinates() != null) {
				tweetLocation.put(tweet.getTweet(), tweet.getLocation().getBoundingBoxCoordinates()[0][0].getLatitude()
						+ "@" + tweet.getLocation().getBoundingBoxCoordinates()[0][0].getLongitude());
			}

		}
		System.out.println("Tweet size" + response.size());

		for (RedditData rd : redditList) {
			if(rd.getSelftext() == null || rd.getSelftext().length() == 0) {
				list.add(rd.getTitle());
				sentitmentList.put(rd.getTitle(), "2");
			}
			else {
				list.add(rd.getTitle() + "." + rd.getSelftext());
				sentitmentList.put(rd.getTitle() + "." + rd.getSelftext(), "2");				
			}
			
			
		/*	if (!sentitmentList.containsKey(rd.getTitle() + "." + rd.getSelftext())) {
				list.add(rd.getTitle() + "." + rd.getSelftext());
				redditDataList.put(rd.getTitle() + "." + rd.getSelftext(), rd);
			}*/

		}
		//System.out.println("Reddit size" + redditList.size());

		// System.out.println("Location size"+tweetLocation.size());
		// System.out.println("Tweetsss"+tweetLocation.toString());
		System.out.println("Total Sentiment size" + sentitmentList.size());
		TwitterRequestBean twitterRequestBean = new TwitterRequestBean();
		twitterRequestBean.setTwitterDataList(list);
		twitterRequestBean.setTweetLocationList(tweetLocation);
		twitterRequestBean.setSentitmentList(sentitmentList);
		//twitterRequestBean.setRedditDataList(redditDataList);

		HttpEntity<TwitterRequestBean> entity = new HttpEntity<TwitterRequestBean>(twitterRequestBean);
		String url = "http://localhost:9090/google-semantic/home"; // need to encrypt

		ResponseEntity<SentimentWrapperResponse> result = restTemplate.exchange(url, HttpMethod.POST, entity,
				SentimentWrapperResponse.class);
		SentimentWrapperResponse sentimentWrapperResponse = result.getBody();

		return sentimentWrapperResponse;
	}

	
}
