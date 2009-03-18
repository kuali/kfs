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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * Provides option values the threshold settings operator field.
 */
public class ThresholdSettingsOperatorValuesFinder extends KeyValuesBase implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyLabelPair> keyLabels = new ArrayList<KeyLabelPair>();
        keyLabels.add(new KeyLabelPair(KFSConstants.ParameterValues.YES, "greater than or equal to threshold"));
        keyLabels.add(new KeyLabelPair(KFSConstants.ParameterValues.NO, "less than or equal to threshold"));

        return keyLabels;
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return KFSConstants.ParameterValues.YES;
    }
}
