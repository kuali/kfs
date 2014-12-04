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
