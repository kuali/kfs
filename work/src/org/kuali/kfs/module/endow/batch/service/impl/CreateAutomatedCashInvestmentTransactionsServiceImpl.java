/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import org.kuali.kfs.module.endow.batch.service.CreateAutomatedCashInvestmentTransactionsService;
import org.kuali.rice.kns.service.ParameterService;

public class CreateAutomatedCashInvestmentTransactionsServiceImpl implements CreateAutomatedCashInvestmentTransactionsService{

    private ParameterService parameterService;
    
       
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateAutomatedCashInvestmentTransactionsService#createACITransactions()
     */
    public boolean createACITransactions() {
        return false;        
    }
    
    /**
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
        

}
