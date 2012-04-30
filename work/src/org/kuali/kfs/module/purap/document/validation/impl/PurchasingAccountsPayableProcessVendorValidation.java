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
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.EmailAddressValidationPattern;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;

public class PurchasingAccountsPayableProcessVendorValidation extends GenericValidation {
    
    public boolean validate(AttributedDocumentEvent event) {
        return true;
    }

    /**
     * validate the phone number against the phone pattern
     * 
     * @param PhoneNumber
     * @return true if phone number follows the pattern else return false.
     */
    public boolean validatePhoneNumber(String PhoneNumber) {
        boolean valid = true;
        
        //perform the validation against phone Number
        if (StringUtils.isNotBlank(PhoneNumber)) {
            PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
            if (!phonePattern.matches(PhoneNumber)) {
                return false;
            }
        }
        
        return valid;
    }
    
    /**
     * validate the email Address against the email address pattern
     * 
     * @param emailAddress
     * @return true if email Address follows the pattern else return false.
     */
    public boolean validateEmailAddress(String emailAddress) {
        boolean valid = true;
        
        //perform the validation against email address
        if (StringUtils.isNotBlank(emailAddress)) {
            EmailAddressValidationPattern emailAddressPattern = new EmailAddressValidationPattern();
            if (!emailAddressPattern.matches(emailAddress)) {
                return false;
            }
        }
        
        return valid;
    }
}
