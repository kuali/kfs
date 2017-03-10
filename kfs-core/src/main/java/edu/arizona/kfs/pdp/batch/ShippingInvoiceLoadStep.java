package edu.arizona.kfs.pdp.batch;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;

import edu.arizona.kfs.pdp.batch.service.ShippingInvoiceLoadService;
import edu.arizona.kfs.pdp.batch.service.ShippingInvoiceProcessService;

/**
 * This step will call a service method to load the shipping invoice xml file into the shipping invoice load table. Validates the
 * data before the load. Functions performed by this step: 1) Lookup path and filename from APC for the shipping invoice input file
 * 2) Load the shipping invoice xml file 3) Parse each invoice 4) Clean PDP_SHIPPING_BATCH_T, PDP_SHIPPING_INVOICE_LOAD_T and
 * PDP_SHIPPING_INV_TRACKING_T from the previous run 5) Load new transactions into PDP_SHIPPING_BATCH_T, PDP_SHIPPING_INVOICE_LOAD_T
 * and PDP_SHIPPING_INV_TRACKING_T 6) Rename input file using the current date (backup) RESTART: All functions performed within a
 * single transaction. Step can be restarted as needed.
 */
public class ShippingInvoiceLoadStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ShippingInvoiceLoadStep.class);

    private ShippingInvoiceLoadService shippingInvoiceLoadService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType shippingInputFileType;
    private ShippingInvoiceProcessService shippingInvoiceProcessServiceService;

    /**
     * Controls the shipping file load process.
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        Throwable exceptionThrown = null;
        String exceptionMsg = "";
        boolean isExceptionThrown = false;
        shippingInvoiceLoadService.cleanTransactionsTable();

        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(shippingInputFileType);

        boolean processSuccess = true;

        for (String inputFileName : fileNamesToLoad) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("About to process file: " + inputFileName);
            }

            removeDoneFile(inputFileName);
            
            try {
                processSuccess &= shippingInvoiceLoadService.loadShippingInvoiceFile(inputFileName);
            }
            
            catch (Exception e) {
                isExceptionThrown = true;
                exceptionThrown = e;
                exceptionMsg += e.getMessage() + "\n";
            }
        }

        try {
            // Removed ShippingInvoiceProcessStep and calling the service to process here
            processSuccess &= shippingInvoiceProcessServiceService.processShippingInvoiceRecords(jobRunDate);
        }
        catch (Exception e) {
            isExceptionThrown = true;
            exceptionThrown = e;
            exceptionMsg += e.getMessage() + "\n";
        }
        
        if (isExceptionThrown) {
            LOG.error(exceptionMsg);
            throw new RuntimeException(exceptionMsg, exceptionThrown);
        }
        
        return processSuccess;
    }

    /**
     * Clears out the associated .done file for the data file about to be processed.
     * 
     * @param dataFileName the name of date file with done file to remove
     */
    protected void removeDoneFile(String dataFileName) {
        File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
        if (doneFile.exists()) {
            doneFile.delete();
        }
    }
       
    public void setShippingInvoiceProcessServiceService(ShippingInvoiceProcessService shippingInvoiceProcessServiceService) {
        this.shippingInvoiceProcessServiceService = shippingInvoiceProcessServiceService;
    }
   
    public void setShippingInvoiceLoadService(ShippingInvoiceLoadService shippingInvoiceLoadService) {
        this.shippingInvoiceLoadService = shippingInvoiceLoadService;
    }
   
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }
   
    public void setShippingInputFileType(BatchInputFileType shippingInputFileType) {
        this.shippingInputFileType = shippingInputFileType;
    }

}
