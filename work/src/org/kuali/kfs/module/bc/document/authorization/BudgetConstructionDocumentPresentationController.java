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
