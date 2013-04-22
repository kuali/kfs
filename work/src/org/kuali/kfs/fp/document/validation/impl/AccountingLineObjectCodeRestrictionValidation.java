/*
 * Copyright 2013 The Kuali Foundation.
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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;



/**
 * determine whether the Object code values associated with accounting lines are valid for the given document
 */
public class AccountingLineObjectCodeRestrictionValidation extends GenericValidation {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineObjectCodeRestrictionValidation.class);
    
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    private ParameterService parameterService; 
    private DataDictionaryService dataDictionaryService;
    private ParameterEvaluatorService parameterEvaluatorService;
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent) 
     */
    
    public boolean validate(AttributedDocumentEvent event) { 
        LOG.info("start income object code for subfund validation...");  
        
        ObjectCode objectCode = accountingLineForValidation.getObjectCode();
        Account account = accountingLineForValidation.getAccount();
        
        if (ObjectUtils.isNotNull(objectCode) && ObjectUtils.isNotNull(account)){
            
            String objectTypeCode =  objectCode.getFinancialObjectTypeCode(); 
            String subFundGroupCode = account.getSubFundGroupCode(); 
            
            
            if(parameterService.parameterExists(accountingDocumentForValidation.getDocumentClassForAccountingLineValueAllowedValidation(), 
                    KFSParameterKeyConstants.InvalidSubFundsByObjCdParameterConstant.INVALID_SUBFUND_GROUP_BY_OBJ_TYPE)){
                
                ParameterEvaluator incomeObjectEvaluator = parameterEvaluatorService.getParameterEvaluator(accountingDocumentForValidation.getDocumentClassForAccountingLineValueAllowedValidation(),
                    KFSParameterKeyConstants.InvalidSubFundsByObjCdParameterConstant.INVALID_SUBFUND_GROUP_BY_OBJ_TYPE,
                    objectTypeCode, subFundGroupCode);
                
                if(!incomeObjectEvaluator.evaluationSucceeds()){
                                 
                   String documentLabel = dataDictionaryService.getDocumentLabelByClass(accountingDocumentForValidation.getClass());
                    
                   GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS,
                            KFSKeyConstants.ERROR_INVALID_INCOME_OBJCODE_SUB_FUND,documentLabel,accountingLineForValidation.getAccountKey());
                    
                   LOG.error(GlobalVariables.getMessageMap().getErrorMessages());
                   return false;
                }                         
            }            
        }         
        return true;        
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * 
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
   
    /**
     * Sets the parameterEvaluatorService attribute value.
     * 
     * @param parameterEvaluatorService The parameterEvaluatorService to set.
     */
    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    
}
