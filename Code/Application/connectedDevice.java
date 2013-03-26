   public class connectedDevice{
      private String hostName;
      private String IP;
      private boolean charsIn;
      private int port = 4000;
      private byte[] seed;
   
      public connectedDevice(String from, String IP){
         this.hostName = from;
         this.IP = IP;
         charsIn = false;
      }
   
      public connectedDevice(String from, String IP, int port){
         this.hostName = from;
         this.IP = IP;
         this.port = port;
         charsIn = false;    
      }
   
      public String getName(){
         return hostName;
      }
   
      public String getIP(){
         return IP;
      }
   
      public int getPort(){
         return port;
      }
   
      public boolean charsAreIn(){
         return charsIn;
      }
   
      public void setCharsIn(boolean b){
         charsIn = b;
      }
     
      public void setSeed(byte[] seed){
         this.seed = seed;
      }
   	
      public byte[] getSeed(){
         return seed;
      }
   }
