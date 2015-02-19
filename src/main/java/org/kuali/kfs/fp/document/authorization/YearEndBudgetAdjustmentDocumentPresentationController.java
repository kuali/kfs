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
package org.kuali.kfs.fp.document.authorization;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.springframework.util.ObjectUtils;

/**
 * Document Authorizer for the Year End Budget Adjustment document.
 */
public class YearEndBudgetAdjustmentDocumentPresentationController extends BudgetAdjustmentDocumentPresentationController {

    /**
     * Checks whether the BA document is active for the year end posting year.
     *
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.krad.bo.user.KualiUser)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
        List allowedYears = SpringContext.getBean(FiscalYearFunctionControlService.class).getBudgetAdjustmentAllowedYears();
        Integer previousPostingYear = new Integer(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1);
        boolean previousActive = false;
		if (allowedYears != null) {
            for (Iterator iter = allowedYears.iterator(); iter.hasNext();) {
                FiscalYearFunctionControl fyControl = (FiscalYearFunctionControl) iter.next();
                if (fyControl.getUniversityFiscalYear().equals(previousPostingYear)) {
                    previousActive = true;
                }
            }
        }
        return super.canInitiate(documentTypeName) && previousActive;
    }

    /**

     * Overrode the superclass so that we are comparing the previous

     * fiscal year with the document's posting year, because this is

     * a year end document and the posting year should be the previous

     * fiscal year. The non-year-end documents are comparing the

     * posting year with the current fiscal year and this is done

     * in the LedgerPostingDocumentPresentationControllerBase.

     *

     *

     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)

     */

   @Override

   public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
       final boolean result = super.canErrorCorrect(document);
       if (result) {
           YearEndPendingEntryService yearEndPendingEntryService = SpringContext.getBean(YearEndPendingEntryService.class);
           Integer previousFiscalYear = yearEndPendingEntryService.getPreviousFiscalYear();
           if (!ObjectUtils.nullSafeEquals(previousFiscalYear, ((LedgerPostingDocument)document).getPostingYear())) {
               return false;
           }
       }

       return result;
   }
}

