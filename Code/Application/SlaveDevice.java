   import java.util.*;
   import java.net.*;
   import java.io.*;
   //import biz.source_code.base64Coder.Base64Coder;

///constructor waits for hello
class SlaveDevice extends DissCore{
   	
	public static void main(String[] args) {
		 SlaveDevice dev = new SlaveDevice();
	}
	
	SlaveDevice() {
		super();
		addyList.openFiles();
		addyList.readFiles();
		addyList.closeFiles();
		sayHello();
		try {
			Thread.sleep(2000L);
		}catch(InterruptedException ie) {
			System.out.println(ie);
		}
		// negotiateKey(true);
// 		getDeviceListing();

		boardOn = getKeyPrep();
		System.out.println("This will be where keyboards open");
		pack.addChar(0);
		pack.addChar(1);
		pack.addChar(2);
		pack.addChar(3);
		pack.addChar(4);
		boardOn = waitForSubmit();
		System.out.println("This will be where keyboards close");
		
	}
   
	void sayHello() {
		CommWrapper channel = new CommWrapper(3,SLAVE_PORT);
		channel.receiveRespond("1::null","ACK1",false);
		channel.closeConnection();
		System.out.println("Acknowledged Hello");
	}
   
	void negotiateKey(boolean masterDevice) {
			//receive/ACK8 seed
			CommWrapper channel = new CommWrapper(14,SLAVE_PORT);
			channel.receiveRespond("8::","ACK8",false);
			channel.closeConnection();
			String temp = channel.getReceivedData();
			String[] a = temp.split("::");
			byte[] theirSeed = a[1].getBytes();
			
			//generate mySeed
			Random b = new Random(System.currentTimeMillis());
			long seed = b.nextLong();
			byte[] mySeed = Long.toString(seed).getBytes();
			String hexSeed = Long.toString(seed,16);
			
			//send/Ack9 mySeed
			if(masterDevice) {
				addyList.addInfo("master:"+channel.getReceivedIP());
				channel = new CommWrapper(channel.getReceivedIP(),"9::"+hexSeed,MASTER_PORT);
			}
			else
				channel = new CommWrapper(channel.getReceivedIP(),"9::"+hexSeed,SERVER_PORT);
				
			channel.setUpReceive(4);
			while(!channel.sendTestAck("ACK9",true));
			channel.closeConnection();
			
			
			//xor seeds
			for(int j=0;j<8;j++) {
				theirSeed[j] = (byte)(theirSeed[j] ^ mySeed[j]);
			}
			
			addyList.storeNewKey(channel.getReceivedIP(),theirSeed);
			
			
	}
  
    void getDeviceListing() {
		///receive from master 
		CommWrapper channel = new CommWrapper(100,SLAVE_PORT);
		channel.receiveRespond("2::","ACK2",false);
		channel.closeConnection();
		
		///store info in addy book
		String temp = channel.getReceivedData();
		String[] temp2 = temp.split("::");
		String[] devList = temp2[1].split("%");
		for(int i=0;i<devList.length;i++) {
			addyList.addInfo(devList[i]);
		}
	}
	
	void negotiateOtherKeys() {
		String[] iplist = addyList.getIpList(false);
		//for each device
		///sendtestack 
		
		//for server
		///sendReceiveRespond
		
	
	}
	
	private class SeedHandler extends Thread {
		private boolean throwing;
		private String ip, data;
		private int port;
		private long seed;
		
		SeedHandler(boolean catching, int port, long seed) {
			throwing = !catching;
			this.port = port;
			this.seed = seed;
		}
		
		SeedHandler(boolean catching, int port, String ip, String data, long seed) {
			throwing = !catching;
			this.port = port;
			this.ip = ip;
			this.data = data;
			this.seed = seed;
		}
		
		public void run() {
			if(throwing) {
				CommWrapper channel = new CommWrapper(ip,data,port);
				while(!channel.sendTestAck("ACK8",true));
				channel.closeConnection();
			}
			else {
				byte[] theirSeed;
				CommWrapper channel = new CommWrapper(14,port);
				channel.receiveRespond("8::","ACK8",false);
				channel.closeConnection();
				String[] temp = new String[2];
				temp[0] = channel.getReceivedData();
				temp = temp[0].split("::");
				theirSeed = temp[1].getBytes();				
				byte[] mySeed = Long.toString(seed).getBytes();
				//xor seeds
				for(int j=0;j<mySeed.length;j++) {
					theirSeed[j] = (byte)(theirSeed[j] ^ mySeed[j]);
				}
				
				
				addyList.storeNewKey(channel.getReceivedIP(),theirSeed);
			}
		
		}
	}
	
   ///will wait for seeds, then synch time
    boolean getKeyPrep() {
         int a,b;
         System.out.println("Starting KeyPrep");
         CommWrapper channel = new CommWrapper(20,SLAVE_PORT);
         channel.receiveRespond("3::","ACK3",false);
		 channel.closeConnection();
         String[] res = channel.getReceivedData().split("::");
       
         res = res[1].split(":");
         System.out.println("Seeds Received: " + res[0] + " " + res[1]);
         a = Integer.parseInt(res[0].trim(),16);
         b = Integer.parseInt(res[1].trim(),16);
         pack.setNewSeeds(a,b);
         pack.updateOffset(retrieveOffset(SLAVE_PORT));
		System.out.println("Got Server Time");
         return true;
      }
   
    boolean waitForSubmit() {
         CommWrapper channel = new CommWrapper(7,SLAVE_PORT);
		 	channel.receiveRespond("5::","ACK5",false);
		 	channel.closeConnection();
         sendFirstCharPack();
         return false;
      }
  
	
}