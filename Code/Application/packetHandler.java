   import java.io.*;
   import java.net.*;

   public class packetHandler implements Runnable{
   
      private DatagramPacket p;
   
      public packetHandler(DatagramPacket p){
         this.p = p;
      }
   
      public void run(){
         String message = new String(p.getData(), 0, p.getLength());
   		InetAddress sentFrom = p.getAddress();
         String from = sentFrom.toString().substring(1);
         String[] messageParts = message.split("::");
         int f = Integer.parseInt(messageParts[0]);
   		System.out.println("\n------IN-----\nMessage from " + from + " port " + p.getPort()
				+ ":\n" + message + "\n------IN-----\n");      
			String d = messageParts[1];
         messageIn m = new messageIn(f, from, d, p.getPort());
         dissServer.doFunction(f, m);
      }
   }
