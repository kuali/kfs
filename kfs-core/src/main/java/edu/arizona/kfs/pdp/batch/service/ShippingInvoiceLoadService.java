package edu.arizona.kfs.pdp.batch.service;

/**
 * This service interface defines the methods that a ShippingInvoiceLoadService implementation must provide.
 * 
 * Provides methods to load batch files for the shipping invoice load batch job.
 */
public interface ShippingInvoiceLoadService {
    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     * 
     * @param fileName Name of file to be uploaded and processed.
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean loadShippingInvoiceFile(String fileName);
    
    /**
     * Clears out the temporary transaction table.
     */
    public void cleanTransactionsTable();
}
