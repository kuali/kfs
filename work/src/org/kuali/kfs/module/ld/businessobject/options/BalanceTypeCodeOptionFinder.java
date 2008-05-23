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
package org.kuali.module.labor.web.optionfinder;

import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_ACTUAL;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE;
import static org.kuali.module.labor.LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.web.ui.KeyLabelPair;

/**
 * Option Finder for Labor Balance Type Code.
 */
public class BalanceTypeCodeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new KeyLabelPair(BALANCE_TYPE_ACTUAL, "Actual"));
        labels.add(new KeyLabelPair(BALANCE_TYPE_AC_AND_A21, "A21"));
        labels.add(new KeyLabelPair(BALANCE_TYPE_INTERNAL_ENCUMBRANCE, "Internal Encumbrance"));

        return labels;
    }

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return BALANCE_TYPE_ACTUAL;
    }
}
