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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;

/**
 * This class returns list of bank account type value pairs.
 * 
 * 
 */
public class OrganizationReversionCodeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("", ""));
        keyValues.add(new KeyLabelPair("A", "A - CF +/- bal in same account"));
        keyValues.add(new KeyLabelPair("C1", "C1 - CF budget then CF + and R -"));
        keyValues.add(new KeyLabelPair("C2", "C2 - Don't CF budget then CF + and R -"));
        keyValues.add(new KeyLabelPair("N1", "N1 - CF budget then R + and CF -"));
        keyValues.add(new KeyLabelPair("N2", "N2 - Don't CF budget then R + and CF -"));
        keyValues.add(new KeyLabelPair("R1", "R1 - CF budget then R Remaining"));
        keyValues.add(new KeyLabelPair("R2", "R2 - Don't CF budget then R Remaining"));
        return keyValues;
    }

}