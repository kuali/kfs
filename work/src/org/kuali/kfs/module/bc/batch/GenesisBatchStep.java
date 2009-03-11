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
package org.kuali.kfs.module.bc.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PersistenceStructureService;

import org.kuali.kfs.module.bc.util.BudgetParameterFinder;

public class GenesisBatchStep extends AbstractStep {

    private GenesisService genesisService;
    
    // parameter constants and logging
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_CODE = "KFS-BC";
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = "GenesisBatchStep";
    private static final String RUN_INDICATOR_PARAMETER_VALUE = "N";
    private static final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    private static final String RUN_INDICATOR_PARAMETER_DESCRIPTION = "Tells the job framework whether to run this job or not; set to know because the GenesisBatchJob needs to only be run once after database initialization.";
    private static final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";
    
    

    public boolean execute(String jobName, Date jobRunDate) {
        genesisService = SpringContext.getBean(GenesisService.class);
        genesisService.genesisStep(BudgetParameterFinder.getBaseFiscalYear());
        setInitiatedParameter();
        return true;
    }
    
    /**
     * This method sets a parameter that tells the step that it has already run and it does not need to run again.
     */
    private void setInitiatedParameter() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        PersistenceStructureService psService = SpringContext.getBean(PersistenceStructureService.class);
        // first see if we can find an existing Parameter object with this key
        Parameter runIndicatorParameter = (Parameter) boService.findByPrimaryKey(Parameter.class, this.buildSearchKeyMap());
        if (runIndicatorParameter == null)
        {
           runIndicatorParameter = new Parameter();
           runIndicatorParameter.setVersionNumber(new Long(1));
           runIndicatorParameter.setParameterNamespaceCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);
           runIndicatorParameter.setParameterDetailTypeCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_STEP);
           runIndicatorParameter.setParameterName(Job.STEP_RUN_PARM_NM);
           runIndicatorParameter.setParameterDescription(GenesisBatchStep.RUN_INDICATOR_PARAMETER_DESCRIPTION);
           runIndicatorParameter.setParameterConstraintCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_ALLOWED);
           runIndicatorParameter.setParameterTypeCode(GenesisBatchStep.RUN_INDICATOR_PARAMETER_TYPE);
        }
        runIndicatorParameter.setParameterValue(GenesisBatchStep.RUN_INDICATOR_PARAMETER_VALUE);
        boService.save(runIndicatorParameter);
    }
    
    private Map<String,Object> buildSearchKeyMap()
    {
       Map<String,Object> pkMapForParameter = new HashMap<String,Object>();
       PersistenceStructureService psService = SpringContext.getBean(PersistenceStructureService.class);

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

       // get the primary keys and assign them to values
       List<String> parameterPKFields = psService.getPrimaryKeys(Parameter.class);
       for (String pkFieldName: parameterPKFields)
       {
           pkMapForParameter.put(pkFieldName,fieldNamesValuesForParameter.get(pkFieldName));
       }
       return (pkMapForParameter);
    }
}
