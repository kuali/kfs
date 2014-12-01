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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link OrganizationReversionCode}s)
 */
public class OrganizationReversionCodeValuesFinder extends KeyValuesBase {

    /**
     * This is a static list of {@link OrganizationReversionCode}s
     * <ul>
     * <li>"A", "A - CF +/- bal in same account"</li>
     * <li>"C1", "C1 - CF budget then CF + and R -"</li>
     * <li>"C2", "C2 - Don't CF budget then CF + and R -"</li>
     * <li>"N1", "N1 - CF budget then R + and CF -"</li>
     * <li>"N2", "N2 - Don't CF budget then R + and CF -"</li>
     * <li>"R1", "R1 - CF budget then R Remaining"</li>
     * <li>"R2", "R2 - Don't CF budget then R Remaining"</li>
     * </ul>
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        keyValues.add(new ConcreteKeyValue("A", "A - CF +/- bal in same account"));
        keyValues.add(new ConcreteKeyValue("C1", "C1 - CF budget then CF + and R -"));
        keyValues.add(new ConcreteKeyValue("C2", "C2 - Don't CF budget then CF + and R -"));
        keyValues.add(new ConcreteKeyValue("N1", "N1 - CF budget then R + and CF -"));
        keyValues.add(new ConcreteKeyValue("N2", "N2 - Don't CF budget then R + and CF -"));
        keyValues.add(new ConcreteKeyValue("R1", "R1 - CF budget then R Remaining"));
        keyValues.add(new ConcreteKeyValue("R2", "R2 - Don't CF budget then R Remaining"));
        return keyValues;
    }

}
