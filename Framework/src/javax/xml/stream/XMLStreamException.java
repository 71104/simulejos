package javax.xml.stream;

public class XMLStreamException extends Exception {
	private Location loc;
	
	public XMLStreamException(String msg, Location loc) {
		super(msg);
		this.loc = loc;
	}
	
	public XMLStreamException(String msg) {
		super(msg);
	}
	
	public Location getLOcation() {
		return loc;
	}
}
