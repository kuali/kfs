/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
