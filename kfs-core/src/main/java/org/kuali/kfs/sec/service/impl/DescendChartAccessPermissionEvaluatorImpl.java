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
