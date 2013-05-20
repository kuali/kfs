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
package org.kuali.kfs.module.ar.businessobject.options;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class gets the minimum invoice amount for AwardInoice from system parameter.
 */
public class AwardInvoiceMinimumInvoiceAmountValueFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        String minInvoiceAmt = SpringContext.getBean(ParameterService.class).getParameterValueAsString(ContractsGrantsInvoiceDocument.class, ArConstants.MINIMUM_INVOICE_AMOUNT);
        return minInvoiceAmt;
    }

}
