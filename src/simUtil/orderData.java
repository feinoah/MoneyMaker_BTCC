package simUtil;

public class orderData {

    int id;
    double price;
    double amount;

    public orderData() {  // constructor 
 	   id=0;
 	   price=0.00;
 	   amount=0.00;
    }
    
    public orderData(int i, double p, double a) {  // constructor 
  	   this.id=i;
  	   this.price=p;
  	   this.amount=a;
     }
    
    public void setValues(int i, double p, double a) {  // constructor 
  	   this.id=i;
  	   this.price=p;
  	   this.amount=a;
     }

   public void setID(int i) {
       this.id = i;
   }

   public int getID() {
       return id;
   }
   
   public void setPrice(double p) {
       this.price = p;
   }

   public double getPrice( ) {
       return price;
   }
   
   public void setAmount(double a) {
       this.amount = a;
   }

   public double getAmount( ) {
       return amount;
   }

   public void printSelf(){
	   System.out.println("ID: "+this.id+", Price: "+this.price+", Amount: "+this.amount);
   }
   
}
