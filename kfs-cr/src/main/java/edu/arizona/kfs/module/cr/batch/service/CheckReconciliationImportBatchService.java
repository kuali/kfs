package edu.arizona.kfs.module.cr.batch.service;

/**
 * Performs the work necessary for the CheckReconciliationImportStep.
 */

public interface CheckReconciliationImportBatchService {

    /**
     * Initializes objects inside the service that cannot be injected via Spring.
     */
    public boolean initializeServiceObjects();

    /**
     * Imports Payments from the Pre-Disbursement Processor.
     */
    public boolean importPdpPayments();

    /**
     * Reads the data files and in the staging folder, and processes the contents.
     */
    public boolean processDataFiles() throws Exception;

}
