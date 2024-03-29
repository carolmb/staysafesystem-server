import java.io.Serializable;

/*
 * Source: https://stackoverflow.com/questions/5303539/didnt-java-once-have-a-pair-class
 * */
public class Pair<K, V>  implements Serializable {

    private static final long serialVersionUID = 1L;
	
	private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
        return new Pair<K, V>(element0, element1);
    }

    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return element0;
    }

    public V getElement1() {
        return element1;
    }

}