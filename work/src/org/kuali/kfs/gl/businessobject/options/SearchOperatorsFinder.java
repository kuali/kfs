/*
 * Copyright 2006 The Kuali Foundation.
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
import org.kuali.core.web.uidraw.KeyLabelPair;

public class SearchOperatorsFinder extends KeyValuesBase {

    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new KeyLabelPair("eq", "Equals"));
        activeLabels.add(new KeyLabelPair("ne", "Not equal to"));
        // activeLabels.add(new KeyLabelPair("gt", "Greater than"));
        // activeLabels.add(new KeyLabelPair("lt", "Less than"));
        activeLabels.add(new KeyLabelPair("sw", "Starts with"));
        activeLabels.add(new KeyLabelPair("ew", "Ends with"));
        activeLabels.add(new KeyLabelPair("ct", "Contains"));
        return activeLabels;
    }
}
