package traders;

import apiUtil.UserConfig;
import tradeAPIs.*;

public class Trader {

	private String configFile;
	
	public Trader(){
		this.configFile = UserConfig.getDefaultConfig();
	}
	
	public Trader( String configFile ){
		this.configFile = configFile;	
	}
	
	/**
     * Trade APIs.
     *
     * @param 
     * 				
     * @return 
	 * @throws  
     */	
	// Trade: Get Account Info of type
	public String getAccountInfo(String type ) throws Exception {
		GetAccountInfo getInfo = new GetAccountInfo(configFile);
		String result = getInfo.getAccountInfo(type);
		return result;
	}
	
	// Trade: Get Account Info of all three types: balance, frozen and profile
	public String getAccountInfo() throws Exception {
		GetAccountInfo getInfo = new GetAccountInfo(configFile);
		String result = getInfo.getAccountInfo();
		return result;
	}
	
	// Trade: Buy Order on @market
	public String buyOrder(double price, double amount, String market) throws Exception {
		BuyOrder buyOrder = new BuyOrder(configFile);
		String result = buyOrder.buyOrder2(price, amount, market);
		return result;
	}	
	
	// Trade: Buy Order BTC/CNY
	public String buyOrder(double price, double amount) throws Exception {
		BuyOrder buyOrder = new BuyOrder(configFile);
		String result = buyOrder.buyOrder2(price,amount,"btccny");
		return result;
	}	
	
	// Trade: Buy Order BTC/CNY
	public String buyOrderBTC(double price, double amount) throws Exception {
		BuyOrder buyOrder = new BuyOrder(configFile);
		String result = buyOrder.buyOrder2(price, amount,"btccny");
		return result;
	}	
	
	// Trade: Buy Order LTC/CNY
	public String buyOrderLTC(double price, double amount) throws Exception {
		BuyOrder buyOrder = new BuyOrder(configFile);
		String result = buyOrder.buyOrder2(price, amount,"LTCCNY");
		return result;
	}
	
	// Trade: Buy Order LTC/BTC
	public String buyOrderLtcBtc(double price, double amount) throws Exception {
		BuyOrder buyOrder = new BuyOrder(configFile);
		String result = buyOrder.buyOrder2(price, amount,"LTCBTC");
		return result;
	}
	
	// Trade: Sell Order on @market
	public String sellOrder(double price, double amount, String market) throws Exception {
		SellOrder sellOrder = new SellOrder(configFile);
		String result = sellOrder.sellOrder2(price, amount, market);
		return result;
	}	
	
	// Trade: Sell Order BTC/CNY
	public String sellOrder(double price, double amount) throws Exception {
		SellOrder sellOrder = new SellOrder(configFile);
		String result = sellOrder.sellOrder2(price, amount,"btccny");
		return result;
	}		
	
	// Trade: Sell Order BTC/CNY
	public String sellOrderBTC(double price, double amount) throws Exception {
		SellOrder sellOrder = new SellOrder(configFile);
		String result = sellOrder.sellOrder2(price, amount,"btccny");
		return result;
	}	
	
	// Trade: Sell Order LTC/CNY
	public String sellOrderLTC(double price, double amount) throws Exception {
		SellOrder sellOrder = new SellOrder(configFile);
		String result = sellOrder.sellOrder2(price, amount, "ltccny");
		return result;
	}	
	
	// Trade: Sell Order LTC/BTC
	public String sellOrderLtcBtc(double price, double amount) throws Exception {
		SellOrder sellOrder = new SellOrder(configFile);
		String result = sellOrder.sellOrder2(price, amount, "ltcbtc");
		return result;
	}
	
	// Trade: Get order on @market with @withdetail
	public String getOrder(int order_id, String market, boolean withdetail) throws Exception{
		GetOrder getOrder = new GetOrder(configFile);

		String result = getOrder.getOrder(order_id, market, withdetail);
		
		return result;
	}
	
	// Trade: Get order on @market
	public String getOrder(int order_id, String market) throws Exception{
		GetOrder getOrder = new GetOrder(configFile);

		String result = getOrder.getOrder(order_id, market);
		
		return result;
	}
	
	// Trade: Get BTCCNY order
	public String getOrder(int order_id) throws Exception{
		GetOrder getOrder = new GetOrder(configFile);

		String result = getOrder.getOrder(order_id,"btccny",true);
		
		return result;
	}
	
	// Trade: Get BTCCNY order
	public String getOrderBTC(int order_id) throws Exception{
		GetOrder getOrder = new GetOrder(configFile);

		String result = getOrder.getOrder(order_id);
		
		return result;
	}
	
	// Trade: Get LTCCNY order
	public String getOrderLTC(int order_id) throws Exception{
		GetOrder getOrder = new GetOrder(configFile);

		String result = getOrder.getOrder(order_id, "ltccny");
		
		return result;
	}
	
	// Trade: Get LTCBTC order
	public String getOrderLtcBtc(int order_id) throws Exception{
		GetOrder getOrder = new GetOrder(configFile);

		String result = getOrder.getOrder(order_id, "ltcbtc");
		
		return result;
	}
	
