package org.kuali.kfs.sys.batch;

/**
 * Interface for flat file parsed into objects which generates error messages per top-level business object 
 */
public interface  FlatFileData  {
    /**
     * @return the FlatFileTransactionInformation for this parsed into business object
     */
    public FlatFileTransactionInformation getFlatFileTransactionInformation() ;
    
}
