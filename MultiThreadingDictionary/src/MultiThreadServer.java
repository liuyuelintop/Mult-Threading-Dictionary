//MultiThreadServer.java: file to handle query, add and remove operations.
//Name: Yuelin Liu
//Student ID: 1128680
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.*;

public class MultiThreadServer implements Runnable {

    // DictServer server=null;
    Socket client=null;
    int id;
    String file = "dictionary.json";
    
    MultiThreadServer (Socket client, int id, String filename) throws IOException {
        
        this.client=client;
        this.id=id;
        file = filename;
        System.out.println("Connection "+id+" established with client "+client+"\n");
        
    }
 	/**
 	 * run method processes I/O with respect to three instructions.
 	 */	 
	@Override
	public void run() {
		try {
			String clientMsg = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
			String command, word, def;
            
				while((clientMsg = in.readLine()) != null) {

					String[] input = clientMsg.split(",");
                    System.out.println("User input："+ clientMsg);
                    command = input[0];
                    word = input[1];
                    switch(command){
                        case "query":
                            out.write(query(word));
                            out.flush();
                            break;
                        case "add":
                            def = input[2];
                            out.write(add(word+","+def));
                            out.flush();
                            break;
                        case "remove":
                            out.write(remove(word));
                            out.flush();;
                            break;
                    }
				}
			}
			catch(SocketException e) {
				System.out.println("ERROR: SOCKET EXCEPTION. CLOSED.... ");
			}
			catch (IOException e) {
                System.out.println("ERROR! I/O Exception ! ");
			}try{
					client.close();
				} catch (IOException e) {
					System.out.println("ERROR: I/O Exception ! ");
				}
			}


// query the meaning from the dictionary
    public String query(String message){
        JSONParser myParser = new JSONParser();
        String result = null;
        try{
            Object myObject = myParser.parse(new FileReader(file));
            JSONObject myJsonObject = (JSONObject) myObject;
            
            // check the meaning from dictionary
            if(message != null){
                
                String meaning = (String) myJsonObject.get(message);
                System.out.println("Word to query: " + message);
            
                if (meaning == null)
                    meaning = "No such word in dictionary, please try again!";
                result = meaning + "\n";
            }
        
        }  catch (FileNotFoundException e) {
        // e.printStackTrace();
            System.out.println("ERROR: FILE NOT FOUND!");
        } catch (IOException e) {
        // e.printStackTrace();
            System.out.println("ERROR: I/O EXCEPTION!");
        } catch (ParseException e) {
        // e.printStackTrace();
            System.out.println("ERROR: PARSE EXCEPTION!");
        }
        
        return result;
    }

    // add message to dictionary
    @SuppressWarnings("unchecked")
    public synchronized String add(String message){
        JSONParser myParser = new JSONParser();
        String result = null;
        try{
            Object myObject = myParser.parse(new FileReader(file));
            JSONObject myJsonObject = (JSONObject) myObject;
            String[] input = message.split(",");
            String word = input[0];
            String def, meaning;
            meaning = (String) myJsonObject.get(word);
            
            // check the meaning from dictionary
            if(meaning == null){
                def = input[1];
                myJsonObject.put(word,def);
                System.out.println(myJsonObject);
                
                @SuppressWarnings("resource")
                FileWriter fw = new FileWriter(file, false);
                try {
                    fw.write(myJsonObject.toJSONString());
                    fw.flush();
                } catch (IOException e) {
                // e.printStackTrace();
                    System.out.println("ERROR: I/O EXCEPTION!");
                }

            System.out.println("Word to add: " +  word);
            result =  word +" has been updated to the dictionary successfully!\n";
            }
            else if( word != null){
                System.out.println("Word already exists! ");
                result = "Sorry, add operation failed: the word is already in the dictionary!\n";
            }
            else 
                result = "ERROR!";
        } catch (FileNotFoundException e) {
        // e.printStackTrace();
            System.out.println("ERROR: FILE NOT FOUND");
        } catch (IOException e) {
        // e.printStackTrace();
            System.out.println("ERROR: I/O EXCEPTION");
        } catch (ParseException e) {
        // e.printStackTrace();
            System.out.println("ERROR: PARSE EXCEPTION");
        }
        return result;
    }


    // remove a word from the dictionary
    // add message to dictionary
    // to add a word like hello, format is " add,hello,Used as a greeting or to begin a telephone conversation. "
    public synchronized String remove(String message){
        JSONParser myParser = new JSONParser();
        String result = null;
        try{
        Object myObject = myParser.parse(new FileReader(file));
        JSONObject myJsonObject = (JSONObject) myObject;
        
        // check the meaning from dictionary
        String meaning = (String) myJsonObject.get(message);

        if(meaning != null){
        
            myJsonObject.remove(message);
            System.out.println(myJsonObject);
            
            @SuppressWarnings("resource")
            FileWriter fw = new FileWriter(file, false);
            try {
                fw.write(myJsonObject.toJSONString());
                fw.flush();
            } catch (IOException e) {
                System.out.println("ERROR: I/O EXCEPTION");
            }

            System.out.println("Deletes" + message +" from the dictionary successfully!\nThe current dictionary is:\n");
            System.out.println(myJsonObject);
            result = "Deletes word successfully!\n";
        }  
        else if(meaning == null){
            System.out.println("Delete operation failed: No such word in the dictionary!");
            result = "ERROR: No such word in the dictionary!\n";
        }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: FILE NOT FOUND");
        } catch (IOException e) {
            System.out.println("ERROR: I/O EXCEPTION");
        } catch (ParseException e) {
            System.out.println("ERROR: PARSE EXCEPTION");
        }
        return result;
    }
}
