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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValueFinder that allows the selection of a period code
 */
public class PeriodCodeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns this default value of this ValueFinder, in this case the current period code
     * @return the key of the default Key/Value pair
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        UniversityDate ud = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        return ud.getUniversityFiscalAccountingPeriod();
    }

    /**
     * Returns a list of possible options for this ValueFinder; here, each of the fiscal periods
     * @return a List of key/value pair options
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH1, KFSConstants.MONTH1));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH2, KFSConstants.MONTH2));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH3, KFSConstants.MONTH3));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH4, KFSConstants.MONTH4));

        labels.add(new ConcreteKeyValue(KFSConstants.MONTH5, KFSConstants.MONTH5));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH6, KFSConstants.MONTH6));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH7, KFSConstants.MONTH7));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH8, KFSConstants.MONTH8));

        labels.add(new ConcreteKeyValue(KFSConstants.MONTH9, KFSConstants.MONTH9));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH10, KFSConstants.MONTH10));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH11, KFSConstants.MONTH11));
        labels.add(new ConcreteKeyValue(KFSConstants.MONTH12, KFSConstants.MONTH12));

        labels.add(new ConcreteKeyValue(KFSConstants.MONTH13, KFSConstants.MONTH13));
        labels.add(new ConcreteKeyValue(KFSConstants.PERIOD_CODE_ANNUAL_BALANCE, KFSConstants.PERIOD_CODE_ANNUAL_BALANCE));
        labels.add(new ConcreteKeyValue(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE, KFSConstants.PERIOD_CODE_BEGINNING_BALANCE));
        labels.add(new ConcreteKeyValue(KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE, KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE));

        return labels;
    }

}
