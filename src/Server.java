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
	    public final static int GUARDIANS = 4;
	    public final static int PROTECTED = 5;
	    
	};
	
	static private Server server;
	private StorageManager<Map<String, Person>> clients;
	private StorageManager<ArrayList<Pair<Person, Integer>>> history; // guardian and call
	private StorageManager<ArrayList<Call>> calls; // all calls
		
	int serverPort = 5555;
	int clientPort = 5561;
	
	Map<String, Person> clientsCache;
	ArrayList<Pair<Person, Integer>> historyCache;
	ArrayList<Call> callsCache;
	
	private Server() {
		clients = new StorageManager<Map<String, Person>>("clients");
		clientsCache = clients.retrive();
		if(clientsCache == null) {
			clientsCache = new HashMap<String, Person>();
		}
		printClients();
		
		history = new StorageManager<ArrayList<Pair<Person, Integer>>>("history");
		historyCache = history.retrive();
		if(historyCache == null) {
			historyCache = new ArrayList<Pair<Person, Integer>>();
		}
		
		calls = new StorageManager<ArrayList<Call>>("call");
		callsCache = calls.retrive();
		if(callsCache == null) {
			callsCache = new ArrayList<Call>();
		}
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
                clients.save(clientsCache);
                history.save(historyCache);
                calls.save(callsCache);
            }
        } catch (IOException e) {
            System.out.println("deu ruim");
        	e.printStackTrace();
        }
    }
    
    public ArrayList<Person> JSONtoArray(JSONObject json) {
    	ArrayList<Person> friends = new ArrayList<>();
		JSONArray jsonFriends = json.getJSONArray("friends");
		for (int i = 0; i < jsonFriends.length(); i++) {
			JSONObject jFriend = (JSONObject) jsonFriends.get(i);
			String fPhone = jFriend.getString("phone");
			System.out.println(fPhone);
			if(clientsCache.containsKey(fPhone)) {
				Person p = clientsCache.get(fPhone);
				friends.add(p);
			}
		}	
		return friends;
    }
    
    private void doAction(JSONObject json, String ip) { // client ip 
    	System.out.println(json.toString());
    	int action = json.getInt("action");
    	String phone = json.getString("phone");
    	String name = json.getString("name");
    	Person client = new Person(ip, phone, name);
    	ArrayList<Person> friends;
    	switch (action) {
    		case ACTION.MSG: // msg
    			friends = JSONtoArray(json);
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
    			sendHistory(client);
    			break;
    		case ACTION.UPDATE: // update call
    			int callId = json.getInt("callID");
    			boolean status = json.getBoolean("status");
    			updateCall(client, callId, status);
    			break;
    		case ACTION.GUARDIANS: // save guardians of protected user
    			friends = JSONtoArray(json);
    			setFriends(client, friends);
    			break;
    		case ACTION.PROTECTED:
    			sendProtectedFriends(client);
    			
    	}
    }
    
    private void sendProtectedFriends(Person client) {
    	ArrayList<Person> protectedFriends = new ArrayList<Person>();
    	for (Map.Entry<String, Person> person : clientsCache.entrySet()) {
    		if(person.getValue().getFriends().contains(client)) {
    			protectedFriends.add(person.getValue());
    		}
    	}
    	JSONArray array = new JSONArray();
    	for(Person person: protectedFriends) {
    		JSONObject jsonPerson = person.toJson();
    		array.put(jsonPerson);
    	}
    	JSONObject jsonMsg = new JSONObject();
    	jsonMsg.put("action", ACTION.PROTECTED);
    	jsonMsg.put("prot", array);
    	sendSocket(client.ip, jsonMsg.toString(), clientPort);
    }
    
    private void setFriends(Person client, ArrayList<Person> friends) {
    	Person clientInList = clientsCache.get(client.phoneNumber);
    	clientInList.setFriends(friends);
    }
    
    private void sendMsgs(Msg msg, Person client, ArrayList<Person> friends) {
    	for (Person p : friends) {
    		System.out.println("Mensagem enviada para cliente " + p.toString());
    		sendSocket(p.ip, msg.toJson().toString(), clientPort);
    		int id = saveCall(msg, client); // TODO save in list calls
    		saveHistory(p, id);
    	}
    	//System.out.println(msg.toJson().toString());
    	sendSocket(client.ip, msg.toJson().toString(), clientPort);
    }
    
    private void addClient(Person person) {
    	// TODO verify if p is in client list, if it isn't add
    	String key = person.phoneNumber;
    	if(!clientsCache.containsKey(key)) {
    		clientsCache.put(key, person);
    	}
    	System.out.println("Adicionou cliente novo"); // debug
    }
    
    private void sendHistory(Person client) {
    	ArrayList<Call> historyCalls = new ArrayList<Call>();
    	for(Pair<Person, Integer> pair: historyCache) {
    		if(pair.getElement0() == client) {
    			int id = pair.getElement1();
    			Call call = callsCache.get(id);
    			historyCalls.add(call);
    		}
    	}
    	
    	for(Call call: callsCache) {
    		if(call.getFrom() == client) {
    			historyCalls.add(call);
    		}
    	}
    	
    	// TODO different histories to guardian view and protected user view
    	JSONArray array = new JSONArray();
    	for(Call call: historyCalls) {
    		JSONObject jsonCall = call.toJson();
    		array.put(jsonCall);
    	}
    	
    	JSONObject jsonMsg = new JSONObject();
    	jsonMsg.put("action", ACTION.HIST);
    	jsonMsg.put("hist", array);
    	sendSocket(client.ip, jsonMsg.toString(), clientPort);
    }
    
    private void updateCall(Person client, int callId, boolean status) {
    	// TODO update value and send new info to everybody
    	Call call = callsCache.get(callId);
    	call.setStatus(status);
    	JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("action", ACTION.UPDATE);
    	for(Pair<Person, Integer> pair: historyCache) {
    		if(pair.getElement1() == callId && pair.getElement0() != client) {
    			sendSocket(pair.getElement0().ip, jsonMsg.toString(), clientPort);
    		}
    	}
    	Person from = call.getFrom();
    	if(from != client) {
    		sendSocket(from.ip, jsonMsg.toString(), clientPort);
    	}
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
    
    private int saveCall(Msg msg, Person from) {
    	Call call = new Call(msg, from);
    	int id = callsCache.size();
    	callsCache.add(call);
    	return id;
    }
    
    private void saveHistory(Person guardian, int id) {
    	Pair<Person, Integer> pair = Pair.createPair(guardian, id);
    	historyCache.add(pair);
    }
    
    private void printClients() {
    	for(Person p : clientsCache.values()) {
    		System.out.println(p.toString());
    	}
    }
}