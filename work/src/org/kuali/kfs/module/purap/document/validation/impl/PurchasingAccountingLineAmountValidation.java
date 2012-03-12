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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingAccountingLineAmountValidation extends GenericValidation {

    private AccountingLine updatedAccountingLine;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurApAccountingLine purapAccountingLine = (PurApAccountingLine)updatedAccountingLine;
      //  PurchasingDocumentBase purapDoc = (PurchasingDocumentBase) event.getDocument();
        PurchasingAccountsPayableDocumentBase purapDoc = (PurchasingAccountsPayableDocumentBase) event.getDocument();

        if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(purapDoc.getAccountDistributionMethod())) {
            if (ObjectUtils.isNull(purapAccountingLine.getAmount())) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ACCOUNTS, PurapKeyConstants.ERROR_PURCHASING_AMOUNT_MISSING);
                valid &= false;                
            }
        }

        return valid;
    }

    public AccountingLine getUpdatedAccountingLine() {
        return updatedAccountingLine;
    }

    public void setUpdatedAccountingLine(AccountingLine updatedAccountingLine) {
        this.updatedAccountingLine = updatedAccountingLine;
    }

}
