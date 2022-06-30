//ServerGUI.java: Server GUI implementing multiple threading TCP socket
//Name: Yuelin Liu
//Student ID: 1128680
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerGUI extends JFrame implements ActionListener{
    public static int WIDTH = 400;
    public static int HEIGHT = 400;
    static String msg = null;
    static String error_msg = null;
    private static JTextArea result_on_screen;
    
    static ExecutorService threadPool = null;

    static int serverPort = 8888;//default port
	static String file = "dictionary.json";

    static int clientID = 0;
    static int clientPort = 0;
    static String clientHostname = null;

    //constructor for ServerGUI
    ServerGUI(){
        super("Dictionary Server Backend");
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set GirdLayout for the frame: two horizontal parts: System Output Panel and Buttons Panel
        setLayout(new GridLayout(2,1));

        JTextArea outputPanel = new JTextArea(20,10);
        JPanel buttonPanel  = new JPanel();
        buttonPanel.setBackground(Color.lightGray);

        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBackground(Color.lightGray);

        result_on_screen = new JTextArea(10,10);
        outputPanel.add(result_on_screen,BorderLayout.SOUTH);

        JLabel welcomeJLabel = new JLabel("Welcome to Online Dictionary Server Backend!");
        outputPanel.add(welcomeJLabel,BorderLayout.NORTH);
        add(outputPanel);

        JButton showButton = new JButton("Show Client Number");
        showButton.addActionListener(this);
        buttonPanel.add(showButton);
        JButton detailButton = new JButton("Show Client Details");
        detailButton.addActionListener(this);
        buttonPanel.add(detailButton);
        add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();
        if(actionCommand.equals("Show Client Number")){
            result_on_screen.setText(clientID + " clients have connected.");
            msg = "scn";// scn = show client number
        }
        if(actionCommand.equals("Show Client Details")){
            String str = "Client Remote Hostname: "+ clientHostname +" \n With remote port "+ clientPort+" just connected.";
            result_on_screen.setText(str);
        }
    }

    public static void start(DictServer dictServer){
        ServerSocket listeningSocket  = null;
        Socket clientSocket = null;
        threadPool = Executors.newFixedThreadPool(6);
        try {
            listeningSocket = new ServerSocket(serverPort);
            while(true){
                clientSocket = listeningSocket.accept();
                clientPort = clientSocket.getPort();
                clientHostname = clientSocket.getInetAddress().getHostName();
                clientID++;
                System.out.println("Server is listening on port: "+serverPort+ " for connections");
				System.out.println("Client connection accepted number:  " + clientID );
                System.out.println("Remote Hostname: " + clientHostname);
				System.out.println("Remote Port: " + clientPort);
				System.out.println("Local Port: " + clientSocket.getLocalPort());
				
	           MultiThreadServer runnableObj= new MultiThreadServer(clientSocket,clientID,file);
	           threadPool.execute(runnableObj);
            }

        }catch (SocketException e) {
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

    public static void main(String[] args) {
        //handle args
        if (args.length == 2){
			try {
			    serverPort = Integer.parseInt(args[0]);
				file = args[1];
				System.out.println("Input file name is: "+file);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
        ServerGUI myServerGUI = new ServerGUI();
        myServerGUI.setVisible(true);
        DictServer myDictServer = new DictServer();
        start(myDictServer);
    }
}