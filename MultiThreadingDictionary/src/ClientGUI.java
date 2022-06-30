//ClientGUI.java: Simple GUI for client using online dictionary.
//Name: Yuelin Liu
//Student ID: 1128680
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI extends JFrame implements ActionListener{

    public static int WIDTH = 600;
    public static int HEIGHT = 500;
    private static JTextArea userInput, result_on_screen;
    static String inputStr = null;
    static String msg = null;
    static String error_msg = null;

    //constructors
    ClientGUI(){
        super("Online Dictionary");
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /**
        *Set GirdLayout for the frame: three horizontal parts: User input panel,
        *System Output Panel and Buttons Panel
        */
        setLayout(new GridLayout(3,1));

        //User Input Panel
        JTextArea inputPanel = new JTextArea(10,10);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(new Color(102,178,255));

        JLabel welcomeJLabel = new JLabel("Thanks for using Online Dictionary");
        JLabel instructionLabel1 = new JLabel("Please follow the instruction below:  Query:word; Add:word,definition; Remove:word");
        inputPanel.add(welcomeJLabel,BorderLayout.NORTH);
        inputPanel.add(instructionLabel1,BorderLayout.CENTER);

        userInput = new JTextArea(5,10);
        userInput.setBackground(new Color(224,224,224));
        inputPanel.add(userInput,BorderLayout.SOUTH);

        add(inputPanel);
        
        //Output Panel
        JTextArea outputPanel = new JTextArea(10,10);
        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBackground(new Color(102,178,255));
        JLabel serverOutputLabel = new JLabel("Information get from the server");
        outputPanel.add(serverOutputLabel,BorderLayout.NORTH);
        result_on_screen = new JTextArea(8,10);
        result_on_screen.setBackground(new Color(224,224,224));
        outputPanel.add(result_on_screen,BorderLayout.SOUTH);
        add(outputPanel);

       // Buttons Panel:  for Query, Add and Remove operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(102,178,255));
        JButton queryBtn = new JButton("Query");
        queryBtn.addActionListener(this);
        buttonPanel.add(queryBtn);
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(this);
        buttonPanel.add(addBtn);
        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(this);
        buttonPanel.add(removeBtn);
        add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();
        if(actionCommand.equals("Query"))
            msg = "query";
        if(actionCommand.equals("Add"))
            msg = "add";
        if(actionCommand.equals("Remove"))
            msg = "remove";
    }
    
    static void start(Socket socket) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));

        String ClientToServer = null;
        while (userInput.getText()!= null){
				
            if(msg == null){
                System.out.println("Connected, waiting for operation");
            }
            if (msg == "query"){
                
                inputStr = userInput.getText();	
                ClientToServer= "query"+","+inputStr;
                if(!checkInput(ClientToServer)){
                    result_on_screen.setText("Invalid Input! Please Try again!");
					continue;
				}
                out.write(ClientToServer + "\n");
                out.flush();
                result_on_screen.setText(in.readLine());
                msg = null;
            }
            if (msg == "add"){
                
                inputStr = userInput.getText();
                ClientToServer= "add"+","+inputStr;
                if(!checkInput(ClientToServer)){
					result_on_screen.setText("Invalid Input! Please Try again!");
					continue;
				}
                out.write(ClientToServer + "\n");
                out.flush();
                result_on_screen.setText(in.readLine());
                msg = null;
            }
            
            if (msg == "remove"){
                inputStr = userInput.getText();	
                ClientToServer= "remove"+","+inputStr;
                if(!checkInput(ClientToServer)){
					result_on_screen.setText("Invalid Input! Please Try again!");
					continue;
				}
                out.write(ClientToServer + "\n");
                out.flush();
                result_on_screen.setText(in.readLine());
                msg = null;
            }
        }	

    }
    public static boolean checkInput(String inputString) {
		String[] input = inputString.split(",");
		boolean flag = true;
		if(input.length<2){
			flag = false;
		}
		else if(input.length == 2){
			if(input[0].equals("add")){
				flag = false;
			}
		}
		return flag;	
	}
    public static void main(String[] args) {
        ClientGUI myClientGUI = new ClientGUI();
        myClientGUI.setVisible(true);
        //handle args here
        Socket socket = null;
        String server_address = "localhost";//default server address
        int server_port = 8888;//default server port
        if (args.length == 2){
			try {
                server_address = args[0];
				server_port = Integer.parseInt(args[1]);
				System.out.println("Server address is: "+server_address);
                System.out.println("Server host is: "+server_port);
			} catch (Exception e) {
				System.out.println(e.getMessage());
		    }
		}
        try {
            socket = new Socket(server_address, server_port);
			System.out.println("Welcome to Online Dictionary! You can query, add or remove word!");
            System.out.println("You should follow below instructions:");
            System.out.println("Query: query,word\nAdd: add,word,definition\nRemove: remove,word");
            result_on_screen.setText("Connect to the server successfully!");
			start(socket);
			System.out.println("Connect to the server successfully!");
            
			
        }catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            result_on_screen.setText(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            // Close the socket
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}