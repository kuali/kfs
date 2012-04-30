/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.EmailAddressValidationPattern;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Performs validations for the requestor's email address and phone number. 
 */
public class PurchasingProcessRequestorPhoneAndEmailAddressValidation extends PurchasingAccountsPayableProcessVendorValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingDocument purDocument = (PurchasingDocument) event.getDocument();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(PurapConstants.ADDITIONAL_TAB_ERRORS);

        valid &= super.validate(event);

        //perform the validation against phone Number
        if (StringUtils.isNotBlank(purDocument.getRequestorPersonPhoneNumber())) {
            if (!validatePhoneNumber(purDocument.getRequestorPersonPhoneNumber())) {
                valid &= false;
                errorMap.putError(PurapPropertyConstants.REQUESTOR_PERSON_PHONE_NUMBER, PurapKeyConstants.ERROR_INVALID_PH0NE_NUMBER);
            }
        }

        //perform the validation against email address
        if (StringUtils.isNotBlank(purDocument.getRequestorPersonEmailAddress())) {
            if (!validateEmailAddress(purDocument.getRequestorPersonEmailAddress())) {
                valid &= false;
                errorMap.putError(PurapPropertyConstants.REQUESTOR_PERSON_EMAIL_ADDRESS, PurapKeyConstants.ERROR_INVALID_EMAIL_ADDRESS);
            }
        }
        
        errorMap.clearErrorPath();
        
        return valid;
    }
}
