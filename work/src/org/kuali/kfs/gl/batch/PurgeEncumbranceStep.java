/*
 * Copyright 2006 The Kuali Foundation
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
/*
 * Created on Apr 7, 2006
 *
 */
package org.kuali.kfs.gl.batch;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * A step to remove old encumbrances from the database.
 */
public class PurgeEncumbranceStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeEncumbranceStep.class);
    private ChartService chartService;
    private EncumbranceService encumbranceService;

    /**
     * This step will purge data from the gl_encumbrance_t table older than a specified year. It purges the data one chart at a time
     * each within their own transaction so database transaction logs don't get completely filled up when doing this. This step
     * class should NOT be transactional.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the step completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        String yearStr = getParameterService().getParameterValueAsString(getClass(), KFSConstants.SystemGroupParameterNames.PURGE_GL_ENCUMBRANCE_T_BEFORE_YEAR);
        LOG.info("PurgeEncumbranceStep was run with year = "+yearStr);
        int year = Integer.parseInt(yearStr);
        List charts = chartService.getAllChartCodes();
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            String chart = (String) iter.next();
            encumbranceService.purgeYearByChart(chart, year);
        }
        return true;
    }

    /**
     * Sets the encumbranceService attribute, allowing the injection of an implementation of the service.
     * 
     * @param encumbranceService the encumbranceService implementation to set
     * @see org.kuali.kfs.gl.service.EncumbranceService
     */
    public void setEncumbranceService(EncumbranceService encumbranceService) {
        this.encumbranceService = encumbranceService;
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
}
