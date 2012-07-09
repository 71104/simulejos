package javax.xml.stream;

public interface StreamFilter {
	public boolean accept(XMLStreamReader reader);
}
