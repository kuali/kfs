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
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.Constants;
import org.kuali.kfs.KeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;

public class PurchaseOrderDocumentRule extends PurchasingDocumentRuleBase {
    
    /**
     * Tabs included on Purchase Order Documents are:
     *   Stipulation
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processVendorStipulationValidation((PurchaseOrderDocument)purapDocument);
        return valid;
    }
    

    /**
     * This method performs any validation for the Stipulation tab.
     * 
     * @param poDocument
     * @return
     */
    public boolean processVendorStipulationValidation(PurchaseOrderDocument poDocument) {
        boolean valid = true;
        List<PurchaseOrderVendorStipulation> stipulations = poDocument.getPurchaseOrderVendorStipulations();
        for (int i = 0; i < stipulations.size(); i++) {
            PurchaseOrderVendorStipulation stipulation = stipulations.get(i);
            if (StringUtils.isBlank(stipulation.getVendorStipulationDescription())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION + "[" + i + "]." + PurapPropertyConstants.VENDOR_STIPULATION_DESCRIPTION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean valid = super.processVendorValidation(purapDocument);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) purapDocument;
        if (StringUtils.isBlank(poDocument.getVendorCountryCode())) {
            //TODO can't this be done by the data dictionary?
            valid = false;
            errorMap.putError(PurapPropertyConstants.VENDOR_COUNTRY_CODE, KeyConstants.ERROR_REQUIRED);
        }
        else if (poDocument.getVendorCountryCode().equals(Constants.COUNTRY_CODE_UNITED_STATES)) {
            if (StringUtils.isBlank(poDocument.getVendorStateCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_STATE_CODE, KeyConstants.ERROR_REQUIRED_FOR_US);
            }
            ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
            if (StringUtils.isBlank(poDocument.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, KeyConstants.ERROR_REQUIRED_FOR_US);
            }
            else if (!zipPattern.matches(poDocument.getVendorPostalCode())) {
                valid = false;
                errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
            }
        }
        return valid;
    }

    @Override
    public boolean processAdditionalValidation(PurchasingDocument purDocument) {
        boolean valid = super.processAdditionalValidation(purDocument);
        valid = validateFaxNumberIfTransmissionTypeIsFax(purDocument);
        return valid;
    }

    //TODO check comments; mentions REQ, but this class performs only PO validation
    /**
     * Validate that if Vendor Id (VendorHeaderGeneratedId) is not empty, and tranmission method is fax, 
     *   vendor fax number cannot be empty and must be valid. In other words: allow reqs to not force fax # 
     *   when transmission type is fax if vendor id is empty because it will not be allowed to become an APO 
     *   and it will be forced on the PO. 
     * 
     * @return False if VendorHeaderGeneratedId is not empty, tranmission method is fax, and
     *   VendorFaxNumber is empty or invalid. True otherwise.
     */
    private boolean validateFaxNumberIfTransmissionTypeIsFax(PurchasingDocument purDocument) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(purDocument.getVendorHeaderGeneratedIdentifier()) &&
              purDocument.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX)) {
            if (ObjectUtils.isNull(purDocument.getVendorFaxNumber()) ||
                    ! SpringServiceLocator.getPhoneNumberService().isValidPhoneNumber(purDocument.getVendorFaxNumber())  ) {
                  GlobalVariables.getErrorMap().putError(PurapPropertyConstants.REQUISITION_VENDOR_FAX_NUMBER, 
                  PurapKeyConstants.ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE);
                valid &= false;
            }
        } 
        return valid;
    }

}
