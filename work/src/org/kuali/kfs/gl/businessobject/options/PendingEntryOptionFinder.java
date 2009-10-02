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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.Constant;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * An implementation of ValueFinder that allows inquirers to include no pending entries, approved pending entries,
 * or all pending entries in the results of their lookup
 */
public class PendingEntryOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns a list of key/value pairs for this ValueFinder, in this case no pending entries, approved pending entries, and all pending entries
     * @return a List of key/value pairs to populate a control
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        List labels = new ArrayList();
        labels.add(new KeyLabelPair(Constant.NO_PENDING_ENTRY, Constant.NO_PENDING_ENTRY));
        labels.add(new KeyLabelPair(Constant.APPROVED_PENDING_ENTRY, Constant.APPROVED_PENDING_ENTRY));
        labels.add(new KeyLabelPair(Constant.ALL_PENDING_ENTRY, Constant.ALL_PENDING_ENTRY));

        return labels;
    }

    /**
     * Returns the default value for this ValueFinder, in this case no pending entries
     * @return the key of the default value
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return Constant.NO_PENDING_ENTRY;
    }
}
