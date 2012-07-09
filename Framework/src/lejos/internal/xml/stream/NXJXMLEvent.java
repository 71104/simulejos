package lejos.internal.xml.stream;

import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import lejos.util.IterableEnumeration;

public class NXJXMLEvent implements StartElement, EndElement, Characters {
	private int event;
	private Location loc;
	private String text, localName;
	private Hashtable<String,String> attributes;
	private Hashtable <String,String> namespaces;
	
	private String[] eventNames = {
			"START_ELEMENT", "END_ELEMENT", "PROCESSING_INSTRUCTION",
			"CHARACTERS", "COMMENT", "SPACE",
			"START_DOCUMENT", "END_DOCUMENT", "ENTITY_REFERENCE",
			"ATTRIBUTE", "DTD", "CDATA",
			"NAMESPACE", "NOTATION_DECLARATION", "ENTITY_DECLARATION"
	};
	
	public NXJXMLEvent(int event, Location loc) {
		this.event = event;
		this.loc = loc;
	}
	
	public NXJXMLEvent(int event, Location loc, String text) {
		this.event = event;
		this.loc = loc;
		this.text = text;
	}
	
	public NXJXMLEvent(int event, Location loc, String localName, Hashtable<String,String> attributes) {
		this.event = event;
		this.loc = loc;
		this.localName = localName;
		this.attributes = attributes;
	}

	public int getEventType() {
		return event;
	}

	public Location getLocation() {
		return loc;
	}

	public boolean isStartElement() {
		return (event == START_ELEMENT);
	}

	public boolean isAttribute() {
		return (event == ATTRIBUTE);
	}

	public boolean isNamespace() {
		return (event == NAMESPACE);
	}

	public boolean isEndElement() {
		return (event == END_ELEMENT);
	}

	public boolean isEntityReference() {
		return false;
	}

	public boolean isProcessingInstruction() {
		return false;
	}

	public boolean isCharacters() {
		return (event == CHARACTERS);
	}

	public boolean isStartDocument() {
		return (event == START_DOCUMENT);
	}

	public boolean isEndDocument() {
		return (event == END_DOCUMENT);
	}

	public StartElement asStartElement() {
		return this;
	}

	public EndElement asEndElement() {
		return this;
	}

	public Characters asCharacters() {
		return this;
	}

	public QName getSchemaType() {
		return null;
	}

	public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
		// Not implemented
	}

	public QName getName() {
		return new QName(localName);
	}

	public Iterator getAttributes() {
		return (new IterableEnumeration<String>(attributes.keys()).iterator());
	}

	public Iterator getNamespaces() {
		return (new IterableEnumeration<String>(namespaces.keys()).iterator());
	}

	public Attribute getAttributeByName(QName name) {
		return null;
	}

	public NamespaceContext getNamespaceContext() {
		return null;
	}

	public String getNamespaceURI(String prefix) {
		return namespaces.get(prefix);
	}

	public String getData() {
		return null;
	}

	public boolean isWhiteSpace() {
		if (text == null) return true;
		for(int i=0;i<text.length();i++) {
			if (text.charAt(i) != ' ') return false;
		}
		return true;
	}

	public boolean isCData() {
		return false;
	}

	public boolean isIgnorableWhiteSpace() {
		return false;
	}
	
	@Override
	public String toString() {
		if (attributes != null && localName != null) {
			String s = localName;
			for(Enumeration<String> e = attributes.keys();e.hasMoreElements();) {
				String t = e.nextElement();
				s += " " + t + "="  + attributes.get(t);
			}
			return s;
		}
		else if (localName != null) return (event == END_ELEMENT ? "/ " : "") + localName;
		else if (event == COMMENT) return "<!--" + text + "-->";
		else if (event == CHARACTERS) return text;
		else return eventNames[event-1];		
	}
}
