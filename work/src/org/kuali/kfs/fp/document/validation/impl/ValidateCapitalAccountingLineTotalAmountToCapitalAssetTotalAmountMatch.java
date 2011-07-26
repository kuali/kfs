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

import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.web.struts.CapitalAccountingLinesFormBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * validate the capital accounting lines associated with the capital assets totals match validation
 */
public class ValidateCapitalAccountingLineTotalAmountToCapitalAssetTotalAmountMatch extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidateCapitalAccountingLineTotalAmountToCapitalAssetTotalAmountMatch.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return this.validateCapitlAssetsAmountToAccountingLineAmount(accountingDocumentForValidation);
    }

    // determine whether the given document's all capital accounting lines are processed.
    public boolean validateCapitlAssetsAmountToAccountingLineAmount(AccountingDocument accountingDocument) {
        boolean valid = true;
       
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) accountingDocument;
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        

        List<CapitalAssetInformation> capitalAssetInformation = caldb.getCapitalAssetInformation();
        
        for(CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.getAmount().isLessThan(getCapitalAssetsAmountAllocated(capitalAssetInformation, capitalAccountingLine))) {
                GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CAPITAL_ASSETS_AMOUNTS_GREATER_THAN_CAPITAL_ACCOUNTING_LINE, capitalAccountingLine.getSequenceNumber().toString(), capitalAccountingLine.getLineType(), capitalAccountingLine.getChartOfAccountsCode(), capitalAccountingLine.getAccountNumber(), capitalAccountingLine.getFinancialObjectCode());
                valid = false;
            }
        }
        
        return valid;
    }
    
    /**
     * sums the capital assets amount distributed so far for a given capital accounting line
     * 
     * @param currentCapitalAssetInformation
     * @param existingCapitalAsset
     * @return capitalAssetsAmount amount that has been distributed for the specific capital accounting line
     */
    protected KualiDecimal getCapitalAssetsAmountAllocated(List<CapitalAssetInformation> currentCapitalAssetInformation, CapitalAccountingLines capitalAccountingLine) {
        //check the capital assets records totals
        KualiDecimal capitalAssetsAmount = KualiDecimal.ZERO;

        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            if (capitalAsset.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
                    capitalAsset.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
                    capitalAsset.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
                    capitalAsset.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
                    capitalAsset.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                    capitalAssetsAmount = capitalAssetsAmount.add(capitalAsset.getAmount());
            }
        }
        
        return capitalAssetsAmount;
    }
    
    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
