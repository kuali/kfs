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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;

/**
 * This class returns list of chart key value pairs.
 */
public class ChartValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link Chart} using their code as the key and their code "-" description
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection chartCodes = boService.findAll(Chart.class);
        List chartKeyLabels = new ArrayList();
        chartKeyLabels.add(new KeyLabelPair("", ""));
        for (Iterator iter = chartCodes.iterator(); iter.hasNext();) {
            Chart element = (Chart) iter.next();
            chartKeyLabels.add(new KeyLabelPair(element.getChartOfAccountsCode(), element.getChartOfAccountsCode() + " - " + element.getFinChartOfAccountDescription()));
        }

        return chartKeyLabels;
    }

}