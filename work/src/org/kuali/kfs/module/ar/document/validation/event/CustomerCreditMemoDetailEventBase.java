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
package org.kuali.kfs.module.ar.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class CustomerCreditMemoDetailEventBase extends KualiDocumentEventBase implements CustomerCreditMemoDetailEvent{

    private static final Logger LOG = Logger.getLogger(CustomerCreditMemoDetailEventBase.class);
    private final CustomerCreditMemoDetail customerCreditMemoDetail;
    
    public CustomerCreditMemoDetailEventBase(String description, String errorPathPrefix, Document document, CustomerCreditMemoDetail customerCreditMemoDetail) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.customerCreditMemoDetail = (CustomerCreditMemoDetail) ObjectUtils.deepCopy(customerCreditMemoDetail);
        
        logEvent();
    }
    
    public CustomerCreditMemoDetail getCustomerCreditMemoDetail() {
        return customerCreditMemoDetail;
    }
    
    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (customerCreditMemoDetail == null) {
            throw new IllegalArgumentException("invalid (null) customer credit memo detail");
        }
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (customerCreditMemoDetail == null) {
            logMessage.append("null customerCreditMemoDetail");
        }
        else {
            logMessage.append(" customer credit memo detail# ");
            logMessage.append( customerCreditMemoDetail.getReferenceInvoiceItemNumber() );
        }

        LOG.debug(logMessage);
    }

}
