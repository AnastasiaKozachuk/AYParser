package de.aboutyou;

import java.util.HashSet;


//hash that consists of unique parsed URLs
//this hash helps to determine whether an URL needs to be parsed 
//I used Singleton pattern to create only one hash that contains already parsed URLs
public class SingletonHash {	
	
	private SingletonHash() {}
	
	//this class provides thread safety
	private static class SingletonHelper{
		private static final SingletonHash INSTANCE = new SingletonHash();
		private static HashSet<String> finishedURL = new HashSet<String>();
	}
	
	public static SingletonHash getInstance() {
		return SingletonHelper.INSTANCE;
	}
	
	public static HashSet<String> getFinishedUrl(){
		return SingletonHelper.finishedURL;
	}
	
	public static void addToFinishedUrl(String url) {
		SingletonHelper.finishedURL.add(url);
	}
	
}
