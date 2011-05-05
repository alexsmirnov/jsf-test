package org.jboss.test.faces.writer;

public class CDATARecord extends RecordBase implements Record {

	@Override
	public String toString() {
	    return "<![CDATA["+getText()+"]]>";
	}


}
