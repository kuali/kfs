/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * An extension of KeyValueBase that gives the user a choice of search operator options
 */
public class SearchOperatorsFinder extends KeyValuesBase {

    /**
     * Returns a list of all valid search operations that can be carried out by certain GL inquiries
     * 
     * @return a List of key/value pair options
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue("eq", "Equals"));
        activeLabels.add(new ConcreteKeyValue("ne", "Not equal to"));
        activeLabels.add(new ConcreteKeyValue("gt", "Greater than"));
        activeLabels.add(new ConcreteKeyValue("ge", "Greater than or equal"));
        activeLabels.add(new ConcreteKeyValue("lt", "Less than"));
        activeLabels.add(new ConcreteKeyValue("le", "Less than or equal"));
        activeLabels.add(new ConcreteKeyValue("sw", "Starts with"));
        activeLabels.add(new ConcreteKeyValue("ew", "Ends with"));
        activeLabels.add(new ConcreteKeyValue("ct", "Contains"));
        return activeLabels;
    }
}
