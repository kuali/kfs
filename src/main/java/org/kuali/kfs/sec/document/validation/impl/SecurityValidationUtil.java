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
package org.kuali.kfs.sec.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sec.businessobject.SecurityAttributeMetadata;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * Contains some common validation logic
 */
public class SecurityValidationUtil {

    /**
     * Validates the given value exist for the attribute. SECURITY_ATTRIBUTE_METADATA_MAP maps the attribute to the business object class and primitive key field need to do the
     * existence search.
     * 
     * @param attributeName name of attribute for value
     * @param attributeValue the value to validate
     * @param errorKeyPrefix prefix for error key if the value does not exist
     * @return boolean true if the value exist, false if it does not
     */
    public static boolean validateAttributeValue(String attributeName, String attributeValue, String errorKeyPrefix) {
        boolean isValid = true;

        if (!SecConstants.SECURITY_ATTRIBUTE_METADATA_MAP.containsKey(attributeName)) {
            return isValid;
        }
        SecurityAttributeMetadata attributeMetadata = (SecurityAttributeMetadata) SecConstants.SECURITY_ATTRIBUTE_METADATA_MAP.get(attributeName);

        String[] attributeValues;
        if (StringUtils.contains(attributeValue, SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER)) {
            attributeValues = StringUtils.split(attributeValue, SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        }
        else {
            attributeValues = new String[1];
            attributeValues[0] = attributeValue;
        }

        for (int i = 0; i < attributeValues.length; i++) {
            if (!StringUtils.contains(attributeValues[i], SecConstants.SecurityValueSpecialCharacters.WILDCARD_CHARACTER)) {
                Map<String, String> searchValues = new HashMap<String, String>();
                searchValues.put(attributeMetadata.getAttributeField(), attributeValues[i]);

                int matches = SpringContext.getBean(BusinessObjectService.class).countMatching(attributeMetadata.getAttributeClass(), searchValues);
                if (matches <= 0) {
                    GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.ATTRIBUTE_VALUE, SecKeyConstants.ERROR_ATTRIBUTE_VALUE_EXISTENCE, attributeValues[i], attributeName);
                    isValid = false;
                }
            }
        }

        return isValid;
    }

}
