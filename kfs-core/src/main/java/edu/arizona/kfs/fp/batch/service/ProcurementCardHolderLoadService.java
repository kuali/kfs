package edu.arizona.kfs.fp.batch.service;

/**
 * This service interface defines the methods that a ProcurementCardHolderLoadService implementation must provide.
 * Provides methods to load batch files for the procurement cardholder batch job.
 */
public interface ProcurementCardHolderLoadService {
    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     * 
     * @param fileName
     *            Name of file to be uploaded and processed.
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean loadProcurementCardHolderFile(String fileName);

    /**
     * Clears out the temporary transaction table.
     */
    public void cleanTransactionsTable();
    
    /**
     * Validates and parses all files ready to go in the batch staging area.
     *
     * @return True if no errors were encountered, False otherwise.
     */
    public boolean loadFiles();
}
