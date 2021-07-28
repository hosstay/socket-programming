package netNode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;


public class NetHardware extends Thread {

    IOException ioException;
    UnknownHostException unknownHostException;
    static final int port = 8080;
    String ip;
    InputStream in = null;
    OutputStream out = null;
    Socket sendSocket = null;
    Socket listenSocket = null;
    ServerSocket serverSocket = null;

    public NetHardware(String ip) {
        this.ip = ip;
        try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("IOException creating serverSocket");
			e.printStackTrace();
		}
    }
	
    public void run() {

        //create socket
        try {
        	System.out.println("creating sockets...");
            //open socket for sending requests to server
        	sendSocket = new Socket(ip, port);
        	in = sendSocket.getInputStream();
            out = sendSocket.getOutputStream();
        	
            //open socket for listening for requests from the server
        	listenSocket = serverSocket.accept();
        	
        	new NetHardwareRequestThread(listenSocket).start();
        	
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

    public void dump(){
    	//create request string
        String request = "0 " + "DumpDatabase~";
        
		try {
			out.flush();
			out.write(request.getBytes());
			out.flush();
	        
	        // send request through socket
	        System.out.println("sending request through socket...");
	        System.out.println("string sent: " + request);
	    	
	    	System.out.println("sending databaseToSend.txt to server...");
	
	        //get file from external storage
	    	URL url = getClass().getResource("databaseToSend.txt");
            File file = new File(url.getPath());
	
	        //byte array with size of the file 
	        byte[] bytes = new byte[(int) file.length()];
	
	        //read in from the file
	        try{
	        	BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(file));
	        	
	        	bIn.read(bytes, 0, bytes.length);
	        	
		        //output on socket
	        	out.flush();
	        	out.write(bytes, 0, bytes.length);
	        	out.flush();
	        	out.write("~".getBytes());
	        	out.flush();
		
		        bIn.close();
	        	
	        } catch (FileNotFoundException e)
	        {
	        	e.printStackTrace();
	        	System.out.println("FileNotFoundException in dump()");
	        }
	        
	        System.out.println("Dumped Database");
        
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException in dump()");
		}
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

    public void close(){
    	if (sendSocket != null) {
            try {
            	System.out.println("closing socket");
                sendSocket.close();  
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }else if (listenSocket != null){
        	try {
            	System.out.println("closing socket");
                listenSocket.close();
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }else if (serverSocket != null){
        	try {
            	System.out.println("closing socket");
                listenSocket.close();
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    }

    private class NetHardwareRequestThread extends Thread {

        private Socket socket;
        StringBuilder sb = new StringBuilder();
        String[][] fakeDatabase;
        InputStream in = null;
        OutputStream out = null;

        NetHardwareRequestThread(Socket socket) {
        	System.out.println("NetHardwareRequestThread constructor...");
            this.socket = socket;
            
            try {
				this.in = socket.getInputStream();
				this.out = socket.getOutputStream();
			} catch (IOException e) {
				System.out.println("IOException in NetHardwareRequestThread constructor...");
				e.printStackTrace();
			}
            
            
            //fake database that i'll be pulling from. Will need to edit code to interface with real database API
            fakeDatabase = new String[][]{
	                {"Bucket of bolts", "10lb"},
	                {"Box of Nails", "5lb"},
	                {"Cup of Screws", "2lb"}
            };
        }

        @Override
        public void run() {
            try {
            	//while the socket is alive
            	while(socket != null)
            	{
	            	/**First we're getting input from the client to see what it wants. **/
	                int byteRead = 0;
	
	                // Read from input stream. Note: inputStream.read() will block
	                // if no data return
	                //reset stringbuilder buffer
	                sb.setLength(0);
	                
	                System.out.println("attempting to read in from socket...");
	                
	                while (byteRead != -1) {
	                    byteRead = in.read();
	                    if (byteRead == 126){
	                        byteRead = -1;
	                    }else {
	                        sb.append((char) byteRead);
	                    }
	                }
	
	                //split the front and back of the string into item ID and purpose
	                String[] request = new String[2];
	                request[0] = "";
	                request[1] = "";
	                request = sb.toString().split(" ");
	
	                //check for errors in user input
	                System.out.println("request[0] is currently: " + request[0]);
	                System.out.println("request[1] is currently: " + request[1]);
	                if(Integer.parseInt(request[0]) > fakeDatabase.length){
	                    request[0] = "";
	                    request[1] = "";
	
	                    // send response
	                    System.out.println("outputting response to socket...");
	                    out.flush();
	                    out.write(("Number exceeds entries in database(" + fakeDatabase.length + ").~").getBytes());
	                    out.flush();
	
	                    System.out.println("User entered number too large for database.");
	
	                }
	                
	                System.out.println("request[0] is currently: " + request[0]);
	                System.out.println("request[1] is currently: " + request[1]);
	
	                /** then checking and responding **/
	                // compare lexigraphically since bytes will be different
	                if(request[1].compareTo("ReqDatabase") == 0){
	                	reqDatabase(out);
	                }
            	}

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in SocketServerRequestThread"
                        + e.toString());
            } finally {
                if (socket != null) {
                    try {
                    	System.out.println("closing socket...");
                        socket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("IOException in SocketServerRequestThread"
                                + e.toString());
                    }
                }
            }
        }

        private void reqDatabase(OutputStream out){
        	try{
        		System.out.println("outputting response to socket...");
        
    	        //get file from external storage
        		URL url = getClass().getResource("databaseToSend.txt");
                File file = new File(url.getPath());
    	
    	        byte[] bytes = new byte[(int) file.length()];
    	        BufferedInputStream bIn;
    	
    	        //read in from the file
    	        bIn = new BufferedInputStream(new FileInputStream(file));
    	        bIn.read(bytes, 0, bytes.length);
    	
    	        //output on socket
    	        out.flush();
    	        out.write(bytes, 0, bytes.length);
    	        out.flush();
    	        out.write("~".getBytes());
    	        out.flush();
    	
    	        bIn.close();
    	        System.out.println("Requested Database");
        	} catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in reqDatabase");
        	}
        }
        
    }
}