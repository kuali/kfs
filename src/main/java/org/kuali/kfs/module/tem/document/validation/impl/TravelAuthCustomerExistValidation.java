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

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthCustomerExistValidation extends GenericValidation {

    public static Logger LOG = Logger.getLogger(TravelAuthCustomerExistValidation.class);

    protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.TRIP_OVERVIEW);
        boolean rulePassed = true;
        final TravelDocumentBase taDocument = (TravelDocumentBase)event.getDocument();

        LOG.debug("Looking up customer with number " + taDocument.getTraveler().getCustomerNumber());
        taDocument.getTraveler().refreshReferenceObject(TemPropertyConstants.CUSTOMER);
        LOG.debug("Got " + taDocument.getTraveler().getCustomer());

        if (taDocument.getTraveler().getCustomer() == null
            && getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.NON_EMPLOYEE_TRAVELER_TYPE_CODES).contains(taDocument.getTraveler().getTravelerTypeCode())) {
            // if not found and non-employee, throw an error
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRAVELER_TYPE, TemKeyConstants.ERROR_TA_AR_CUST_NOT_FOUND);
            rulePassed = false;
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.TRIP_OVERVIEW);
        return rulePassed;
    }

    public void setParameterService(final ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

}
