package org.kuali.kfs.sys.businessobject.datadictionary;

import org.kuali.rice.kns.datadictionary.FieldDefinition;

public class FinancialSystemFieldDefinition extends FieldDefinition {
    protected String urlParameterName;

    public String getUrlParameterName() {
        return urlParameterName;
    }

    public void setUrlParameterName(String urlParameterName) {
        this.urlParameterName = urlParameterName;
    }
}
