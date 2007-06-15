/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.chart.batch;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.budget.service.DateMakerService;

/**
 * This is the batch step implementation for Fiscal Year Maker. It brings forward the appropriate rows from certain chart, gl, and
 * labor tables specified by the makerObjectsList property of the fiscalYearMakersDao bean.
 */
public class FiscalYearMakerStep extends AbstractStep {
    private DateMakerService dateMakerService;

    /**
     * Calls the fiscalYearMakers method of DateMakerService, passing the
     * KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_SOURCE_FISCAL_YEAR and KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_REPLACE_MODE
     * parameters from the KFSConstants.ParameterGroups.CHART_FISCAL_YEAR_MAKER security group.
     * 
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) throws InterruptedException {
        dateMakerService.fiscalYearMakers(Integer.parseInt(getConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.CHART_FISCAL_YEAR_MAKER, KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_SOURCE_FISCAL_YEAR)), getConfigurationService().getApplicationParameterIndicator(KFSConstants.ParameterGroups.CHART_FISCAL_YEAR_MAKER, KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_REPLACE_MODE));
        return true;
    }

    /**
     * For Spring.
     * 
     * @param dateMakerService
     */
    public void setDateMakerService(DateMakerService dateMakerService) {
        this.dateMakerService = dateMakerService;
    }
}
