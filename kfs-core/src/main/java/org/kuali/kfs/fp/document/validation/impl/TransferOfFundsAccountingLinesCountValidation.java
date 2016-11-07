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

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_TOF_ACCOUNTING_LINES_COUNT_MULTIPLE;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validation for Transfer of Funds document that tests the number of document "From" accounting lines against the number of document "To" accounting lines. 
 */
public class TransferOfFundsAccountingLinesCountValidation extends GenericValidation {	
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    private ParameterService parameterService;
      
    /**
     * This is a helper method that wraps the accounting line count check. This check can be configured by updating the 
     * application parameter table that is associated with this check. See the document's specification for details.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return isAccountingLinesCountValid(accountingDocumentForValidation, KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.ALLOW_MANY_TO_MANY_TRANSFERS);
    }
        
   /**
    * This method checks the number of document "From" accounting lines against the number of document "To" accounting lines. 
    * Transfer transactions with multiple "From" accounting lines and multiple "To" accounting lines are not allowed.
    * This is to enable better tracking and matching of transfers by account.
    * 
    * @param tranDoc
    * @param componentClass component class for parameter retrieval
    * @param parameterName parameter name for parameter retrieval
    * @return True if many to many accounting line condition is allowed or no many to many accounting line condition exists; false otherwise.
    */
    protected boolean isAccountingLinesCountValid(AccountingDocument tranDoc, Class componentClass, String parameterName) {    	
        // don't need to continue of this if there's no parameter
        if (!getParameterService().parameterExists(componentClass, parameterName)) {
            return true;
        }
       
        // don't need to continue if parameter evaluates to true (many to many accounting lines are allowed)
        if (getParameterService().getParameterValueAsBoolean(componentClass, parameterName)) {
    	    return true;
        }
                              
        int sourceNumberOfAccountingLines = tranDoc.getSourceAccountingLines().size();
        int targetNumberOfAccountingLines = tranDoc.getTargetAccountingLines().size();
              
        //Count the line that is being validated, but only if the accounting line currently being validated does not yet have a sequence number
        if (ObjectUtils.isNull(accountingLineForValidation.getSequenceNumber())) {
            if (accountingLineForValidation.isSourceAccountingLine()) {
                sourceNumberOfAccountingLines = sourceNumberOfAccountingLines + 1;
            }
            else {
                targetNumberOfAccountingLines = targetNumberOfAccountingLines + 1;
            }
        }
                         
        // check that this is not a multiple-to-multiple Transfer transaction
        boolean isValid = true;
        
        if (sourceNumberOfAccountingLines > 1 && targetNumberOfAccountingLines > 1) {
            isValid = false;
            GlobalVariables.getMessageMap().putError(getGroupName(), ERROR_DOCUMENT_TOF_ACCOUNTING_LINES_COUNT_MULTIPLE); 
        }      
        
        return isValid;
    }
    
    /**
     * Returns the name of the accounting line group 
     * @return the name of the accounting line group 
     */
    protected String getGroupName() {
        return (accountingLineForValidation.isSourceAccountingLine() ? KFSConstants.SOURCE_ACCOUNTING_LINES_GROUP_NAME : KFSConstants.TARGET_ACCOUNTING_LINES_GROUP_NAME);
    }
    
    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
    
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }   
   
}
