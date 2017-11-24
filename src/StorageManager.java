
public class StorageManager {
	static private StorageManager manager;
	
	private StorageManager() {}
	
	static public StorageManager getInstance() {
		if(manager == null) {
			manager = new StorageManager();
		}
		return manager;
	}
	
}
