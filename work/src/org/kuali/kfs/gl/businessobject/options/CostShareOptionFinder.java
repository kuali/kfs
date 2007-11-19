/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.optionfinder;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.gl.web.Constant;

/**
 * An implmentation of ValueFinder that allows the balance inquiries to choose whether to include or exclude cost share entries
 */
public class GLCostShareOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value for this ValueFinder, in this case, exclude cost share entries
     * @return a String with the default key
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return Constant.COST_SHARE_EXCLUDE;
    }

    /**
     * Returns a list of possible values for this ValueFinder, in this case include cost share entries or exclude cost share entries
     * @return a List of key/value pairs to populate radio buttons
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new KeyLabelPair(Constant.COST_SHARE_INCLUDE, Constant.COST_SHARE_INCLUDE));
        labels.add(new KeyLabelPair(Constant.COST_SHARE_EXCLUDE, Constant.COST_SHARE_EXCLUDE));
        return labels;
    }
}
