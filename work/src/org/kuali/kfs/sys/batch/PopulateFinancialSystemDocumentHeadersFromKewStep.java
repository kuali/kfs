/*
 * Copyright 2014 The Kuali Foundation.
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

import org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService;

/**
 * This step will populate the initiator principal id, document status code, application document status, and document type name
 * from workflow document headers on to the FinancialSystemDocumentHeader
 */
public class PopulateFinancialSystemDocumentHeadersFromKewStep extends AbstractStep implements TestingStep {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PopulateFinancialSystemDocumentHeadersFromKewStep.class);

    protected boolean logMode = false;
    protected int batchSize = 500;
    protected FinancialSystemDocumentHeaderPopulationService populationService;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        populationService.populateFinancialSystemDocumentHeadersFromKew(isLogMode(), getBatchSize(), LOG);
        return true;
    }

    /**
     * @return true if instead of updating financial system document headers, the job should simply log what values it would like to set
     */
    public boolean isLogMode() {
        return logMode;
    }

    /**
     * Changes step behavior so that instead of updating the FinancialSystemDocumentHeader records, it simply logs the changes to make
     * out to the system log.  This defaults to false, but can be overridden easily via the populate.financial.system.headers.from.kew.log.mode
     * system property
     * @param logMode true if the step should simply log the changes it wishes to make; false if the step will actually make the changes.
     */
    public void setLogMode(boolean logMode) {
        this.logMode = logMode;
    }

    /**
     * @return the number of document header records which should be processed at once
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Sets the number of document header records which should be processed in a batch.  The default is 500
     * @param batchSize the batch size to process
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public FinancialSystemDocumentHeaderPopulationService getPopulationService() {
        return populationService;
    }

    public void setPopulationService(FinancialSystemDocumentHeaderPopulationService populationService) {
        this.populationService = populationService;
    }
}
