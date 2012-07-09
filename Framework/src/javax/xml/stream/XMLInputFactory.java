package javax.xml.stream;

import java.io.InputStream;

import lejos.internal.xml.stream.NXJXMLInputFactory;

public abstract class XMLInputFactory {
	public static XMLInputFactory newInstance() {
		return new NXJXMLInputFactory();
	}
	
	public abstract XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException;
	
	public abstract XMLStreamReader createStreamReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException;
	
	public abstract XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException;
}
