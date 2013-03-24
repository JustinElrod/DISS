package com.example.distributediss;

   import java.io.*;
   import java.util.*;
   import java.net.*;

class CommWrapper {
      private final int TIMEOUT = 2000;
      private DatagramSocket box;
      private DatagramPacket outgoing,incoming;
   
    CommWrapper() {
         try {
            box = new DatagramSocket(0);
            box.setSoTimeout(TIMEOUT);
         }  
            catch(SocketException se) {
               System.out.println("CommWrapper: " + se);
            }
         incoming = null;
         outgoing = null;
      }
      
    CommWrapper(int sourcePort) {
         try {
            box = new DatagramSocket(sourcePort);
            box.setSoTimeout(TIMEOUT);
         }  
            catch(SocketException se) {
               System.out.println("CommWrapper: " + se);
            }
         incoming = null;
         outgoing = null;
      }
   
	///for sending first(designated port)
    CommWrapper(String ip, String data, int destPort, int sourcePort) {
         this(sourcePort);
         setUpSend(ip,data,destPort);
      }
   
	///for sending first(random port)
    CommWrapper(String ip, String data, int destPort) {
         this(ip,data,destPort,0);
      }
   
	///for receiving first
    CommWrapper(int expectedLength, int sourcePort) {
         this(sourcePort);
         setUpReceive(expectedLength);
      }
   
	
	//both packets must be set up
    boolean sendTestAck(String ack, boolean timeout) {
         this.send();
         this.receive(timeout);
         if(Arrays.equals(incoming.getData(),ack.getBytes()))
            return true;
         else
            return false;
      }
   
    void receiveRespond(String msgR, String msgS, boolean strict) {
		if(strict) {
			while(this.getReceivedData().compareTo(msgR) != 0)
				this.receive(false);
		}
		else {
			while((getReceivedData().charAt(0))!=(msgR.charAt(0)))
				this.receive(false);
		}
		//System.out.println(channel.getReceivedData());
		this.setUpSend(msgS);
		this.send();
	}
	
	void sendReceiveRespond(String msgR, String msgS) {
		while((getReceivedData().charAt(0)) != (msgR.charAt(0))) {
			this.send();
			this.receive(true);
		}
		this.setUpSend(msgS);
		this.send();
	}
	
	static void sendAll(String[] ipList, String data, String ack) {
         int numHosts = ipList.length;
         CommWrapper[] channel = new CommWrapper[numHosts];
         boolean[] done = new boolean[ipList.length];
      
      //set up and try to send each packet
         for(int i=0; i<numHosts; i++) {
            channel[i] = new CommWrapper(ipList[i],data,5555);
            channel[i].setUpReceive(4);
            done[i] = channel[i].sendTestAck(ack,true);
         }
         
       //re-send until all have been acknowledged
         boolean more = true;
         while(more) {
            more = false;
            for(int i=0;i<numHosts;i++) {
               if(!done[i])
                  done[i] = channel[i].sendTestAck(ack,true);
               if(!done[i])
                  more = true;
            } 
         }
         
       //close the sockets
         for(int i=0; i<numHosts; i++) 
            channel[i].closeConnection();
      }
   
	static void sendAllConnected(String[] ipList, String data, String ack,boolean[] connected) {
         int numHosts = ipList.length;
         CommWrapper[] channel = new CommWrapper[numHosts];
         boolean[] done = new boolean[numHosts];
      
      //set up and try to send each packet
         for(int i=0; i<numHosts; i++) {
				done[i] = false;
				if(i==0) {
					channel[i] = new CommWrapper(ipList[i],data,4000);
					channel[i].setUpReceive(4);
					done[i] = channel[i].sendTestAck(ack,true);
				}
				else if(connected[i]) {
					channel[i] = new CommWrapper(ipList[i],data,5555);
					channel[i].setUpReceive(4);
					done[i] = channel[i].sendTestAck(ack,false);
				}
         }
         
       //re-send until all have been acknowledged
         boolean more = true;
         while(more) {
            more = false;
            for(int i=0;i<numHosts;i++) {
               if(!done[i] && connected[i])
                  done[i] = channel[i].sendTestAck(ack,true);
               if(!done[i] && connected[i])
                  more = true;
            } 
         }
         
       //close the sockets
         for(int i=0; i<numHosts; i++) {
     			if(connected[i])      
			   	channel[i].closeConnection();
  			}    
		}
   
	
	void setUpSend(String ip, String data, int destPort) {      		
         byte[] buffer = data.getBytes();
         try {
            InetAddress destIP = InetAddress.getByName(ip);
            outgoing = new DatagramPacket(buffer, buffer.length, destIP, destPort);
         }
            catch(UnknownHostException uhe) {
               System.out.println("CommWrapper: " + uhe);
            }
      
      
      }
	
	//if a packet has been received
	void setUpSend(String data) {
		setUpSend(incoming.getAddress().toString().substring(1),data,incoming.getPort());
		outgoing.setData(data.getBytes());
	}
     
	void setUpReceive(int expectedLength) {
         byte[] buff = new byte[expectedLength];
         incoming = new DatagramPacket(buff, buff.length);
    }
   
    void closeConnection() {
         box.disconnect();
         box.close();
         while(!box.isBound());
      }
   
    void send(){
         try {
            box.send(outgoing);
         }
            catch(IOException e) {
               System.out.println("Couldnt Send: "+ e);
            }
            
      }
   
    void receive(boolean timeout) {
         try {
			if(!timeout)
				box.setSoTimeout(0);
			while(Arrays.equals(incoming.getData(),new byte[incoming.getLength()]))
				box.receive(incoming);
			box.setSoTimeout(TIMEOUT);
		 }
            catch(IOException e) {
               System.out.println("Couldnt Recieve: "+ e);
            }
      }
   
    String getReceivedIP() {
		return incoming.getAddress().toString().substring(1);
	}
	
	String getReceivedData() {
         return new String(incoming.getData());
      }
   
}