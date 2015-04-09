/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
