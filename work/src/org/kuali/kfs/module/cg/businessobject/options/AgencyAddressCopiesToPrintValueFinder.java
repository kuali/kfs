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
package org.kuali.kfs.module.cg.businessobject.options;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;


/**
 * Gets the Copies-to-Print default number for AgencyAddress from system parameter.
 */
public class AgencyAddressCopiesToPrintValueFinder implements ValueFinder {

    public String getValue() {
        String nbrOfCopiesToPrint = SpringContext.getBean(ParameterService.class).getParameterValueAsString(AgencyAddress.class, CGConstants.DEFAULT_NUMBER_OF_COPIES_TO_PRINT);
        return nbrOfCopiesToPrint;
    }

}
