import java.io.Serializable;

import org.json.JSONObject;

public class Call implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Msg msg;
	private Person from;
	private boolean status;
	
	public Call(Msg msg, Person from) {
		this.msg = msg;
		this.from = from;
		this.status = false;
	}
	
	public Person getFrom() {
		return from;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("msg", msg.toJson());
		json.put("from", from.toString());
		json.put("status", status);
		return json;
	}
}
