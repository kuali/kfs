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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list containg O = Open or C = Closed
 */
public class FiscalPeriodValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        keyValues.add(new ConcreteKeyValue("01", "01"));
        keyValues.add(new ConcreteKeyValue("02", "02"));
        keyValues.add(new ConcreteKeyValue("03", "03"));
        keyValues.add(new ConcreteKeyValue("04", "04"));
        keyValues.add(new ConcreteKeyValue("05", "05"));
        keyValues.add(new ConcreteKeyValue("06", "06"));
        keyValues.add(new ConcreteKeyValue("07", "07"));
        keyValues.add(new ConcreteKeyValue("08", "08"));
        keyValues.add(new ConcreteKeyValue("09", "09"));
        keyValues.add(new ConcreteKeyValue("10", "10"));
        keyValues.add(new ConcreteKeyValue("11", "11"));
        keyValues.add(new ConcreteKeyValue("12", "12"));
        keyValues.add(new ConcreteKeyValue("13", "13"));
        keyValues.add(new ConcreteKeyValue("AB", "Annual Bal"));
        keyValues.add(new ConcreteKeyValue("BB", "Beginning Bal"));
        keyValues.add(new ConcreteKeyValue("CB", "CG Beginning Bal"));
        return keyValues;
    }

}
