/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch.service.impl;

import org.kuali.kfs.sys.batch.service.LockModuleService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class LockModuleServiceImpl implements LockModuleService {
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    
    public void lockModule(String namespaceCode , boolean lockModule) {
        Parameter parameter  = parameterService.getParameter(namespaceCode, KfsParameterConstants.PARAMETER_ALL_DETAIL_TYPE, KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_ACTIVE_IND);
        Parameter.Builder updatedParameter = Parameter.Builder.create(parameter);
        if (lockModule) {
            updatedParameter.setValue("Y");
        }
        else {
            updatedParameter.setValue("N");
        }
        parameterService.updateParameter(updatedParameter.build());
        
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}
