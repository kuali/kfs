/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/impl/ChartServiceImpl.java,v $
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
package org.kuali.module.chart.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.kuali.core.bo.user.UniversalUser;

import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.dao.ChartDao;
import org.kuali.module.chart.service.ChartService;

/**
 * This class is the service implementation for the Chart structure. This is the default, Kuali delivered implementation.
 * 
 * 
 */
public class ChartServiceImpl implements ChartService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartServiceImpl.class);

    private ChartDao chartDao;

    /**
     * @see org.kuali.module.chart.service.ChartService#getByPrimaryId(java.lang.String)
     */
    public Chart getByPrimaryId(String chartOfAccountsCode) {
        return chartDao.getByPrimaryId(chartOfAccountsCode);
    }

    public Chart getUniversityChart() {
        return chartDao.getUniversityChart();
    }

    /**
     * @see org.kuali.module.chart.service.ChartService#getAllChartCodes()
     */
    public List getAllChartCodes() {
        Collection charts = chartDao.getAll();
        List chartCodes = new ArrayList();
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            Chart element = (Chart) iter.next();
            chartCodes.add(element.getChartOfAccountsCode());
        }

        return chartCodes;
    }


    /**
     * @see org.kuali.module.chart.service.getReportsToHierarchy()
     */
    public Map<String, String> getReportsToHierarchy() {

        LOG.debug("getReportsToHierarchy");
        Map<String, String> reportsToHierarchy = new HashMap();

        Iterator iter = getAllChartCodes().iterator();
        while (iter.hasNext()) {
            Chart chart = (Chart) getByPrimaryId((String) iter.next());

            if (LOG.isDebugEnabled()) {
                LOG.debug("adding " + chart.getChartOfAccountsCode() + "-->" + chart.getReportsToChartOfAccountsCode());
            }
            reportsToHierarchy.put(chart.getChartOfAccountsCode(), chart.getReportsToChartOfAccountsCode());
        }

        return reportsToHierarchy;
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ChartService#getChartsThatUserIsResponsibleFor(org.kuali.core.bo.user.KualiUser)
     */
    public List getChartsThatUserIsResponsibleFor(UniversalUser universalUser) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving chartsResponsible list for user " + universalUser.getPersonName());
        }

        // gets the list of accounts that the user is the Fiscal Officer of
        List chartList = chartDao.getChartsThatUserIsResponsibleFor(universalUser);
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved chartsResponsible list for user " + universalUser.getPersonName());
        }
        return chartList;
    }

    /**
     * @return Returns the chartDao.
     */
    public ChartDao getChartDao() {
        return chartDao;
    }

    /**
     * @param chartDao The chartDao to set.
     */
    public void setChartDao(ChartDao chartDao) {
        this.chartDao = chartDao;
    }

}