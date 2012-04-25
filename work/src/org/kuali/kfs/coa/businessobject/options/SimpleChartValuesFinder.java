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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of chart key value pairs with the key and label both being the chart code.
 */
public class SimpleChartValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link Chart}s using their code as their key, and their code as the display value
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        Collection<Chart> chartCodes = SpringContext.getBean(ChartService.class).getAllActiveCharts();

        List<KeyValue> chartKeyLabels = new ArrayList<KeyValue>();
        chartKeyLabels.add(new ConcreteKeyValue("", ""));
        for ( Chart chart : chartCodes ) {
            chartKeyLabels.add(new ConcreteKeyValue(chart.getChartOfAccountsCode(), chart.getChartOfAccountsCode()));
        }

        return chartKeyLabels;
    }

}
