package com.eqarus.sentalyze.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.abinj.twittersentimentanalysis.TweetData;
import com.abinj.twittersentimentanalysis.TweetWithSentiment;
import com.abinj.twittersentimentanalysis.application.MainApplication;
import com.eqarus.sentalyze.model.SentimentRequest;
import com.eqarus.sentalyze.request.TwitterRequestBean;
import com.equarus.sentalyze.response.SentimentWrapperResponse;

@RestController
public class SocialDataController {
	@Autowired
	private RestTemplate restTemplate;

	private final MainApplication appService = new MainApplication();

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

		for (TweetData tweet : response) {
			System.out.println("##Language of tweet : " + tweet.getLanguage());	//added language restrictions
			if (tweet.getLanguage().equalsIgnoreCase("en"))
				;
			list.add(tweet.getTweet());
		}
		TwitterRequestBean twitterRequestBean = new TwitterRequestBean();
		twitterRequestBean.setTwitterDataList(list);
		HttpEntity<TwitterRequestBean> entity = new HttpEntity<TwitterRequestBean>(twitterRequestBean);
		String url = "http://192.168.1.11:8090/google-semantic/home";	//need to encrypt

		ResponseEntity<SentimentWrapperResponse> result = restTemplate.exchange(url, HttpMethod.POST, entity,
				SentimentWrapperResponse.class);
		SentimentWrapperResponse sentimentWrapperResponse = result.getBody();

		System.out.println("####Response from Nandan####" + sentimentWrapperResponse.getSentimentResponseMap());

		return sentimentWrapperResponse;
	}
}
