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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.RequisitionDocument;

public class RequisitionDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        RequisitionDocument reqDocument = (RequisitionDocument) document;
        return isValid &= processValidation(reqDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        RequisitionDocument reqDocument = (RequisitionDocument) document;
        return isValid &= processValidation(reqDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        RequisitionDocument reqDocument = (RequisitionDocument) approveEvent.getDocument();
        return isValid &= processValidation(reqDocument);
    }

    private boolean processValidation(RequisitionDocument document) {
        boolean valid = true;
        valid &= processDocumentOverviewValidation(document);
        valid &= processVendorValidation(document);
        valid &= processItemValidation(document);
        valid &= processPaymentInfoValidation(document);
        valid &= processDeliveryValidation(document);
        valid &= processAdditionalValidation(document);
        return valid;
    }
    
    /**
     * 
     * This method performs validations for the fields in vendor tab.
     * The business rules to be validated are:
     * 1.  If this is a standard order requisition (not B2B), then if Country is United 
     *     States and the postal code is required and if zip code is entered, it should 
     *     be a valid US Zip code. (format)
     * 2.  If this is a standard order requisition (not a B2B requisition), then if 
     *     the fax number is entered, it should be a valid fax number. (format) 
     *     
     * @param document The requisition document object whose vendor tab is to be validated
     * 
     * @return true if it passes vendor validation and false otherwise.
     */
    boolean processVendorValidation(RequisitionDocument document) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean valid = super.processVendorValidation(document);
        if (document.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.STANDARD_ORDER)) { 
            if (!StringUtils.isBlank(document.getVendorCountryCode()) &&
                document.getVendorCountryCode().equals(Constants.COUNTRY_CODE_UNITED_STATES) && 
                !StringUtils.isBlank(document.getVendorPostalCode())) {
                ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
                if (!zipPattern.matches(document.getVendorPostalCode())) {
                    valid = false;
                    errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
                }
            }
        }
        return valid;
    }

    boolean processItemValidation(RequisitionDocument document) {
        boolean valid = super.processItemValidation(document);
        // TODO code validation
        return valid;
    }

    boolean processPaymentInfoValidation(RequisitionDocument document) {
        boolean valid = super.processPaymentInfoValidation(document);
 
        return valid;
    }

    boolean processDeliveryValidation(RequisitionDocument document) {
        boolean valid = super.processDeliveryValidation(document);
        // TODO code validation
        return valid;
    }

    boolean processAdditionalValidation(RequisitionDocument document) {
        boolean valid = super.processAdditionalValidation(document);
        return valid;
    }
    
    /**
     * 
     * This method validates that: 
     * 1. If the purchaseOrderBegDate is entered then the purchaseOrderEndDate is also entered, and vice versa. 
     * 2. If both dates are entered, the purchaseOrderBegDate is before the purchaseOrderEndDate.
     *
     * The date fields are required so we should know that we have valid dates.
     * 
     * @return True if the beginning date is before the end date. False otherwise.
     */
    boolean validatePOBeginEndDates(RequisitionDocument document) {
        boolean valid = true;
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
        if (valid && ObjectUtils.isNotNull(document.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(document.getPurchaseOrderEndDate())) {
            if (document.getPurchaseOrderBeginDate().after(document.getPurchaseOrderEndDate())) {
                GlobalVariables.getErrorMap().putError( PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
                valid &= false;
            }
        }
  
        return valid;
    }
    
}
