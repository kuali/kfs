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
package org.kuali.kfs.vnd;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Utility class with helper methods for Vendor processing 
 */
public class VendorUtils {

    public static final char LEFT_COLLECTION_SEPERATOR = '[';
    public static final char RIGHT_COLLECTION_SEPERATOR = ']';
    public static final char FIELD_SEPERATOR = '.';

    /**
     * Builds up a string and a position like so abc, 1 becomes abc[1] it is used for fields that require operations on
     * collections.
     * 
     * @param full
     * @param collections
     * @param pos
     * @return Newly formatted string
     */
    public static String assembleWithPosition(String full, String[] collections, int[] positions) {

        if (collections.length != positions.length) {
            throw new IllegalArgumentException();
        }

        String[] parts = StringUtils.split(full, FIELD_SEPERATOR);

        for (int j = 0; j < parts.length; j++) {
            for (int i = 0; i < collections.length; i++) {
                if (StringUtils.equals(parts[j], collections[i])) {
                    parts[j] = collections[i] + LEFT_COLLECTION_SEPERATOR + positions[i] + RIGHT_COLLECTION_SEPERATOR;
                    break;
                }

            }
        }

        return StringUtils.join(parts, FIELD_SEPERATOR);
    }

    /**
     * A helper to call assembleWithPosition(String full, String[] collections, int[] positions) when only one
     * collection
     * 
     * @param full
     * @param collection
     * @param position
     * @return Newly formatted string
     */
    public static String assembleWithPosition(String full, String collection, int position) {
        String[] collections = { collection };
        int[] positions = { position };
        return assembleWithPosition(full, collections, positions);
    }

    /**
     * Returns the headerId portion from a composite vendor number.
     * 
     * @param vendorNumber - composite vendor number (detail and header)
     * @return returns the headerId number
     */
    public static Integer getVendorHeaderId(String vendorNumber) {

        // validate the vendorNumber passed in
        if (!VendorUtils.validVendorNumberFormat(vendorNumber)) {
            return null;
        }

        // return the headerId, everything before the dash (-)
        String[] vendorNumberParts = vendorNumber.split("-");
        return new Integer(Integer.parseInt(vendorNumberParts[0]));
    }

    /**
     * Returns the detailId portion from a composite vendor number.
     * 
     * @param vendorNumber - composite vendor number (detail and header)
     * @return returns the detailId number
     */
    public static Integer getVendorDetailId(String vendorNumber) {

        if (!VendorUtils.validVendorNumberFormat(vendorNumber)) {
            return null;
        }

        // return the headerId, everything before the dash (-)
        String[] vendorNumberParts = vendorNumber.split("-");
        return new Integer(Integer.parseInt(vendorNumberParts[1]));
    }

    /**
     * Accepts a vendorNumber string, and evaluates it to make sure it is of the correct format. This method does not
     * test whether the given vendor number exists in the database, rather it just tests that the format is correct.
     * 
     * @param vendorNumber - String representing the vendor number
     * @return - returns an empty string on success, or an error message on a failure
     */
    public static boolean validVendorNumberFormat(String vendorNumber) {

        // disallow null string
        if (vendorNumber == null) {
            return false;
        }

        // validate the overall format: numbers - numbers
        if (!vendorNumber.matches("\\d+-\\d+")) {
            return false;
        }

        return true;
    }
    
    /**
     * Composes the text for the note related to parent change to be added to the old parent vendor.
     * 
     * @param messageKey
     * @param parameters
     * @return
     */
    public static String buildMessageText(String messageKey, String... parameters) {
        String result = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(messageKey);
        if (ObjectUtils.isNotNull(parameters)) {
            for (int i = 0; i < parameters.length; i++) {
                result = StringUtils.replace(result, "{" + i + "}", parameters[i]);
            }
        }
        return result;
    }
}
