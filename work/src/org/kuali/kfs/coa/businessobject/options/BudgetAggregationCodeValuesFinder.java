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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.coa.businessobject.BudgetAggregationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class returns list of Budget Aggregation Code type value pairs.
 */
public class BudgetAggregationCodeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link BudgetAggregationCode}s using their code as their key, and their code "-" aggregation code as the
     * display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all budget aggregations codes
        List<BudgetAggregationCode> budgetAggregationCodes = (List<BudgetAggregationCode>) SpringContext.getBean(KeyValuesService.class).findAll(BudgetAggregationCode.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( budgetAggregationCodes == null ) {
            budgetAggregationCodes = new ArrayList<BudgetAggregationCode>(0);
        } else {
            budgetAggregationCodes = new ArrayList<BudgetAggregationCode>( budgetAggregationCodes );
        }

        // sort using comparator.
        Collections.sort(budgetAggregationCodes, new BudgetAggregationCodeComparator());

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>();

        for (BudgetAggregationCode budgetAggregationCode : budgetAggregationCodes) {
            if(budgetAggregationCode.isActive()) {
                labels.add(new ConcreteKeyValue(budgetAggregationCode.getCode(), budgetAggregationCode.getCodeAndDescription()));
            }
        }

        return labels;
    }

}
