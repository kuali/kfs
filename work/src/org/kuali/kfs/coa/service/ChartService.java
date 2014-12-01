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
     * Retrieves all of the charts in the system and returns them in a List.
     *
     * @return A List of ACTIVE chart codes.
     */
    public List<String> getAllActiveChartCodes();

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

