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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService;
import org.kuali.kfs.gl.batch.service.YearEndService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.springframework.util.StopWatch;

/**
 * A step that runs the reversion and carry forward process. The beginning of year version of the process is supposed to be run at
 * the beginning of a fiscal year, and therefore, it uses prior year accounts instead of current year accounts.
 */
public class OrganizationReversionCurrentYearAccountStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionCurrentYearAccountStep.class);
    private OrganizationReversionProcessService organizationReversionProcessService;
    private YearEndService yearEndService;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * Runs the organization reversion process, retrieving parameter, creating the origin entry group for output entries, and
             * generating the reports on the process.
             * @return true if the job completed successfully, false if otherwise
             * @see org.kuali.kfs.sys.batch.Step#execute(String, java.util.Date)
             */
            public boolean execute() {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("OrganizationReversionCurrentYearAccountStep");

                Map jobParameters = organizationReversionProcessService.getJobParameters();
                Map<String, Integer> organizationReversionCounts = new HashMap<String, Integer>();

                getYearEndService().logAllMissingSubFundGroups((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR));

                getOrganizationReversionProcessService().organizationReversionCurrentYearAccountProcess(jobParameters, organizationReversionCounts);

                stopWatch.stop();
                LOG.info("OrganizationReversionCurrentYearAccountStep took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
                return true;
            }
        };
    }
    
    /**
     * Sets the organizationReversionProcessService (not to be confused with the OrganizationReversionService, which doesn't do a
     * process, but which does all the database stuff associated with OrganizationReversion records; it's off in Chart), which
     * allows the injection of an implementation of the service.
     * 
     * @param organizationReversionProcessService the implementation of the organizationReversionProcessService to set
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService
     */
    public void setOrganizationReversionProcessService(OrganizationReversionProcessService organizationReversionProcessService) {
        this.organizationReversionProcessService = organizationReversionProcessService;
    }
    
    /**
     * Gets the yearEndService attribute. 
     * @return Returns the yearEndService.
     */
    public YearEndService getYearEndService() {
        return yearEndService;
    }

    /**
     * Sets the yearEndService attribute value.
     * @param yearEndService The yearEndService to set.
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

    /**
     * Gets the organizationReversionProcessService attribute. 
     * @return Returns the organizationReversionProcessService.
     */
    public OrganizationReversionProcessService getOrganizationReversionProcessService() {
        return organizationReversionProcessService;
    }
}
