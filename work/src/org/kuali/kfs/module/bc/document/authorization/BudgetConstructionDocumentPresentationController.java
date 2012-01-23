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
package org.kuali.kfs.module.bc.document.authorization;

import java.util.Set;

import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;

public class BudgetConstructionDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) document;

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
        boolean accountReportsExist = budgetDocumentService.isAccountReportsExist(bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber());

        return accountReportsExist;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canReload(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canReload(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canSave(Document document) {
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) document;

        return SpringContext.getBean(BudgetDocumentService.class).isAccountReportsExist(bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber()) && SpringContext.getBean(FiscalYearFunctionControlService.class).isBudgetUpdateAllowed(bcDocument.getUniversityFiscalYear());
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) document;

        Set<String> editModes = super.getEditModes(document);
        if (!SpringContext.getBean(FiscalYearFunctionControlService.class).isBudgetUpdateAllowed(bcDocument.getUniversityFiscalYear())) {
            editModes.add(BCConstants.EditModes.SYSTEM_VIEW_ONLY);
        }

        return editModes;
    }
}
