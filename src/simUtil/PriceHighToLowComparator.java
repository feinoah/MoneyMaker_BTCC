package simUtil;

import java.util.Comparator;

public class PriceHighToLowComparator implements Comparator<orderData> { 
	 
    @Override 
    public int compare(orderData orderData1, orderData orderData2) { 
         
        double price1 = orderData1.getPrice(); 
        double price2 = orderData2.getPrice(); 
         
        if(price1 > price2){ 
            return -1; 
        } 
        else if(price1 < price2){ 
            return 1; 
        } 
        else{ 
            return 0; 
        } 
    } 
 
} 