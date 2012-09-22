/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.util;

import java.text.MessageFormat;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ErrorMessage;

public class MessageUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MessageUtils.class);
    
    /**
     * Resolve the message from the key and the parameters on ErrorMessage propertie
     * 
     * @param error
     * @return
     */
    public static String getErrorMessage(ErrorMessage error){
        return getMessage(error.getErrorKey(), error.getMessageParameters());
    }
    
    /**
     * Resolve the message from the key and the parameters
     * 
     * @param error
     * @return
     */
    public static String getMessage(String key, String... parameters){
        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(key);
        return MessageFormat.format(message, (Object[])parameters);
    }

}