	// Trade: Get orders on @market with @openonly @limit @offset
	public String getOrders(boolean openonly, String market, int limit, int offset, int since, boolean withdetail ) throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders(openonly, market, limit, offset, since, withdetail);
		
		return result;
	}
	
	// Trade: Get orders on @market with @openonly @limit @offset
	public String getOrders(boolean openonly, String market, int limit, int offset) throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders(openonly, market, limit, offset);
		
		return result;
	}
	
	// Trade: Get orders on @market
	public String getOrders(String market) throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders(true, market, 1000, 0);
		
		return result;
	}
	
	// Trade: Get BTCCNY orders
	public String getOrders() throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders();
		
		return result;
	}
	
	// Trade: Get BTCCNY orders
	public String getOrdersBTC() throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders(true, "btccny", 1000, 0);
		
		return result;
	}
	
	// Trade: Get LTCCNY orders
	public String getOrdersLTC() throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders(true, "ltccny", 1000, 0);
		
		return result;
	}
	
	// Trade: Get LTCBTC orders
	public String getOrdersLtcBtc() throws Exception{
		GetOrders getOrders = new GetOrders(configFile);

		String result = getOrders.getOrders(true, "ltcbtc", 1000, 0);
		
		return result;
	}
	
	// Trade: Cancel order on @market
	public String cancelOrder(int order_id, String market) throws Exception{
		CancelOrder cancelOrder = new CancelOrder(configFile);

		String result = cancelOrder.cancelOrder(order_id, market);
		
		return result;
	}
	
	// Trade: Cancel BTCCNY order
	public String cancelOrder(int order_id) throws Exception{
		CancelOrder cancelOrder = new CancelOrder(configFile);

		String result = cancelOrder.cancelOrder(order_id);
		
		return result;
	}
	
	// Trade: Cancel BTCCNY orders
	public String cancelOrderBTC(int order_id) throws Exception{
		CancelOrder cancelOrder = new CancelOrder(configFile);

		String result = cancelOrder.cancelOrder(order_id);
		
		return result;
	}
	
	// Trade: Cancel LTCCNY order
	public String cancelOrderLTC(int order_id) throws Exception{
		CancelOrder cancelOrder = new CancelOrder(configFile);

		String result = cancelOrder.cancelOrder(order_id, "ltccny");
		
		return result;
	}
	
	// Trade: Cancel LTCBTC order
	public String cancelOrderLtcBtc(int order_id) throws Exception{
		CancelOrder cancelOrder = new CancelOrder(configFile);

		String result = cancelOrder.cancelOrder(order_id, "ltcbtc");
		
		return result;
	}
	
	// Trade: Get market depth @market
	public String getMarketDepth(String market) throws Exception{
		GetMarketDepth getDepth = new GetMarketDepth(configFile);

		String result = getDepth.getMarketDepth(10, market);
		
		return result;
	}
	
	// Trade: Get market depth @limit, @market
	public String getMarketDepth(int limit, String market) throws Exception{
		GetMarketDepth getDepth = new GetMarketDepth(configFile);

		String result = getDepth.getMarketDepth(limit, market);
		
		return result;
	}
	
	// Trade: Get market depth BTCCNY
	public String getMarketDepth() throws Exception{
		GetMarketDepth getDepth = new GetMarketDepth(configFile);

		String result = getDepth.getMarketDepth();
		
		return result;
	}
	
	// Trade: Get market depth BTCCNY
	public String getMarketDepthBTC() throws Exception{
		GetMarketDepth getDepth = new GetMarketDepth(configFile);

		String result = getDepth.getMarketDepth();
		
		return result;
	}
	
	// Trade: Get market depth LTCCNY
	public String getMarketDepthLTC() throws Exception{
		GetMarketDepth getDepth = new GetMarketDepth(configFile);

		String result = getDepth.getMarketDepth(10, "ltccny");
		
		return result;
	}
	
	// Trade: Get market depth LTCBTC
	public String getMarketDepthLtcBtc() throws Exception{
		GetMarketDepth getDepth = new GetMarketDepth(configFile);

		String result = getDepth.getMarketDepth(10, "ltcbtc");
		
		return result;
	}
	
	// Trade: Get transactions on @type,@limit,@offset,@since,@sincetype
	public String getTransactions(String type, int limit, int offset, int since, String sincetype) throws Exception{
		GetTransactions getTransactions = new GetTransactions(configFile);

		String result = getTransactions.getTransactions(type,limit,offset,since,sincetype);
		
		return result;
	}
	
	// Trade: Get transactions on @type
	public String getTransactions(String type) throws Exception{
		GetTransactions getTransactions = new GetTransactions(configFile);

		String result = getTransactions.getTransactions(type,10,0);
		
		return result;
	}
	
	// Trade: Get transactions on all default
	public String getTransactions() throws Exception{
		GetTransactions getTransactions = new GetTransactions(configFile);

		String result = getTransactions.getTransactions();
		
		return result;
	}
}
