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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.service.TransferOfFundsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Override of standard accounting line value allowed definition to make sure that any object sub type on a transfer of funds is either mandatory transfer or non-mandatory transfer.
 */
public class TransferOfFundsObjectSubTypeValueAllowedValidation extends AccountingLineValueAllowedValidation {
    private TransferOfFundsService transferOfFundsService;

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code is either Mandatory Transfer or
     * Non-Mandatory Transfer. 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        getAccountingLineForValidation().refreshReferenceObject("objectCode");
        String objectSubTypeCode = getAccountingLineForValidation().getObjectCode().getFinancialObjectSubTypeCode();

        //get the accounting line sequence string to identify which line has error.
        String accountIdentifyingPropertyName = getAccountIdentifyingPropertyName(getAccountingLineForValidation());
        
        // make sure a object sub type code exists for this object code
        if (StringUtils.isBlank(objectSubTypeCode)) {
            GlobalVariables.getMessageMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_IS_NULL, accountIdentifyingPropertyName, getAccountingLineForValidation().getFinancialObjectCode());
            return false;
        }

        if (!transferOfFundsService.isMandatoryTransfersSubType(objectSubTypeCode) && !transferOfFundsService.isNonMandatoryTransfersSubType(objectSubTypeCode)) {
            GlobalVariables.getMessageMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_NOT_MANDATORY_OR_NON_MANDATORY_TRANSFER, new String[] { accountIdentifyingPropertyName, getAccountingLineForValidation().getObjectCode().getFinancialObjectSubType().getFinancialObjectSubTypeName(), getAccountingLineForValidation().getFinancialObjectCode() });
            return false;
        }

        return true;
    }

    protected String getAccountIdentifyingPropertyName(AccountingLine accountingLine) {
        String errorProperty = "";
        
        if (accountingLine.getSequenceNumber() != null) {
            errorProperty = "Accounting Line: " + accountingLine.getSequenceNumber() + ", Chart: " + accountingLine.getChartOfAccountsCode() + ", Account: " + accountingLine.getAccountNumber() + " - ";
        }
        
        return errorProperty;
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
