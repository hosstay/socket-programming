package Test;

import java.util.Scanner;

import netNode.NetServer;

public class TestServer {
    public void test(NetServer netServer) {
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
       
       /* choose network function */
       while(choice != 2){
    	   //check if client has closed socket or not
    	   choice = netServer.checkIfSocketClosed(choice);
    	   
    	   //prompt user for input
	       System.out.println("(1) Request new item, (2) End server communication: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netServer.request();
	       }
	       
	       
       }
       scanner.close();
       return;
    }		
}
   