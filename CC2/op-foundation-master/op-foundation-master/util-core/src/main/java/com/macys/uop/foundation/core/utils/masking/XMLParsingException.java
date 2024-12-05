package com.macys.uop.foundation.core.utils.masking;

/**
 * The purpose of this exception class is to encapsulate the following exceptions into one. Created to avoid Sonar code smell.
 * <p>
 * <ul>
 * <li>ParserConfigurationException</li>
 * <li>SAXException</li>
 * <li>IOException</li>
 * </ul>
 */
public class XMLParsingException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
     * Create a new <code>XMLParsingException</code> with no detail mesage.
     */

    public XMLParsingException() {
        super();
    }

    /**
     * Create a new <code>XMLParsingException</code> with
     * the <code>String</code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    public XMLParsingException(String msg) {
        super(msg);
    }
    
    /**
     * Create a new <code>XMLParsingException</code> with
     * the <code>Exception</code> specified as an exception.
     *
     * @param msg The internal exception
     */
    public XMLParsingException(Exception ex) {
        super(ex);
    }
}
