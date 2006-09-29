/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.purap.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.RequisitionDocument;

public class RequisitionDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        RequisitionDocument reqDocument = (RequisitionDocument) document;
        return isValid &= processValidation(reqDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        RequisitionDocument reqDocument = (RequisitionDocument) document;
        return isValid &= processValidation(reqDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
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
    
    boolean processVendorValidation(RequisitionDocument document) {
        boolean valid = super.processVendorValidation(document);
        // TODO code validation
        return valid;
    }

    boolean processItemValidation(RequisitionDocument document) {
        boolean valid = super.processItemValidation(document);
        // TODO code validation
        return valid;
    }

    boolean processPaymentInfoValidation(RequisitionDocument document) {
        boolean valid = super.processPaymentInfoValidation(document);
        // TODO code validation

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
