package netNode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import Test.TestServer;


public class NetServer extends Thread {

    IOException ioException;
    UnknownHostException unknownHostException;
    Socket socket = null;
    InputStream in = null;
    OutputStream out = null;

    public NetServer(Socket s) {
        socket = s;
        
        try {
        	System.out.println("getting input and output streams for NetServer...");
            in = socket.getInputStream();
            out = socket.getOutputStream();

        } catch (UnknownHostException e) {
            this.unknownHostException = e;
            System.out.println("UnknownHostException in socket creation");
            return;
        } catch (IOException e) {
            this.ioException = e;
            System.out.println("IOException in socket creation");
            return;
        }
    }
	
    public void run() {
    	TestServer testServer = new TestServer();
    	testServer.test(this);
    	System.out.println("NetServer ready for function calls...");
    }
    
    public void request(){
    	//create request string
        String request = "0 " + "ReqDatabase~";
        
        try {
        	out.flush();
	        out.write(request.getBytes());
	        out.flush();
	        
	        // send request through socket
	        System.out.println("sending request through socket...");
	        System.out.println("string sent: " + request);
	        
	        //open file
	        URL url = getClass().getResource("databaseToReceive.txt");
            File file = new File(url.getPath());
	        //will need to increase size of byte array if information exceeds 1024 bytes
	        byte[] bytes = new byte[1024];
	        BufferedOutputStream bOut = new BufferedOutputStream(new FileOutputStream(file));
	
	        //read in from the socket input stream and write to file output stream
	        int bytesRead = in.read(bytes, 0, bytes.length);
	        bOut.write(bytes, 0, bytesRead);
	        
	        bOut.close();
	        
	        System.out.println("Database received in databaseToReceive.txt");
        } catch (IOException e){
			e.printStackTrace();
			System.out.println("IOException in request()");
        }
    }

    public int checkIfSocketClosed(int choice){
    	if(socket == null){
    		System.out.println("Client closed socket...ending communication.");
    		return 3;
    	}
		return choice;
    }
}

