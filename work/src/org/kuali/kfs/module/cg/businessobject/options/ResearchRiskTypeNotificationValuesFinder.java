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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class ResearchRiskTypeNotificationValuesFinder extends KeyValuesBase {

    /**
     * Constructs a ResearchRiskTypeNotificationValuesFinder.java.
     */
    public ResearchRiskTypeNotificationValuesFinder() {
        super();
    }

    /**
     * Builds a collection of possible values to be selected from. These values are used to build out a drop down list for user
     * selection.
     * 
     * @return A list of KeyValue objects.
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        List<KeyValue> notificationValuesPairList = new ArrayList(4);

        notificationValuesPairList.add(new ConcreteKeyValue(ResearchRiskType.YES, ResearchRiskType.YES + " (Yes)"));
        notificationValuesPairList.add(new ConcreteKeyValue(ResearchRiskType.NO, ResearchRiskType.NO + " (No)"));
        notificationValuesPairList.add(new ConcreteKeyValue(ResearchRiskType.ALL, ResearchRiskType.ALL + " (All)"));
        notificationValuesPairList.add(new ConcreteKeyValue(ResearchRiskType.NEVER, ResearchRiskType.NEVER + " (Never)"));

        return notificationValuesPairList;
    }

}
