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
                    KFSParameterKeyConstants.InvalidSubFundsByObjCdParameterConstant.INVALID_SUBFUND_GROUPS_BY_OBJ_TYPE)){
                
                ParameterEvaluator incomeObjectEvaluator = parameterEvaluatorService.getParameterEvaluator(accountingDocumentForValidation.getDocumentClassForAccountingLineValueAllowedValidation(),
                    KFSParameterKeyConstants.InvalidSubFundsByObjCdParameterConstant.INVALID_SUBFUND_GROUPS_BY_OBJ_TYPE,
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
