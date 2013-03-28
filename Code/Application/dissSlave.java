   import java.util.*;
   import java.net.*;
   import java.io.*;

   public class dissSlave{
      private static ArrayList<connectedDevice> deviceList = new ArrayList<connectedDevice>();
      private static CharPack pack = new CharPack();
   
      private static int SERVER_PORT = 4000;
      private static final int SLAVE_PORT = 5555;
      private static int MASTER_PORT = 6666;
      private static int SERVER_RESPONSE_THRESHOLD = 500;	
   	
      public static void addChararcter(int index){
         pack.addChar(index);
      }
   
      public static synchronized void doFunction(int function, messageIn m){
      
         switch(function){
         
         //setup password
            case 1:
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK1");
               deviceList.add(new connectedDevice("<Master>", m.getIP()));
               System.out.println("Acknowledged Hello");
            
            //get device list
            case 2:
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK2");
               String hostAddress = "";
               try
               {
                  InetAddress localHost = InetAddress.getLocalHost();	
                  hostAddress = localHost.getHostAddress();
                  System.out.println("My IP: " + hostAddress);
               }
                  catch (UnknownHostException e)
                  {
                     System.out.println("ERROR Determining IP address");
                  }
            
               if(deviceList.size() == 0){
                  deviceList.add(new connectedDevice("<Master>", m.getIP()));
               }
               String data = m.getData();
               String[] devices = data.split("%");
               if(deviceList.size() - 1 < devices.length){
                  for(int i = 0; i < devices.length; i++){
                     String[] params = devices[i].split(":");
                     if(params[1].compareTo(hostAddress) != 0)
                        deviceList.add(new connectedDevice(params[0], params[1]));
                  }
               }
            	
               System.out.println("\nDevice List added: ");
               for(int i = 0; i < deviceList.size(); i++){
                  System.out.println("Hostname: " + deviceList.get(i).getName()
                     + "\tIP Address: " + deviceList.get(i).getIP());
               }	
               return;
            
            //password prep
            case 3:
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK3");
            
               System.out.println("Starting Password Prep");
               CommWrapper channel = new CommWrapper(20,SLAVE_PORT);
               channel.receiveRespond("3::","ACK3",false);
               channel.closeConnection();
               String[] res = channel.getReceivedData().split("::");
            
               res = res[1].split(":");
               System.out.println("Seeds Received: " + res[0] + " " + res[1]);
               int a = Integer.parseInt(res[0].trim(),16);
               int b = Integer.parseInt(res[1].trim(),16);
               pack.setNewSeeds(a,b);
               
            	
            //get time from server and set offset
               boolean done = false;
               long offset = 0;
               long time = 0;
               while(!done){
                  long elapsed = System.currentTimeMillis();
                  
                  channel = new CommWrapper(deviceList.get(1).getIP(),"4::null",SERVER_PORT);
                  channel.setUpReceive(14);
                  channel.sendReceiveRespond("4::null","ACK4");
                  channel.closeConnection();
               
                  String reply = channel.getReceivedData();
                  String[] spl = reply.split("::");
               //spl[1] = spl[1].substring(0,8);
                  time = Long.parseLong(spl[1],16);
                  elapsed = System.currentTimeMillis()-elapsed;
                  if(elapsed < SERVER_RESPONSE_THRESHOLD){
                     offset = (((elapsed/2)+time)-System.currentTimeMillis());
                     done = true;
                  }
               }
            
               pack.updateOffset(offset);
               System.out.println("Got Server Time");
               
            	//OPEN KEYBOARD FUNCTION GOES HERE
            	
               return;
            
         //send charPack
            case 5:
               CommWrapper.sendBasic(m.getIP(), m.getPort(), "ACK2");
               System.out.println("Sending charPack to server.");
               CommWrapper submission = new CommWrapper(deviceList.get(1).getIP(), "6::"+pack.getPack(), SERVER_PORT);
               submission.setUpReceive(4);     
               done = false;
               while(!done) {      
                  done = submission.sendTestAck("ACK6",true);
               }
               submission.closeConnection();
              
               return; 
            	
            default:
               System.out.println("Error: unknown function.");         
         }
      }
   }