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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.TravelPerDiem;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class returns list of per diem category value pairs.
 */
public class TravelPerDiemCategoryValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        List boList = (List) SpringContext.getBean(KeyValuesService.class).findMatching(TravelPerDiem.class, criteria);
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = boList.iterator(); iter.hasNext();) {
            TravelPerDiem element = (TravelPerDiem) iter.next();
            keyValues.add(new ConcreteKeyValue(element.getPerDiemCountryName(), element.getPerDiemCountryName()));
        }

        return keyValues;
    }

}
