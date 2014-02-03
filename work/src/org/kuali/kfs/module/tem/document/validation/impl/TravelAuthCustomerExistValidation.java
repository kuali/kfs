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

    private ParameterService parameterService;

    //@Override
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
