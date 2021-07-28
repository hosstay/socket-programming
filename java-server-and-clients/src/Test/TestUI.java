package Test;

import java.util.Scanner;

import netNode.NetUI;

public class TestUI {
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
       
       //initialize socket in new thread
       NetUI netUI = new NetUI(ip);
	   netUI.start();
       
       /* choose network function */
       while(choice != 3){
	       System.out.println("(1) Mat DB Dump, (2) Request new item, (3) Exit: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netUI.dump();
	       }else if(choice == 2){
	    	   netUI.request();
	       }else if(choice == 3){
	    	   netUI.close();
	       }
       }
       scanner.close();
   }		
}
   