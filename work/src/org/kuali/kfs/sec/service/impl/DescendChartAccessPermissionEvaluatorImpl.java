/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Custom access permission evaluator that looks at the chart hierarchy when matching values
 */
public class DescendChartAccessPermissionEvaluatorImpl extends AccessPermissionEvaluatorImpl {

    /**
     * Sets the match values to the chart code and its child charts (if any)
     * 
     * @see org.kuali.kfs.sec.service.impl.AccessPermissionEvaluatorImpl#setMatchValues()
     */
    @Override
    protected void setMatchValues() {
        ChartService chartService = SpringContext.getBean(ChartService.class);

        List<String> matchChartCodes = new ArrayList<String>();
        matchChartCodes.add(propertyValue);

        List<String> allChartCodes = chartService.getAllChartCodes();
        for (String chart : allChartCodes) {
            if (chartService.isParentChart(chart, propertyValue)) {
                matchChartCodes.add(chart);
            }
        }

        matchValues = new String[matchChartCodes.size()];
        matchValues = (String[]) matchChartCodes.toArray(matchValues);
    }

}
