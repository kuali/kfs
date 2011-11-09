/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.util;

import java.text.MessageFormat;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.ObjectUtils;

public class KcUtils {

    /**
     * Utility method to translate a property error to error string
     * 
     * @param propertyKey
     * @param messageParameters
     * @return
     */
    public static String getErrorMessage(String propertyKey, Object[] messageParameters){
        
        // get error text
        String errorText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(propertyKey);

        // apply parameters
        if(ObjectUtils.isNotNull(messageParameters)){
            errorText = MessageFormat.format(errorText, messageParameters);
        }

        return errorText;
    }

}
