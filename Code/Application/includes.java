   import java.util.*;
   import java.net.*;
   import java.io.*;
   //import biz.source_code.base64Coder.Base64Coder;

   abstract class DissCore {
      final int SERVER_PORT = 4000;
      final int SLAVE_PORT = 5555;
      final int MASTER_PORT = 6666;
      final int SERVER_RESPONSE_THRESHOLD = 500;
      DatagramSocket mailbox;
      AddressBook addyList;
      CharPack pack;
      boolean boardOn=false;
   
      DissCore() {
         addyList = new AddressBook();
         pack = new CharPack();
      
      }
      
      long getServerTime(int port){
         System.out.println("Requesting Server Time..");
         CommWrapper channel = new CommWrapper(addyList.getIpAddr("Server"),"4::null",SERVER_PORT);
         channel.setUpReceive(14);
         channel.sendReceiveRespond("4::null","ACK4");
         channel.closeConnection();
         
         String reply = channel.getReceivedData();
         String[] spl = reply.split("::");
      	//spl[1] = spl[1].substring(0,8);
         return Long.parseLong(spl[1],16);
      }
   
      long retrieveOffset(int port) {
         boolean done = false;
         long offset = 0;
         while(!done){
            long elapsed = System.currentTimeMillis();
            long time = getServerTime(port);
            elapsed = System.currentTimeMillis()-elapsed;
            if(elapsed < SERVER_RESPONSE_THRESHOLD){
               offset = (((elapsed/2)+time)-System.currentTimeMillis());
               done = true;
            }
         }
         return offset;
      }
   
      void sendCharPack(){
         CommWrapper submission = new CommWrapper(addyList.getIpAddr("Server"), "6::"+pack.getPack(), SERVER_PORT);
         submission.setUpReceive(4);     
         boolean done = false;
         while(!done) {      
            done = submission.sendTestAck("ACK6",true);
         }
         submission.closeConnection();    
      }
      
      void sendFirstCharPack(){
         CommWrapper submission = new CommWrapper(addyList.getIpAddr("Server"), "0::"+pack.getPack(), SERVER_PORT);
         submission.setUpReceive(4);
         boolean done = false;
         while(!done) {      
            done = submission.sendTestAck("ACK0",true);
         } 
         submission.closeConnection();
      }
   
   
   }

///String getIpAddr(String host)
///String getHostName(String ip)
   class AddressBook {
      
     
      class key {
         byte[] kc = new byte[44];		
      }
      private Scanner y1,y2,y3; 
      int NumDevices;
      String[] hosts = new String[10];
      String[] ips = new String[10];
      key[] keybook = new key[10];
      
      public AddressBook() {
        
      }
     
   //	 
      void openFiles(){
         try{
            y1 = new Scanner(new File("HostFile.txt"));
         }
            catch(Exception e){
               System.out.println("Could not open hostnames file");
            }
         try{
            y2 = new Scanner(new File("IpFile.txt"));
         }
            catch(Exception e){
               System.out.println("Could not open IP addresses file");
            }
         try{
            y3 = new Scanner(new File("KeyFile.txt"));
         }
            catch(Exception e){
               System.out.println("Could not open key file");
            }
      }
      
      void readFiles(){
         int i=0;
         while(y1.hasNextLine() && y2.hasNextLine()) {
            hosts[i] = y1.next();
            ips[i] = y2.next();
         	//keybook[i].kc = y3.next().getBytes();
         			
            i++;
         }
         NumDevices=i;
      		
      }
      
      void closeFiles(){
         y1.close();
         y2.close();
         y3.close();
      }
     
      boolean addInfo(String data) {
         String[] temp = data.split(":");
         if(getIndex(temp[1]) < 0) {
            hosts[NumDevices] = temp[0];
            ips[NumDevices] = temp[1];
            NumDevices++;
            return true;
         }
         else 
            return false;
      }
   
      int getIndex(String ip) {
         for(int i=0; i<NumDevices; i++) {
            if(ips[i].compareTo(ip) == 0) 
               return i;
         }
         return -1;
      }
   ///
   
   /// 
      void storeNewKey(String ip,byte[] key) {
         int x = getIndex(ip);
         keybook[x].kc = key;
      }
     
      byte[] keyByIp(String ip) {
         for(int i=0; i<NumDevices; i++) {
            if(ips[i].compareTo(ip) == 0) 
               return keybook[i].kc;
         }
         return null;
      }
     
      byte[] keyByName(String host) {
         for(int i=0; i<NumDevices; i++) {
            if(hosts[i].compareTo(host) == 0) 
               return keybook[i].kc;
         }
         return null;
      }
    /// 
     
   /// 
      String getIpAddr(String host){
         for(int i=0; i<NumDevices; i++) {
            if(hosts[i].compareTo(host) == 0) 
               return ips[i];
         }
         return null;
      }
      
      String getHostName(String ip){
         int x = getIndex(ip);
         if(x>=0)
            return hosts[x];
         else
            return null;
      }
    ///
    
   ///
      String[] getIpList(String[] hostList) {
         String[] ips = new String[hostList.length];
         for(int i=0;i<hostList.length;i++) {
            ips[i] = this.getIpAddr(hostList[i]);
         }
         return ips;
      }
      
      String[] getIpList(boolean serverIncluded) {
         String output = "";
         for(int i=0;i<NumDevices;i++) {
            if(serverIncluded&&i==0){
               output+=ips[i]+":";
            }
            else if(i==1) {
               output+=ips[i];
            }
            else if(i!=0)
               output+=":"+ips[i];
         }		
         return output.split(":");		
      }
   ///
     
   }

