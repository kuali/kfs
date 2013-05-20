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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of string key value pairs for LOC Creation Types.
 */
public class DaysPastDueValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();
        // This blank option would be useful for Cash Control document on change event
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT, ArConstants.DunningLetters.DYS_PST_DUE_CURRENT));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_31_60, ArConstants.DunningLetters.DYS_PST_DUE_31_60));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_61_90, ArConstants.DunningLetters.DYS_PST_DUE_61_90));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_91_120, ArConstants.DunningLetters.DYS_PST_DUE_91_120));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_121, ArConstants.DunningLetters.DYS_PST_DUE_121));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_FINAL, ArConstants.DunningLetters.DYS_PST_DUE_FINAL));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL));
        return activeLabels;
    }
}
