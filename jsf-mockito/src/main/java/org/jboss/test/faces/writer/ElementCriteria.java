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
    private int position= Integer.MAX_VALUE;
    private Pattern contentPattern;
    private Pattern namePattern;

	public ElementCriteria(Record base, String name) {
		this(Collections.singleton(base), name);
	}

	public ElementCriteria(Collection<? extends Record> base, String name) {
		this.base = base;
		this.name = name;
		this.namePattern = Pattern.compile(name);
	}

	public ElementCriteria atLevel(int level) {
		this.level = level;
		return this;
	}

	public ElementCriteria atPosition(int position) {
		this.position = position;
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

	public ElementCriteria contains(String regex) {
	    this.contentPattern = Pattern.compile(regex);
		return this;
	}

	public String getName() throws NotFoundException {
        return lookupSingleElement().getName();
    }
	
	public String getText() throws NotFoundException {
		return lookupSingleElement().getText();
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

    public boolean matches(){
	    return lookup().size()>0;
	}

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if(level != Integer.MAX_VALUE){
            for(int i=0;i<level;i++){
                result.append("/*");
            }
        }
        result.append('/').append(name);
        if(position != Integer.MAX_VALUE){
            result.append("(").append(position).append(")");
        }
        if(null != attribute){
            result.append("[").append(attribute);
            if(null != attributePattern){
                result.append("='").append(attributePattern).append("'");
            }
            result.append("]");
        }
        if(null != contentPattern){
            result.append(" '").append(attributePattern).append("'");
        }
        return result.toString();
    }
	private ElementRecord lookupSingleElement() throws NotFoundException {
		List<ElementRecord> result = lookup();
		if (0 == result.size()) {
			throw new NotFoundException("No element found for criteria "
			        + toString());
		} else if(result.size()>1){
            throw new NotFoundException("More then one element found for criteria "
                + toString());
		}
		return result.get(0);
	}

	private List<ElementRecord> lookup() {
		List<ElementRecord> result = new ArrayList<ElementRecord>();
		for (Record record : base) {
			result.addAll(lookup(record, 0,0));
		}
		return result;
	}

	private List<ElementRecord> lookup(Record record, int level,int currentPosition) {
		List<ElementRecord> result = new ArrayList<ElementRecord>();
		if (level <= this.level) {
			if (record instanceof ElementRecord) {
				ElementRecord element = (ElementRecord) record;
				if (this.namePattern.matcher(element.getName()).matches()
				        && (this.level == Integer.MAX_VALUE || level == this.level)
                        && (this.position == Integer.MAX_VALUE || currentPosition == this.position)
				        && (null == attribute || element
				                .containsAttribute(attribute))
				        && (null == attributePattern || attributePattern.matcher(element
				                .getAttribute(attribute).getValue().toString()).matches())
				        && (null == contentPattern || contentPattern.matcher(element.getText()).matches())) {
					result.add(element);
				}
                int childPosition=0;
				for (Record childRecord : record.getChildren()) {
					result.addAll(lookup(childRecord, level + 1,childPosition));
                    if (childRecord instanceof ElementRecord) {
                        childPosition++;
                    }
				}
				;
			} else {
				for (Record childRecord : record.getChildren()) {
					result.addAll(lookup(childRecord, level,currentPosition));
					if (childRecord instanceof ElementRecord) {
					    currentPosition++;
					}
				}
			}
		}
		return result;
	}

	public ElementCriteria element(String name) {
	    List<Record> records = new ArrayList<Record>();
	    List<ElementRecord> elements = lookup();
	    for (ElementRecord elementRecord : elements) {
            records.addAll(elementRecord.getChildren());
        }
		return new ElementCriteria(records, name);
	}

}
