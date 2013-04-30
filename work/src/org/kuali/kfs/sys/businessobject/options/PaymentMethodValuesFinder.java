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
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of payment method key value pairs.
 */
public class PaymentMethodValuesFinder extends KeyValuesBase {

    static List<KeyValue> activeLabels = new ArrayList<KeyValue>();
    static {
        activeLabels.add(new ConcreteKeyValue("", ""));
        activeLabels.add(new ConcreteKeyValue("P", "P - Check/ACH"));
        activeLabels.add(new ConcreteKeyValue("F", "F - Foreign Draft"));
        activeLabels.add(new ConcreteKeyValue("W", "W - Wire Transfer"));
    }
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return activeLabels;
    }

}
