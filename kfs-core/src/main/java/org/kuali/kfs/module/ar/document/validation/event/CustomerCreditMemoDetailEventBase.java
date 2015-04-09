/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
