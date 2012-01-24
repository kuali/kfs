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
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class CashControlDetailEventBase extends KualiDocumentEventBase implements CashControlDetailEvent {

    private static final Logger LOG = Logger.getLogger(CashControlDetailEventBase.class);


    private final CashControlDetail cashControlDetail;

    /**
     * Initializes fields common to all subclasses
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param check
     */
    public CashControlDetailEventBase(String description, String errorPathPrefix, Document document, CashControlDetail cashControlDetail) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.cashControlDetail = (CashControlDetail) ObjectUtils.deepCopy(cashControlDetail);

        logEvent();
    }


    /**
     * @see org.kuali.kfs.module.ar.document.validation.event.CustomerInvoiceDetailEvent#getCustomerInvoiceDetail()
     */
    public CashControlDetail getCashControlDetail() {
        return cashControlDetail;
    }


    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getCashControlDetail() == null) {
            throw new IllegalArgumentException("invalid (null) cash control detail");
        }
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (cashControlDetail == null) {
            logMessage.append("null cashControlDetail");
        }
        else {
            logMessage.append(" cashControlDetail# ");
            logMessage.append(cashControlDetail.getDocumentNumber());
        }

        LOG.debug(logMessage);
    }
}
