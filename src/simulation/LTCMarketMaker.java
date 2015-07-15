package simulation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import apiUtil.ConfigProperty;
import apiUtil.UserConfig;
import simUtil.PriceHighToLowComparator;
import simUtil.PriceLowToHighComparator;
import simUtil.orderData;
import traders.Trader;

public class LTCMarketMaker {

	private String configFile;
	private String ltcConfigFile;
	private Trader trader; 
	
	private int openorder_length;
	private long last_order_dateline=0;
	private long local_timer=0;

	private double pair_timer;
	private double pair_timer_clone;
	private double last_pair_timer_clone=1;

	private double session_timer;
	private double next_session_timer;
	
	private int ask_cancel_times=1;
	private int bid_cancel_times=1;
	
	private int ltc_openorder_limit;
	private double ltc_order_amount;
	private double ltc_spread_price;
	
	private List<orderData> open_bid_list_array = new ArrayList<orderData>();
	private List<orderData> open_ask_list_array = new ArrayList<orderData>();
	private List<orderData> last_pair_list = new ArrayList<orderData>();
	
	private List<orderData> last_filled_list = new ArrayList<orderData>();

	private List<ConfigProperty> prop = new ArrayList<ConfigProperty>();

	private double last_ask_ticker_price=0.0;
	private double last_bid_ticker_price=0.0;

	private static Logger logger = Logger.getLogger(LTCMarketMaker.class.getName());
	
	public void run(){
		try{
			local_timer++;
			putPair();
			shrinkOrders();
			if(last_pair_timer_clone!=pair_timer_clone || local_timer>100){
				updateLTCProp();
				local_timer=0;
				last_pair_timer_clone=pair_timer_clone;
			}
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}
	}
	
	public LTCMarketMaker(){
		ltcConfigFile="config/flasheryu_ltc.pty";
		configFile = "config/flasheryu.pty";
		trader=new Trader(configFile);	
		pair_timer = Double.parseDouble(UserConfig.readPty(configFile).getProperty("ltc_pair_timer"));
		pair_timer_clone=Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_pair_timer"));
		session_timer = Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_session_timer"));
		ltc_openorder_limit = Integer.parseInt(UserConfig.readPty(ltcConfigFile).getProperty("ltc_openorder_limit"));
		ltc_order_amount = Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_order_amount"));
		ltc_spread_price = Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_spread_price"));
		try{
			updateOpenList();
			initLastFilled();
			initLastPair();
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}
	}
	
