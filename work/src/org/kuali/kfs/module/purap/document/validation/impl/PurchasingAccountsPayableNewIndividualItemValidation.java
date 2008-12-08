/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurchasingAccountsPayableNewIndividualItemValidation extends GenericValidation {

    private ParameterService parameterService;
    private PurApItem itemForValidation;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) event.getDocument();
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];
        boolean valid = true;
        if (itemForValidation.getItemType().isAdditionalChargeIndicator()) {
            valid &= validateBelowTheLineValues(documentType, itemForValidation);
            return valid;
        }
        return success;
    }
    
    /**
     * Performs validations for below the line items. If the unit price is zero, and the system parameter indicates that the item
     * should not allow zero, then the validation fails. If the unit price is positive and the system parameter indicates that the
     * item should not allow positive values, then the validation fails. If the unit price is negative and the system parameter
     * indicates that the item should not allow negative values, then the validation fails. If the unit price is entered and is not
     * zero and the item description is empty and the system parameter indicates that the item requires user to enter description,
     * then the validation fails.
     * 
     * @param documentType The type of the PurchasingAccountsPayable document to be validated.
     * @param item The item to be validated.
     * @return boolean true if it passes the validation.
     */
    protected boolean validateBelowTheLineValues(String documentType, PurApItem item) {
        boolean valid = true;
        try {
            if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isZero()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_ZERO) && !parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_ZERO, item.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "zero");
                }
            }
            else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isPositive()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE) && !parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE, item.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "positive");
                }
            }
            else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNegative()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_NEGATIVE) && !parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_NEGATIVE, item.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "negative");
                }
            }
            if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNonZero() && StringUtils.isEmpty(item.getItemDescription())) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION) && parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION, item.getItemTypeCode()).evaluationSucceeds()) {
                    // if
                    // (parameterService.getIndicatorParameter(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)),
                    // PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION)) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, "The item description of " + item.getItemType().getItemTypeDescription(), "empty");
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("The valideBelowTheLineValues of PurchasingAccountsPayableDocumentRuleBase was unable to resolve a document type class: " + PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType), e);
        }

        return valid;
    }
        
    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }    


}
