/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/ChartService.java,v $
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
package org.kuali.module.chart.service;

import java.util.List;
import java.util.Map;


import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Chart;

/**
 * This interface defines methods that a Chart Service must provide
 * 
 * 
 */
public interface ChartService {
    /**
     * Retrieves a chart object by its primary key - the chart code.
     * 
     * @param chartOfAccountsCode
     * @return
     */
    public Chart getByPrimaryId(String chartOfAccountsCode);

    public Chart getUniversityChart();

    /**
     * Retrieves all of the charts in the system and returns them in a List.
     * 
     * @return A List of chart objects.
     */
    public List getAllChartCodes();

    /**
     * 
     * Retrieves a map of reportsTo relationships (e.g. A reports to B, B reports to B, C reports to A)
     * 
     * @return
     */
    public Map<String, String> getReportsToHierarchy();

    /**
     * 
     * Retrieves a list of chart objects that the User is responsible for
     * 
     * @param kualiUser
     * @return
     */
    public List getChartsThatUserIsResponsibleFor(UniversalUser kualiUser);

}