   import java.io.File;
   import java.io.FileWriter;
   import java.io.FileInputStream;
   import java.io.FileOutputStream;
   import java.io.IOException;
   import java.io.InputStream;
   import java.security.MessageDigest;   
   import java.util.*;

/**
* Class used by server to validate and update password
*/
   public class pwValidation{
   
      private static String hashFile = "hashFile.txt";
      private static ArrayList<pwChar> pwCharList = new ArrayList<pwChar>();
   	
   	
   	
      public static boolean validatePW(ArrayList<String> charInList){
      	
         String pwInput = "";
         String storedPW = "";
         String nextPW = "";
      	
         pwValidation.parseCharPacks(charInList);
         pwValidation.sortPWCharList();
      	
         for(int i = 0; i < pwCharList.size(); i++){
            pwInput += pwCharList.get(i).getCurrentChar();
            nextPW += pwCharList.get(i).getNextChar();
         }
      	
         pwCharList.clear();
      	
         if(Hasher.hash(pwInput).equals(Hasher.getHash(hashFile))){
            Hasher.saveHash(hashFile, Hasher.hash(nextPW));
            return true;
         }
         else 
            return false;	
      }
   
      public static void savePW(ArrayList<String> charInList){
         String pwInput = "";
         String nextPW = "";
      	
         pwValidation.parseCharPacks(charInList);
         pwValidation.sortPWCharList();
      	
         for(int i = 0; i < pwCharList.size(); i++){
            pwInput += pwCharList.get(i).getCurrentChar();
            nextPW += pwCharList.get(i).getNextChar();
         }
      	
         pwCharList.clear();      	
         Hasher.saveHash(hashFile, Hasher.hash(nextPW));
      }
   	
   //Generates ArrayList of pwChar objects
   //CharPack syntax: currentChar:timestamp:nextChar%currentChar2:...
      private static void parseCharPacks(ArrayList<String> charInList){
         
         String charPacks = charInList.get(0);			
         for(int i = 1; i < charInList.size(); i++){
            charPacks += ("%" + charInList.get(i));
         }			
      	         		
         String[] pwChars = charPacks.split("%");
      	
         for(int i = 0; i < pwChars.length; i++){
         	if(pwChars[i].compareTo("null") !=0) {
	            String[] temp = pwChars[i].split(":");
	            pwCharList.add(new pwChar(temp[0], Double.parseDouble(temp[1]), temp[2]));
            }
         }
      }
      
   	
      private static void sortPWCharList(){
      	
         Collections.sort(pwCharList, 
               new Comparator<pwChar>(){
                  public int compare(pwChar char1, pwChar char2){
                     return Double.compare(char1.getTimestamp(), char2.getTimestamp());
                  }
               });
      }
   	
   }

