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

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * An extension of KeyValueBase that gives the user a choice of search operator options
 */
public class SearchOperatorsFinder extends KeyValuesBase {

    /**
     * Returns a list of all valid search operations that can be carried out by certain GL inquiries
     * 
     * @return a List of key/value pair options
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue("eq", "Equals"));
        activeLabels.add(new ConcreteKeyValue("ne", "Not equal to"));
        activeLabels.add(new ConcreteKeyValue("gt", "Greater than"));
        activeLabels.add(new ConcreteKeyValue("ge", "Greater than or equal"));
        activeLabels.add(new ConcreteKeyValue("lt", "Less than"));
        activeLabels.add(new ConcreteKeyValue("le", "Less than or equal"));
        activeLabels.add(new ConcreteKeyValue("sw", "Starts with"));
        activeLabels.add(new ConcreteKeyValue("ew", "Ends with"));
        activeLabels.add(new ConcreteKeyValue("ct", "Contains"));
        return activeLabels;
    }
}
