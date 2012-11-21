/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class AttendeeTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        List<KeyValue> attendeeType = new ArrayList<KeyValue>();
        attendeeType.add(new ConcreteKeyValue("", ""));
        attendeeType.add(new ConcreteKeyValue("BG", "Business Guest"));
        attendeeType.add(new ConcreteKeyValue("EE", "Employee"));
        attendeeType.add(new ConcreteKeyValue("SP", "Spouse/Partner"));
        attendeeType.add(new ConcreteKeyValue("ST", "Student"));
        attendeeType.add(new ConcreteKeyValue("OT", "Other"));
        return attendeeType;
    }
}
