package org.kuali.kfs.sys.businessobject.datadictionary;

import org.kuali.rice.krad.datadictionary.AttributeDefinition;

public class FinancialSystemAttributeDefinition extends AttributeDefinition {
    protected String urlParameterName;

    public String getUrlParameterName() {
        return urlParameterName;
    }

    public void setUrlParameterName(String urlParameterName) {
        this.urlParameterName = urlParameterName;
    }
}
