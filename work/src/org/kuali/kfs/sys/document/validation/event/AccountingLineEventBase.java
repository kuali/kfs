/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.rule.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;

/**
 * Defines methods common to all AccountingLineEvents.
 * 
 * 
 */
public abstract class AccountingLineEventBase extends KualiDocumentEventBase implements AccountingLineEvent {
    private static final Logger LOG = Logger.getLogger(AccountingLineEventBase.class);


    private final AccountingLine accountingLine;

    /**
     * Initializes fields common to all subclasses
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AccountingLineEventBase(String description, String errorPathPrefix, Document document, AccountingLine accountingLine) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.accountingLine = (AccountingLine) ObjectUtils.deepCopy(accountingLine);

        logEvent();
    }


    /**
     * @see org.kuali.core.rule.event.AccountingLineEvent#getAccountingLine()
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getAccountingLine() == null) {
            throw new IllegalArgumentException("invalid (null) accounting line");
        }
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (accountingLine == null) {
            logMessage.append("null accountingLine");
        }
        else {
            String accountingLineType = StringUtils.substringAfterLast(accountingLine.getOjbConcreteClass(), ".");

            logMessage.append(accountingLineType);
            logMessage.append(" seq# ");
            logMessage.append(accountingLine.getSequenceNumber());
        }

        LOG.debug(logMessage);
    }
}