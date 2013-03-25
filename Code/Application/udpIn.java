   import java.net.*;
   import java.io.*;   
   
	//in main: Listen
   public class udpIn extends Thread{ 
      public void run(){ 
         while(true){
            DatagramSocket serverSocket = null;            
            try {
               serverSocket = new DatagramSocket(4000);
            } 
               catch (IOException e) {
                  System.out.println("Could not listen on port: 4000" + e);
                  System.exit(-1);
               } 
            byte[] inData = new byte[1400];
            int inPacketLength; 
            String inputLine;
            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            try {
               serverSocket.receive(inPacket);
               new Thread(new packetHandler(inPacket)).start(); 
    				//System.out.println("message recived.");        
				}
               catch (IOException e){
                  System.out.println("Error: Could not recieve packet in udpIn.");
               }
            		    
            serverSocket.close();
         }
      } 
   } 

















