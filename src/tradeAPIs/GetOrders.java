package tradeAPIs;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import apiUtil.*;
 
public class GetOrders {
 
	private String configFile;
	private String ACCESS_KEY;
	private String SECRET_KEY;
	private String apiUrl;
	
	private static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static Logger logger = Logger.getLogger(GetOrders.class.getName());
	 
	public GetOrders(){
		this.configFile = UserConfig.getDefaultConfig();
		this.ACCESS_KEY = UserConfig.getAccessKey(this.configFile);
		this.SECRET_KEY = UserConfig.getSecretKey(this.configFile);
		this.apiUrl = UserConfig.getApiUrl(this.configFile);
	}
	
	public GetOrders(String configFile){
		this.configFile = configFile;
		this.ACCESS_KEY = UserConfig.getAccessKey(this.configFile);
		this.SECRET_KEY = UserConfig.getSecretKey(this.configFile);
		this.apiUrl = UserConfig.getApiUrl(this.configFile);
	}
	 
	public static String getSignature(String data,String key) throws Exception {
	 
		// get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		 
		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		 
		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(data.getBytes());
		 
		return bytArrayToHex(rawHmac);
	}
 
 
	private static String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for(byte b: a)
			sb.append(String.format("%02x", b&0xff));
		return sb.toString();
	}
 
	public String getOrders() throws Exception{
		 
		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		//int order_id=10;
	//	String param_order_id=""+order_id;
//		String param_order_id = SubZeroAndDot.removeZero(new Integer(order_id).toString());
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getOrders&params=";
		String hash = getSignature(params, SECRET_KEY);
	
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
		URLConnection conn = obj.openConnection();
		
		if (conn instanceof HttpsURLConnection) {
		    HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
		    
		  //add reuqest header
		    httpsConn.setRequestMethod("POST");
		    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		    httpsConn.setRequestProperty ("Authorization", basicAuth);
		 
			String postdata = "{\"method\": \"getOrders\", \"params\": [], \"id\": 1}";
		 
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
		 
//			int responseCode = httpsConn.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + apiUrl);
//			System.out.println("Post parameters : " + postdata);
//			System.out.println("Response Code : " + responseCode);
			
			logger.log(Level.INFO, "Post parameters : {0}", params);	
			logger.log(Level.INFO, "Post parameters : {0}", postdata);	
					
			BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
		
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			//print result
//			System.out.println(response.toString());
			logger.log(Level.WARNING, "Response : {0}", response.toString());	
			
			return response.toString();
		}	
		else{
		    HttpURLConnection httpConn = (HttpURLConnection) conn;
			//add reuqest header
		    httpConn.setRequestMethod("POST");
		    httpConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		    httpConn.setRequestProperty ("Authorization", basicAuth);
		 
			String postdata = "{\"method\": \"getOrders\", \"params\": [], \"id\": 1}";
		 
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
		 
//			int responseCode = httpConn.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + apiUrl);
//			System.out.println("Post parameters : " + postdata);
//			System.out.println("Response Code : " + responseCode);

			logger.log(Level.INFO, "Post parameters : {0}", params);	
			logger.log(Level.INFO, "Post parameters : {0}", postdata);	
					
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
		
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			//print result
//			System.out.println(response.toString());

			logger.log(Level.WARNING, "Response : {0}", response.toString());	
			return response.toString();
		}
	}
	
	public String getOrders(boolean openonly, String market, int limit, int offset) throws Exception{
		 
		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		//int order_id=10;
	//	String param_order_id=""+order_id;
		String param_openonly = (openonly==true)?"1":""; 
		String postdata_openonly = (openonly==true)?"true":"false"; 
		String param_limit = SubZeroAndDot.removeZero(new Integer(limit).toString());
		String param_offset = SubZeroAndDot.removeZero(new Integer(offset).toString());

		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getOrders&params="+param_openonly+","+market+","+param_limit+","+param_offset;
		String hash = getSignature(params, SECRET_KEY);
	
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
		URLConnection conn = obj.openConnection();
		
		if (conn instanceof HttpsURLConnection) {
		    HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
		    
		  //add reuqest header
		    httpsConn.setRequestMethod("POST");
		    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		    httpsConn.setRequestProperty ("Authorization", basicAuth);
		 
			String postdata = "{\"method\": \"getOrders\", \"params\": ["+postdata_openonly+",\""+market+"\","+param_limit+","+param_offset+"], \"id\": 1}";
		 
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
		 
//			int responseCode = httpsConn.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + apiUrl);
//			System.out.println("Post parameters : " + postdata);
//			System.out.println("Response Code : " + responseCode);
		
			logger.log(Level.INFO, "Post parameters : {0}", params);	
			logger.log(Level.INFO, "Post parameters : {0}", postdata);	
		
			BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
		
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			//print result
//			System.out.println(response.toString());

			logger.log(Level.WARNING, "Response : {0}", response.toString());	

			return response.toString();
		}	
		else{
		    HttpURLConnection httpConn = (HttpURLConnection) conn;
			//add reuqest header
		    httpConn.setRequestMethod("POST");
		    httpConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		    httpConn.setRequestProperty ("Authorization", basicAuth);
		 
			String postdata = "{\"method\": \"getOrders\", \"params\": ["+postdata_openonly+",\""+market+"\","+param_limit+","+param_offset+"], \"id\": 1}";
		 
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
		 
//			int responseCode = httpConn.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + apiUrl);
//			System.out.println("Post parameters : " + postdata);
//			System.out.println("Response Code : " + responseCode);
		
			logger.log(Level.INFO, "Post parameters : {0}", params);	
			logger.log(Level.INFO, "Post parameters : {0}", postdata);	
				
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
		
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			//print result
//			System.out.println(response.toString());

			logger.log(Level.WARNING, "Response : {0}", response.toString());	

			return response.toString();
		}
	}
	
	public String getOrders(boolean openonly, String market, int limit, int offset, int since, boolean withdetail) throws Exception{
		 
		String tonce = ""+(System.currentTimeMillis() * 1000);
		
		//int order_id=10;
	//	String param_order_id=""+order_id;
		String param_openonly = (openonly==true)?"1":""; 
		String postdata_openonly = (openonly==true)?"true":"false"; 
		
		String param_withdetail = (withdetail==true)?"1":""; 
		String postdata_withdetail = (withdetail==true)?"true":"false"; 
		
		String param_since = SubZeroAndDot.removeZero(new Integer(since).toString());
		String param_limit = SubZeroAndDot.removeZero(new Integer(limit).toString());
		String param_offset = SubZeroAndDot.removeZero(new Integer(offset).toString());

		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getOrders&params="+param_openonly+","+market+","+param_limit+","+param_offset+","+param_since+","+param_withdetail;
		String hash = getSignature(params, SECRET_KEY);
	
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		URL obj = new URL(apiUrl);
		URLConnection conn = obj.openConnection();
		
		if (conn instanceof HttpsURLConnection) {
		    HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
		    
		  //add reuqest header
		    httpsConn.setRequestMethod("POST");
		    httpsConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		    httpsConn.setRequestProperty ("Authorization", basicAuth);
		 
			String postdata = "{\"method\": \"getOrders\", \"params\": ["+postdata_openonly+",\""+market+"\","+param_limit+","+param_offset+","+param_since+","+postdata_withdetail+"], \"id\": 1}";
		 
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpsConn.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
		 
//			int responseCode = httpsConn.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + apiUrl);
//			System.out.println("Post parameters : " + postdata);
//			System.out.println("Response Code : " + responseCode);
		
			logger.log(Level.INFO, "Post parameters : {0}", params);	
			logger.log(Level.INFO, "Post parameters : {0}", postdata);	
		
			BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
		
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			//print result
//			System.out.println(response.toString());

			logger.log(Level.WARNING, "Response : {0}", response.toString());	

			return response.toString();
		}	
		else{
		    HttpURLConnection httpConn = (HttpURLConnection) conn;
			//add reuqest header
		    httpConn.setRequestMethod("POST");
		    httpConn.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		    httpConn.setRequestProperty ("Authorization", basicAuth);
		 
			String postdata = "{\"method\": \"getOrders\", \"params\": ["+postdata_openonly+",\""+market+"\","+param_limit+","+param_offset+","+param_since+","+postdata_withdetail+"], \"id\": 1}";
		 
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();
		 
//			int responseCode = httpConn.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + apiUrl);
//			System.out.println("Post parameters : " + postdata);
//			System.out.println("Response Code : " + responseCode);
		
			logger.log(Level.INFO, "Post parameters : {0}", params);	
			logger.log(Level.INFO, "Post parameters : {0}", postdata);	
				
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
		
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			//print result
//			System.out.println(response.toString());

			logger.log(Level.WARNING, "Response : {0}", response.toString());	

			return response.toString();
		}
	}
}

