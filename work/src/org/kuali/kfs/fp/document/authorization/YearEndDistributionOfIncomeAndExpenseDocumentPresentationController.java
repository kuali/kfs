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

import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.springframework.util.ObjectUtils;

public class YearEndDistributionOfIncomeAndExpenseDocumentPresentationController extends DistributionOfIncomeAndExpenseDocumentPresentationController {

    /**
      * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
      */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {

        //We have to do this because we couldn't call super.canErrorCorrect, because we cannot use the
        //canErrorCorrect method in LedgerPostingDocumentPresentationControllerBase for year end documents,
        //which will return false if the posting year is not equal to current fiscal year. All of the year end
        //documents are supposed to have previous fiscal year as posting year, therefore it will always be
        //false and will cause the year end documents not error correctable.

        FinancialSystemTransactionalDocumentPresentationControllerBase presentationController = new FinancialSystemTransactionalDocumentPresentationControllerBase();

        final boolean result = presentationController.canErrorCorrect(document);

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
