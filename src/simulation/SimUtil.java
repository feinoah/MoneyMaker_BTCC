package simulation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apiUtil.UserConfig;
import traders.Trader;

public class SimUtil {
	private String configFile;
	private Trader trader; 
	Properties configPty; 

	public SimUtil(){
		this.configFile="config/flasheryu.pty";
		this.trader=new Trader(configFile);	
		this.configPty=UserConfig.readPty(configFile);
	}
	
	public SimUtil(String configFile){
		this.configFile=configFile;
		this.trader=new Trader(this.configFile);	
		this.configPty=UserConfig.readPty(this.configFile);
	}

	/**
     * Use getMarketDepth to get BTC's bid and ask market price
     *
     * @param 
     * 				
     * @return 
	 * @throws  
     */	
	public List<Double> getBtcMPriceInfo() throws Exception{
		int btc_num_mdepth_determine_mprice = Integer.parseInt(configPty.getProperty("btc_num_mdepth_determine_mprice"));
		double btc_valid_mprice_amount = Double.parseDouble(configPty.getProperty("btc_valid_mprice_amount"));
				
		String res = trader.getMarketDepth(btc_num_mdepth_determine_mprice,"btccny");
		JSONObject jObject = new JSONObject(res);
		JSONObject market_depth = null;
		JSONArray last_bid=null;
		JSONArray last_ask=null;
		List<Double> info = new ArrayList<Double>();
		double bid_price=0.00;
		double bid_amount=0.00;

		double ask_price=0.00;
		double ask_amount=0.00;
		
		double bid_ticker_price=0.00;
		double bid_ticker_amount=0.00;

		double ask_ticker_price=0.00;
		double ask_ticker_amount=0.00;
		
		double bid_ticker_next_price=0.00;
		double bid_ticker_next_amount=0.00;

		double ask_ticker_next_price=0.00;
		double ask_ticker_next_amount=0.00;

		try{
			JSONObject result = jObject.getJSONObject("result");
			market_depth = result.getJSONObject("market_depth");
			
			last_bid = market_depth.getJSONArray("bid");
			bid_ticker_price = last_bid.getJSONObject(0).getDouble("price");
			bid_ticker_amount = last_bid.getJSONObject(0).getDouble("amount");
			
			bid_ticker_next_price = last_bid.getJSONObject(1).getDouble("price");
			bid_ticker_next_amount = last_bid.getJSONObject(1).getDouble("amount");
			
			for(int i=0;i<btc_num_mdepth_determine_mprice;i++){
				bid_amount+=last_bid.getJSONObject(i).getDouble("amount");		
				if(bid_amount<btc_valid_mprice_amount) continue;
				else {
					bid_price=last_bid.getJSONObject(i).getDouble("price");		
					break;
				}
			}
			
			last_ask = market_depth.getJSONArray("ask");
			ask_ticker_price = last_ask.getJSONObject(0).getDouble("price");
			ask_ticker_amount = last_ask.getJSONObject(0).getDouble("amount");
			
			ask_ticker_next_price = last_ask.getJSONObject(1).getDouble("price");
			ask_ticker_next_amount = last_ask.getJSONObject(1).getDouble("amount");
			
			for(int i=0;i<btc_num_mdepth_determine_mprice;i++){
				ask_amount+=last_ask.getJSONObject(i).getDouble("amount");		
				if(ask_amount<btc_valid_mprice_amount) continue;
				else {
					ask_price=last_ask.getJSONObject(i).getDouble("price");		
					break;
				}
			}
			info.add(bid_price);
			info.add(bid_amount);
			
			info.add(ask_price);
			info.add(ask_amount);
			
			info.add(bid_ticker_price);
			info.add(bid_ticker_amount);
			
			info.add(ask_ticker_price);
			info.add(ask_ticker_amount);
			
			info.add(bid_ticker_next_price);
			info.add(bid_ticker_next_amount);
			
			info.add(ask_ticker_next_price);
			info.add(ask_ticker_next_amount);
		}
		catch (JSONException e){
			JSONObject error = jObject.getJSONObject("error");
			System.out.println("In SimuUtil::getBtcMPriceInfo():: Error Happens: " + error.getString("message"));		
		}
		
		return info;
	}
	
	
	/**
     * Use getTransactions("buybtc") to get last buy BTC's price and amount
     *
     * @param 
     * 				
     * @return 
	 * @throws  
     */		
	public List<Double> getLastBuyBtcTrans() throws Exception{
		List<Double> info = new ArrayList<Double>();
		String res = trader.getTransactions("buybtc");
		
		JSONObject jObject = new JSONObject(res);
		JSONArray transaction = null;
		JSONObject last_order=null;
		double btc=0.00;
		double price=0.00;
		try{
			JSONObject result = jObject.getJSONObject("result");
			transaction = result.getJSONArray("transaction");
			
			last_order = transaction.getJSONObject(0);
			btc=Math.abs(last_order.getDouble("btc_amount"));		
			price = Math.abs(last_order.getDouble("cny_amount") /btc);
			info.add(price);
			info.add(btc);

		}
		catch (JSONException e){
			JSONObject error = jObject.getJSONObject("error");
			System.out.println("In GetTransactionTest::getLastLtcPrice(String res):: Error Happens: " + error.getString("message"));		
		}
		
		return info;
	}
	

