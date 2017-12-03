import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Person implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String ip;
	String phoneNumber;
	String name;
	
	ArrayList<Person> friends;
	
	public Person(String ip, String phoneNumber, String name) {
		this.ip = ip;
		this.phoneNumber = phoneNumber;
		this.name = name;
		friends = new ArrayList<Person>();
	}
	
	public void setFriends(ArrayList<Person> friends) {
		this.friends = friends;
	}
	
	public ArrayList<Person> getFriends() {
		return friends;
	}
	
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        // null check
        if (other == null)
            return false;
        // type check and cast
        if (getClass() != other.getClass())
            return false;
        Person person = (Person) other;
        // field comparison
        return name.contentEquals(person.name)
                && phoneNumber.contentEquals(person.phoneNumber);
    }
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
