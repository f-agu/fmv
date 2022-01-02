package org.fagu.fmv.mymedia.sources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author f.agu
 * @created 2 janv. 2022 10:46:23
 */
public class ReadPomXML extends DefaultHandler {

	private final Deque<String> stack = new ArrayDeque<>();

	private final String pomNodeName;

	private String characters;

	private String parentName;

	private String name;

	public ReadPomXML(String pomNodeName) {
		this.pomNodeName = Objects.requireNonNull(pomNodeName);
	}

	public String getInfo(Path path) throws IOException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			return getInfo(inputStream);
		}
	}

	public String getInfo(InputStream inputStream) {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(inputStream, this);
			return name != null ? name : parentName;
		} catch(ParserConfigurationException | SAXException | IOException e) {
			return null;
		}
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) {
		return new InputSource(new ByteArrayInputStream(new byte[0]));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			stack.push(qName);
			characters = null;
		} catch(Exception e) {
			// ignore
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if(pomNodeName.equals(stack.pop())) {
			if(stack.size() == 1) {
				name = characters;
			} else if(stack.size() == 2 && "parent".equals(stack.getFirst())) {
				parentName = characters;
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		characters = new String(ch, start, length);
	}

}
