/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentRequestNonZeroAccountingLineAmountValidation extends GenericValidation {

    private PurApItem itemForValidation;
    protected ParameterService parameterService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;        
        String status = ((PaymentRequestDocument)event.getDocument()).getStatusCode();

        //Do this for AFOA only
        if (StringUtils.equals(PaymentRequestStatuses.AWAITING_FISCAL_REVIEW, status)) {
            
            for (PurApAccountingLine acct : itemForValidation.getSourceAccountingLines()) {
                //if amount is null, throw an error.  If amount is zero, check system parameter and determine
                //if the line can be approved.
                if(ObjectUtils.isNull(acct.getAmount()) || (ObjectUtils.isNotNull(acct.getAmount()))){
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
        String approveZeroAmountLine = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.APPROVE_ACCOUNTING_LINES_WITH_ZERO_DOLLAR_AMOUNT_IND);
        
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
     * sets parameterService
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
