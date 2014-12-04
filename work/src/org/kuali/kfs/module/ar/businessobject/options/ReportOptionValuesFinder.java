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
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Option Finder for Report Option in Dunning Letter Campaign
 */
public class ReportOptionValuesFinder extends KeyValuesBase implements ValueFinder {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(ArConstants.ReportOptionFieldValues.PROCESSING_ORG, ArConstants.ReportOptionFieldValues.PROCESSING_ORG));
        keyValues.add(new ConcreteKeyValue(ArConstants.ReportOptionFieldValues.BILLING_ORG, ArConstants.ReportOptionFieldValues.BILLING_ORG));
        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return ArConstants.ReportOptionFieldValues.PROCESSING_ORG;
    }
}
