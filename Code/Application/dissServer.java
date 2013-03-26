   import java.util.*;
  
   public class dissServer{
      private static ArrayList<String> charPacks = new ArrayList<String>();
      private static ArrayList<connectedDevice> deviceList = new ArrayList<connectedDevice>();
   
      public static synchronized void doFunction(int function, messageIn m){
      
         switch(function){
         	
         	//setup password
            case 0:
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK6");
               System.out.println("\nSetting up Password.");
               for(int i = 0; i < deviceList.size(); i++){
                  if(m.getIP().compareTo(deviceList.get(i).getIP()) == 0){
                     if(deviceList.get(i).charsAreIn() == true)
                        break;
                     else{
                        charPacks.add(m.getData());
                        deviceList.get(i).setCharsIn(true);
                     }
                  }
               }
               
               if(charPacks.size() != deviceList.size())
                  return;
               else{
                  pwValidation.savePW(charPacks);
                  for(int i = 0; i < deviceList.size(); i++){
                     deviceList.get(i).setCharsIn(false);
                  }
                  charPacks.clear();
               }
               System.out.println("Password saved!");
               System.out.println("Saved hash:");
               System.out.println(Hasher.getHash("hashFile.txt"));  
            	
               return;
            
            //get device list
            case 2:
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK2");  
               
               if(deviceList.size() == 0){
                  deviceList.add(new connectedDevice("<Master>", m.getIP()));
                 // deviceList.add(new connectedDevice("Dev1", "127.0.0.1"));
               }
               String data = m.getData();
               String[] devices = data.split("%");
               if(deviceList.size() - 1 < devices.length){
                  for(int i = 1; i < devices.length; i++){
                     String[] params = devices[i].split(":");
                     deviceList.add(new connectedDevice(params[0], params[1]));
                  }
                 
               }
            	
               System.out.println("\nDevice List added: ");
               for(int i = 0; i < deviceList.size(); i++){
                  System.out.println("Hostname: " + deviceList.get(i).getName()
                     + "\tIP Address: " + deviceList.get(i).getIP());
               }	
               return;
            
         //sendTimes
            case 4:
            	
               //System.out.println("\nsending time");
            	
               String theTime = Long.toString(System.currentTimeMillis(),16);
               
               //System.out.println("time: " + theTime);
               
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "4::" + theTime);
               return;
            
            //validate pw   
            case 6: 
            
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK6");
            	
               boolean validPW = false;  
            	
               for(int i = 0; i < deviceList.size(); i++){
                  if(m.getIP().compareTo(deviceList.get(i).getIP())==0){
                     if(deviceList.get(i).charsAreIn())
                        break;
                     else{
                        charPacks.add(m.getData());
                        deviceList.get(i).setCharsIn(true);
                     }
                  }
               }
               
               if(charPacks.size() != deviceList.size())
                  return;
               else{
                  validPW = pwValidation.validatePW(charPacks);
                  for(int i = 0; i < deviceList.size(); i++){
                     deviceList.get(i).setCharsIn(false);
                  }
                  charPacks.clear();
               }
            	
            	//SEND result to Master
               String result;
               if(validPW){
                  result = "7::1";
                  System.out.println("PASSWORD VALIDATED!!!!");
               }
               else{
                  result = "7::0";
                  System.out.println("...password is wrong.");
               }
            	
               CommWrapper reply = new CommWrapper(deviceList.get(0).getIP(), result, 6666);
               boolean acked = false;
               while(!reply.sendTestAck("ACK7",true));
               reply.closeConnection();
            
               return;
            
         //negotiate keys
         /*
         8
         8
         8		separate to recieve seeds first, then get new function to send seeds
         8
         8
         */
            case 8:
               System.out.println("\nNegotiating seed with master.\n");
            	
               //for(int i = 0; i <= 4; i++){
               //   try{
               //      Thread.sleep(500);
               //   }
               //      catch(InterruptedException e){
               //      }
                  CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK8");
               //}
               	
            //generate mySeed
               Random b = new Random(System.currentTimeMillis());
               long seed = b.nextLong();
               byte[] mySeed = Long.toString(seed).getBytes();
               String hexSeed = Long.toString(seed,16);
               
               System.out.println("\nMy Seed:  " + mySeed + "\nHexSeed:  " + hexSeed);
            	
            	
            //send/Ack9 mySeed	
            	
               CommWrapper channel = new CommWrapper(m.getIP(),"9::"+hexSeed, 6666);
               while(!channel.sendTestAck("ACK9", true));
               channel.closeConnection();
            
               long longSeed = Long.parseLong(m.getData(),16);
               byte[] theirSeed = Long.toString(longSeed).getBytes();
            //xor seeds
               for(int j=0;j<8;j++) {
                  theirSeed[j] = (byte)(theirSeed[j] ^ mySeed[j]);
               }
            
               //deviceList.get(0).setSeed(theirSeed);
            
            default:
               System.out.println("Error: unknown function.");
         			
         }
      
      }
   
   }