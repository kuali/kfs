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

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingUpdateAccountingLineValidation extends GenericValidation {

    private AccountingLine updatedAccountingLine;

    public boolean validate(AttributedDocumentEvent event) {
        //this is necessary because sometimes this method is called for baseline accounts, should not be needed once baseline is removed
        if(updatedAccountingLine instanceof PurApAccountingLine) {
            return verifyAccountingLinePercent((PurApAccountingLine) updatedAccountingLine);
        }//else
        return true;
    }

    /**
     * Verifies that the accounting line percent is a whole number.
     * 
     * @param purapAccountingLine the accounting line to be validated
     * @return boolean false if the accounting line percent is not a whole number.
     */
    protected boolean verifyAccountingLinePercent(PurApAccountingLine purapAccountingLine) {
        // make sure it's a whole number
        if (purapAccountingLine.getAccountLinePercent().stripTrailingZeros().scale() > 0) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ACCOUNTS, PurapKeyConstants.ERROR_PURCHASING_PERCENT_NOT_WHOLE, purapAccountingLine.getAccountLinePercent().toPlainString());

            return false;
        }

        return true;
    }

    public AccountingLine getUpdatedAccountingLine() {
        return updatedAccountingLine;
    }

    public void setUpdatedAccountingLine(AccountingLine updatedAccountingLine) {
        this.updatedAccountingLine = updatedAccountingLine;
    }

}
