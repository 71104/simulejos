package javax.xml.namespace;

public class QName {
	private String localName;
	
	public QName(String localPart) {
		localName = localPart;
	}
	
	public QName(String namespaceURI, String localPart) {
		this(localPart);
	}
	
	public QName(String namespaceURI, String localPart, String prefix) {
		this(localPart);
	}

	public String getLocalPart() {
		return localName;
	}
	
	public String getPrefix() {
		return null;
	}
	
	public String getNamespaceURI() {
		return null;
	}
	
	@Override
	public String toString() {
		return localName;
	}
}