	public LTCMarketMaker( String configfile){
		ltcConfigFile="config/flasheryu_ltc.pty";
		configFile=configfile;
		trader=new Trader(configFile);
		pair_timer = Double.parseDouble(UserConfig.readPty(configFile).getProperty("ltc_pair_timer"));
		pair_timer_clone=Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_pair_timer"));
		session_timer = Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_session_timer"));
		ltc_openorder_limit = Integer.parseInt(UserConfig.readPty(ltcConfigFile).getProperty("ltc_openorder_limit"));
		ltc_order_amount = Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_order_amount"));
		ltc_spread_price = Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("ltc_spread_price"));
		try{
			updateOpenList();
			initLastFilled();
			initLastPair();
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}
	}
	
	public void putPair(){
		//
		SimUtil simutil = new SimUtil(configFile);
		List<Double> info;
		try {
			info = simutil.getLtcMPriceInfo();

			double bid_ticker_price = info.get(4);
//			double bid_ticker_amount = (Double)info.get(5);
			double ask_ticker_price = info.get(6);
//			double ask_ticker_amount = (Double)info.get(7);
			
			double ask_price=ask_ticker_price;
			double bid_price=bid_ticker_price;

			// put bid orders
			String res;
			JSONObject jObject=null;
			updateOpenList();
			System.out.println("In MarketMaker::putPair():: the length of open order is : " +openorder_length);
			
			if(last_ask_ticker_price==ask_ticker_price && last_bid_ticker_price==bid_ticker_price) {
				System.out.println("In MarketMaker::putPair():: The market has not changed! Return from putPair()!");
				return;
			}
			
			last_ask_ticker_price=ask_ticker_price;
			last_bid_ticker_price=bid_ticker_price;

			int tmp_ask_id=0;
			int tmp_bid_id=0;
			orderData new_bid_order=null;
			orderData new_ask_order=null;
			if(open_bid_list_array.size()<ltc_openorder_limit && open_ask_list_array.size()<ltc_openorder_limit){
				boolean last_pair_completed=lastPairCompleted();
				if((System.currentTimeMillis()/1000-last_order_dateline>60*pair_timer_clone) || last_pair_completed){
					if(last_pair_completed) pair_timer_clone=pair_timer; //reset pair time when the last pair completed

					bid_price-=ltc_spread_price;
					ask_price+=ltc_spread_price;
					try{
						res=trader.sellOrderLTC(ask_price, ltc_order_amount);
						jObject = new JSONObject(res);
						tmp_ask_id = jObject.getInt("result");
						last_order_dateline=System.currentTimeMillis()/1000;
					} catch (Exception e){
						int err = jObject.getJSONObject("error").getInt("code");
						if(err==-32004 || err==-32003){
							try{
								trader.buyOrderLTC(ask_ticker_price, ltc_order_amount+0.001);
								last_order_dateline=System.currentTimeMillis()/1000;
							} catch (Exception e1){
								last_order_dateline=System.currentTimeMillis()/1000;
								logger.log(Level.SEVERE, "Exception found!! : {0}", e1.toString());
								return;
							}
						}
						logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
						return;
					}
					try{
						res=trader.buyOrderLTC(bid_price, ltc_order_amount);
						jObject = new JSONObject(res);
						tmp_bid_id = jObject.getInt("result");
						last_order_dateline=System.currentTimeMillis()/1000;
					} catch (Exception e){
						try{
							trader.cancelOrderLTC(tmp_ask_id);
							last_order_dateline=System.currentTimeMillis()/1000;
							new_ask_order=new orderData(tmp_ask_id,ask_price,ltc_order_amount);
							updateLastPair("ask",new_ask_order);
							logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
						} catch (Exception e1){
							logger.log(Level.SEVERE, "Miracle happens! : {0}", e1.toString());
							return;
						}
						return;
					}
					try{
						new_bid_order=new orderData(tmp_bid_id,bid_price,ltc_order_amount);
						new_ask_order=new orderData(tmp_ask_id,ask_price,ltc_order_amount);
						updateOpenList();
						updateLastPair(new_bid_order,new_ask_order);
						last_order_dateline=System.currentTimeMillis()/1000;
						pair_timer_clone+=1;
					} catch (Exception e){
						logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
					}
				}
			}
		} catch (Exception e1) {
			logger.log(Level.SEVERE, "Exception found!! : {0}", e1.toString());
		}
	}

	private void updateOpenList() {
		try{
		    Comparator<orderData> price_bid_comparator = new PriceHighToLowComparator(); // used for bid, highest with index 0
		    Comparator<orderData> price_ask_comparator = new PriceLowToHighComparator(); // used for ask, lowest with index 0 
			
			String res = trader.getOrders(true,"ltccny",50,0,0,false);
			JSONObject jObject = new JSONObject(res);
			openorder_length=jObject.getJSONObject("result").getJSONArray("order").length();
			open_ask_list_array.clear();
			open_bid_list_array.clear();

			if(jObject.getJSONObject("result").getJSONArray("order").length()!=0) {
				for(int i=0; i<openorder_length;i++){
					orderData tmp_order_data = new orderData();
					String tmp_type=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i).getString("type");
					if(tmp_type.equals("ask"))
					{
						JSONObject tmp_jObject=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i);
						
						tmp_order_data.setValues(tmp_jObject.getInt("id"), tmp_jObject.getDouble("price"), tmp_jObject.getDouble("amount"));
						open_ask_list_array.add(tmp_order_data);
					}
					else if(tmp_type.equals("bid")){
						JSONObject tmp_jObject=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i);
						
						tmp_order_data.setValues(tmp_jObject.getInt("id"), tmp_jObject.getDouble("price"), tmp_jObject.getDouble("amount"));
						open_bid_list_array.add(tmp_order_data);
					}
				}
			}

			Collections.sort(open_ask_list_array,price_ask_comparator);
			Collections.sort(open_bid_list_array,price_bid_comparator);

			System.out.println("In MarketMaker::updateOpenlist():: the length of open bid list array is "+open_bid_list_array.size()+", and the length of open ask list array is "+open_ask_list_array.size());

			System.out.println("In MarketMaker::updateOpenlist():: the open bid list array :");
			for(int i=0; i<open_bid_list_array.size();i++){
				open_bid_list_array.get(i).printSelf();
			}
			System.out.println("In MarketMaker::updateOpenlist():: the open ask list array :");
			for(int i=0; i<open_ask_list_array.size();i++){
				open_ask_list_array.get(i).printSelf();
			}
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}		
	}

	private boolean lastPairCompleted() throws Exception {
		boolean completed=false;
//		{"result":{"order":{"id":30971953,"type":"ask","price":"3101.29","currency":"CNY","amount":"0.05000000","amount_original":"0.05000000","date":1409370922,"status":"cancelled"}},"id":"1"}
		String res = trader.getOrderLTC(last_pair_list.get(0).getID());
		JSONObject jObject = new JSONObject(res);
		String last_bid_status=jObject.getJSONObject("result").getJSONObject("order").getString("status");
		
		res = trader.getOrderLTC(last_pair_list.get(1).getID());
		jObject = new JSONObject(res);
		String last_ask_status=jObject.getJSONObject("result").getJSONObject("order").getString("status");
		
		System.out.println("In MarketMaker::lastPairCompleted:: The last bid order Status: "+last_bid_status+", ID: "+last_pair_list.get(0).getID()+", Price: "+last_pair_list.get(0).getPrice()+", Amount: "+last_pair_list.get(0).getAmount());
		System.out.println("In MarketMaker::lastPairCompleted:: The last ask order Status: "+last_ask_status+", ID: "+last_pair_list.get(1).getID()+", Price: "+last_pair_list.get(1).getPrice()+", Amount: "+last_pair_list.get(1).getAmount());

		if (last_bid_status.equals("closed") && last_ask_status.equals("closed")){ // ask and bid both closed
			last_filled_list.set(0, last_pair_list.get(0));
			last_filled_list.set(1, last_pair_list.get(1));
			completed=true;
			System.out.println("In MarketMaker::lastPairCompleted:: Last pair LTC orders are completed!!");
		}
		return completed;
	}
	
	private void initLastPair() {
		try{
			last_pair_list.clear();
			if(open_bid_list_array.size()==0)
				last_pair_list.add(last_filled_list.get(0));
			else{
				last_pair_list.add(open_bid_list_array.get(0));
			}
			
			if(open_ask_list_array.size()==0)
				last_pair_list.add(last_filled_list.get(1));
			else{
				last_pair_list.add(open_ask_list_array.get(0));
			}
			System.out.println("In MarketMaker::initLastPair():: the last bid is: ID: "+last_pair_list.get(0).getID()+", Price: "+last_pair_list.get(0).getPrice()+", Amount: "+last_pair_list.get(0).getAmount());
			System.out.println("In MarketMaker::initLastPair():: the last ask is: ID: "+last_pair_list.get(1).getID()+", Price: "+last_pair_list.get(1).getPrice()+", Amount: "+last_pair_list.get(1).getAmount());
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}		
	}
	
	private void updateLastPair(orderData bid_order, orderData ask_order) {
		try{
			last_pair_list.clear();
			last_pair_list.add(bid_order);
			last_pair_list.add(ask_order);

			System.out.println("In MarketMaker::updateLastPair(orderData bid_order, orderData ask_order):: the last bid is: ID: "+last_pair_list.get(0).getID()+", Price: "+last_pair_list.get(0).getPrice()+", Amount: "+last_pair_list.get(0).getAmount());
			System.out.println("In MarketMaker::updateLastPair(orderData bid_order, orderData ask_order):: the last ask is: ID: "+last_pair_list.get(1).getID()+", Price: "+last_pair_list.get(1).getPrice()+", Amount: "+last_pair_list.get(1).getAmount());
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}		
	}
	
	private void updateLastPair(String type, orderData order) {
		try{
			if(type.equals("ask"))
				last_pair_list.set(1,order);
			else if (type.equals("bid"))
				last_pair_list.set(0,order);
			System.out.println("In MarketMaker::updateLastPair(String type, orderData order):: the last bid is: ID: "+last_pair_list.get(0).getID()+", Price: "+last_pair_list.get(0).getPrice()+", Amount: "+last_pair_list.get(0).getAmount());
			System.out.println("In MarketMaker::updateLastPair(String type, orderData order):: the last ask is: ID: "+last_pair_list.get(1).getID()+", Price: "+last_pair_list.get(1).getPrice()+", Amount: "+last_pair_list.get(1).getAmount());
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}		
	}
	
	public void shrinkOrders(){
		int cancel_times=(ask_cancel_times<bid_cancel_times)?bid_cancel_times:ask_cancel_times;
		next_session_timer=session_timer*cancel_times;
		if(System.currentTimeMillis()/1000-last_order_dateline<60*session_timer*cancel_times) return;
		try {
			updateOpenList();
//			if(open_bid_list_array.size()>ltc_openorder_limit/3 && open_ask_list_array.size()>ltc_openorder_limit/3) return;
			if(open_bid_list_array.size()<ltc_openorder_limit && open_ask_list_array.size()<ltc_openorder_limit) return;
			double sum_price=0.0;
			double sum_amount=0.0;
			if(open_bid_list_array.size()>ltc_openorder_limit-1){
				for(int i=ltc_openorder_limit-1;i>ltc_openorder_limit/2-1;i--){
					sum_price+=open_bid_list_array.get(i).getPrice()*open_bid_list_array.get(i).getAmount();
					sum_amount+=open_bid_list_array.get(i).getAmount();
					trader.cancelOrderLTC(open_bid_list_array.get(i).getID());
				}
				double bid_price=sum_price/sum_amount;
				BigDecimal tmp_price = new BigDecimal(new Double(bid_price).toString());	
				bid_price = tmp_price.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				trader.buyOrderLTC(bid_price, sum_amount);
			}
			else if(open_ask_list_array.size()>ltc_openorder_limit-1){
				for(int i=ltc_openorder_limit-1;i>ltc_openorder_limit/2-1;i--){
					sum_price+=open_ask_list_array.get(i).getPrice()*open_ask_list_array.get(i).getAmount();
					sum_amount+=open_ask_list_array.get(i).getAmount();
					trader.cancelOrderLTC(open_ask_list_array.get(i).getID());
				}
				double ask_price=sum_price/sum_amount;
				BigDecimal tmp_price = new BigDecimal(new Double(ask_price).toString());	
				ask_price = tmp_price.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				trader.sellOrderLTC(ask_price, sum_amount);				
			}
			updateOpenList();
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}
	}
	
	private void initLastFilled() {
		try{
			last_filled_list.clear();
			orderData tmp_order = new orderData(0,0,0);

			last_filled_list.add(0,tmp_order);
			last_filled_list.add(1,tmp_order);

			boolean ask_filled_done=false;
			boolean bid_filled_done=false;
			
			String res = trader.getOrders(false,"ltccny",21,0,0,false);
			JSONObject jObject = new JSONObject(res);
			int order_length=jObject.getJSONObject("result").getJSONArray("order").length();
			
			if(order_length!=0) {
				for(int i=0; i<order_length;i++){
					if(bid_filled_done && ask_filled_done) break;
					orderData tmp_order_data = new orderData();
					String tmp_status=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i).getString("status");
					if(!tmp_status.equals("closed") && !tmp_status.equals("cancelled")) continue;
					
					String tmp_type=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i).getString("type");
					if(tmp_type.equals("ask"))
					{
						JSONObject tmp_jObject=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i);
						
						tmp_order_data.setValues(tmp_jObject.getInt("id"), tmp_jObject.getDouble("price"), tmp_jObject.getDouble("amount_original"));
						last_filled_list.set(1,tmp_order_data);
						ask_filled_done=true;
					}
					else if(tmp_type.equals("bid")){
						JSONObject tmp_jObject=jObject.getJSONObject("result").getJSONArray("order").getJSONObject(i);
						
						tmp_order_data.setValues(tmp_jObject.getInt("id"), tmp_jObject.getDouble("price"), tmp_jObject.getDouble("amount_original"));
						last_filled_list.set(0,tmp_order_data);
						bid_filled_done=true;
					}
				}
			}
			
			if(last_filled_list.get(0).getID()==0){
				orderData tmp_order_data = new orderData();
				tmp_order_data.setValues(Integer.parseInt(UserConfig.readPty(ltcConfigFile).getProperty("last_filled_bid_id")), Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("last_filled_bid_price")), Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("last_filled_bid_amount")));
				last_filled_list.set(0, tmp_order_data);
			}
			if(last_filled_list.get(1).getID()==0){
				orderData tmp_order_data = new orderData();
				tmp_order_data.setValues(Integer.parseInt(UserConfig.readPty(ltcConfigFile).getProperty("last_filled_ask_id")), Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("last_filled_ask_price")), Double.parseDouble(UserConfig.readPty(ltcConfigFile).getProperty("last_filled_ask_amount")));
				last_filled_list.set(1, tmp_order_data);
			}
		
			System.out.println("In MarketMaker::initLastFilled():: the last filled bid is: ID: "+last_filled_list.get(0).getID()+", Price: "+last_filled_list.get(0).getPrice()+", Amount: "+last_filled_list.get(0).getAmount());
			System.out.println("In MarketMaker::initLastFilled():: the last filled ask is: ID: "+last_filled_list.get(1).getID()+", Price: "+last_filled_list.get(1).getPrice()+", Amount: "+last_filled_list.get(1).getAmount());
	
		} catch (Exception e){
			logger.log(Level.SEVERE, "Exception found!! : {0}", e.toString());
		}		
	}
	
	public void updateLTCProp(){
		int i =0;
		
		prop.add(i++, new ConfigProperty("ltc_pair_timer",new Double(pair_timer_clone).toString()));
		prop.add(i++, new ConfigProperty("ltc_session_timer",new Double(next_session_timer).toString()));
		prop.add(i++, new ConfigProperty("ltc_openorder_limit",new Integer(ltc_openorder_limit).toString()));
		prop.add(i++, new ConfigProperty("ltc_order_amount",new Double(ltc_order_amount).toString()));
		prop.add(i++, new ConfigProperty("ltc_spread_price",new Double(ltc_spread_price).toString()));
		
		prop.add(i++, new ConfigProperty("last_filled_bid_id",new Integer(last_filled_list.get(0).getID()).toString()));
		prop.add(i++, new ConfigProperty("last_filled_bid_price",new Double(last_filled_list.get(0).getPrice()).toString()));
		prop.add(i++, new ConfigProperty("last_filled_bid_amount",new Double(last_filled_list.get(0).getAmount()).toString()));
		
		prop.add(i++, new ConfigProperty("last_filled_ask_id",new Integer(last_filled_list.get(1).getID()).toString()));
		prop.add(i++, new ConfigProperty("last_filled_ask_price",new Double(last_filled_list.get(1).getPrice()).toString()));
		prop.add(i++, new ConfigProperty("last_filled_ask_amount",new Double(last_filled_list.get(1).getAmount()).toString()));
		
		UserConfig.writePty(ltcConfigFile, prop);
	}
}
