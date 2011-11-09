/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.batch;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.batch.service.BatchExtractService;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class PreAssetTaggingStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(PreAssetTaggingStep.class);
    private BatchExtractService batchExtractService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        try {
            java.sql.Date currentSqlDate = dateTimeService.getCurrentSqlDate();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pre Asset Tagging extract started at " + dateTimeService.getCurrentTimestamp());
            }
            Collection<PurchaseOrderAccount> preTaggablePOAccounts = batchExtractService.findPreTaggablePOAccounts();
            if (preTaggablePOAccounts != null && !preTaggablePOAccounts.isEmpty()) {
                batchExtractService.savePreTagLines(preTaggablePOAccounts);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pre Asset Tagging extract finished at " + dateTimeService.getCurrentTimestamp());
            }
            batchExtractService.updateLastExtractDate(currentSqlDate);
        }
        catch (Throwable e) {
            LOG.error("Unexpected error occured during Pre Asset Tagging extract", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Gets the batchExtractService attribute.
     * 
     * @return Returns the batchExtractService.
     */
    public BatchExtractService getBatchExtractService() {
        return batchExtractService;
    }

    /**
     * Sets the batchExtractService attribute value.
     * 
     * @param batchExtractService The batchExtractService to set.
     */
    public void setBatchExtractService(BatchExtractService batchExtractService) {
        this.batchExtractService = batchExtractService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
