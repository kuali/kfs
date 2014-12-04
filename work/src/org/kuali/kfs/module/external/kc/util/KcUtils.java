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
