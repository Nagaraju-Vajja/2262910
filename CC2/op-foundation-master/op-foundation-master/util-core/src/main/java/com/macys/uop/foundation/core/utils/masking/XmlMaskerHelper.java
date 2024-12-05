package com.macys.uop.foundation.core.utils.masking;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper methods for {@link com.macys.uop.foundation.core.utils.masking.XmlMasker}
 * Primarily created to avoid sonar issue related to more than 20 objects reference
 */
public interface XmlMaskerHelper 
{
	/**
	 * Creates XML {@link Document} from valid XML string. 
	 * <br> 
	 * Note : Neither DocumentBuilderFactory nor DocumentBuilder are guaranteed to
	 * be thread safe. Ref : https://stackoverflow.com/questions/12455602/is-documentbuilder-thread-safe
	 * 
	 * @param str XML string
	 * 
	 * @return XML {@link Document} 
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	default Document toXmlDocument(String str) throws XMLParsingException {
		Document result=null;
		try { 
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
			docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); 
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			result=docBuilder.parse(new InputSource(new StringReader(str)));
		} catch(ParserConfigurationException | SAXException | IOException ex) {
			throw new XMLParsingException(ex);
		}
		return result;
	}

	/**
	 * Converts {@link Document} into XML string
	 * 
	 * @param document input {@link Document}
	 * 
	 * @return XML string
	 * 
	 * @throws TransformerException
	 */
	default String toXmlString(Document document) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
	    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, ""); 
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StringWriter strWriter = new StringWriter();
		StreamResult result = new StreamResult(strWriter);
		transformer.transform(source, result);
		return strWriter.getBuffer().toString();
	}
}
