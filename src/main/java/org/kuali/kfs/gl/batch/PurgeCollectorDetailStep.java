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
package org.kuali.kfs.gl.batch;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.gl.service.CollectorDetailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * A step to purge old collector details from the database.
 */
public class PurgeCollectorDetailStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeCollectorDetailStep.class);
    private ChartService chartService;
    private CollectorDetailService collectorDetailService;

    /**
     * This step will purge data from the gl_id_bill_t table older than a specified year. It purges the data one chart at a time
     * each within their own transaction so database transaction logs don't get completely filled up when doing this. This step
     * class should NOT be transactional.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        String yearStr = getParameterService().getParameterValueAsString(getClass(), KFSConstants.SystemGroupParameterNames.PURGE_GL_ID_BILL_T_BEFORE_YEAR);
        LOG.info("PurgeCollectorDetailStep was run with year = "+yearStr);
        int year = Integer.parseInt(yearStr);
        List charts = chartService.getAllChartCodes();
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            String chart = (String) iter.next();
            collectorDetailService.purgeYearByChart(chart, year);
        }
        return true;
    }

    /**
     * Sets the chartService attribute, allowing the injection of an implementation of the service.
     * 
     * @param chartService the chartService implementation to set
     * @see org.kuali.kfs.coa.service.ChartService
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * Sets the collectorDetailService attribute, allowing the injection of an implementation of the service.
     * 
     * @param collectorDetailService the collectorDetailService implementation to set
     * @see org.kuali.kfs.gl.service.CollectorDetailService
     */
    public void setCollectorDetailService(CollectorDetailService collectorDetailService) {
        this.collectorDetailService = collectorDetailService;
    }
}
