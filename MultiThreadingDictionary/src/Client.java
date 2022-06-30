import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void sendMessage(Socket socket) throws IOException{
		
			// Get the input/output streams for reading/writing data from/to the socket
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			Scanner scanner = new Scanner(System.in);
			String inputStr = null;

			//While the user input differs from "exit"
			while (!(inputStr = scanner.nextLine()).equals("exit")) {
				
				// Send the input string to the server by writing to the socket output stream
				if(!checkInput(inputStr)){
					System.out.println("Invalid Input!");
					continue;
				}
				out.write(inputStr + "\n");
				out.flush();
			
				// Receive the reply from the server by reading from the socket input stream
				String received = in.readLine(); 
				System.out.println(received);
			}
			scanner.close();
	}
	
	public static boolean checkInput(String inputString) {
		String[] input = inputString.split(",");
		boolean flag = true;
		if(input.length<2){
			flag = false;
		}
		else if(input.length == 2){
			if(input[0].equals("query")||input[0].equals("remove")){
				flag = true;
			}
			else{
				flag = false;
			}
		}
		else if(input.length ==3){
			if(input[0].equals("add")){
				flag = true;
			}
			else{
				flag = false;
			}
		}
		return flag;	
	}
	public static void main(String[] args) {
	
		Socket socket = null;
        // String server_address = "localhost";
		int server_port = 8888;
		String server_address = null;;
		if(args.length <2){
			System.out.println("Usage: Client <Host Name> <Port>");
			System.exit(1);
		}
		try {
			server_address = args[0];
			server_port = Integer.parseInt(args[1]);
			System.out.println("Server address is: "+server_address);
            System.out.println("Server host is: "+server_port);

			socket = new Socket(server_address, server_port);
			System.out.println("\nWelcome to Online Dictionary! You can query, add or remove words!");
            System.out.println("You should follow below instructions:\n>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("Query: query,word\nAdd: add,word,definition\nRemove: remove,word");
			System.out.println("For example: query,hello\n>>>>>>>>>>>>>>>>>>>>>>");
			sendMessage(socket);
			
			// catch error messages and print error messages.
			} catch (UnknownHostException e) {
				//e.printStackTrace();
				System.out.println("ERROR: HOST EXCEPTION! ");
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("ERROR: I/O EXCEPTION! ");
			} finally {
				// Close the socket
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						//e.printStackTrace();
						System.out.println("ERROR: I/O EXCEPTION! ");
					}
				}
			}
	}

}
