package Test;

import netNode.StartServer;

public class TestStartServer {
	   public static void main(String[] args) {
		   
		   StartServer server = new StartServer();
	       System.out.println("The server has started.");
	       System.out.println(server.getIpAddress() + ":" + server.getPort());
	   }
}
