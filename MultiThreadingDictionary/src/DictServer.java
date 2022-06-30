/**
 * Name: Yuelin Liu
 * Student ID: 1128680
 * DictServer.java
 * Dictionary Server achieving multiple threading socket server.
 * It uses the Work-Pool method, default pool size is 6
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DictServer{

    ExecutorService threadPool = null;
    public static void main(String[] args) {

		int port = 8888;
		String file = null;;

		if(args.length <2){
			System.out.println("Usage: Multi-threaded Server <Port> <File Name>");
			System.exit(1);
		}
		try {
			port = Integer.parseInt(args[0]);
			file = args[1];
			System.out.println("Input file name is: "+file);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        DictServer myServer = new DictServer();
        myServer.start(port,file);
    }

    public void start(int port, String file){
		
		ServerSocket listeningSocket = null;
		Socket clientSocket = null;
		// creates 6 worker pool threads.
		threadPool = Executors.newFixedThreadPool(4);
		
		
		try {
			//Create a server socket listening on port 8888
			listeningSocket = new ServerSocket(port);
			int i = 0; //counter for clients numbers
			
			while (true) {
				System.out.println("Server is listening on port: "+port+ " for connections");
				clientSocket = listeningSocket.accept();
				i++;
				System.out.println("Client connection accepted number:  " + i );
                System.out.println("Remote Hostname: " + clientSocket.getInetAddress().getHostName());
				System.out.println("Remote Port: " + clientSocket.getPort());
				System.out.println("Local Port: " + clientSocket.getLocalPort());
				
	        	MultiThreadServer runnableObj= new MultiThreadServer(clientSocket,i,file);
	            threadPool.execute(runnableObj);
			}
		// handle exceptions here
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}catch (IOException e) {
			System.out.println(e.getMessage());
		} 
		finally {
			if(listeningSocket != null) {
				try {
					listeningSocket.close();
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("ERROR: I/O EXCEPTION");
				}
			}
		}
	}
}