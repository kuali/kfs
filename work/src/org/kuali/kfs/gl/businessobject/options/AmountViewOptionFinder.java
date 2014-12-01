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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.Constant;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValuesFinder that allows GL inquiries to select either monthly totals or accumulated totals on balance inquiries
 */
public class AmountViewOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value of this ValueFinder: here, MONTHLY
     * @return the default value for this finder, which should be "monthly totals"
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return Constant.MONTHLY;
    }

    /**
     * Returns a list of key values to populate a drop down to choose between monthly totals and accumulated totals
     * @return a List of key value pairs
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue(Constant.MONTHLY, Constant.MONTHLY));
        labels.add(new ConcreteKeyValue(Constant.ACCUMULATE, Constant.ACCUMULATE));
        return labels;
    }
}
