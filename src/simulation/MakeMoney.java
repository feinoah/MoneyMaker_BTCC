package simulation;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import apiUtil.DisableSSLValidation;

public class MakeMoney {
	
//	private String configFile = "config/flasheryu.pty";
	private String configFile = "config/prodtest.pty";
	SimUtil simutil = new SimUtil(configFile);
	private BTCMarketMaker BMM = new BTCMarketMaker(configFile);
//	private LTCMarketMaker LMM = new LTCMarketMaker(configFile);
	
	private static Logger logger = Logger.getLogger(MakeMoney.class.getName());

	public static void main(String[] args) throws Exception {
		FileHandler fh;  
		DisableSSLValidation.install();

	    try {  
	    	// This block configure the logger with handler and formatter  
	    	fh = new FileHandler("/Users/guoqing/makeMoney.log");  
		    logger.addHandler(fh);
		    SimpleFormatter formatter = new SimpleFormatter();  
		    fh.setFormatter(formatter);  
		    // the following statement is used to log any messages  
		    logger.info("Trading logs");  

	    } catch (SecurityException e) {  
	    	e.printStackTrace();  
	    } catch (IOException e) {  
	    	e.printStackTrace();  
	    }  

		MakeMoney MM = new MakeMoney();
		while(true){
			try{
				MM.BMM.run();
//				MM.LMM.run();
			} catch (Exception e){
				logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
				continue;
			}
		}		
	}
			
}
