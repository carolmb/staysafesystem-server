import java.io.Serializable;

import org.json.JSONObject;

public class Msg implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String title;
	String subtitle;
	String content;
	
	public Msg(String title, String subtitle, String content) {
		this.title = title;
		this.subtitle = subtitle;
		this.content = content;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("action", Server.ACTION.MSG);
		json.put("title", title);
		json.put("subtitle", subtitle);
		json.put("content", content);
		return json;
	}
}
