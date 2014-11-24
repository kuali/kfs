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
