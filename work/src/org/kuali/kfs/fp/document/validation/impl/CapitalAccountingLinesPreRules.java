/*
 * Copyright 2008-2009 The Kuali Foundation
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
import org.kuali.kfs.integration.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Performs warning checks and prompts for capital accounting lines object sub type matching
 */
public class CapitalAccountingLinesPreRules extends PromptBeforeValidationBase {

    /**
     * Default Constructor
     */
    public CapitalAccountingLinesPreRules() {
        super();
    }
    
    /**
     * Main hook point to perform rules check.
     * 
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        AccountingDocument accountingDocument = (AccountingDocument) document;
        List<AccountingLine> accountingLines = accountingDocument.getSourceAccountingLines();
        accountingLines.addAll(accountingDocument.getTargetAccountingLines());
        
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) document;
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();        

        if (hasDifferentObjectSubTypes(capitalAccountingLines, accountingLines)) {
            if (!isOkHavingDifferentObjectSubTypes()) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * This method determines whether or not an asset has different object sub type codes in its documents.
     * 
     * @return true when the asset has payments with object codes that point to different object sub type codes
     */
    public boolean hasDifferentObjectSubTypes(List<CapitalAccountingLines> capitalAccountingLines, List<AccountingLine> accountingLines) {
        List<String> subTypes = new ArrayList<String>();
        List<String> objectSubTypeList = new ArrayList<String>();
        List<String> validObjectSubTypes = new ArrayList<String>();
        
        subTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(Asset.class, KFSParameterKeyConstants.CamParameterConstants.OBJECT_SUB_TYPE_GROUPS) );

        for (String subType : subTypes) {
            validObjectSubTypes = Arrays.asList(StringUtils.split(subType, ","));
        }
        
        for (CapitalAccountingLines capitalAccountLine : capitalAccountingLines) {
            if (capitalAccountLine.isSelectLine() && capitalAccountLine.isAmountDistributed()) {
                AccountingLine matchAccountingLine = findAccountingLine(accountingLines, capitalAccountLine);
                if (ObjectUtils.isNotNull(matchAccountingLine)) {
                    matchAccountingLine.refreshReferenceObject("objectCode");  
                }
                if (validObjectSubTypes.contains(matchAccountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
                    if (!objectSubTypeList.contains(matchAccountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
                        //different object subtypes
                        return true;
                    }
                    objectSubTypeList.add(matchAccountingLine.getObjectCode().getFinancialObjectSubTypeCode());
                }
            }
        }
        
        if (objectSubTypeList.isEmpty()) {
            return false;
        }
        
        return false;
        
    }

    /**
     * Finds the accounting line that matches the capital accounting line.
     * @param capitalAccountLine
     * @return accounting line
     */
    protected AccountingLine findAccountingLine(List<AccountingLine> accountingLines, CapitalAccountingLines capitalAccountingLine) {
        AccountingLine accountingLine = null;
        
        for (AccountingLine line : accountingLines) {
            if (capitalAccountingLine.getChartOfAccountsCode().equals(line.getChartOfAccountsCode()) && 
                    capitalAccountingLine.getAccountNumber().equals(line.getAccountNumber()) &&
                    capitalAccountingLine.getFinancialObjectCode().equals(line.getFinancialObjectCode()) &&
                    capitalAccountingLine.getLineType().equalsIgnoreCase(line instanceof SourceAccountingLine ? KFSConstants.SOURCE : KFSConstants.TARGET)) {
                return line;
            }
        }
        
        return accountingLine;
    }
    
    protected boolean isOkHavingDifferentObjectSubTypes() {
        String parameterDetail = "(module:" + KRADServiceLocatorWeb.getKualiModuleService().getNamespaceCode(Asset.class) + "/component:" + Asset.class.getSimpleName() + ")";
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        String continueQuestion = kualiConfiguration.getPropertyValueAsString(KFSKeyConstants.CONTINUE_QUESTION);
        String warningMessage = kualiConfiguration.getPropertyValueAsString(KFSKeyConstants.WARNING_NOT_SAME_OBJECT_SUB_TYPES) + " " + KFSParameterKeyConstants.CamParameterConstants.OBJECT_SUB_TYPE_GROUPS + " " + parameterDetail + ". " + continueQuestion;
        return super.askOrAnalyzeYesNoQuestion(KFSConstants.FinancialDocumentTypeCodes.ASSET_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION, warningMessage);
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }
}
