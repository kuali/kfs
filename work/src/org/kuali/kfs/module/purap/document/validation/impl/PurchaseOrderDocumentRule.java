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
import org.kuali.KeyConstants;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public class PurchaseOrderDocumentRule extends PurchasingDocumentRuleBase {
    
    private boolean processValidation(PurchaseOrderDocument document) {
        boolean valid = true;
        valid &= processDocumentOverviewValidation(document);
        valid &= processVendorValidation(document);
        valid &= processItemValidation(document);
        valid &= processPaymentInfoValidation(document);
        valid &= processDeliveryValidation(document);
        valid &= processAdditionalValidation(document);
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
    
}
