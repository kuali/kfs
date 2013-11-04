package org.kuali.kfs.sys.businessobject;

import java.beans.PropertyEditorSupport;

import org.kuali.rice.core.api.util.type.KualiInteger;

public class KualiIntegerPropertyEditor extends PropertyEditorSupport {

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
	    this.setValue(new KualiInteger(text));
	}

    @Override
    public String getAsText() {
    	return this.getValue().toString();
    }

}
