import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;

public class Server {

	static private Server server;
	
	int port = 5560;
	
	private Server() {}
	
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
            ServerSocket server = new ServerSocket(5555);
            while(true) {
                Socket socket = server.accept();
                Scanner scanner = new Scanner(socket.getInputStream());
                String clientSolicitation = scanner.nextLine();
                scanner.close();
                JSONObject json = new JSONObject(clientSolicitation);
                doAction(json);
            }
        } catch (IOException e) {
            System.out.println("deu ruim");
        	e.printStackTrace();
        }
    }
    
    private void doAction(JSONObject json) {
    	int action = json.getInt("action");
    	String ip = json.getString("ip");
    	String phone = json.getString("phone");
    	String name = json.getString("name");
    	Person client = new Person(ip, phone, name);
    	switch (action) {
    		case 0: // msg
    			ArrayList<Person> friends = new ArrayList<>();
    			String title = json.getString("title");
    			String subtitle = json.getString("subtitle");
    			String content = json.getString("content");
    		
    			Msg msg = new Msg(title, subtitle, content);
    			sendMsgs(msg, client, friends); 
    			break;
    		case 1: // login
    			addClient(client);
    			break;
    		case 2: // hist
    			getHistory(client);
    			break;
    		case 3: // update call
    			int callId = json.getInt("callID");
    			updateCall(callId);
    	}
    }
    
    private void sendMsgs(Msg msg, Person client, ArrayList<Person> friends) {
    	for (Person p : friends) {
    		sendSocket(p.ip, msg.toJson().toString(), port);
    		saveCall(); // TODO save in list calls
    	}
    }
    
    private void addClient(Person p) {
    	// TODO verify if p is in client list, if it isn't add 
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