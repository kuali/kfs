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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.TravelExpenseTypeCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class returns list of travel pre paid expense type category value pairs.
 */
public class TravelPrePaidExpenseTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List boList = (List) SpringContext.getBean(KeyValuesService.class).findAllOrderBy(TravelExpenseTypeCode.class, "name", true);
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = boList.iterator(); iter.hasNext();) {
            TravelExpenseTypeCode element = (TravelExpenseTypeCode) iter.next();
            if (element.isPrepaidExpense()) {
                keyValues.add(new ConcreteKeyValue(element.getCode(), element.getCode() + " - " + element.getName()));
            }
        }

        return keyValues;
    }

}
