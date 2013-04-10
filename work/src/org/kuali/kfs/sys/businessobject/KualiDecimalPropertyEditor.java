package org.kuali.kfs.sys.businessobject;

import java.beans.PropertyEditorSupport;

import org.kuali.rice.core.api.util.type.KualiDecimal;

// Created for Research Participant Upload
public class KualiDecimalPropertyEditor extends PropertyEditorSupport {

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
	    this.setValue(new KualiDecimal(text));
	}

    @Override
    public String getAsText() {
    	return this.getValue().toString();
    }

}
