package de.aboutyou.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import de.aboutyou.SingletonHash;

public class ParsePage {
	private final String main_URL = "https://www.aboutyou.de";
	private final String parse_URL;
	private static final Integer AMMOUNTOFTHREADS = 200;
	private static final Logger LOGGER = Logger.getLogger( ParsePage.class.getName() );
	
	ParsePage (String searchURL){
		this.parse_URL = searchURL;
	}
	
	
	public void startPageParse() {
		
		Document document = null;     	
		HashSet<String> links=new HashSet<String>();
		String url =parse_URL;
		
		int amountOFPage=0;
		
		long timer = -System.currentTimeMillis();
		
		XMLWriter writer = new XMLWriter();
		writer.writeHeader();
		while(!url.equals("")) {	
			
			try {
				document = Jsoup.connect(url).get(); 	
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
        	
			Elements productsURL = document.select("a.anchor_wgmchy");
			productsURL = (productsURL.size()>0?productsURL:document.select("a[itemprop=url]"));
    		links.addAll(getHashSetOfLnk(productsURL));
    		
    		amountOFPage++;
    		
    		Elements pageURL1 = document.select("li.nextButton_3hmsj").select("a.arrow_1wkskdo");
    		Elements pageURL2 = document.select("li.next").select("a");
    		url = (pageURL1.size()>0?pageURL1.attr("href"):(pageURL2.size()>0?(main_URL+pageURL2.attr("href")):""));
    		
    	}
		
		Semaphore smp = new Semaphore(AMMOUNTOFTHREADS);
		
		for(String link : links) {
			ParseProduct parseProduct = new ParseProduct(main_URL+link,smp);
			Thread thread = new Thread(parseProduct);
			thread.start();
		}
		

		do {
			
		}while(Thread.activeCount()>1);
		
		writer.writeFooter();
		timer += System.currentTimeMillis();
		
		LOGGER.info("\nRun-time: " + timer/1000.0+" sec.\n");
		LOGGER.info("\nAmmount of triggered http requests: "+(SingletonHash.getFinishedUrl().size()+amountOFPage)+"\n");
		LOGGER.info("\nAmmount of extracted products found on result pages: "+links.size()+"\n");	
		LOGGER.info("\nAmmount of extracted products taking in account all of their color and size combinations (saved in \"Offers.xml\" file): "+SingletonHash.getFinishedUrl().size()+"\n");
		
	}
	
	
	
	
	private HashSet<String> getHashSetOfLnk(Elements links){
		HashSet<String> setOfLink = null;
		if(links!=null) {
			setOfLink = new HashSet<String>();
        	for(Element link: links) {
        		setOfLink.add(link.attr("href"));
        	}
    	}
		return setOfLink;
	}
}
