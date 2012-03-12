/*
 * Copyright 2009 The Kuali Foundation
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
