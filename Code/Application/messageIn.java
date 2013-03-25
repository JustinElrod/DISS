   public class messageIn{
   
      private int function;
      private String sourceName;
      private String sourceIP;
      private String data;
      private int sourcePort;
   
      public messageIn(int funct, String name, String IP, String data){
         this.function = funct;
         this.sourceName = name;
         this.sourceIP = IP;
         this.data = data;
      }
   
      public messageIn(int funct, String IP, String data, int sourcePort){
         this.function = funct;
         this.sourceIP = IP;
         this.data = data;
         this.sourcePort = sourcePort;
      } 	
   	
      public int getFunction(){
         return function;
      }
   
      public String getHostName(){
         return sourceName;
      }
   
      public String getIP(){
         return sourceIP;
      }
   
      public String getData(){
         return data;
      }
   	
      public int getPort(){
         return sourcePort;
      }
   }