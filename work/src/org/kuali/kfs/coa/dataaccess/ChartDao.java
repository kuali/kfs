/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/dataaccess/ChartDao.java,v $
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
package org.kuali.module.chart.dao;

import java.util.Collection;
import java.util.List;


import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Chart;

/**
 * This is the data access interface for Chart objects.
 * 
 * 
 */
public interface ChartDao {
    public Collection getAll();

    public Chart getUniversityChart();

    public Chart getByPrimaryId(String chartOfAccountsCode);

    public List getChartsThatUserIsResponsibleFor(UniversalUser kualiUser);
}