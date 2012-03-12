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
package org.kuali.kfs.module.ec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * returns valid effort reporting fiscal periods 1 - 12
 */
public class FiscalPeriodNonNumberValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("01", "1"));
        keyValues.add(new ConcreteKeyValue("02", "2"));
        keyValues.add(new ConcreteKeyValue("03", "3"));
        keyValues.add(new ConcreteKeyValue("04", "4"));
        keyValues.add(new ConcreteKeyValue("05", "5"));
        keyValues.add(new ConcreteKeyValue("06", "6"));
        keyValues.add(new ConcreteKeyValue("07", "7"));
        keyValues.add(new ConcreteKeyValue("08", "8"));
        keyValues.add(new ConcreteKeyValue("09", "9"));
        keyValues.add(new ConcreteKeyValue("10", "10"));
        keyValues.add(new ConcreteKeyValue("11", "11"));
        keyValues.add(new ConcreteKeyValue("12", "12"));
        return keyValues;
    }
}
