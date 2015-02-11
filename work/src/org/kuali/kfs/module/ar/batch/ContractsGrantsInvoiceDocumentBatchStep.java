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
package org.kuali.kfs.module.ar.batch;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceBatchCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * A step to run the cgInvoice document creation process.
 */
public class ContractsGrantsInvoiceDocumentBatchStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocumentBatchStep.class);

    protected String batchFileDirectoryName;
    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected ContractsGrantsInvoiceBatchCreateDocumentService cgInvoiceBatchDocumentCreateService;
    protected DateTimeService dateTimeService;

    /**
     * See the class description.
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());
            String errOutputFile1 = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.CGINVOICE_BATCH_VALIDATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
            String errOutputFile2 = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.CGINVOICE_BATCH_CREATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
            cgInvoiceBatchDocumentCreateService.processBatchInvoiceDocumentCreation(errOutputFile1, errOutputFile2);
        } else {
            LOG.info("Contracts & Grants Billing enhancement not turned on; therefore, not running contractsGrantsInvoiceDocumentBatchStep");
        }
        return true;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    @Override
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * Sets the cgInvoiceDocumentCreateService attribute value.
     *
     * @param cgInvoiceDocumentCreateService The cgInvoiceDocumentCreateService to set.
     */
    public void setCgInvoiceBatchDocumentCreateService(ContractsGrantsInvoiceBatchCreateDocumentService cgInvoiceBatchDocumentCreateService) {
        this.cgInvoiceBatchDocumentCreateService = cgInvoiceBatchDocumentCreateService;
    }


    /**
     * This method sets the batch file directory name.
     *
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
