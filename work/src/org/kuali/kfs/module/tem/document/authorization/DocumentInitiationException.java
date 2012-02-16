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
package org.kuali.kfs.module.tem.document.authorization;

import java.text.MessageFormat;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.exception.KualiException;
import org.kuali.rice.kns.service.KualiConfigurationService;

public class DocumentInitiationException extends KualiException {
    public DocumentInitiationException(String messageKey, Object[] messageParameters) {
        super(MessageFormat.format(getConfigurationService().getPropertyString(messageKey), messageParameters));
    }
    
    public DocumentInitiationException(String messageKey, Object[] messageParameters, boolean hideIncidentReport) {
        super(MessageFormat.format(getConfigurationService().getPropertyString(messageKey), messageParameters), hideIncidentReport);
    }
    
    private static KualiConfigurationService configurationService;
    private static KualiConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(KualiConfigurationService.class);
        }
        return configurationService;
    }
}
