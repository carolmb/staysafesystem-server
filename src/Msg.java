import org.json.JSONObject;

public class Msg {
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
		json.put("title", title);
		json.put("subtitle", subtitle);
		json.put("content", "content");
		return json;
	}
}
