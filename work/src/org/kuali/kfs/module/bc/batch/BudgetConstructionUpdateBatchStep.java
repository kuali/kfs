/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.batch.AbstractStep;

public class BudgetConstructionUpdateBatchStep extends AbstractStep {

    private GenesisService genesisService;
    
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        genesisService = SpringContext.getBean(GenesisService.class);    
        //  @TODO: this is set up to run for fiscal year 2008 (base year is 2007)
        //  genesisFiscalYearFromToday in the genesis Service is the preferred source for the base fiscal year
        genesisService.bCUpdateStep(2007);
        return true;
    }

}
