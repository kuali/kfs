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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValueFinder that allows balance inquiries to choose between consolidated results or detailed results
 */
public class InvoiceReportOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns a list of key value pairs to allow inquirers to choose between consolidated results or detailed results
     * 
     * @return a List of key value pairs to poplulate radio buttons
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue(ArConstants.OUTSTANDING_INVOICES, ArConstants.OUTSTANDING_INVOICES));
        labels.add(new ConcreteKeyValue(ArConstants.PAST_DUE_INVOICES, ArConstants.PAST_DUE_INVOICES));

        return labels;
    }

    /**
     * Gets the default value for this ValueFinder, in this case CONSOLIDATED
     * 
     * @return a String with the default value for this ValueFinder
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return ArConstants.OUTSTANDING_INVOICES;
    }
}
