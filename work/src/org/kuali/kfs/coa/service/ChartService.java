/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This interface defines methods that a Chart Service must provide
 */
public interface ChartService {
    /**
     * Retrieves a chart object by its primary key - the chart code.
     *
     * @param chartOfAccountsCode
     * @return
     */
    public Chart getByPrimaryId(String chartOfAccountsCode);

    /**
     *
     * This method returns the university chart
     * @return
     */
    public Chart getUniversityChart();


    /**
     * Retrieves all of the charts in the system and returns them in a List.
     *
     * @return A List of chart codes.
     */
    public List<String> getAllChartCodes();

    /**
     * Retrieves all of the "active" charts in the system in chart code order.
     *
     * @return
     */
    Collection<Chart> getAllActiveCharts();

    /**
     * Retrieves a map of reportsTo relationships (e.g. A reports to B, B reports to B, C reports to A)
     *
     * @return
     */
    public Map<String, String> getReportsToHierarchy();

    /**
     * Returns the chart manager form KIM for the given chart code
     *
     * @param chartOfAccountsCode chart code to get manager for
     * @return chart manager <code>Person</code>
     */
    public Person getChartManager(String chartOfAccountsCode);

    /**
     * This method traverses the hierarchy to see if the potentialChildChartCode reports to the potentialParentChartCode
     *
     * @param potentialChildChartCode
     * @param potentialParentChartCode
     * @return boolean indicating whether the first parameter reports to the second
     */
    public boolean isParentChart(String potentialChildChartCode, String potentialParentChartCode);
}

