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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * This class returns list of balance type value pairs.
 */
public class BalanceTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Collection balanceTypeCodeCollection = SpringServiceLocator.getBalanceTypService().getAllBalanceTyps();
        List balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(new KeyLabelPair("", ""));

        Iterator iterator = balanceTypeCodeCollection.iterator();
        while (iterator.hasNext()) {
            BalanceTyp balanceType = (BalanceTyp) iterator.next();
            String element = balanceType.getCode();

            balanceTypeCodes.add(new KeyLabelPair(element, element));
        }
        return balanceTypeCodes;
    }
}
