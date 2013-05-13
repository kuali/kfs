/*
 * Copyright 2010 The Kuali Foundation.
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
