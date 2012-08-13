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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.integration.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * validate the capital accounting lines object subtypes
 */
public class CapitalAccountingLinesObjectSubtypeValidations extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAccountingLinesObjectSubtypeValidations.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return this.validateCapitalAccountingLinesObjectSubtypes(accountingDocumentForValidation);
    }

    protected boolean validateCapitalAccountingLinesObjectSubtypes(AccountingDocument accountingDocumentForValidation) {
        LOG.debug("validateCapitalAccountingLines - start");

        boolean isValid = true;
        
        if (accountingDocumentForValidation instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAccountingLinesDocumentBase capitalAccountingLinesDocumentBase = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;
        List <CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesDocumentBase.getCapitalAccountingLines();
        
        
        if (totalCapitalAccountLinesSelected(capitalAccountingLines) <= 1) {
            return false;
        }
        
        isValid &= hasDifferentObjectSubTypes(capitalAccountingLines);
        return isValid;
    }

    protected int totalCapitalAccountLinesSelected(List <CapitalAccountingLines> capitalAccountingLines) {
        int totalLinesSelected = 0;
        
        for (CapitalAccountingLines capitalLine : capitalAccountingLines) {
            if (capitalLine.isSelectLine()) {
                totalLinesSelected++; 
            }
        }
        
        return totalLinesSelected;
    }
    
    /**
     * This method determines whether or not an asset has different object sub type codes in its documents.
     * 
     * @return true when the asset has payments with object codes that point to different object sub type codes
     */
    public boolean hasDifferentObjectSubTypes(List<CapitalAccountingLines> capitalAccountingLines) {
        List<String> subTypes = new ArrayList<String>();
        List<String> objectSubTypeList = new ArrayList<String>();
        List<String> validObjectSubTypes = new ArrayList<String>();
        
        subTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(Asset.class, KFSParameterKeyConstants.CamParameterConstants.OBJECT_SUB_TYPE_GROUPS) );

        for (String subType : subTypes) {
            validObjectSubTypes.addAll(Arrays.asList(StringUtils.split(subType, ",")));
        }
        
        for (CapitalAccountingLines capitalAccountLine : capitalAccountingLines) {
            if (capitalAccountLine.isSelectLine() && !capitalAccountLine.isAmountDistributed()) {
                AccountingLine matchAccountingLine = findAccountingLine(capitalAccountLine);
                if (ObjectUtils.isNotNull(matchAccountingLine)) {
                    matchAccountingLine.refreshReferenceObject("objectCode");  
                }
                if ( ObjectUtils.isNotNull( matchAccountingLine.getObjectCode() ) ) {
                    if (validObjectSubTypes.contains(matchAccountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
                        if (!objectSubTypeList.isEmpty()) {
                            if (!objectSubTypeList.contains(matchAccountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
                                //different object subtypes
                                return true;
                            }
                        }
                        objectSubTypeList.add(matchAccountingLine.getObjectCode().getFinancialObjectSubTypeCode());
                    }
                }
            }
        }
        
        return false;
        
    }

    /**
     * Finds the accounting line that matches the capital accounting line.
     * @param capitalAccountLine
     * @return accounting line
     */
    protected AccountingLine findAccountingLine(CapitalAccountingLines capitalAccountingLine) {
        AccountingLine accountingLine = null;
        
        if (KFSConstants.SOURCE.equalsIgnoreCase(capitalAccountingLine.getLineType())) {
            List<SourceAccountingLine> sourceLines = accountingDocumentForValidation.getSourceAccountingLines();
            for (SourceAccountingLine line : sourceLines) {
                if (capitalAccountingLine.getChartOfAccountsCode().equals(line.getChartOfAccountsCode()) && 
                        capitalAccountingLine.getAccountNumber().equals(line.getAccountNumber()) &&
                        capitalAccountingLine.getFinancialObjectCode().equals(line.getFinancialObjectCode()) &&
                        capitalAccountingLine.getLineType().equalsIgnoreCase(KFSConstants.SOURCE)) {
                    return line;
                }
            }
        }
        else {
            List<TargetAccountingLine> targetLines = accountingDocumentForValidation.getTargetAccountingLines();
            for (TargetAccountingLine line : targetLines) {
                if (capitalAccountingLine.getChartOfAccountsCode().equals(line.getChartOfAccountsCode()) && 
                        capitalAccountingLine.getAccountNumber().equals(line.getAccountNumber()) &&
                        capitalAccountingLine.getFinancialObjectCode().equals(line.getFinancialObjectCode()) &&
                        capitalAccountingLine.getLineType().equalsIgnoreCase(KFSConstants.TARGET)) {
                    return line;
                }
            }
            
        }
        
        return accountingLine;
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
