package com.eqarus.sentalyze.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.eqarus.sentalyze.request.TwitterRequestBean;
import com.eqarus.sentalyze.twitter.MainApplication;
import com.eqarus.sentalyze.twitter.configs.TweetData;
import com.equarus.sentalyze.response.SentimentResponseBean;
import com.equarus.sentalyze.response.SentimentWrapperResponse;

@RestController
public class SocialDataController {
	@Autowired
	private RestTemplate restTemplate;

	private final MainApplication appService = new MainApplication();
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/getSentiments")
	public SentimentWrapperResponse getSentiments(
			@RequestParam(value = "keyword", defaultValue = "Article370") String keyword) {
		List<TweetData> response = null;
		List<String> list = new ArrayList<String>();
		try {
			response = appService.getSocialNetDetails(keyword);
		} catch (InterruptedException e) {
			// TODO The developer has to define global exceptions and log it here
			e.printStackTrace();
		} catch (IOException e) {
			// TODO to be done by Nandan
			e.printStackTrace();
		}
		System.out.println("Response :"+response.toString());
		for (TweetData tweet : response) {
			list.add(tweet.getTweet());
		}
		TwitterRequestBean twitterRequestBean = new TwitterRequestBean();
		twitterRequestBean.setTwitterDataList(list);
		HttpEntity<TwitterRequestBean> entity = new HttpEntity<TwitterRequestBean>(twitterRequestBean);
		String url = "http://localhost:8080/google-semantic/home"; // need to encrypt

		ResponseEntity<SentimentWrapperResponse> result = restTemplate.exchange(url, HttpMethod.POST, entity,
				SentimentWrapperResponse.class);
		SentimentWrapperResponse sentimentWrapperResponse = result.getBody();

		return sentimentWrapperResponse;
	}
}
