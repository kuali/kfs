/*
 * Copyright 2007 The Kuali Foundation
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
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business Prerules applicable to purchase order document.
 */
public class PurchaseOrderDocumentPreRules extends PurchasingDocumentPreRulesBase {

    /**
     * Overrides the method in PromptBeforeValidationBase to also invoke the confirmNotToExceedOverride if the PromptBeforeValidationEvent is
     * blank and the question matches with the OverrideNotToExceed
     * 
     * @param document The purchase order document upon which we're performing the prerules logic.
     * @return boolean true if it passes the pre rules conditions.
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {

        boolean preRulesOK = true;

        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument) document;

        if (StringUtils.isBlank(event.getQuestionContext()) || StringUtils.equals(question, PurapConstants.PO_OVERRIDE_NOT_TO_EXCEED_QUESTION)) {
            preRulesOK &= confirmNotToExceedOverride(purchaseOrderDocument);
        }
        
        if (isDocumentInStateToReceiveNextFyWarning(purchaseOrderDocument) && 
                (StringUtils.isBlank(event.getQuestionContext()) || StringUtils.equals(question, PurapConstants.PO_NEXT_FY_WARNING))) {
            preRulesOK &= confirmNextFYPriorToApoAllowedDate(purchaseOrderDocument);
        }
        
        if (!purchaseOrderDocument.isUseTaxIndicator()){
            preRulesOK &= checkForTaxRecalculation(purchaseOrderDocument);
        }
        
        return preRulesOK;
    }
    
    /**
     * Give next FY warning if the PO status is "In Process" or "Awaiting Purchasing Review"
     * 
     * @param poDocument
     * @return boolean
     */
    protected boolean isDocumentInStateToReceiveNextFyWarning(PurchaseOrderDocument poDocument){
        return (PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus()) ||
                PurapConstants.PurchaseOrderStatuses.APPDOC_AWAIT_PURCHASING_REVIEW.equals(poDocument.getApplicationDocumentStatus()));
    }

    /**
     * Checks whether the 'Not-to-exceed' amount has been exceeded by the purchase order total dollar limit. If so, it
     * prompts the user for confirmation.
     * 
     * @param purchaseOrderDocument The current PurchaseOrderDocument
     * @return True if the 'Not-to-exceed' amount is to be overridden or if the total dollar amount is less than the purchase order
     *         total dollar limit.
     */
    protected boolean confirmNotToExceedOverride(PurchaseOrderDocument purchaseOrderDocument) {

        // If the total exceeds the limit, ask for confirmation.
        if (!validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(purchaseOrderDocument)) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_OVERRIDE_NOT_TO_EXCEED);

            boolean confirmOverride = super.askOrAnalyzeYesNoQuestion(PurapConstants.PO_OVERRIDE_NOT_TO_EXCEED_QUESTION, questionText);

            // Set a marker to record that this method has been used.
            if (confirmOverride && StringUtils.isBlank(event.getQuestionContext())) {
                event.setQuestionContext(PurapConstants.PO_OVERRIDE_NOT_TO_EXCEED_QUESTION);
            }

            if (!confirmOverride) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);

                return false;
            }
        }

        return true;
    }

    /**
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @param purDocument The purchase order document to be validated.
     * @return True if the TotalDollarAmount is less than the PurchaseOrderTotalLimit. False otherwise.
     */
    public boolean validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderTotalLimit()) && ObjectUtils.isNotNull(((AmountTotaling) purDocument).getTotalDollarAmount())) {
            KualiDecimal totalAmount = ((AmountTotaling) purDocument).getTotalDollarAmount();
            if (((AmountTotaling) purDocument).getTotalDollarAmount().isGreaterThan(purDocument.getPurchaseOrderTotalLimit())) {
                valid &= false;
                KNSGlobalVariables.getMessageList().add(PurapKeyConstants.PO_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT);
            }
        }

        return valid;
    }

    /**
     * If the PO is set to encumber in the next fiscal year and the PO is created before the APO allowed date, then give the user a
     * warning that this might be a mistake. Prompt the user for confirmation that the year is set correctly both at submit and upon
     * approval at the Purchasing Internal Review route level.
     * 
     * @param purchaseOrderDocument The current PurchaseOrderDocument
     * @return True if the user wants to continue with PO routing; False to send the user back to the PO for editing.
     */
    protected boolean confirmNextFYPriorToApoAllowedDate(PurchaseOrderDocument poDocument) {

        // If the FY is set to NEXT and today is not within APO allowed range, ask for confirmation to continue
        if (poDocument.isPostingYearNext() && !SpringContext.getBean(PurapService.class).isTodayWithinApoAllowedRange()) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.WARNING_PURCHASE_ORDER_ENCUMBER_NEXT_FY);
            boolean confirmOverride = super.askOrAnalyzeYesNoQuestion(PurapConstants.PO_NEXT_FY_WARNING, questionText);

            // Set a marker to record that this method has been used.
            if (confirmOverride && StringUtils.isBlank(event.getQuestionContext())) {
                event.setQuestionContext(PurapConstants.PO_NEXT_FY_WARNING);
}
            if (!confirmOverride) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean checkCAMSWarningStatus(PurchasingAccountsPayableDocument purapDocument) {
        return PurapConstants.CAMSWarningStatuses.PURCHASEORDER_STATUS_WARNING_NO_CAMS_DATA.contains(purapDocument.getApplicationDocumentStatus());
    }
    
}
