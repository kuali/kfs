/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_BLANK_AMOUNT;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class validates asset zero amount condition
 */
public class AssetPaymentZeroAmountValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Asset payment amount should be a non-zero value
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        // skip check if accounting line is from CAB
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return true;
        }

        KualiDecimal amount = accountingLineForValidation.getAmount();
        if(ObjectUtils.isNull(amount)) {
            GlobalVariables.getMessageMap().putError(AMOUNT_PROPERTY_NAME,ERROR_BLANK_AMOUNT,"");
            return false;
        }
        else if (amount.isZero()) {
            GlobalVariables.getMessageMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }
        return true;
    }

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLine) {
        this.accountingLineForValidation = accountingLine;
    }

}
