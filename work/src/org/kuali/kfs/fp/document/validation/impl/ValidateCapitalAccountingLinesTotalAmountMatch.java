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
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * validate the capital accounting lines associated with the accounting document for validation
 */
public class ValidateCapitalAccountingLinesTotalAmountMatch extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidateCapitalAccountingLinesTotalAmountMatch.class);

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

        isValid = totalAmountMatchForCapitalAccountingLinesAndCapitalAssets(capitalAccountingLines, capitalAssets);
        
        return isValid;
    }
    
    /**
     * 
     * @param capitalAccountingLines
     * @param capitalAssets
     * @return
     */
    protected boolean totalAmountMatchForCapitalAccountingLinesAndCapitalAssets(List<CapitalAccountingLines> capitalAccountingLines, List<CapitalAssetInformation> capitalAssets) {
        boolean totalAmountMatched = true;
        
        KualiDecimal capitalAccountingLinesTotals = KualiDecimal.ZERO;
        KualiDecimal capitalAAssetTotals = KualiDecimal.ZERO;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            capitalAccountingLinesTotals = capitalAccountingLinesTotals.add(capitalAccountingLine.getAmount());
        }
    
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            capitalAAssetTotals = capitalAAssetTotals.add(capitalAsset.getAmount());
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
