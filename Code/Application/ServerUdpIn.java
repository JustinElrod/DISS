   import java.net.*;
   import java.io.*;   
   
	//in main: Listen
   public class ServerUdpIn extends Thread{ 
      String type; //server or slave
	  	  
	  public ServerUdpIn(String type){
		this.type = type;
		}
	  
	  public void run(){ 
         while(true){
            DatagramSocket dSocket = null;            
            try {
               dSocket = new DatagramSocket(4000);
            } 
               catch (IOException e) {
                  System.out.println("Could not listen on port: 4000" + e);
                  System.exit(-1);
               } 
            byte[] inData = new byte[1400];
            int inPacketLength; 
            String inputLine;
            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            if(type == "server"){
			try {
               dSocket.receive(inPacket);
               new Thread(new serverPacketHandler(inPacket)).start(); 
    				//System.out.println("message recived.");        
				}
               catch (IOException e){
                  System.out.println("Error: Could not recieve packet in udpIn.");
               }
            }
			else if(type == "slave"){
			try {
               dSocket.receive(inPacket);
               new Thread(new slavePacketHandler(inPacket)).start(); 
    				//System.out.println("message recived.");        
				}
               catch (IOException e){
                  System.out.println("Error: Could not recieve packet in udpIn.");
               }
            }
            dSocket.close();
         }
      } 
   } 

















