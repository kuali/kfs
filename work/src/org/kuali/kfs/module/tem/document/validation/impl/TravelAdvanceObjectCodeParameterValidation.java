/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This checks, simply, that the object code for accounting lines has been set
 */
public class TravelAdvanceObjectCodeParameterValidation extends GenericValidation {
    protected ParameterService parameterService;

    /**
     * Determines if the object code for travel advances has been set; if not, relays an error message
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        final TravelAuthorizationDocument travelAuthorization = (TravelAuthorizationDocument)event.getDocument();
        if (travelAuthorization.shouldProcessAdvanceForDocument() && StringUtils.isBlank(getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_OBJECT_CODE, KFSConstants.EMPTY_STRING))) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.TRAVEL_ADVANCES, TemKeyConstants.ERROR_AUTHORIZATION_ADVANCE_OBJECT_CODE_PARAMETER_NOT_SET);
            return false;
        }
        return true;
    }

    /**
     * @return the injected implementation of ParameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Injects an implementation of the ParameterService
     * @param parameterService the implementation of ParameterService to inject
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
