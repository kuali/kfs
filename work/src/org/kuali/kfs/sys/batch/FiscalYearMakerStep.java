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
package org.kuali.kfs.sys.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.service.FiscalYearMakerService;

/**
 * Batch step implementation for Fiscal Year Maker
 */
public class FiscalYearMakerStep extends AbstractStep {
    private FiscalYearMakerService fiscalYearMakerService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        fiscalYearMakerService.runProcess();

        return true;
    }

    /**
     * Sets the fiscalYearMakerService attribute value.
     * 
     * @param fiscalYearMakerService The fiscalYearMakerService to set.
     */
    public void setFiscalYearMakerService(FiscalYearMakerService fiscalYearMakerService) {
        this.fiscalYearMakerService = fiscalYearMakerService;
    }

}
