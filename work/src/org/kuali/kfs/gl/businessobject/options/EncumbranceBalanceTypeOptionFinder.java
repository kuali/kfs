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

import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValueFinder that returns all balance types, but which defaults to external encumbrance
 */
public class EncumbranceBalanceTypeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value of this ValueFinder, in this case, external encumbrance
     * 
     * @return a String with the key of the default value
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        OptionsService os = SpringContext.getBean(OptionsService.class);
        SystemOptions o = os.getCurrentYearOptions();

        return o.getExtrnlEncumFinBalanceTypCd();
    }

    /**
     * Returns a list of all balance types
     * 
     * @return a List of key/value pairs to populate a drop down box
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();

        OptionsService os = SpringContext.getBean(OptionsService.class);
        SystemOptions o = os.getCurrentYearOptions();

        labels.add(new ConcreteKeyValue(o.getExtrnlEncumFinBalanceTypCd(), o.getExtrnlEncumFinBalanceTypCd() + " - " + o.getExtrnlEncumFinBalanceTyp().getFinancialBalanceTypeName()));
        labels.add(new ConcreteKeyValue(o.getIntrnlEncumFinBalanceTypCd(), o.getIntrnlEncumFinBalanceTypCd() + " - " + o.getIntrnlEncumFinBalanceTyp().getFinancialBalanceTypeName()));
        labels.add(new ConcreteKeyValue(o.getPreencumbranceFinBalTypeCd(), o.getPreencumbranceFinBalTypeCd() + " - " + o.getPreencumbranceFinBalType().getFinancialBalanceTypeName()));

        return labels;
    }
}
