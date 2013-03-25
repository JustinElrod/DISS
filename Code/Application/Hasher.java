   import java.io.File;
   import java.io.FileWriter;
   import java.io.FileInputStream;
   import java.io.FileOutputStream;
   import java.io.IOException;
   import java.io.InputStream;
   import java.security.MessageDigest;
   import java.util.*;

   public class Hasher{
   
      public static String hash(String input){
      
         String output = null;
      	
         try {
         
            MessageDigest hash = MessageDigest.getInstance("SHA-512");
            hash.update(input.getBytes());
            byte[] digest = hash.digest();
         
            output = Base64Coder.encodeLines(digest, 0, digest.length, 76, "");
         } 
            
            catch (java.security.NoSuchAlgorithmException e) {
               System.out.println("No Such AlgorithmException: " + e);
               System.exit(-1);
            }
            
         return output;
      }
   
   
      public static String getHash(String filename){
      
         String oldHash = "";
         Scanner scan;
      	
         try
         {
            scan = new Scanner(new File(filename));
            while (scan.hasNext())
            {
               oldHash += scan.next();
            }
            scan.close();
         }
         
         catch (IOException e)
            {
               System.out.println("IO exception!");
               System.exit(-1);
            } 
      
         return oldHash;
      }
      
   	
      public static void saveHash(String filename, String hashData){
         
         try
         {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(hashData.getBytes());
            fos.close();
         }
            catch (IOException e)
            {
               System.out.println("IOException: " + e);
            }
      }
   		
   }