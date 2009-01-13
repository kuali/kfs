/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.TransactionalDocument;

/**
 * Authorization permissions specific to the Credit Card Receipt document.
 */
public class CreditCardReceiptDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * Overrides to always return false because there is never FO routing or FO approval for CCR docs.
     * 
     * @see org.kuali.module.financial.document.FinancialDocumentAuthorizer#userOwnsAnyAccountingLine(org.kuali.rice.kns.bo.user.KualiUser,
     *      java.util.List)
     */
    protected boolean userOwnsAnyAccountingLine(Person user, List accountingLines) {
        return false;
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the CCR doc.
     * 
     * @see org.kuali.rice.kns.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.rice.kns.document.TransactionalDocument,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    public Map getEditableAccounts(TransactionalDocument document, Person user) {
        return new HashMap();
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the CCR doc.
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#getEditableAccounts(java.util.List,
     *      org.kuali.module.chart.bo.ChartUser)
     */
    @Override
    public Map getEditableAccounts(List<AccountingLine> lines, Person user) {
        return new HashMap();
    }
}