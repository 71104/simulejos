package lejos.internal.xml.stream;

import java.io.InputStream;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A leJOS NXJ implementation of XMLInputFactory. 
 * Creates instances of NXJXMLStreamReader.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJXMLInputFactory extends XMLInputFactory  {
	
	@Override
	public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
		return new NXJXMLStreamReader(stream);
	}
	
	@Override
	public XMLStreamReader createStreamReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
		NXJXMLStreamReader r = new NXJXMLStreamReader(((NXJXMLStreamReader) reader).getInputStream());
		r.setFilter(filter);
		return r;
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream stream)
			throws XMLStreamException {
		NXJXMLStreamReader reader = new NXJXMLStreamReader(stream);
		return new NXJXMLEventReader(reader);
	}
}
