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
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingAccountsPayableAccountPercentBetween0And100Validation extends GenericValidation {

    private PurApAccountingLine accountingLine;
    private String errorPropertyName;
    private String itemIdentifier;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        double pct = accountingLine.getAccountLinePercent().doubleValue();
        
        if (pct <= 0 || pct > 100) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, PurapKeyConstants.ERROR_ITEM_PERCENT, "%", itemIdentifier);

            valid = false;
        }

        return valid;
    }

    public PurApAccountingLine getAccountingLine() {
        return accountingLine;
    }

    public void setAccountingLine(PurApAccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }

    public String getErrorPropertyName() {
        return errorPropertyName;
    }

    public void setErrorPropertyName(String errorPropertyName) {
        this.errorPropertyName = errorPropertyName;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

}
