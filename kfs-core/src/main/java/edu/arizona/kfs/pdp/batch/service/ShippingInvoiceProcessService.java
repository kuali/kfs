package edu.arizona.kfs.pdp.batch.service;

import java.util.Date;

/**
 * 
 * Service interface for implementing methods to add header info to the loaded
 * shipping invoice tables and then write the pertinent data to PDP payment tables.
 * 
 */
public interface ShippingInvoiceProcessService {    
    /**
     * Insert shipping invoice and KFS accounting string data into PDP payment tables. 
     * 
     * * @return True if the updates were successful, false otherwise.
     */    
    public boolean processShippingInvoiceRecords( Date processRunDate );
}