///Constructing auto loads old seeds
///Setting new seeds auto generates tables
///after both have been completed chars can be added
   class CharPack {
   //variables:
      
      long Offset;
      String Pack;
      int[] OldCipher = new int[94];
      int[] newCipher = new int[94];
      int[] OldDummy = new int[30];
      int[] newDummy = new int[30];
      int seedA, seedB, seedAp, seedBp;
   
      public CharPack() {
         Pack = "";
         getOldSeeds();
      //setNewSeeds( 5, 4);	
      
      }
   
      void getOldSeeds(){
         try{
            BufferedReader z = new BufferedReader(new FileReader("seeds.txt"));
            String temp;
            temp = z.readLine();
            seedA = Integer.valueOf(temp);
            temp = z.readLine();
            seedB = Integer.valueOf(temp);
            z.close();
         }
            catch (FileNotFoundException z) {
               System.out.println("Couldn't find File!");
            }
            catch(IOException z){
               System.out.println("Input output exception");
            }
      }
   
      void setNewSeeds(int A, int B){
         seedAp = A;
         seedBp = B;
         generateTables();
      }
   
      void storeNewSeeds() {
         try {
            PrintWriter w = new PrintWriter("storedNewSeedsFile.txt");
            w.println(seedAp);
            w.println(seedBp);
            w.close();
         }
            catch (FileNotFoundException w) {
               System.out.println("Couldn't find File!");
            }
      }		
   	
   //****For Fill up arrray****
   //Before:Seeds are set (A,B,Ap,Bp)
   //After: Tables are filled 
      void generateTables(){ 
         Random ran, ran2, nran, nran2;
      
         ran = new Random(seedA);
         ran2 = new Random(seedB);
         nran = new Random(seedAp);
         nran2 = new Random(seedBp);
         int x;
      		
         for(int i=0; i<94; i++){
         // fills old Cipher
            x=0;
         // while(x<8){
         // 			x=ran2.nextInt(32);
         // 		}
         
            OldCipher[i]=ran.nextInt();
         // fills new Cipher 
            x=0;
         // 		while(x<8){
         // 			x=nran2.nextInt(32);
         // 			}
            newCipher[i]=nran.nextInt();
         }
      
         for(int i=0; i<30; i++){
         
         // fills old Dummy
            x=0;
         //		while(x<8){
         //			x=ran2.nextInt(32);
         //		}
            OldDummy[i]=ran.nextInt();
         // fills new Dummy
            x=0;
         //		while(x<8){
         //			x=nran2.nextInt(32);
         //			}
            newDummy[i]=nran.nextInt();
         }
      }
   
   // For calculating time stamps
      void updateOffset(long num){
         Offset = num;
      }
   
   
   //only happens after tables/seeds set up
      void addChar(int index) {
         long time = System.currentTimeMillis() + Offset;
         if(Pack == ""){
            Pack = OldCipher[index] + ":" + time + ":" + newCipher[index];
         }
         else	{
            Pack += "%" + OldCipher[index] + ":" + time + ":" + newCipher[index];
         }
         if(index%3==0) {
            time++;
            Pack += "%" + OldDummy[index] + ":" + (time+1) + ":" + newDummy[index];
         }
      }
   
      String getPack(){	
         if(Pack != "")
            return Pack;
         else
            return "null";
      }
     
      void clearPack(){
         Pack = "";
      }
   }
