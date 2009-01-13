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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Abstract base class for all TransactionalDocumentAuthorizers, since there's this one bit of common code.
 */
public class CashReceiptDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(CashReceiptDocumentAuthorizer.class);

    /**
     * Overrides to always return false because there is never FO routing or FO approval for CR docs.
     * 
     * @see org.kuali.module.financial.document.FinancialDocumentAuthorizer#userOwnsAnyAccountingLine(org.kuali.rice.kns.bo.user.KualiUser,
     *      java.util.List)
     */
    @Override
    protected boolean userOwnsAnyAccountingLine(Person user, List accountingLines) {
        return false;
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the CR doc.
     * 
     * @see org.kuali.rice.kns.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.rice.kns.document.TransactionalDocument,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public Map getEditableAccounts(TransactionalDocument document, Person user) {
        return new HashMap();
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the CR doc.
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#getEditableAccounts(java.util.List,
     *      org.kuali.module.chart.bo.ChartUser)
     */
    @Override
    public Map getEditableAccounts(List<AccountingLine> lines, Person user) {
        return new HashMap();
    }
    
    /**
     * Method used by <code>{@link org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService}</code> to determine of the
     * <code>{@link CashReceiptDocument}</code> validates business rules for generating a cover page. <br/> <br/> Rule is the
     * <code>{@link Document}</code> must be ENROUTE.
     * 
     * @param document submitted cash receipt document
     * @return true if state is not cancelled, initiated, disapproved, saved, or exception
     */
    public boolean isCoverSheetPrintable(CashReceiptFamilyBase document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }
}

