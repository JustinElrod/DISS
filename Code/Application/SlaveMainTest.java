   import java.net.*;
   import java.io.*;
   
   public class SlaveMainTest{
      public static void main(String[] args){
         new SlaveUdpIn("slave").start();
      }
   } 