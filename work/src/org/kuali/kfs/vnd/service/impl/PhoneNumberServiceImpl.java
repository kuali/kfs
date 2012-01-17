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
package org.kuali.kfs.vnd.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

public class PhoneNumberServiceImpl implements PhoneNumberService {

    public ParameterService parameterService;
    public List<String> phoneNumberFormats;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    // This 1-based index is used to pick the format among those in phoneNumberFormats
    // which is the default.
    public final int PHONE_FORMAT_RULE_DEFAULT_INDEX = 1;

    /**
     * Converts a valid phone number to the default format. Must be changed if the generic format changes. The string passed in is
     * stripped of non-number chars. If it is then the right length it is formatted. If not the right length the original string is
     * returned.
     * 
     * @param phone The phone number String to be converted
     * @return A String in the default valid format
     * @see org.kuali.rice.core.web.format.PhoneNumberFormatter
     */
    public String formatNumberIfPossible(String unformattedNumber) {
        if (ObjectUtils.isNull(unformattedNumber)) {
            return unformattedNumber;
        }
        String formattedNumber = unformattedNumber.replaceAll("\\D", "");
        Integer defaultPhoneNumberDigits = new Integer(parameterService.getParameterValueAsString(VendorDetail.class, VendorParameterConstants.DEFAULT_PHONE_NUMBER_DIGITS));
        // Before moving to the parameter table:
        // if ( formattedNumber.length() != VendorConstants.GENERIC_DEFAULT_PHONE_NUM_DIGITS ) {
        if (formattedNumber.length() != defaultPhoneNumberDigits) {
            return unformattedNumber;
        }
        else {
            return formattedNumber.substring(0, 3) + "-" + formattedNumber.substring(3, 6) + "-" + formattedNumber.substring(6, 10);
        }
    }

    /**
     * A predicate to determine the validity of phone numbers, using only the formats which are common in North America (which we
     * are calling Generic formats) as examples.
     * 
     * @param phone A phone number String
     * @return True if the phone number is known to be in a valid format
     */
    public boolean isValidPhoneNumber(String phone) {
        String[] formats = parseFormats();
        for (int i = 0; i < formats.length; i++) {
            if (phone.matches(formats[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits the set of phone number formats which are returned from the rule service as a semicolon-delimeted String into a String
     * array.
     * 
     * @return A String array of the phone number format regular expressions.
     */
    protected String[] parseFormats() {
        if (ObjectUtils.isNull(phoneNumberFormats)) {
            phoneNumberFormats = new ArrayList<String>( parameterService.getParameterValuesAsString(VendorDetail.class, VendorParameterConstants.PHONE_NUMBER_FORMATS) );
        }
        return phoneNumberFormats.toArray(new String[] {});
    }

    /**
     * A predicate to determine whether the given phone number is in the default format.
     * 
     * @param phone A phone number String
     * @return True if the phone number is in the default format.
     */
    public boolean isDefaultFormatPhoneNumber(String phone) {
        String[] formats = parseFormats();
        String defaultPhoneFormat = formats[PHONE_FORMAT_RULE_DEFAULT_INDEX - 1];
        if (phone.matches(defaultPhoneFormat)) {
            return true;
        }
        return false;
    }

}
