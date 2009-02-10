/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public class InvoiceRecurrenceIntervalCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List getKeyValues() {
        List keyLabels = new ArrayList();
        List<String> parameterValues = SpringContext.getBean(ParameterService.class).getParameterValues(InvoiceRecurrence.class, ArConstants.INVOICE_RECURRENCE_INTERVALS);
        keyLabels.add(new KeyLabelPair("", ""));

        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                keyLabels.add(new KeyLabelPair(StringUtils.substringBefore(parameterValue, "="), 
                                               StringUtils.substringBefore(parameterValue, "=") + " - " + StringUtils.substringAfter(parameterValue, "=")));
            }
        }
        return keyLabels;
    }

}
