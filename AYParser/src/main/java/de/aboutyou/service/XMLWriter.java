package de.aboutyou.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import de.aboutyou.Main;
import de.aboutyou.model.Offer;

public class XMLWriter {
	private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );
	private static final File fileXML = new File("Offers.xml");
	
	public void writeHeader() {
		
		BufferedWriter buffWriter =null;
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter(fileXML);
			buffWriter =new BufferedWriter(fileWriter);
			
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			                "<offers>\n";
			
			buffWriter.write(header); 
			buffWriter.flush();
        }
        catch(IOException ex){ 
        	LOGGER.severe(ex.getMessage()); 
        } finally{
            
           try{
               if(buffWriter!=null) {
            	   buffWriter.close();
               }
               
               if(fileWriter!=null) {
            	   fileWriter.close();
               }
               
           }
           catch(IOException ex){
            
        	   LOGGER.severe(ex.getMessage()); 
           }
       }  
	}
	
	synchronized void writeBody(List<Offer> offersList) {
		
		BufferedWriter buffWriter =null;
		FileWriter fileWriter = null;
		
		try{
			
			fileWriter = new FileWriter(fileXML,true);
			buffWriter =new BufferedWriter(fileWriter);
			
			String body = "";
			
			for(Offer offer :offersList) {
				body+="<offer>\n"+
					  "<name>"+offer.getName()+"</name>\n"+
					  "<brand>"+offer.getBrand()+"</brand>\n"+
					  "<color>"+offer.getColor()+"</color>\n"+
					  "<shop-size>"+offer.getShop_size()+"</shop-size>\n"+
					  "<vendor-size>"+offer.getVendor_size()+"</vendor-size>\n"+
					  "<price>"+offer.getPrice()+"</price>\n"+
					  "<initial-price>"+offer.getInitialPrice()+"</initial-price>\n"+
					  "<description>"+offer.getDescription()+"</description>\n"+
					  "<article-id>"+offer.getArticleID()+"</article-id>\n"+
					  "<shipping-costs>"+offer.getShippingCosts()+"</shipping-costs>\n"+
					  "</offer>\n";
			}
			
			buffWriter.write(body); 
			buffWriter.flush();
        }
        catch(IOException ex){   
        	LOGGER.severe(ex.getMessage()); 
        } finally{
            
           try{
               if(buffWriter!=null) {
            	   buffWriter.close();
               }
               
               if(fileWriter!=null) {
            	   fileWriter.close();
               }
               
           }
           catch(IOException ex){
            
        	   LOGGER.severe(ex.getMessage()); 
           }
       }  
	}
	
	public void writeFooter() {
		
		BufferedWriter buffWriter =null;
		FileWriter fileWriter = null;
		
		try{
			fileWriter = new FileWriter(fileXML,true);
			buffWriter =new BufferedWriter(fileWriter);
			
			String footer = "</offers>";
			
			buffWriter.write(footer); 
			buffWriter.flush();
        }
        catch(IOException ex){   
        	LOGGER.severe(ex.getMessage()); 
        } finally{
            
           try{
               if(buffWriter!=null) {
            	   buffWriter.close();
               }
               
               if(fileWriter!=null) {
            	   fileWriter.close();
               }
               
           }
           catch(IOException ex){
            
        	   LOGGER.severe(ex.getMessage()); 
           }
       }  
	}
	
}
