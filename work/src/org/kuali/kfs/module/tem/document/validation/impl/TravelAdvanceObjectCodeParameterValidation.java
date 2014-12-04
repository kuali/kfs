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
