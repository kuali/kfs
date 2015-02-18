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
package org.kuali.kfs.fp.document.validation.event;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.document.validation.CashManagingRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * The default implementation of the CashieringTransactionApplicationEvent interface
 */
public class CashieringTransactionApplicationEventBase extends KualiDocumentEventBase implements CashieringTransactionApplicationEvent {
    private CashieringTransaction cashieringTransaction;
    private CashDrawer cashDrawer;
    
    /**
     * Constructs a CashieringTransactionApplicationEventBase
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param cashieringTransaction
     */
    public CashieringTransactionApplicationEventBase(String description, String errorPathPrefix, Document document, CashDrawer cashDrawer, CashieringTransaction cashieringTransaction) {
        super(description, errorPathPrefix, document);
        this.cashieringTransaction = cashieringTransaction;
        this.cashDrawer = cashDrawer;
    }

    /**
     * Returns the cashieringTransaction to validate
     * @see org.kuali.kfs.fp.document.validation.event.CashieringTransactionApplicationEvent#getCashieringTransaction()
     */
    public CashieringTransaction getCashieringTransaction() {
        return this.cashieringTransaction;
    }

    /**
     * Returns the cash drawer the cashiering transaction will apply to
     * @see org.kuali.kfs.fp.document.validation.event.CashieringTransactionApplicationEvent#getCashDrawer()
     */
    public CashDrawer getCashDrawer() {
        return this.cashDrawer;
    }

    /**
     * Returns CashManagingRule.class
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return CashManagingRule.class;
    }

    /**
     * Casts the rule to CashManagingRule and calls processCashieringTransactionApplication
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((CashManagingRule)rule).processCashieringTransactionApplication(getCashDrawer(), getCashieringTransaction());
    }

}
