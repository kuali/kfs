/*
 * Copyright 2006-2007 The Kuali Foundation.
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
/*
 * Created on Apr 7, 2006
 *
 */
package org.kuali.module.gl.batch;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.gl.service.EntryService;

public class PurgeEntryStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeEntryStep.class);
    private ChartService chartService;
    private EntryService entryService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * This step will purge data from the gl_entry_t table older than a specified year. It purges the data one chart at a time each
     * within their own transaction so database transaction logs don't get completely filled up when doing this. This step class
     * should NOT be transactional.
     */
    public boolean execute(String jobName) {
        String yearStr = kualiConfigurationService.getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM, KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR);
        int year = Integer.parseInt(yearStr);
        List charts = chartService.getAllChartCodes();
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            String chart = (String) iter.next();
            entryService.purgeYearByChart(chart, year);
        }
        return true;
    }

    public void setEntryService(EntryService entryService) {
        this.entryService = entryService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