	/**
     * Use getTransactions("sellbtc") to get last sell BTC's price and amount
     *
     * @param 
     * 				
     * @return 
	 * @throws  
     */	
	public List<Double> getLastSellBtcTrans() throws Exception{
		List<Double> info = new ArrayList<Double>();
		String res = trader.getTransactions("sellbtc");
		
		JSONObject jObject = new JSONObject(res);
		JSONArray transaction = null;
		JSONObject last_order=null;
		double btc=0.00;
		double price=0.00;
		try{
			JSONObject result = jObject.getJSONObject("result");
			transaction = result.getJSONArray("transaction");
			
			last_order = transaction.getJSONObject(0);
			btc=Math.abs(last_order.getDouble("btc_amount"));		
			price = Math.abs(last_order.getDouble("cny_amount") /btc);
			info.add(price);
			info.add(btc);

		}
		catch (JSONException e){
			JSONObject error = jObject.getJSONObject("error");
			System.out.println("In GetTransactionTest::getLastLtcPrice(String res):: Error Happens: " + error.getString("message"));		
		}
        BigDecimal bg = new BigDecimal(btc);
        btc = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		return info;
	}
	
	/**
     * Use getMarketDepth to get LTC's bid and ask market price
     *
     * @param 
     * 				
     * @return 
	 * @throws  
     */	
	public List<Double> getLtcMPriceInfo() throws Exception{
		int ltc_num_mdepth_determine_mprice = Integer.parseInt(configPty.getProperty("ltc_num_mdepth_determine_mprice"));
		double ltc_valid_mprice_amount = Double.parseDouble(configPty.getProperty("ltc_valid_mprice_amount"));
				
		String res = trader.getMarketDepth(ltc_num_mdepth_determine_mprice,"ltccny");
		JSONObject jObject = new JSONObject(res);
		JSONObject market_depth = null;
		JSONArray last_bid=null;
		JSONArray last_ask=null;
		List<Double> info = new ArrayList<Double>();
		double bid_price=0.00;
		double bid_amount=0.00;

		double ask_price=0.00;
		double ask_amount=0.00;
		
		double bid_ticker_price=0.00;
		double bid_ticker_amount=0.00;

		double ask_ticker_price=0.00;
		double ask_ticker_amount=0.00;
		
		double bid_ticker_next_price=0.00;
		double bid_ticker_next_amount=0.00;

		double ask_ticker_next_price=0.00;
		double ask_ticker_next_amount=0.00;

		try{
			JSONObject result = jObject.getJSONObject("result");
			market_depth = result.getJSONObject("market_depth");
			
			last_bid = market_depth.getJSONArray("bid");
			bid_ticker_price = last_bid.getJSONObject(0).getDouble("price");
			bid_ticker_amount = last_bid.getJSONObject(0).getDouble("amount");
			
			bid_ticker_next_price = last_bid.getJSONObject(1).getDouble("price");
			bid_ticker_next_amount = last_bid.getJSONObject(1).getDouble("amount");
			
			for(int i=0;i<ltc_num_mdepth_determine_mprice;i++){
				bid_amount+=last_bid.getJSONObject(i).getDouble("amount");		
				if(bid_amount<ltc_valid_mprice_amount) continue;
				else {
					bid_price=last_bid.getJSONObject(i).getDouble("price");		
					break;
				}
			}
			
			last_ask = market_depth.getJSONArray("ask");
			ask_ticker_price = last_ask.getJSONObject(0).getDouble("price");
			ask_ticker_amount = last_ask.getJSONObject(0).getDouble("amount");
			
			ask_ticker_next_price = last_ask.getJSONObject(1).getDouble("price");
			ask_ticker_next_amount = last_ask.getJSONObject(1).getDouble("amount");
			
			for(int i=0;i<ltc_num_mdepth_determine_mprice;i++){
				ask_amount+=last_ask.getJSONObject(i).getDouble("amount");		
				if(ask_amount<ltc_valid_mprice_amount) continue;
				else {
					ask_price=last_ask.getJSONObject(i).getDouble("price");		
					break;
				}
			}
			info.add(bid_price);
			info.add(bid_amount);
			
			info.add(ask_price);
			info.add(ask_amount);
			
			info.add(bid_ticker_price);
			info.add(bid_ticker_amount);
			
			info.add(ask_ticker_price);
			info.add(ask_ticker_amount);
			
			info.add(bid_ticker_next_price);
			info.add(bid_ticker_next_amount);
			
			info.add(ask_ticker_next_price);
			info.add(ask_ticker_next_amount);
		}
		catch (JSONException e){
			JSONObject error = jObject.getJSONObject("error");
			System.out.println("In SimuUtil::getLtcMPriceInfo():: Error Happens: " + error.getString("message"));		
		}
		
		return info;
	}
	
}
