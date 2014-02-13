/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service.impl;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.service.ContractsGrantsBillingService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This Class provides the implementation to the services required for Contracts Grants Billing (CGB).
 */
public class ContractsGrantsBillingServiceImpl implements ContractsGrantsBillingService {

    private ParameterService parameterService;

    /**
     * Returns an implementation of the parameterService
     *
     * @return an implementation of the parameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }


    /**
     * Retrieve the boolean value for whether CG and Billing Enhancements are on from system parameter
     * @return true if parameter ENABLE_CG_BILLING_ENHANCEMENTS_IND is set to "Y"; otherwise false.
     */
    @Override
    public boolean isContractsGrantsBillingEnhancementsActive() {
        return getParameterService().getParameterValueAsBoolean(KfsParameterConstants.CONTRACTS_AND_GRANTS_ALL.class, CGConstants.ENABLE_CG_BILLING_ENHANCEMENTS_IND);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}

