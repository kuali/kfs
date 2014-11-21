/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
