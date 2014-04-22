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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This step will populate the initiator principal id, document status code, application document status, and document type name
 * from workflow document headers on to the FinancialSystemDocumentHeader
 */
public class PopulateFinancialSystemDocumentHeadersFromKewStep extends AbstractStep implements TestingStep {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PopulateFinancialSystemDocumentHeadersFromKewStep.class);

    protected final int DEFAULT_BATCH_SIZE = 1000;
    protected FinancialSystemDocumentHeaderPopulationService populationService;
    protected ParameterService parameterSize;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        populationService.populateFinancialSystemDocumentHeadersFromKew(getBatchSize(), getPopulationLimit());
        return true;
    }

    /**
     * @return the number of document header records which should be processed at once, based on the KFS-SYS / PopulateFinancialSystemDocumentHeadersFromKewStep / BATCH_SIZE parameter
     */
    public int getBatchSize() {
        final String batchSizeString = getParameterService().getParameterValueAsString(PopulateFinancialSystemDocumentHeadersFromKewStep.class, KFSParameterKeyConstants.PopulateFinancialSystemDocumentHeaderParameterNames.BATCH_SIZE, Integer.toString(DEFAULT_BATCH_SIZE));
        if (!StringUtils.isEmpty(batchSizeString)) {
            try {
                final int batchSize = Integer.parseInt(batchSizeString);
                if (batchSize > 0) {
                    return batchSize;
                }
            } catch (NumberFormatException nfe) {
                LOG.warn("This is legal, but the value of KFS-SYS / PopulateFinancialSystemDocumentHeadersFromKewStep / BATCH_SIZE is not numeric; that should likely be corrected.  Process will continue using batch size of "+DEFAULT_BATCH_SIZE);
            }
        }
        return DEFAULT_BATCH_SIZE;
    }

    /**
     * @return the number of document header records which should be processed in the course of the current job run, based on the KFS-SYS / PopulateFinancialSystemDocumentHeadersFromKewStep / POPULATION_LIMIT parameter
     */
    public Integer getPopulationLimit() {
        final String populationLimitString = getParameterService().getParameterValueAsString(PopulateFinancialSystemDocumentHeadersFromKewStep.class, KFSParameterKeyConstants.PopulateFinancialSystemDocumentHeaderParameterNames.POPULATION_LIMIT, "");
        if (!StringUtils.isEmpty(populationLimitString)) {
            try {
                final int populationLimit = Integer.parseInt(populationLimitString);
                if (populationLimit > 0) {
                    return populationLimit;
                }
            } catch (NumberFormatException nfe) {
                LOG.warn("This is legal, but the value of KFS-SYS / PopulateFinancialSystemDocumentHeadersFromKewStep / POPULATION_LIMIT is not numeric; that should likely be corrected.  Process will continue, populating all available records");
            }
        }
        return null;
    }

    public FinancialSystemDocumentHeaderPopulationService getPopulationService() {
        return populationService;
    }

    public void setPopulationService(FinancialSystemDocumentHeaderPopulationService populationService) {
        this.populationService = populationService;
    }

    public ParameterService getParameterSize() {
        return parameterSize;
    }

    public void setParameterSize(ParameterService parameterSize) {
        this.parameterSize = parameterSize;
    }
}
