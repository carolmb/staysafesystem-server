import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StorageManager<T> {
	
	String local;
	
	public StorageManager(String local) {
		this.local = local;
	}
	
	public void save(T obj) {
		FileOutputStream f_out;
		try {
			f_out = new FileOutputStream(local + ".data");
			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
			obj_out.writeObject(obj);
			obj_out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public T retrive() {
		FileInputStream f_in;
		T obj = null;
		try {
			f_in = new FileInputStream(local + ".data");
			ObjectInputStream obj_in = new ObjectInputStream (f_in);
			obj = (T) obj_in.readObject();
			obj_in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
}
