/*

 * Copyright 2007-2008 The Kuali Foundation

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

/**
 * Document Authorizer for the Year End Budget Adjustment document.
 */
public class YearEndGeneralErrorCorrectionDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

   /**
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