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

import java.util.Comparator;

import org.kuali.module.chart.bo.codes.BudgetAggregationCode;

public class BudgetAggregationCodeComparator implements Comparator {

    public BudgetAggregationCodeComparator() {
    }

    public int compare(Object o1, Object o2) {

        BudgetAggregationCode obj1 = (BudgetAggregationCode) o1;
        BudgetAggregationCode obj2 = (BudgetAggregationCode) o2;

        return obj1.getCode().compareTo(obj2.getCode());
    }

}
