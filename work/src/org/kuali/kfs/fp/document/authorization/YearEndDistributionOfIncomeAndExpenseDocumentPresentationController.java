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
