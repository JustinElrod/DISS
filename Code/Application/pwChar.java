//CLASS pwChar:
   public class pwChar{
      private String currentChar;
      private String nextChar;
      private double timestamp;
   
      public pwChar(String current, double time, String next){
         this.currentChar = current;
         this.timestamp = time;
         this.nextChar = next;
      }
   
      public String getCurrentChar(){
         return currentChar;
      }
   
      public String getNextChar(){
         return nextChar;
      }
   
      public double getTimestamp(){
         return timestamp;
      }
   }