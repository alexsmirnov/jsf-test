package org.jboss.test.faces.writer;

public class RecordCriteria implements Criteria {

	private final Record content;

	public RecordCriteria(Record content) {
		this.content = content;
    }


	public ElementCriteria element(String name) {
		return new ElementCriteria(content, name);
	}


}
