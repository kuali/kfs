/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.dao.ChartDao;
import org.kuali.module.chart.service.ChartService;

/**
 * This class is the service implementation for the Chart structure. This is the default, Kuali delivered implementation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
    public Map<String,String> getReportsToHierarchy() {

        LOG.debug("getReportsToHierarchy");
        Map<String,String> reportsToHierarchy = new HashMap();

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
    public List getChartsThatUserIsResponsibleFor(KualiUser kualiUser) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving chartsResponsible list for user " + kualiUser.getPersonName());
        }

        // gets the list of accounts that the user is the Fiscal Officer of
        List chartList = chartDao.getChartsThatUserIsResponsibleFor(kualiUser);

        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved chartsResponsible list for user " + kualiUser.getPersonName());
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