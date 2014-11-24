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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * A value finder that returns all balance type, but selects the actual balance type
 */
public class ActualBalanceTypeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the value to select: here the value of the actual balance type
     * 
     * @return the balance type code for actual balances
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        OptionsService os = SpringContext.getBean(OptionsService.class);
        SystemOptions o = os.getCurrentYearOptions();

        return o.getActualFinancialBalanceTypeCd();
    }

    /**
     * Returns a list of the key value pairs of all balance type codes and their names
     * 
     * @return a List of all balance types to populate a dropdown control
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();

        BalanceTypeService bts = SpringContext.getBean(BalanceTypeService.class);
        Collection c = bts.getAllBalanceTypes();

        for (Iterator iter = c.iterator(); iter.hasNext();) {
            BalanceType bt = (BalanceType) iter.next();
            labels.add(new ConcreteKeyValue(bt.getCode(), bt.getCode() + " - " + bt.getName()));
        }

        return labels;
    }

}
