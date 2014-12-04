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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class InvoiceRecurrenceIntervalCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List getKeyValues() {
        List keyLabels = new ArrayList();
        List<String> parameterValues = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(InvoiceRecurrence.class, ArConstants.INVOICE_RECURRENCE_INTERVALS) );
        keyLabels.add(new ConcreteKeyValue("", ""));

        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                keyLabels.add(new ConcreteKeyValue(StringUtils.substringBefore(parameterValue, "="), 
                                               StringUtils.substringBefore(parameterValue, "=") + " - " + StringUtils.substringAfter(parameterValue, "=")));
            }
        }
        return keyLabels;
    }

}
