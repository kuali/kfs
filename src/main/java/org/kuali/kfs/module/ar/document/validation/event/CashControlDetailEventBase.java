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
