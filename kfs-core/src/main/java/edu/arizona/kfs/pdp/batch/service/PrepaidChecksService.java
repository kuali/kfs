package edu.arizona.kfs.pdp.batch.service;

import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.sys.batch.BatchInputFileType;

public interface PrepaidChecksService extends PaymentFileService {

    /**
     * Process all incoming prepaidChecks payment files
     * 
     * @param prepaidChecksInputFileType <code>BatchInputFileType</code> for payment files
     * @return true if process is successful, false otherwise
     */
	public boolean processPrepaidChecks(BatchInputFileType prepaidChecksInputFileType);    
    
}

