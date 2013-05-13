/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class defines a values finder for the month part in the frequency code.
 */
public class FrequencyMonthsValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();

        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.JANUARY, "January"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.FEBRUARY, "February"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.MARCH, "March"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.APRIL, "April"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.MAY, "May"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.JUNE, "June"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.JULY, "July"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.AUGUST, "August"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.SEPTEMBER, "September"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.OCTOBER, "October"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.NOVEMBER, "November"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyMonths.DECEMBER, "December"));

        return labels;
    }

}
