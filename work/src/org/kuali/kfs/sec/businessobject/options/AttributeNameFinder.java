/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.SecConstants.NonSecurityAttributeNames;
import org.kuali.kfs.sec.SecConstants.SecurityAttributeNames;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


/**
 * Returns list of attribute names
 */
public class AttributeNameFinder extends KeyValuesBase {

    protected static final List<KeyValue> OPTIONS = new ArrayList<KeyValue>();
    static {
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.ACCOUNT, SecurityAttributeNames.ACCOUNT));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.CHART, SecurityAttributeNames.CHART));
        OPTIONS.add(new ConcreteKeyValue(NonSecurityAttributeNames.OBJECT_CODE, NonSecurityAttributeNames.OBJECT_CODE));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.OBJECT_CONSOLIDATION, SecurityAttributeNames.OBJECT_CONSOLIDATION));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.OBJECT_LEVEL, SecurityAttributeNames.OBJECT_LEVEL));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.ORGANIZATION, SecurityAttributeNames.ORGANIZATION));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.PROJECT_CODE, SecurityAttributeNames.PROJECT_CODE));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.SUB_ACCOUNT, SecurityAttributeNames.SUB_ACCOUNT));
        OPTIONS.add(new ConcreteKeyValue(NonSecurityAttributeNames.SUB_OBJECT_CODE, NonSecurityAttributeNames.SUB_OBJECT_CODE));
    }
    
    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return OPTIONS;
    }
}
