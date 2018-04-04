package de.aboutyou.service;

import java.net.URLEncoder;

public class StartParsing {
	private final String base_URL = "https://www.aboutyou.de/suche?fromCategory=20201&gender=female&term=";
	private final String search_URL;
	
	public StartParsing(String keyWord){
		this.search_URL = getSearchURL(keyWord);
	}
	
	private String getSearchURL(String keyWord) {
		String encodeKW = null;
		try {
			encodeKW = URLEncoder.encode(keyWord, "UTF-8");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(encodeKW!=null) {
			return this.base_URL+encodeKW;
		}
		return null;
	}
	
	public void runParsing() {
		if(this.search_URL!=null) {
			ParsePage parseURL = new ParsePage(this.search_URL);
			parseURL.startPageParse();
		}
	}
}
