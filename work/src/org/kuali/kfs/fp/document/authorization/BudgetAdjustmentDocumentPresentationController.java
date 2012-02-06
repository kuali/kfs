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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;

import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Presentation Controller for Budget Adjustment documents
 */
public class BudgetAdjustmentDocumentPresentationController extends AccountingDocumentPresentationControllerBase {
    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canInitiate(java.lang.String)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
        List<Integer> allowedYears = SpringContext.getBean(FiscalYearFunctionControlService.class).getBudgetAdjustmentAllowedYears();

        // if no allowed years found, BA document is not allowed to be initiated
        if (allowedYears == null || allowedYears.isEmpty()) {
            return false;
        }

        return super.canInitiate(documentTypeName);
    }

    // TODO is there really anything specific to the BA in this logic?
    /**
     * @see org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
     */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        final WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (!(document instanceof Correctable)) return false;
        if (!((FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(document.getClass().getName())).getAllowsErrorCorrection()) return false;
        if (document.getFinancialSystemDocumentHeader().getCorrectedByDocumentId() != null) return false;
        return (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal());
    }

}
