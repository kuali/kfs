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
