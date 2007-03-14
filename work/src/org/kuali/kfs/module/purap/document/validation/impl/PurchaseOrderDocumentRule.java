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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public class PurchaseOrderDocumentRule extends PurchasingDocumentRuleBase {
    
    
    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(poDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(poDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) approveEvent.getDocument();
        return isValid &= processValidation(poDocument);
    }

    private boolean processValidation(PurchaseOrderDocument document) {
        boolean valid = true;
        valid &= processVendorStipulationValidation(document);
        valid &= processDocumentOverviewValidation(document);
        valid &= processVendorValidation(document);
        valid &= processItemValidation(document);
        valid &= processPaymentInfoValidation(document);
        valid &= processDeliveryValidation(document);
        valid &= processAdditionalValidation(document);
        return valid;
    }
    
    boolean processVendorStipulationValidation(PurchaseOrderDocument document) {
        boolean valid = true;
        List<PurchaseOrderVendorStipulation> stipulations = document.getPurchaseOrderVendorStipulations();

        for (int i = 0; i < stipulations.size(); i++) {
            PurchaseOrderVendorStipulation stipulation = stipulations.get(i);

            if (StringUtils.isBlank(stipulation.getVendorStipulationDescription())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION + "[" + i + "]." + PurapPropertyConstants.VENDOR_STIPULATION_DESCRIPTION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);

                valid = false;
            }
        }
        return valid;
    }

    boolean processVendorValidation(PurchaseOrderDocument document) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean valid = super.processVendorValidation(document);
        if (StringUtils.isBlank(document.getVendorCountryCode())) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.VENDOR_COUNTRY_CODE, KeyConstants.ERROR_REQUIRED);
        } else if (document.getVendorCountryCode().equals(Constants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(document.getVendorStateCode())   ) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_STATE_CODE, KeyConstants.ERROR_REQUIRED_FOR_US);    
            }
            ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
            if (StringUtils.isBlank(document.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, KeyConstants.ERROR_REQUIRED_FOR_US);   
            } else if (!zipPattern.matches(document.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
            }
        } 
        return valid;
    }
    
    boolean processPaymentInfoValidation(RequisitionDocument document) {
        boolean valid = super.processPaymentInfoValidation(document);
        return valid;
    }
    
    boolean processAdditionalValidation(PurchasingDocument document) {
        boolean valid = super.processAdditionalValidation(document);
        valid = validateFaxNumberIfTransmissionTypeIsFax(document);
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
    boolean validateFaxNumberIfTransmissionTypeIsFax(PurchasingDocument document) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(document.getVendorHeaderGeneratedIdentifier()) &&
              document.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX)) {
            if (ObjectUtils.isNull(document.getVendorFaxNumber()) ||
                    ! SpringServiceLocator.getPhoneNumberService().isValidPhoneNumber(document.getVendorFaxNumber())  ) {
                  GlobalVariables.getErrorMap().putError(PurapPropertyConstants.REQUISITION_VENDOR_FAX_NUMBER, 
                  PurapKeyConstants.ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE);
                valid &= false;
            }
        } 
        return valid;
    }

}
