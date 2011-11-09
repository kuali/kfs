/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.EvaluationOperator;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.api.parameter.Parameter.Builder;
import org.kuali.rice.core.api.parameter.ParameterType;
import org.kuali.rice.core.framework.parameter.ParameterService;
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
    
    private Map<String,Object> buildSearchKeyMap()
    {
       Map<String,Object> pkMapForParameter = new HashMap<String,Object>();

       // set up a list of all the  field names and values of the fields in the Parameter object.
       // the OJB names are nowhere in Kuali properties, apparently.
       // but, since we use set routines above, we know what the names must be.  if they change at some point, we will have to change the set routines anyway.
       // we can change the code here also when we do that.
       Map<String,Object> fieldNamesValuesForParameter = new HashMap<String,Object>();
       fieldNamesValuesForParameter.put("parameterNamespaceCode",GenesisBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);
       fieldNamesValuesForParameter.put("parameterDetailTypeCode",GenesisBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_STEP);
       fieldNamesValuesForParameter.put("parameterName",Job.STEP_RUN_PARM_NM);
       fieldNamesValuesForParameter.put("parameterConstraintCode",GenesisBatchStep.RUN_INDICATOR_PARAMETER_ALLOWED);
       fieldNamesValuesForParameter.put("parameterTypeCode",GenesisBatchStep.RUN_INDICATOR_PARAMETER_TYPE);
       fieldNamesValuesForParameter.put("parameterApplicationNamespaceCode",GenesisBatchStep.RUN_INDICATOR_PARAMETER_APPLICATION_NAMESPACE_CODE);
       
       // get the primary keys and assign them to values
       List<String> parameterPKFields = psService.getPrimaryKeys(Parameter.class);
       for (String pkFieldName: parameterPKFields)
       {
           pkMapForParameter.put(pkFieldName,fieldNamesValuesForParameter.get(pkFieldName));
       }
       return (pkMapForParameter);
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
