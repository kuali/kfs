/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/validation/impl/AssignContractManagerDocumentRule.java,v $
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
import org.kuali.core.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
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
import org.kuali.module.purap.util.PhoneNumberUtils;

public class AssignContractManagerDocumentRule extends PurchasingDocumentRuleBase {

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
        if (document.getRequisitionSourceCode().equals(PurapConstants.REQ_SOURCE_STANDARD_ORDER)) { 
            if (!StringUtils.isBlank(document.getVendorCountryCode()) &&
                document.getVendorCountryCode().equals(Constants.COUNTRY_CODE_UNITED_STATES) && 
                !StringUtils.isBlank(document.getVendorPostalCode())) {
                ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
                if (!zipPattern.matches(document.getVendorPostalCode())) {
                    valid = false;
                    errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
                }
            }
            if (!StringUtils.isBlank(document.getVendorFaxNumber())) {
                PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
                if (!phonePattern.matches(document.getVendorFaxNumber())) {
                    valid = false;
                    errorMap.putError(Constants.DOCUMENT_PROPERTY_NAME + "." + PurapPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
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
                if (document.getPurchaseOrderBeginDate().after(document.getPurchaseOrderEndDate())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
                    valid &= false;
                }
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

    boolean processDeliveryValidation(RequisitionDocument document) {
        boolean valid = super.processDeliveryValidation(document);
        // TODO code validation
        return valid;
    }

    boolean processAdditionalValidation(RequisitionDocument document) {
        boolean valid = super.processAdditionalValidation(document);
        // TODO code validation
        validateReqTotAmtIsLessThanPOTotLimit(document);
        validateFaxNumberIfTransmissionTypeIsFax(document);
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

    /**
     * Validate that if the PO Total Limit is not null, 
     *   then the Requisition Total Amount cannot be greater than the PO Total Limit. 
     * 
     * @return True if the Requisition Total Amount is less than the PO Total Limit. False otherwise.
     */
    boolean validateReqTotAmtIsLessThanPOTotLimit(RequisitionDocument document) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(document.getPurchaseOrderTotalLimit()) &&
              ObjectUtils.isNotNull(document.getTotalDollarAmount())) {
            if (document.getTotalDollarAmount().isGreaterThan(document.getPurchaseOrderTotalLimit())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_TOTAL_LIMIT, 
                  PurapKeyConstants.REQ_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT);
                valid &= false;
            }
        } 
        return valid;
    }
 
    /**
     * Validate that if Vendor Id (VendorHeaderGeneratedId) is not empty, and tranmission method is fax, 
     *   vendor fax number cannot be empty and must be valid. In other words: allow reqs to not force fax # 
     *   when transmission type is fax if vendor id is empty because it will not be allowed to become an APO 
     *   and it will be forced on the PO. 
     * 
     * @return False if VendorHeaderGeneratedId is not empty, tranmission method is fax, and
     *   VendorFaxNumber is empty or invalid. True otherwise.
     */
    boolean validateFaxNumberIfTransmissionTypeIsFax(RequisitionDocument document) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(document.getVendorHeaderGeneratedIdentifier()) &&
              document.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.PO_TRANSMISSION_METHOD_FAX)) {
            if (ObjectUtils.isNull(document.getVendorFaxNumber()) ||
                  ! PhoneNumberUtils.isValidPhoneNumber(document.getVendorFaxNumber())  ) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.REQUISITION_VENDOR_FAX_NUMBER, 
                  PurapKeyConstants.ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE);
                valid &= false;
            }
        } 
        return valid;
    }
    
}
