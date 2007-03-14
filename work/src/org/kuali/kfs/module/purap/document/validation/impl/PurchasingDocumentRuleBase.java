/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.rules;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase {

    boolean processDocumentOverviewValidation(PurchasingDocument document) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }
    
    boolean processAdditionalValidation(PurchasingDocument document) {
        boolean valid = true;
        valid = validateTotDollarAmtIsLessThanPOTotLimit(document);
        return valid;
    }

    boolean processVendorValidation(PurchasingDocument document) {
        boolean valid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        if( !document.getRequisitionSourceCode().equals( PurapConstants.RequisitionSources.B2B ) ) {
            if( StringUtils.isNotBlank(document.getVendorFaxNumber()) ) {
                PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
                if( !phonePattern.matches(document.getVendorFaxNumber()) ) {
                    valid &= false;
                    errorMap.putError(Constants.DOCUMENT_PROPERTY_NAME + "." + PurapPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
                }
            }
        }
        return valid;
    }

    boolean processItemValidation(PurchasingDocument document) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    boolean processPaymentInfoValidation(PurchasingDocument document) {
        boolean valid = true;
        valid &= checkBeginDateBeforeEndDate( document );
        
        if (ObjectUtils.isNotNull(document.getPurchaseOrderBeginDate()) ||
                ObjectUtils.isNotNull(document.getPurchaseOrderEndDate())) {
            if (ObjectUtils.isNotNull(document.getPurchaseOrderBeginDate()) && ObjectUtils.isNull(document.getPurchaseOrderEndDate())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE);
                    valid &= false;
            } 
            else {
                if (ObjectUtils.isNull(document.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(document.getPurchaseOrderEndDate())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
                    valid &= false;
                }
            }
        }   
        if (valid && ObjectUtils.isNotNull(document.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(document.getPurchaseOrderEndDate())) {
                if (ObjectUtils.isNull(document.getRecurringPaymentTypeCode())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE, PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE);
                    
                    valid &= false;
                }
        } else if (ObjectUtils.isNotNull(document.getRecurringPaymentTypeCode())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE);
                valid &= false; 
        }
        
        
        return valid;
    }

    boolean processDeliveryValidation(PurchasingDocument document) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    /**
     * This method is the implementation of the rule that if a document has a recurring payment
     * begin date and end date, the begin date should come before the end date.  In EPIC, we needed
     * to play around with this order if the fiscal year is the next fiscal year, since we were 
     * dealing just with month and day, but we don't need to do that here; we're dealing with the
     * whole Date object.
     * 
     * @param document
     * @return
     */
    boolean checkBeginDateBeforeEndDate(PurchasingDocument document) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringServiceLocator.getDateTimeService();
        int currentFiscalYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();   

        Date beginDate = document.getPurchaseOrderBeginDate();
        Date endDate = document.getPurchaseOrderEndDate();
        if( ObjectUtils.isNotNull( beginDate ) && ObjectUtils.isNotNull( endDate ) ) {
            if( beginDate.after( endDate ) ) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
            }
        }
        return valid;
    }
    
    /**
     * Validate that if the PurchaseOrderTotalLimit is not null 
     *   then the TotalDollarAmount cannot be greater than the PurchaseOrderTotalLimit. 
     * 
     * @return True if the TotalDollarAmount is less than the PurchaseOrderTotalLimit. False otherwise.
     */
    boolean validateTotDollarAmtIsLessThanPOTotLimit(PurchasingDocument document) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(document.getPurchaseOrderTotalLimit()) &&
              ObjectUtils.isNotNull(((AmountTotaling) document).getTotalDollarAmount())) {
            if (((AmountTotaling) document).getTotalDollarAmount().isGreaterThan(document.getPurchaseOrderTotalLimit())) {
                if (document instanceof PurchaseOrderDocument) {
                    // TODO: issue a warning here.
                }
                if (document instanceof RequisitionDocument) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_TOTAL_LIMIT, 
                            PurapKeyConstants.REQ_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT);
                          valid &= false;
                }
            }
        } 
        return valid;
    }

}