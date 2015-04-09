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
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.service.TravelExpenseTypeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class returns list of travel expense company value pairs.
 */
public class TravelExpenseTypeServiceImpl implements TravelExpenseTypeService {
    private KeyValuesService keyValuesService;

    @Override
    public Map<String, String> getCompanyNameMapFrom(final String expenseTypeCode) {
        final Map<String, String> retval = new HashMap<String, String>();
        final List<TravelCompanyCode> boList = getCompanyCodesBy(expenseTypeCode);
        for (TravelCompanyCode element : boList) {
            retval.put(element.getName(), element.getName());
        }
        return retval;
    }

    @Override
    public List<ConcreteKeyValue> getCompanyNamePairsFrom(final String expenseTypeCode) {
        final List<TravelCompanyCode> boList = getCompanyCodesBy(expenseTypeCode);
        final List<ConcreteKeyValue> keyValues = new ArrayList<ConcreteKeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));
        for (TravelCompanyCode element : boList) {
            if(element.isActive()) {
                keyValues.add(new ConcreteKeyValue(element.getName(), element.getName()));
            }
        }
        return keyValues;
    }

    protected List<TravelCompanyCode> getCompanyCodesBy(final String expenseTypeCode) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE, expenseTypeCode);
        final List<TravelCompanyCode> boList = (List<TravelCompanyCode>)
            getKeyValuesService().findMatching(TravelCompanyCode.class, criteria);
        return boList;
    }

    protected KeyValuesService getKeyValuesService() {
        return SpringContext.getBean(KeyValuesService.class);
    }
}
