package txtParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ConfigXmlParser {
	String dir = "/configParser.xml";
	List<String> xmlList, parsedList = new ArrayList<String>();

	public void parseFile(String fileName) {
		if (fileName != null)
			dir = fileName;
		parseXML(dir);
	}

	public void parseXML(String dir) {

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			XMLEventReader xmlEventReader = xmlInputFactory
					.createXMLEventReader(new FileInputStream(dir));
			while (xmlEventReader.hasNext()) {

				XMLEvent xmlEvent = xmlEventReader.nextEvent();
				StartElement startElement = xmlEvent.asStartElement();
				parsedList.add(startElement.getName().getLocalPart());

			}

		} catch (FileNotFoundException | XMLStreamException e) {
			e.printStackTrace();
		}

	}

	public List<String> getParsedList() {
		return parsedList;
	}

}