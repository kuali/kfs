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
package org.kuali.module.gl.batch;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.batch.closing.year.service.YearEndService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OrganizationReversionProcessService;
import org.springframework.util.StopWatch;

public class OrganizationReversionBeginningOfYearStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionEndOfYearStep.class);
    private OrganizationReversionProcessService organizationReversionProcessService;

    public boolean execute(String jobName) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        
        OriginEntryGroup outputGroup = organizationReversionProcessService.createOrganizationReversionProcessOriginEntryGroup();
        Map jobParameters = organizationReversionProcessService.getJobParameters();
        Map<String, Integer> organizationReversionCounts = new HashMap<String, Integer>();
        
        YearEndService yearEndService = SpringContext.getBean(YearEndService.class);
        yearEndService.logAllMissingPriorYearAccounts((Integer)jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
        yearEndService.logAllMissingSubFundGroups((Integer)jobParameters.get(KFSConstants.UNIV_FISCAL_YR));
        
        organizationReversionProcessService.organizationReversionProcessBeginningOfYear(outputGroup, jobParameters, organizationReversionCounts);
        
        organizationReversionProcessService.generateOrganizationReversionProcessReports(outputGroup, jobParameters, organizationReversionCounts);
        
        stopWatch.stop();
        LOG.info(jobName+" took "+(stopWatch.getTotalTimeSeconds()/60.0)+" minutes to complete");
        return true;
    }

    public void setOrganizationReversionProcessService(OrganizationReversionProcessService organizationReversionProcessService) {
        this.organizationReversionProcessService = organizationReversionProcessService;
    }
}
