package de.aboutyou;

import java.util.logging.Logger;

import de.aboutyou.service.StartParsing;

/**
 * @author Anastasiia Kozachuk
 *
 */
public class Main 
{
	private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );
	
    public static void main( String[] args )
    {
    	long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    	
    	StartParsing parserAY = new StartParsing(args[0]);
    	parserAY.runParsing();
    	
    	long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    	
    	LOGGER.info("\nMemory footprint: "+(afterUsedMem-beforeUsedMem)+" bytes\n");
    	
    	
    }
}
