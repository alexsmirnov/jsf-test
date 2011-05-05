package org.jboss.test.faces.writer;

import java.util.ArrayList;
import java.util.List;

import org.jboss.test.faces.FacesTestException;

public class RecordBase implements Record {

	private List<Record> records = new ArrayList<Record>();
	private Record parent;

	public RecordBase() {
		super();
	}

	public void addAttribute(Attribute attr) {
    	throw new FacesTestException("Document does not allows attributes");
    }

	public Record addRecord(Record next) {
    	next.setParent(this);
    	records.add(next);
    	return next;
    }

	/**
     * @return the records
     */
    public List<Record> getChildren() {
    	return records;
    }

	public String getName() {
    	return "HTML document";
    }

	public Record getParent() {
    	return parent;
    }

	/**
     * @param parent the parent to set
     */
    public void setParent(Record parent) {
    	this.parent = parent;
    }

	public String getText() {
	    StringBuilder text = new StringBuilder();
	    for (Record record : records) {
	        text.append(record.getText());
        }
	    return text.toString();
    }

	@Override
	public String toString() {
	    StringBuilder text = new StringBuilder();
	    for (Record record : records) {
	        text.append(record.toString());
        }
	    return text.toString();
	}
}