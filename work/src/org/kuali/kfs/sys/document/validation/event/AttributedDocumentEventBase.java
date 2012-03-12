/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.validation.event;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Base abstract implementation of an attributed document event.
 */
public class AttributedDocumentEventBase extends KualiDocumentEventBase implements AttributedDocumentEvent {
    private Map<String, Object> attributes;
    private Object iterationSubject;

    /**
     * Constructs a AttributedDocumentEventBase
     * @param description
     * @param errorPathPrefix
     */
    protected AttributedDocumentEventBase(String description, String errorPathPrefix) {
        super(description, errorPathPrefix);
        attributes = new HashMap<String, Object>();
    }
    
    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEventBase#KualiDocumentEventBase(java.lang.String, java.lang.String, org.kuali.rice.krad.document.Document)
     */
    public AttributedDocumentEventBase(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
        attributes = new HashMap<String, Object>();
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    /**
     * Gets the iterationSubject attribute. 
     * @return Returns the iterationSubject.
     */
    public Object getIterationSubject() {
        return iterationSubject;
    }

    /**
     * Sets the iterationSubject attribute value.
     * @param iterationSubject The iterationSubject to set.
     */
    public void setIterationSubject(Object iterationSubject) {
        this.iterationSubject = iterationSubject;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AccountingRuleEngineRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AccountingRuleEngineRule)rule).validateForEvent(this);
    }
}
