package lejos.internal.xml.stream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class NXJXMLEventReader implements XMLEventReader, XMLStreamConstants {
	private NXJXMLStreamReader reader;
	private NXJAllocator allocator = new NXJAllocator();
	
	public NXJXMLEventReader(NXJXMLStreamReader reader) {
		this.reader = reader;
	}

	public Object next() {
		return null;
	}

	public void remove() {
		// Not implemented	
	}

	public XMLEvent nextEvent() throws XMLStreamException {
		reader.next();
		return allocator.allocate(reader);
	}

	public boolean hasNext() {
		return (reader.getEventType() != END_DOCUMENT);
	}

	public XMLEvent peek() throws XMLStreamException {
		return null;
	}

	public String getElementText() throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public XMLEvent nextTag() throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		return null;
	}

	public void close() throws XMLStreamException {
		// Does nothing		
	}
}
