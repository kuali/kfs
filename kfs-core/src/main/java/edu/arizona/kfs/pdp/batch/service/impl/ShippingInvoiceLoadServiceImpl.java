package edu.arizona.kfs.pdp.batch.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.pdp.batch.service.ShippingInvoiceLoadService;
import edu.arizona.kfs.pdp.businessobject.ShippingBatch;
import edu.arizona.kfs.pdp.businessobject.ShippingHeader;
import edu.arizona.kfs.pdp.businessobject.ShippingHeaderHistory;
import edu.arizona.kfs.pdp.businessobject.ShippingInvoice;
import edu.arizona.kfs.pdp.businessobject.ShippingInvoiceTracking;

/**
 * This is the default implementation of the ShippingInvoiceLoadService interface.
 * Handles loading, parsing, and storing of incoming shipping invoice batch files.
 * 
 */
@Transactional
public class ShippingInvoiceLoadServiceImpl implements ShippingInvoiceLoadService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ShippingInvoiceLoadServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected BatchInputFileService batchInputFileService;
    protected BatchInputFileType shippingInputFileType;
    protected DateTimeService dateTimeService;
    
    /**
     * Calls businessObjectService to remove all the shipping invoices from the load tables.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ShippingInvoiceTracking.class, new HashMap());
        businessObjectService.deleteMatching(ShippingInvoice.class, new HashMap());
        businessObjectService.deleteMatching(ShippingHeader.class, new HashMap());
    }

    /**
     * Validates and parses the given file, then stores shipping invoices in temp tables.
     * After file is parsed, there is only one record in header table.
     * 
     * @param fileName The name of the file to be parsed.
     * @return This method always returns true.  An exception is thrown if a problem occurs while loading the file.
     * 
     */
    public boolean loadShippingInvoiceFile(String fileName) {
        FileInputStream fileContents;
        try {
            fileContents = new FileInputStream(fileName);
        } catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName + " " + e1.getMessage(), e1);
            throw new RuntimeException(" Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }

        ShippingBatch batchContents = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            batchContents = (ShippingBatch) batchInputFileService.parse(shippingInputFileType, fileByteContent);
        } catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException(" Error encountered while attempting to get file bytes: " + fileName + " " + e.getMessage(), e);
        } catch (ParseException e) {
            LOG.error("Error parsing xml " + e.getMessage(), e);
            throw new RuntimeException(" Error parsing xml " + fileName + " " + e.getMessage(), e);
        }

        if (batchContents == null) {
            LOG.warn("No invoices in input file " + fileName);
        }
        
        applyHeaderToInvoices(batchContents);
        saveShippingData(batchContents, fileName, dateTimeService.getCurrentTimestamp());

        if ( LOG.isInfoEnabled() ) {
            LOG.info("Total invoices loaded: " + Integer.toString(batchContents.getShippingInvoices().size()));
        }
        return true;
    }

    /**
     * Takes contents of file header and adds the data to each invoice.
     * 
     * @param batchContents
     */
    public void applyHeaderToInvoices(ShippingBatch batchContents) {
        ShippingHeader shippingHeader = batchContents.getShippingHeader();
        
        if (shippingHeader != null) {           
            for (ShippingInvoice invoice: batchContents.getShippingInvoices()) {
                invoice.setOpenCustomField(shippingHeader.getOpenCustomField());
                invoice.setShippingCompany(shippingHeader.getShippingCompany());
                invoice.setCreationDate(shippingHeader.getCreationDate());
                invoice.setTransactionRefNumber(shippingHeader.getTransactionRefNumber());
            }       
            shippingHeader.setInvoices(new ArrayList<ShippingInvoice>(batchContents.getShippingInvoices()));
        }
        
    }
    
    /**
     * Loads all the parsed XML invoices into the temp invoice tables.
     * 
     * @param holders List of invoices to load.
     */
    public void saveShippingData(ShippingBatch batchContents, String fileName, Timestamp loadDate) {
        businessObjectService.save(batchContents.getShippingHeader());
        businessObjectService.save(batchContents.getShippingInvoices());

        // need to create it here so the sequence numbers are in place on the tracking entries
        ShippingHeaderHistory historyHeader = new ShippingHeaderHistory(loadDate, fileName, batchContents.getShippingHeader());
        
        businessObjectService.save( historyHeader );
    }
      
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
      
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }
        
    public void setShippingInputFileType(BatchInputFileType shippingInputFileType) {
        this.shippingInputFileType = shippingInputFileType;
    }
        
    public void setDateTimeService(DateTimeService dateTimeService) {
    	this.dateTimeService = dateTimeService;
    }
                    
}
