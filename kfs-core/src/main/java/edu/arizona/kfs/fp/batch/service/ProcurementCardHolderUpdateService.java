package edu.arizona.kfs.fp.batch.service;

/**
 * Service interface for implementing methods to insert/update procurement card holder records.
 */
public interface ProcurementCardHolderUpdateService {

    /**
     * Insert/update procurement card holder records from the records loaded into the procurement card holder load table.
     * * @return True if the inserts/updates were successful, false otherwise.
     */
    public boolean updateProcurementCardHolderRecords();

}
