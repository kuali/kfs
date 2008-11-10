/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.web;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;

/**
 * Represents a field (plus, optionally, a dynamic name field) to be rendered as part of an accounting line.
 */
public class CreditMemoAccountingLineViewField extends AccountingLineViewField {

    @Override
    protected boolean isRenderingInquiry(AccountingDocument document, AccountingLine line) {
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        if ( purapDocument.isPostingYearPrior() && 
             ( purapDocument.getStatusCode().equals(PurapConstants.CreditMemoStatuses.COMPLETE) ||
               purapDocument.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE) ||
               purapDocument.getStatusCode().equals(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS) ) )  {
            return false;            
        }
        return super.isRenderingInquiry(document, line);
    }
}
