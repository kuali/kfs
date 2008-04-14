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
package org.kuali.module.budget.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.ParameterNamespace;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.batch.Job;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.service.GenesisService;

public class GenesisBatchStep extends AbstractStep {

    private GenesisService genesisService;
    
    // parameter constants and logging
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_CODE = "KFS-BC";
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = "GenesisBatchStep";
    private static final String RUN_INDICATOR_PARAMETER_VALUE = "N";
    private static final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    private static final String RUN_INDICATOR_PARAMETER_DESCRIPTION = "Tells the job framework whether to run this job or not; set to know because the GenesisBatchJob needs to only be run once after database initialization.";
    private static final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";
    private static final String RUN_INDICATOR_PARAMETER_WORKGROUP = "FP_OPERATIONS";

    public boolean execute(String jobName, Date jobRunDate) {
        genesisService = SpringContext.getBean(GenesisService.class);
        // @@TODO: in production, we will use the current fiscal year, not the last one
        Integer baseFiscalYear = genesisService.genesisFiscalYearFromToday() - 1;
        genesisService.genesisStep(baseFiscalYear);
        setInitiatedParameter();
        return true;
    }
    
    /**
     * This method sets a parameter that tells the step that it has already run and it does not need to run again.
     */
    private void setInitiatedParameter() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        
        Parameter runIndicatorParameter = new Parameter();
        runIndicatorParameter.setParameterNamespaceCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);
        runIndicatorParameter.setParameterDetailTypeCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_STEP);
        runIndicatorParameter.setParameterName(Job.STEP_RUN_PARM_NM);
        runIndicatorParameter.setParameterValue(GenesisBatchStep.RUN_INDICATOR_PARAMETER_VALUE);
        runIndicatorParameter.setParameterConstraintCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_ALLOWED);
        runIndicatorParameter.setParameterTypeCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_TYPE);
        runIndicatorParameter.setParameterWorkgroupName(GenesisBatchStep.RUN_INDICATOR_PARAMETER_WORKGROUP);
        runIndicatorParameter.setVersionNumber(new Long(1));
        boService.save(runIndicatorParameter);
    }
}
