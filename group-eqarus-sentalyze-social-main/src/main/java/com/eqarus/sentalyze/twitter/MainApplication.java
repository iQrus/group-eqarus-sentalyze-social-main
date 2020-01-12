package com.eqarus.sentalyze.twitter;


import com.eqarus.sentalyze.model.RedditData;
import com.eqarus.sentalyze.twitter.configs.TweetData;
import com.eqarus.sentalyze.twitter.configs.TweetsManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import twitter4j.GeoLocation;
import twitter4j.RateLimitStatus;
import twitter4j.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MainApplication {

    private static TweetsManager tweetsManager = new TweetsManager("KMho7wQkiqtOXbBk2hImewXA9"
            , "XN6afZwqAmJC2kaUxxd9E9IlnrSFiBazo6m77bfHAVpxKNBpCN"
            , "3322466742-ng0ppcCkvwgHeJIgqab6wLDYt77GPgVBKVabETH"
            , "DpfHxzmMN5pWZkCHlRuxn9ozGt5syXf4vW0YPbThpgcBO");
    
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<TweetData> getSocialNetDetails(String word) throws InterruptedException, IOException{
        String searchKeywords = word;
        List<TweetData> sentiments = null;
        if (searchKeywords == null || searchKeywords.length() == 0) {
            return null;
        }

        Set<String> keywords = new HashSet<>();
        for (String keyword : searchKeywords.split(",")) {
            keywords.add(keyword.trim().toLowerCase());
        }
        if (keywords.size() > 3) {
            keywords = new HashSet<>(new ArrayList<>(keywords).subList(0, 3));
        }
        for (String keyword : keywords) {
            List<Status> statuses = tweetsManager.performQuery(keyword);
            System.out.println("Found statuses ... " + statuses.size());

            //Get sentiment scores
            //    scale of 0 = very negative, 1 = negative, 2 = neutral, 3 = positive,
            //    and 4 = very positive.
            sentiments = new ArrayList<>();
            for (Status status : statuses) {
            	TweetData tweet = new TweetData();
                //TweetWithSentiment tweetWithSentiment = sentimentAnalyzer.findSentiment(status.getText());
                if (status != null ) {
                	tweet.setTweet((null == status? "No text":status.getText()));
                    tweet.setPlace((status.getPlace() == null)? "Mordor":status.getPlace().getCountry());
                    tweet.setUser((status.getUser()==null)? "Sodang":status.getUser().getName());
                    tweet.setHashtags((status.getHashtagEntities()==null)?"Badhia Hashtag" : (status.getHashtagEntities().length>0)? status.getHashtagEntities()[0].getText():"Badhiya Hashtag");
                    tweet.setLanguage(status.getLang());
                    
                    if(!tweet.getPlace().equalsIgnoreCase("Mordor")) {                    	
                    	tweet.setLocation(status.getPlace());
                    }
                    else {
                    	if(!status.getUser().getLocation().isEmpty())
                    		System.out.println(status.getUser().getLocation());
                    	tweet.setLocation(null);
                    }
                    sentiments.add(tweet);
                }
            }
            System.out.println(keyword + ": " + gson.toJson(sentiments));
        }
        return sentiments;
    }
    private String getAuthToken(RestTemplate restTemplate) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("lKo6fF37TJKUcg", "U2-WfN4X31ADgMPLSssNdycxNho");
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.put("User-Agent", Collections
				.singletonList("tomcat:com.sentalyze.s3-website.ap-south-1.amazonaws.senti:v1.0 (by /u/BandiQ21)"));
		String body = "grant_type=client_credentials";
		HttpEntity<String> request = new HttpEntity<>(body, headers);
		String authUrl = "https://www.reddit.com/api/v1/access_token";
		ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			map.putAll(mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
			}));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(response.getBody());
		return String.valueOf(map.get("access_token"));
	}

	public String readArticles(String keyword, RestTemplate restTemplate) {
		HttpHeaders headers = new HttpHeaders();
		String authToken = getAuthToken(restTemplate);
		headers.setBearerAuth(authToken);
		headers.put("User-Agent", Collections
				.singletonList("tomcat:com.sentalyze.s3-website.ap-south-1.amazonaws.senti:v1.0 (by /u/BandiQ21)"));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		// String url = "https://oauth.reddit.com/r/India/hot";
		// String url = "https://oauth.reddit.com/r/India/about";
		String url = "https://oauth.reddit.com/search/?q=" + keyword + "&type=link&sort=new&limit=100";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}

	public List<RedditData> redditPostExtraction(String keyword, RestTemplate restTemplate) {
		JSONObject obj = new JSONObject(readArticles(keyword, restTemplate));
		List<RedditData> redditList = new ArrayList<RedditData>();
		JSONObject obj1 = obj.getJSONObject("data");

		JSONArray arr = obj1.getJSONArray("children");
		for (int i = 0; i < arr.length(); i++) {

			JSONObject obj2 = arr.getJSONObject(i).getJSONObject("data");
			RedditData rd = new RedditData();
			rd.setSubreddit(obj2.getString("subreddit") == null ? "" : obj2.getString("subreddit"));
			rd.setSelftext(obj2.getString("selftext") == null ? "" : obj2.getString("selftext"));
			rd.setTitle(obj2.getString("title") == null ? "" : obj2.getString("title"));
			redditList.add(rd);
		}
		return redditList;
	}

    
}
