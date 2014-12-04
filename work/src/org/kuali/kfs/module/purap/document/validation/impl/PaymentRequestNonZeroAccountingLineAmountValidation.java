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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestNonZeroAccountingLineAmountValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    private PurApItem itemForValidation;
    private ParameterService parameterService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;        
        String status = ((PaymentRequestDocument)event.getDocument()).getApplicationDocumentStatus();

        AccountingDocument accountingDocument = (AccountingDocument)event.getDocument();
        this.setAccountingDocumentForValidation(accountingDocument);
        this.setDataDictionaryService(SpringContext.getBean(DataDictionaryService.class));
        
        //Do this for AFOA only
        if (StringUtils.equals(PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW, status)) {
            for (PurApAccountingLine acct : itemForValidation.getSourceAccountingLines()) {
                this.setAccountingLineForValidation(acct);
                final boolean lineIsAccessible =  lookupAccountingLineAuthorizer().hasEditPermissionOnAccountingLine(accountingDocument, acct, getAccountingLineCollectionProperty(), GlobalVariables.getUserSession().getPerson(), true);
                // if the current logged in user has edit permissions on this line then validate the line..
                if (lineIsAccessible) {
                    //if amount is null, throw an error.  If amount is zero, check system parameter and determine
                    //if the line can be approved.
                    if(ObjectUtils.isNull(acct.getAmount())){
                        GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_AMOUNT_INVALID, itemForValidation.getItemIdentifierString());
                        valid &= false;
                    }
                    else {
                        if (acct.getAmount().isZero()) {
                            if (!canApproveAccountingLinesWithZeroAmount()) {
                                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_AMOUNT_INVALID, itemForValidation.getItemIdentifierString());
                                valid &= false;
                            }
                        }
                    } 
                }
            }
        }
        
        return valid;
    }

    /**
     * checks if an accounting line with zero dollar amount can be approved.  This will check
     * the system parameter APPROVE_ACCOUNTING_LINES_WITH_ZERO_DOLLAR_AMOUNT_IND and determines if the
     * line can be approved or not.
     * 
     * @return true if the system parameter value is Y else returns N.
     */
    public boolean canApproveAccountingLinesWithZeroAmount() {
        boolean canApproveLine = false;
        
        // get parameter to see if accounting line with zero dollar amount can be approved.
        String approveZeroAmountLine = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.APPROVE_ACCOUNTING_LINES_WITH_ZERO_DOLLAR_AMOUNT_IND);
        
        if ("Y".equalsIgnoreCase(approveZeroAmountLine)) {
            return true;
        }
        
        return canApproveLine;
    }
    
    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }
    
    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService
     */
    
    public ParameterService getParameterService() {
        return parameterService;
    }

    /** 
     * Sets the parameterService attribute.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
