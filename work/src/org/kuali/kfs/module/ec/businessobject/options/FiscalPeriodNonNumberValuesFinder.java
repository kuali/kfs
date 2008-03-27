/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.effort.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;

/**
 * returns valid effort reporting fiscal periods 1 - 12
 */
public class FiscalPeriodNonNumberValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("01", "1"));
        keyValues.add(new KeyLabelPair("02", "2"));
        keyValues.add(new KeyLabelPair("03", "3"));
        keyValues.add(new KeyLabelPair("04", "4"));
        keyValues.add(new KeyLabelPair("05", "5"));
        keyValues.add(new KeyLabelPair("06", "6"));
        keyValues.add(new KeyLabelPair("07", "7"));
        keyValues.add(new KeyLabelPair("08", "8"));
        keyValues.add(new KeyLabelPair("09", "9"));
        keyValues.add(new KeyLabelPair("10", "10"));
        keyValues.add(new KeyLabelPair("11", "11"));
        keyValues.add(new KeyLabelPair("12", "12"));
        return keyValues;
    }
}
