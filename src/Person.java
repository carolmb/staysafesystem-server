
public class Person {
	String ip;
	String phoneNumber;
	String name;
	
	public Person(String ip, String phoneNumber, String name) {
		this.ip = ip;
		this.phoneNumber = phoneNumber;
		this.name = name;
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
}
