/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;

public class ResearchRiskTypeNotificationValuesFinder extends KeyValuesBase {

    /**
     * 
     * Constructs a ResearchRiskTypeNotificationValuesFinder.java.
     */
    public ResearchRiskTypeNotificationValuesFinder() {
        super();
    }
    
    /**
     * Builds a collection of possible values to be selected from.  These values are used to build out a
     * drop down list for user selection.
     * 
     * @return A list of KeyLabelPair objects.
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        
        List<KeyLabelPair> notificationValuesPairList = new ArrayList(4);
        
        notificationValuesPairList.add(new KeyLabelPair(ResearchRiskType.YES, ResearchRiskType.YES+" (Yes)"));
        notificationValuesPairList.add(new KeyLabelPair(ResearchRiskType.NO, ResearchRiskType.NO+" (No)"));
        notificationValuesPairList.add(new KeyLabelPair(ResearchRiskType.ALL, ResearchRiskType.ALL+" (All)"));
        notificationValuesPairList.add(new KeyLabelPair(ResearchRiskType.NEVER, ResearchRiskType.NEVER+" (Never)"));
        
        return notificationValuesPairList;
    }
    
}
