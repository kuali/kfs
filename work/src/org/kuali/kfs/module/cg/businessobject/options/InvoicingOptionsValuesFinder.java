/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of string key value pairs for InvoicingOptions
 */
public class InvoicingOptionsValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();
        activeLabels.add(new ConcreteKeyValue(CGPropertyConstants.INV_AWARD, CGPropertyConstants.INV_AWARD));
        activeLabels.add(new ConcreteKeyValue(CGPropertyConstants.INV_ACCOUNT, CGPropertyConstants.INV_ACCOUNT));
        activeLabels.add(new ConcreteKeyValue(CGPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT, CGPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT));
        return activeLabels;
    }
}
