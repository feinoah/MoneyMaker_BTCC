package apiUtil;

public class ConfigProperty {
	String key;
	String value;
	
	public ConfigProperty(){
		this.key=null;
		this.value=null;
	}
	
	public ConfigProperty(String k, String v){
		this.key=k;
		this.value=v;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setKey(String k){
		this.key=k;
	}
	
	public void setValue(String v){
		this.value=v;
	} 
	
	public void setProperty(String k, String v){
		this.key=k;
		this.value=v;
	}
	
	public void printSelf(){
		System.out.println("Key: "+this.key+", Value: "+this.value);
	}	

}
