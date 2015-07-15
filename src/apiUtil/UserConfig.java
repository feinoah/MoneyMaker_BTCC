package apiUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class UserConfig {

	public static void writePty(String configfile) 
	{
		Properties pty = new Properties();
		FileOutputStream file = null;
		try {
			file = new FileOutputStream(configfile);
			pty.storeToXML(file, "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writePty(String configfile, List<ConfigProperty> prop){
		Properties pty = new Properties();
		FileOutputStream fos = null;
		FileInputStream fis = null;
		for(int i=0; i<prop.size();i++)
			pty.put(prop.get(i).getKey(),prop.get(i).getValue());
		try {
			fos = new FileOutputStream(configfile);
			fis = new FileInputStream(configfile);

			pty.storeToXML(fos, "auto trading system");
	         // print the xml
	         while (fis.available() > 0) {
	            System.out.print("" + (char) fis.read());
	         }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public static Properties readPty(String configfile)
	{
		FileInputStream file = null;
		Properties pty = new Properties();
		try {
			file = new FileInputStream(configfile);
			pty.loadFromXML(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pty;
		
	}
	
	public static String getAccessKey(String configFile){
		Properties configPty = readPty(configFile);
		return configPty.getProperty("vip_ACCESS_KEY");
	} 
	
	public static String getSecretKey(String configFile){
		Properties configPty = readPty(configFile);
		return configPty.getProperty("vip_SECRET_KEY");
	} 	
	
	public static String getApiUrl(String configFile){
		Properties configPty = readPty(configFile);
		return 	configPty.getProperty("vip_apiUrl");
	}
		
	public static String getDefaultConfig(){
		return "config/userConfig.pty";
	}
}
