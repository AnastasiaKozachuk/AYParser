package de.aboutyou.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.aboutyou.SingletonHash;
import de.aboutyou.model.Offer;



public class ParseProduct implements Runnable{

	private static final Logger LOGGER = Logger.getLogger( ParseProduct.class.getName() );
	private final String prod_URL;
	private final String main_URL = "https://www.aboutyou.de";
	private final Semaphore sem;
	
	ParseProduct(String prodURL , Semaphore smp){
		this.prod_URL=prodURL;
		this.sem = smp;
	}
		
	public void run() {
		
		try {
			
			this.sem.acquire();
		
			if(!SingletonHash.getFinishedUrl().contains(prod_URL)) {
				LOGGER.info("Parsing url: "+prod_URL);
				startParseProdURL();
			}
    	
			this.sem.release();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void startParseProdURL() {
		
		Document document = null;
		
    	try {
			 document = Jsoup.connect(this.prod_URL).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	JsonObject jsonObject = getPageJsonObject(document);
    	
    	Boolean isActive = jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonObject("data").get("isActive").getAsBoolean();
    	
    	List<Offer> offersList = new LinkedList<Offer>();	 
    	
    	if(isActive) {
    		offersList.addAll(parseActiveProduct(jsonObject));
    		SingletonHash.addToFinishedUrl(prod_URL);
    		offersList.addAll(parseAnotherColor(jsonObject));
    	}
    	
    	XMLWriter writer = new XMLWriter();
    	writer.writeBody(offersList);
	}

	private JsonObject getPageJsonObject(Document document) {
		JsonParser jsonParser = new JsonParser();
    	Elements scripts = document.select("script");
    	String jsonString = (scripts.get(scripts.size()-6)).data();
    	jsonString = jsonString.substring(jsonString.indexOf("{"),jsonString.lastIndexOf(";"));
    	
    	return (JsonObject) jsonParser.parse(jsonString);
	}
	
	
	//parsing products by color separately is needed, since products are not shown in all colors on result page
	private List<Offer> parseAnotherColor(JsonObject jsonObject){
		
		List<Offer> offersList = new LinkedList<Offer>();
		JsonArray jsonArray =  jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonArray("styles");
		Document document = null;
		
		for(int i=1;i<jsonArray.size();i++) {
    		String url = main_URL+jsonArray.get(i).getAsJsonObject().get("url").getAsString();
    		
    		
    		
    		if(!SingletonHash.getFinishedUrl().contains(url)) {
    			LOGGER.info("Parsing url: "+prod_URL);
    			try {
    				 document = Jsoup.connect(url).get();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			
    			JsonObject jsonObj = getPageJsonObject(document);
    			offersList.addAll(parseActiveProduct(jsonObj));
    			SingletonHash.addToFinishedUrl(url);
    		}
			
    	}
		
		return offersList;
	}
	
	
	
	private List<Offer> parseActiveProduct( JsonObject jsonObject){

		String name =  jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonObject("data").get("name").getAsString();		
		String brand = jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonObject("brand").get("name").getAsString();
		String articleID = jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonObject("productInfo").get("articleNumber").getAsString();	
		String descriptionWithHTML = jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonObject("productInfo").get("description").getAsString();	
		String description = Jsoup.parse(descriptionWithHTML).text();
		String color = jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonArray("styles").get(0).getAsJsonObject().get("color").getAsString();
	
    	List<Offer> offersList = new LinkedList<Offer>();
    	
    	for(JsonElement jsObj: jsonObject.getAsJsonObject("adpPage").getAsJsonObject("product").getAsJsonArray("variants")) {
    		
    		Integer quantity = jsObj.getAsJsonObject().get("quantity").getAsInt();
    		
    		if(quantity>0) {
    			
    			Offer offer = new Offer();

    			String currentPrice = String.format("%.2f",jsObj.getAsJsonObject().getAsJsonObject("price").get("current").getAsInt()/100.0);
        		String oldPrice = String.format("%.2f",jsObj.getAsJsonObject().getAsJsonObject("price").get("old").getAsInt()/100.0);	
        		oldPrice = (oldPrice.equals("0,00")?currentPrice:oldPrice);
        		
        		JsonElement jsSize = jsObj.getAsJsonObject().getAsJsonObject("sizes").get("shop");
        		String shop_size = (jsSize==JsonNull.INSTANCE)? "" :jsSize.getAsString();
            	jsSize = jsObj.getAsJsonObject().getAsJsonObject("sizes").get("vendor");
            	String vendor_size = (jsSize==JsonNull.INSTANCE)? "" :jsSize.getAsString();

    			offer.setName(name);
    			offer.setBrand(brand);
    			offer.setArticleID(articleID);
    			offer.setDescription(description);
    			offer.setColor(color);
    			offer.setPrice(currentPrice);
    			offer.setInitialPrice(oldPrice);
    			offer.setShop_size(shop_size);
        		offer.setVendor_size(vendor_size);
    			
    			offersList.add(offer);
    			
    		}	
    		
    	}
    	
    	return offersList;
	}
	
}
