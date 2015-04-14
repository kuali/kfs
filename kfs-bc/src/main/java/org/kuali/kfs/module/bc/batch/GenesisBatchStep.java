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
package org.kuali.kfs.module.bc.batch;

import java.util.Date;

import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.api.parameter.EvaluationOperator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.Parameter.Builder;
import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceStructureService;

public class GenesisBatchStep extends AbstractStep {

    private GenesisService genesisService;
    private PersistenceStructureService psService;
    private BusinessObjectService boService;

    // parameter constants and logging
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_CODE = "KFS-BC";
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = "GenesisBatchStep";
    private static final String RUN_INDICATOR_PARAMETER_VALUE = "N";
    private static final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    private static final String RUN_INDICATOR_PARAMETER_DESCRIPTION = "Tells the job framework whether to run this job or not; set to know because the GenesisBatchJob needs to only be run once after database initialization.";
    private static final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";
    private static final String RUN_INDICATOR_PARAMETER_APPLICATION_NAMESPACE_CODE = "KFS";



    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        genesisService.genesisStep(BudgetParameterFinder.getBaseFiscalYear());
        setInitiatedParameter();
        return true;
    }

    /**
     * This method sets a parameter that tells the step that it has already run and it does not need to run again.
     */
    private void setInitiatedParameter() {
        // first see if we can find an existing Parameter object with this key
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        Parameter runIndicatorParameter = parameterService.getParameter(GenesisBatchStep.class, Job.STEP_RUN_PARM_NM);
        if (runIndicatorParameter == null) {
            Parameter.Builder newParameter = Builder.create(RUN_INDICATOR_PARAMETER_APPLICATION_NAMESPACE_CODE, RUN_INDICATOR_PARAMETER_NAMESPACE_CODE, RUN_INDICATOR_PARAMETER_NAMESPACE_STEP, Job.STEP_RUN_PARM_NM, ParameterType.Builder.create(RUN_INDICATOR_PARAMETER_TYPE));
            newParameter.setValue(RUN_INDICATOR_PARAMETER_VALUE);
            newParameter.setEvaluationOperator( EvaluationOperator.ALLOW );
            newParameter.setDescription(RUN_INDICATOR_PARAMETER_DESCRIPTION);
            parameterService.createParameter(newParameter.build());
        } else {
            Parameter.Builder updatedParameter = Parameter.Builder.create(runIndicatorParameter);
            updatedParameter.setValue(GenesisBatchStep.RUN_INDICATOR_PARAMETER_VALUE);
            parameterService.updateParameter(updatedParameter.build());
        }
    }

    public void setGenesisService (GenesisService genesisService)
    {
        this.genesisService = genesisService;
    }

    public void setPersistenceStructureService (PersistenceStructureService psService)
    {
        this.psService = psService;
    }

    public void setBusinessObjectService (BusinessObjectService boService)
    {
        this.boService = boService;
    }
}
