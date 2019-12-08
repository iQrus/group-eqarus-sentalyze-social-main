package com.eqarus.sentalyze.twitter;


import com.eqarus.sentalyze.twitter.configs.TweetData;
import com.eqarus.sentalyze.twitter.configs.TweetsManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import twitter4j.GeoLocation;
import twitter4j.RateLimitStatus;
import twitter4j.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
