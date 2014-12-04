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
