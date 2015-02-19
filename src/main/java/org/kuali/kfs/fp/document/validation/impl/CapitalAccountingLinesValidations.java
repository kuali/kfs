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

import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * validate the capital accounting lines associated with the accounting document for validation
 */
public class CapitalAccountingLinesValidations extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAccountingLinesValidations.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return this.validateCapitalAccountingLines(accountingDocumentForValidation);
    }

    // determine whether the given document's all capital accounting lines are processed.
    protected boolean validateCapitalAccountingLines(AccountingDocument accountingDocumentForValidation) {
        LOG.debug("validateCapitalAccountingLines - start");

        boolean isValid = true;
        
        if (accountingDocumentForValidation instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAccountingLinesDocumentBase capitalAccountingLinesDocumentBase = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;
        List <CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesDocumentBase.getCapitalAccountingLines();
        
        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocumentForValidation;

        List<CapitalAssetInformation> capitalAssets = capitalAssetEditable.getCapitalAssetInformation();

        isValid &= allCapitalAccountingLinesProcessed(capitalAccountingLines, capitalAssets);
        isValid &= capitalAssetExistsForCapitalAccountingLinesProcessed(capitalAccountingLines, capitalAssets);
        isValid &= totalAmountMatchForCapitalAccountingLinesAndCapitalAssets(capitalAccountingLines, capitalAssets);
        
        return isValid;
    }

    protected boolean allCapitalAccountingLinesProcessed(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssets) {
        LOG.debug("allCapitalAccountingLinesProcessed - start");
        
        boolean processed = true;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (!capitalAccountingLine.isSelectLine()) {
                processed = false;
                GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_NOT_PROCESSED);
                
                break;
            }
        }
        
        return processed;
    }

    protected boolean capitalAssetExistsForCapitalAccountingLinesProcessed(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssets) {
        LOG.debug("capitalAssetExistsForCapitalAccountingLinesProcessed - start");
        
        boolean exists = true;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (!capitalAssetExist(capitalAccountingLine, capitalAssets)) {
                exists = false;
                GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_HAS_NO_CAPITAL_ASSET, capitalAccountingLine.getSequenceNumber().toString(), capitalAccountingLine.getLineType(), capitalAccountingLine.getChartOfAccountsCode(), capitalAccountingLine.getAccountNumber(), capitalAccountingLine.getFinancialObjectCode());
                
                break;
            }
        }
        
        return exists;
    }
    
    /**
     * 
     * @param capitalAccountingLine
     * @param capitalAssetInformation
     * @return true if accounting line has a capital asset information
     */
    protected boolean capitalAssetExist(CapitalAccountingLines capitalAccountingLine, List<CapitalAssetInformation> capitalAssetInformation) {
        boolean exists = false;
        
        if (ObjectUtils.isNull(capitalAssetInformation) && capitalAssetInformation.size() <= 0) {
            return exists;
        }
        
        for (CapitalAssetInformation capitalAsset : capitalAssetInformation) {
          //  if (capitalAsset.getSequenceNumber().compareTo(capitalAccountingLine.getSequenceNumber()) == 0 &&
         //           capitalAsset.getFinancialDocumentLineTypeCode().equals(KFSConstants.SOURCE.equals(capitalAccountingLine.getLineType()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) && 
         //           capitalAsset.getChartOfAccountsCode().equals(capitalAccountingLine.getChartOfAccountsCode()) && 
         //           capitalAsset.getAccountNumber().equals(capitalAccountingLine.getAccountNumber()) && 
         //           capitalAsset.getFinancialObjectCode().equals(capitalAccountingLine.getFinancialObjectCode())) {
                return true;
          //  }
        }
        
        return exists;
    }
    
    protected boolean totalAmountMatchForCapitalAccountingLinesAndCapitalAssets(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssets) {
        boolean totalAmountMatched = true;
        
        KualiDecimal capitalAccountingLinesTotals = KualiDecimal.ZERO;
        KualiDecimal capitalAAssetTotals = KualiDecimal.ZERO;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            capitalAccountingLinesTotals = capitalAccountingLinesTotals.add(capitalAccountingLine.getAmount());
        }
    
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            capitalAAssetTotals = capitalAAssetTotals.add(capitalAsset.getCapitalAssetLineAmount());
        }
        
        if (capitalAccountingLinesTotals.isGreaterThan(capitalAAssetTotals)) {
            //not all the accounting lines amounts have been distributed to capital assets
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINES_NOT_ALL_TOTALS_DISTRIBUTED_TO_CAPITAL_ASSETS);
            return false;
        }
        
        if (capitalAccountingLinesTotals.isLessEqual(capitalAAssetTotals)) {
            //not all the accounting lines amounts have been distributed to capital assets
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_HAS_NO_CAPITAL_ASSET);
            return false;
        }
        
        return totalAmountMatched;
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
