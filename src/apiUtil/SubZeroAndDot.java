package apiUtil;

public class SubZeroAndDot {
	public static String removeZero(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//remove redundant 0  
            s = s.replaceAll("[.]$", "");//if the last is ., remove  
        }  
        return s;  
	}
}
