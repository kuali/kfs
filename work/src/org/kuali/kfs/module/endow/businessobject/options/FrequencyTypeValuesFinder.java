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
 * This class defines a values finder for the frequency type part in the frequency code.
 */
public class FrequencyTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();

        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.DAILY, "Daily"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.WEEKLY, "Weekly"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.SEMI_MONTHLY, "Semi-Monthly"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.MONTHLY, "Monthly"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.SEMI_ANNUALLY, "Semi-Annually"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.QUARTERLY, "Quarterly"));
        labels.add(new ConcreteKeyValue(EndowConstants.FrequencyTypes.ANNUALLY, "Annually"));

        return labels;
    }

}
