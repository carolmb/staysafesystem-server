import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Server {

	public class ACTION {
	    public final static int MSG = 0;
	    public final static int LOGIN = 1;
	    public final static int HIST = 2;
	    public final static int UPDATE = 3;
	};
	
	static private Server server;
	
	int serverPort = 5555;
	int clientPort = 5561;
	
	Map<String, Person> clients;
	
	private Server() {
		clients = new HashMap<>();
	}
	
	static public Server getInstance() {
		if(server == null) {
			server = new Server();
		}
		return server;
	}

    public static void main(String args[]){
    	Server.getInstance().waitClient();
    }
    
    public void waitClient() {
    	try {
            ServerSocket server = new ServerSocket(serverPort); // server port
            while(true) {
                Socket socket = server.accept();
                Scanner scanner = new Scanner(socket.getInputStream());
                String clientSolicitation = scanner.nextLine();
                System.out.println(clientSolicitation);
                scanner.close();
                String clientIp = socket.getInetAddress().getHostAddress();
                JSONObject json = new JSONObject(clientSolicitation);
                doAction(json, clientIp);
            }
        } catch (IOException e) {
            System.out.println("deu ruim");
        	e.printStackTrace();
        }
    }
    
    private void doAction(JSONObject json, String ip) { // client ip 
    	System.out.println(json.toString());
    	int action = json.getInt("action");
    	String phone = json.getString("phone");
    	String name = json.getString("name");
    	Person client = new Person(ip, phone, name);
    	switch (action) {
    		case ACTION.MSG: // msg
    			ArrayList<Person> friends = new ArrayList<>();
    			JSONArray jsonFriends = json.getJSONArray("friends");
    			for (int i = 0; i < jsonFriends.length(); i++) {
    				JSONObject jFriend = (JSONObject) jsonFriends.get(i);
    				String fPhone = jFriend.getString("phone");
    				if(clients.containsKey(fPhone)) {
    					Person p = clients.get(fPhone);
    					friends.add(p);
    				}
    			}	
    			String title = json.getString("title");
    			String subtitle = json.getString("subtitle");
    			String content = json.getString("content");
    		
    			Msg msg = new Msg(title, subtitle, content);
    			sendMsgs(msg, client, friends); 
    			break;
    		case ACTION.LOGIN: // login
    			addClient(client);
    			break;
    		case ACTION.HIST: // hist
    			getHistory(client);
    			break;
    		case ACTION.UPDATE: // update call
    			int callId = json.getInt("callID");
    			updateCall(callId);
    	}
    }
    
    private void sendMsgs(Msg msg, Person client, ArrayList<Person> friends) {
    	for (Person p : friends) {
    		sendSocket(p.ip, msg.toJson().toString(), clientPort);
    		saveCall(); // TODO save in list calls
    	}
    	sendSocket(client.ip, msg.toJson().toString(), clientPort);
    }
    
    private void addClient(Person person) {
    	// TODO verify if p is in client list, if it isn't add
    	String key = person.phoneNumber;
    	if(!clients.containsKey(key)) {
    		clients.put(key, person);
    	}
    	System.out.println("Adicionou cliente novo"); // debug
    }
    
    private void getHistory(Person client) {
    	// TODO read history and return all that client appeirs 
    }
    
    private void updateCall(int callId) {
    	// TODO update value and send new info to everybody
    }
    
    private void sendSocket(String ip, String content, int port) {
    	try {
            Socket soc = new Socket(ip, port);
            PrintWriter writer = new PrintWriter(soc.getOutputStream());
            writer.write(content);
            writer.flush();
            writer.close();
            soc.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void saveCall() {
    	
    }
}