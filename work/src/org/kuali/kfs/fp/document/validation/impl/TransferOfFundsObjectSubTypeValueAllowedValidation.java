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
package org.kuali.module.financial.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.AccountingLineValueAllowedValidation;
import org.kuali.module.financial.service.TransferOfFundsService;

/**
 * Override of standard accounting line value allowed definition to make sure that any object sub type on a transfer of funds is either mandatory transfer or non-mandatory transfer.
 */
public class TransferOfFundsObjectSubTypeValueAllowedValidation extends AccountingLineValueAllowedValidation {
    private TransferOfFundsService transferOfFundsService;

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code is either Mandatory Transfer or
     * Non-Mandatory Transfer. 
     * @see org.kuali.kfs.validation.AccountingLineValueAllowedValidation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        getAccountingLineForValidation().refreshReferenceObject("objectCode");
        String objectSubTypeCode = getAccountingLineForValidation().getObjectCode().getFinancialObjectSubTypeCode();

        // make sure a object sub type code exists for this object code
        if (StringUtils.isBlank(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_IS_NULL, getAccountingLineForValidation().getFinancialObjectCode());
            return false;
        }

        if (!transferOfFundsService.isMandatoryTransfersSubType(objectSubTypeCode) && !transferOfFundsService.isNonMandatoryTransfersSubType(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_NOT_MANDATORY_OR_NON_MANDATORY_TRANSFER, new String[] { getAccountingLineForValidation().getObjectCode().getFinancialObjectSubType().getFinancialObjectSubTypeName(), getAccountingLineForValidation().getFinancialObjectCode() });
            return false;
        }

        return true;
    }

    /**
     * Gets the transferOfFundService attribute. 
     * @return Returns the transferOfFundService.
     */
    public TransferOfFundsService getTransferOfFundsService() {
        return transferOfFundsService;
    }

    /**
     * Sets the transferOfFundService attribute value.
     * @param transferOfFundService The transferOfFundService to set.
     */
    public void setTransferOfFundsService(TransferOfFundsService transferOfFundsService) {
        this.transferOfFundsService = transferOfFundsService;
    }
}
