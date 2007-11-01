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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;

/**
 * This class returns list containing
 */
public class PayeeTypeCodeValuesFinder extends KeyValuesBase {

    /**
     * This method creates a static list of {@link PayeeTypeCode}s
     * <ul>
     * <li>E = Employee</li>
     * <li>F = FEIN</li>
     * <li>P = DV Payee</li>
     * <li>S = SSN</li>
     * <li>V = Vendor</li>
     * </ul>
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("E", "Employee"));
        keyValues.add(new KeyLabelPair("F", "FEIN"));
        keyValues.add(new KeyLabelPair("P", "DV Payee"));
        keyValues.add(new KeyLabelPair("S", "SSN"));
        keyValues.add(new KeyLabelPair("V", "Vendor"));

        return keyValues;
    }

}
