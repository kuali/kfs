/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.service.TravelExpenseTypeService;
import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class returns list of travel expense company value pairs.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
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
    public List<KeyLabelPair> getCompanyNamePairsFrom(final String expenseTypeCode) {
        final List<TravelCompanyCode> boList = getCompanyCodesBy(expenseTypeCode);
        final List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));
        for (TravelCompanyCode element : boList) {
            if(element.isActive()) {
                keyValues.add(new KeyLabelPair(element.getName(), element.getName()));
            }
        }
        return keyValues;
    }

    protected List<TravelCompanyCode> getCompanyCodesBy(final String expenseTypeCode) {
        final Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("travelExpenseTypeCode", expenseTypeCode);
        final List<TravelCompanyCode> boList = (List<TravelCompanyCode>) 
            getKeyValuesService().findMatching(TravelCompanyCode.class, criteria);
        return boList;
    }

    public void setKeyValuesService(final KeyValuesService keyValuesService) {
        this.keyValuesService = keyValuesService;
    }

    protected KeyValuesService getKeyValuesService() {
        return keyValuesService;
    }
}
