   import java.util.*;
   import java.net.*;
   import java.io.*;
   //import biz.source_code.base64Coder.Base64Coder;

   class MasterDevice extends DissCore {
      String[] chosenHostList = {"Dev2"}; ////Get from GUI
      boolean[] connected;

      static boolean good=false,
					 verified=false;
   
      public static void main(String[] args) {
         MasterDevice a = new MasterDevice();
			System.out.println("Greeting Devices:");
	        if(a.sayHello()>=1) {
					System.out.println("---Finished Setting Up---");
					good = true;
					try{
						Thread.sleep(5000L);
					}catch(Exception e) {}
				//	System.out.println("Getting Keys");
				// getKeys();
	// 				System.out.println("Sharing Device List");
					a.distributeIPs();
					a.prepServer(a.chosenHostList);
					a.keyPrep();
					try{
						Thread.sleep(5000L);
					}catch(Exception e) {}
					a.submitPacks();
					a.getServerOK();
			  }
         
         
      }
   
   ///Auto SayHello on construction
      MasterDevice() {
        super();
			addyList.openFiles();
	      addyList.readFiles();
			addyList.closeFiles();	  
	  }
   
   //Tries to say hello to everyone except server(1st in addyBook).
   //updates connected as they come in
   //gives up after 4 tries each
      int sayHello() {
         final int numRetry = 3;
         String[] list = (addyList.getIpList(true));
         CommWrapper[] channel = new CommWrapper[list.length];
         for(int i=1; i<list.length; i++) {
            channel[i] = new CommWrapper();
         }
         connected = new boolean[list.length];
		 	connected[0] = true;
         try {
            for(int i=1; i<list.length; i++) {
               channel[i].setUpSend(list[i], "1::" + null, SLAVE_PORT);
               channel[i].setUpReceive(4);
               connected[i] = channel[i].sendTestAck("ACK1",true);
            }
         }
            catch(NullPointerException e) {
               System.out.println(e.getMessage());
               e.printStackTrace();
            }
         for(int j=0; j<numRetry;j++) {
            for(int i=1; i<list.length; i++) {
               if(!connected[i])
                  connected[i] = channel[i].sendTestAck("ACK1",true);
            }
         }
         int temp = 0;
         
         for(int i=1; i<channel.length; i++) {
            if(connected[i]) {
               temp++;
            }
            channel[i].closeConnection();
         }
         System.out.println(temp + " Connections Found");
         return temp;
      
      }
   
   ///all devices hello'd + server
	void getKeys() {
			//generate seed
			Random a = new Random(System.currentTimeMillis());
			long seed = a.nextLong();
			byte[] mySeed = Long.toString(seed).getBytes();
			
			//send seed to all connected ips
			String[] temp = addyList.getIpList(true);
			CommWrapper.sendAllConnected(temp,"8::"+Long.toString(seed,16),"ACK8",connected);
			
			System.out.println("Seeds SENT");
			//receive seeds one at a time
			for(int i=0;i<temp.length;i++) {
				CommWrapper seedCatch = new CommWrapper(14,MASTER_PORT);
				seedCatch.receiveRespond("9::null","ACK9",false);
				seedCatch.closeConnection();
				////
				String data = seedCatch.getReceivedData();
				String[] te = data.split("::");
				seed = Long.parseLong(te[1],16);
				byte[] theirSeed = Long.toString(seed).getBytes();
				//xor seeds
				for(int j=0;j<8;j++) {
					theirSeed[j] = (byte)(theirSeed[j] ^ mySeed[j]);
				}
				addyList.storeNewKey(seedCatch.getReceivedIP(),theirSeed);
				
			}
	   }
   
   ///all devices hello'd + server
	void distributeIPs() {
		//get connected device info
		String devList = "";
		String[] iplist = addyList.getIpList(true);
		for(int i=0;i<connected.length;i++) {
			if(connected[i]) {				
				if(devList.compareTo("")==0)
					devList= addyList.getHostName(iplist[i]) + ":" + iplist[i];
				else
					devList+= "%" + addyList.getHostName(iplist[i]) + ":" + iplist[i];
			}
		}
		
		//send to all connected
		CommWrapper.sendAllConnected(iplist,"2::"+devList,"ACK2",connected);
	}
   
   
   
   ///setUp("host1:host2:host3")
   ///stores to "chosenHostList"
      void setUpHosts(String chosenHosts) {
         chosenHostList = chosenHosts.split(":");
         System.out.println("Sending HostList to Server..");
		 prepServer(chosenHostList);
      }
   	
	///updates servers device list
      void prepServer(String[] hostList) {
         String[] ips = addyList.getIpList(hostList);
         String output = "";
         for(int i=0;i<hostList.length;i++) {
            if(output=="")
               output += hostList[i] + ":" + ips[i];
            else
               output += "%" + hostList[i] + ":" + ips[i];
         }
        CommWrapper channel = new CommWrapper(addyList.getIpAddr("Server"),"2::"+output,SERVER_PORT);
        channel.setUpReceive(4);
		while(!channel.sendTestAck("ACK2",true)){}
		channel.closeConnection();
		System.out.println("Device list sent to Server");
      	
      }
   
   ///generates, sets, and spreads two random seeds (A' and B')
      void keyPrep() {
         String[] ips = addyList.getIpList(chosenHostList);
      
         int a,b;
         Random ran = new Random(System.currentTimeMillis());
         a = ran.nextInt();
         b = ran.nextInt();
         pack.setNewSeeds(a,b);
         pack.updateOffset(retrieveOffset(MASTER_PORT));
      
         System.out.println("Sending seeds to slave devices..");
         String seedPack = Integer.toString(a,16) + ":" + Integer.toString(b,16);
         CommWrapper.sendAll(ips,"3::"+seedPack,"ACK3");
         System.out.println("Seeds have been spread");
         boardOn = true;
      }
   
   ///sends submit to slave devices
   ///sends own pack
   ///waits for response
      void submitPacks(){
         boardOn = false;
         String[] ips = addyList.getIpList(chosenHostList);
        
         System.out.println("Telling devices to send Pass characters to Server..");
      
         CommWrapper.sendAll(ips,"5::null","ACK5");
         try{
				Thread.sleep(2000L);
   		}catch(Exception e) {}      
			System.out.println("Sending my pass characters to Server..");
      
         sendFirstCharPack();
         
         System.out.println("Waiting for response..");
         verified = getServerOK();
         if(verified)
            System.out.println("We were authorized! :D");
         else
            System.out.println("We were not authorized! D:");
      
      }
   	
      boolean getServerOK(){
         CommWrapper channel = new CommWrapper(4,MASTER_PORT);
         channel.receiveRespond("7::null","ACK7",false);
		 channel.closeConnection();
		 
		 String ret = channel.getReceivedData();
         String[] temp = ret.split("::");
         if(temp[1].compareTo("1") == 0)
            return true;
         else
            return false;
      }
  
   }