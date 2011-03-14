/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.external.kc.service.KcAccountService;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.ParameterService;

public class KcAccountServiceImpl implements KcAccountService {
    private ParameterService parameterService;


    public boolean accountsCanCrossCharts() {
        return !parameterService.getIndicatorParameter(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, SystemGroupParameterNames.ACCOUNTS_CAN_CROSS_CHARTS_IND);
    }
    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */    
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
