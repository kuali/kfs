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

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


/**
 * Returns list of operator codes for security definition
 */
public class SecurityOperatorCodeFinder extends KeyValuesBase {

    protected static final List<KeyValue> OPTIONS = new ArrayList<KeyValue>();
    static {
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.EQUAL, "Equal"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.NOT_EQUAL, "Not Equal"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.GREATER_THAN, "Greater Than"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.GREATER_THAN_EQUAL, "Greater Than or Equal"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.LESS_THAN, "Less Than"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.LESS_THAN_EQUAL, "Less Than or Equal"));
    }
    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return OPTIONS;
    }
}
