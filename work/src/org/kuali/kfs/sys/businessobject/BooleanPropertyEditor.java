package org.kuali.kfs.sys.businessobject;

import java.beans.PropertyEditorSupport;

// Created for Research Participant Upload
public class BooleanPropertyEditor extends PropertyEditorSupport {

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
	    if (("Y").equalsIgnoreCase(text)) {
	    	this.setValue(Boolean.TRUE);
	    }
	    else {
	    	this.setValue(Boolean.FALSE);
	    }
	}

}
