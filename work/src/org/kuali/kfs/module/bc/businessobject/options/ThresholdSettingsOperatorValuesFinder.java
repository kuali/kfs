/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Provides option values the threshold settings operator field.
 */
public class ThresholdSettingsOperatorValuesFinder extends KeyValuesBase implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyValue> keyLabels = new ArrayList<KeyValue>();
        keyLabels.add(new ConcreteKeyValue(Boolean.TRUE.toString(), "greater than or equal to threshold"));
        keyLabels.add(new ConcreteKeyValue(Boolean.FALSE.toString(), "less than or equal to threshold"));

        return keyLabels;
    }
    
    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return Boolean.TRUE.toString();
    }
}
