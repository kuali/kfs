/*
 * Copyright 2008 The Kuali Foundation.
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
 * An implmentation of ValueFinder that allows the balance inquiries to choose whether to show blank entries
 */
public class BlankLineOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value for this ValueFinder, in this case, exclude cost share entries
     * @return a String with the default key
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return Constant.NOT_SHOW_BLANK_LINE;
    }

    /**
     * Returns a list of possible values for this ValueFinder, in this case include cost share entries or exclude cost share entries
     * @return a List of key/value pairs to populate radio buttons
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair(Constant.NOT_SHOW_BLANK_LINE, Constant.NOT_SHOW_BLANK_LINE));
        labels.add(new KeyLabelPair(Constant.SHOW_BLANK_LINE, Constant.SHOW_BLANK_LINE));
        return labels;
    }
}
