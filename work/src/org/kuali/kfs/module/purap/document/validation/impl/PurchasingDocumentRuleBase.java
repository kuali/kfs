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
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.vendor.VendorPropertyConstants;

public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase {

    /**
     * Tabs included on Purchasing Documents are:
     *   Payment Info
     *   Delivery
     *   Additional
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processPaymentInfoValidation((PurchasingDocument)purapDocument);
        valid &= processDeliveryValidation((PurchasingDocument)purapDocument);
        valid &= processAdditionalValidation((PurchasingDocument)purapDocument);
        return valid;
    }

    /**
     * This method performs any validation for the Payment Info tab.
     * 
     * @param purDocument
     * @return
     */
    public boolean processPaymentInfoValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        valid &= checkBeginDateBeforeEndDate(purDocument);

        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) || ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
            if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNull(purDocument.getPurchaseOrderEndDate())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE);
                valid &= false;
            }
            else {
                if (ObjectUtils.isNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
                    valid &= false;
                }
            }
        }
        if (valid && ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
            if (ObjectUtils.isNull(purDocument.getRecurringPaymentTypeCode())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE, PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE);

                valid &= false;
            }
        }
        else if (ObjectUtils.isNotNull(purDocument.getRecurringPaymentTypeCode())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE);
            valid &= false;
        }
        return valid;
    }
    
    /**
     * This method performs any validation for the Delivery tab.
     * 
     * @param purDocument
     * @return
     */
    public boolean processDeliveryValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    /**
     * This method performs any validation for the Additional tab.
     * 
     * @param purDocument
     * @return
     */
    public boolean processAdditionalValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        valid = validateTotDollarAmtIsLessThanPOTotLimit(purDocument);
        return valid;
    }

    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processVendorValidation(purapDocument);
        PurchasingDocument purDocument = (PurchasingDocument)purapDocument;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        if (!purDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {
            //TODO check this; I think we're only supposed to be validation the fax number format if the transmission type is FAX and the vendor ids are null (hjs)
            if (StringUtils.isNotBlank(purDocument.getVendorFaxNumber())) {
                PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
                if (!phonePattern.matches(purDocument.getVendorFaxNumber())) {
                    valid &= false;
                    errorMap.putError(Constants.DOCUMENT_PROPERTY_NAME + "." + VendorPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
                }
            }
        }
        return valid;
    }


    /**
     * This method is the implementation of the rule that if a document has a recurring payment
     * begin date and end date, the begin date should come before the end date.  In EPIC, we needed
     * to play around with this order if the fiscal year is the next fiscal year, since we were 
     * dealing just with month and day, but we don't need to do that here; we're dealing with the
     * whole Date object.
     * 
     * @param purDocument
     * @return
     */
    private boolean checkBeginDateBeforeEndDate(PurchasingDocument purDocument) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringServiceLocator.getDateTimeService();
        int currentFiscalYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear();

        Date beginDate = purDocument.getPurchaseOrderBeginDate();
        Date endDate = purDocument.getPurchaseOrderEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (beginDate.after(endDate)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
            }
        }
        return valid;
    }
    
    /**
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @return True if the TotalDollarAmount is less than the PurchaseOrderTotalLimit. False otherwise.
     */
    private boolean validateTotDollarAmtIsLessThanPOTotLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderTotalLimit()) && 
                ObjectUtils.isNotNull(((AmountTotaling) purDocument).getTotalDollarAmount())) {
            if (((AmountTotaling) purDocument).getTotalDollarAmount().isGreaterThan(purDocument.getPurchaseOrderTotalLimit())) {
                if (purDocument instanceof PurchaseOrderDocument) {
                    // TODO: issue a warning here.
                }
                if (purDocument instanceof RequisitionDocument) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_TOTAL_LIMIT, PurapKeyConstants.REQ_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT);
                    valid &= false;
                }
            }
        }
        return valid;
    }

}