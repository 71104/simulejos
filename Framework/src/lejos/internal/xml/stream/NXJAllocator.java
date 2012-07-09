package lejos.internal.xml.stream;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class NXJAllocator implements XMLStreamConstants {
	
	public XMLEvent allocate(XMLStreamReader reader) {
		int event = reader.getEventType();
		Location loc = reader.getLocation();
		
		if (event == START_ELEMENT) return new NXJXMLEvent(event,loc,reader.getLocalName(), ((NXJXMLStreamReader) reader).getAttributes());
		else if (event == END_ELEMENT) return new NXJXMLEvent(event,loc,reader.getLocalName(), null);
		else if (event == CHARACTERS || event == COMMENT) return new NXJXMLEvent(event,loc,reader.getText());
		else return new NXJXMLEvent(event,loc);
	}
}
