package org.jboss.test.faces.writer;

import org.jboss.test.faces.FacesTestException;

public class TextRecord extends RecordBase implements Record {

	private final Object string;
	private final String property;

	public TextRecord(Object string, String property) {
		this.property = property;
		this.string = string;
    }

	@Override
	public Record addRecord(Record next) {
    	throw new FacesTestException("Text does not allows children elements");
	}
	
	@Override
	public String getText() {
	    return string.toString();
	}
	
	@Override
	public String toString() {
	    return string.toString();
	}

}
