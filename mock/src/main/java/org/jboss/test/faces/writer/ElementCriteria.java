package org.jboss.test.faces.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ElementCriteria implements Criteria {

	private final Collection<? extends Record> base;
	private final String name;
	private int level = Integer.MAX_VALUE;
	private String attribute;
	private Pattern attributePattern;

	public ElementCriteria(Record base, String name) {
		this(Collections.singleton(base), name);
	}

	public ElementCriteria(Collection<? extends Record> base, String name) {
		this.base = base;
		this.name = name;
	}

	public ElementCriteria atLevel(int level) {
		this.level = level;
		return this;
	}

	public ElementCriteria atPosition(int position) {
		return this;
	}

	public ElementCriteria withAttribute(String name) {
		this.attribute = name;
		this.attributePattern = null;
		return this;
	}

	public ElementCriteria withAttribute(String name, String regex) {
		this.attribute = name;
		 this.attributePattern = Pattern.compile(regex);
		return this;
	}

	public ElementCriteria contains(String regexp) {
		return this;
	}

	public String getText() throws NotFoundException {
		return lookupSingleElement().getText();
	}

	private ElementRecord lookupSingleElement() throws NotFoundException {
		List<ElementRecord> result = lookup();
		if (0 == result.size()) {
			throw new NotFoundException("No elements found for criteria "
			        + toString());
		}
		return result.get(0);
	}

	public Object getAttribute(String name) throws NotFoundException {
		ElementRecord elementRecord = lookupSingleElement();
		if (elementRecord.containsAttribute(name)) {
			return elementRecord.getAttribute(name).getValue();
		} else {
			throw new NotFoundException("Element " + elementRecord.getName()
			        + " has no attribute " + name);
		}
	}

	private List<ElementRecord> lookup() {
		List<ElementRecord> result = new ArrayList<ElementRecord>();
		for (Record record : base) {
			result.addAll(lookup(record, 0));
		}
		return result;
	}

	private List<ElementRecord> lookup(Record record, int level) {
		List<ElementRecord> result = new ArrayList<ElementRecord>();
		if (level < this.level) {
			if (record instanceof ElementRecord) {
				ElementRecord element = (ElementRecord) record;
				if (this.name.equals(element.getName())
				        && (this.level == Integer.MAX_VALUE || level == this.level)
				        && (null == attribute || element
				                .containsAttribute(attribute))
				        && (null == attributePattern || attributePattern.matcher(element
				                .getAttribute(attribute).getValue().toString()).matches())) {
					result.add(element);
				}
				for (Record childRecord : record.getChildren()) {
					result.addAll(lookup(childRecord, level + 1));
				}
			} else {
				for (Record childRecord : record.getChildren()) {
					result.addAll(lookup(childRecord, level));
				}
			}
		}
		return result;
	}

	public ElementCriteria element(String name) {
		return new ElementCriteria(lookup(), name);
	}

}
