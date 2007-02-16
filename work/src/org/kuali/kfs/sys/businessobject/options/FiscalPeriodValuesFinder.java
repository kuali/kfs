/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.kfs.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;

/**
 * This class returns list containg O = Open or C = Closed
 * 
 * 
 */
public class FiscalPeriodValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("", ""));
        keyValues.add(new KeyLabelPair("01", "01"));
        keyValues.add(new KeyLabelPair("02", "02"));
        keyValues.add(new KeyLabelPair("03", "03"));
        keyValues.add(new KeyLabelPair("04", "04"));
        keyValues.add(new KeyLabelPair("05", "05"));
        keyValues.add(new KeyLabelPair("06", "06"));
        keyValues.add(new KeyLabelPair("07", "07"));
        keyValues.add(new KeyLabelPair("08", "08"));
        keyValues.add(new KeyLabelPair("09", "09"));
        keyValues.add(new KeyLabelPair("10", "10"));
        keyValues.add(new KeyLabelPair("11", "11"));
        keyValues.add(new KeyLabelPair("12", "12"));
        keyValues.add(new KeyLabelPair("13", "13"));
        keyValues.add(new KeyLabelPair("AB", "Annual Bal"));
        keyValues.add(new KeyLabelPair("BB", "Beginning Bal"));
        keyValues.add(new KeyLabelPair("CB", "CG Beginning Bal"));
        return keyValues;
    }

}