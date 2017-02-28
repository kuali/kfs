package edu.arizona.kfs.fp.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderLoadService;
import edu.arizona.kfs.fp.businessobject.ProcurementCardHolderLoad;

/**
 * This is the default implementation of the ProcurementCardHolderLoadService interface.
 * Handles loading, parsing, and storing of incoming procurement cardholder batch files.
 */
public class ProcurementCardHolderLoadServiceImpl implements ProcurementCardHolderLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderLoadServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType procurementCardHolderInputFileType;

    /**
     * Calls businessObjectService to remove all the procurement cardholder rows from the load table.
     */
    @Override
    public void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardHolderLoad.class, new HashMap<String, String>());
    }

    /**
     * Validates and parses the given file, then stores procurement cardholders in a temp table.
     * 
     * @param fileName
     *            The name of the file to be parsed.
     * @return This method always returns true. An exception is thrown if a problem occurs while loading the file.
     */
    @Override
    public boolean loadProcurementCardHolderFile(String fileName) {
        FileInputStream fileContents;
        try {
            fileContents = new FileInputStream(fileName);
        } catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName, e1);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }

        Collection<ProcurementCardHolderLoad> pcardHolders = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            pcardHolders = (Collection<ProcurementCardHolderLoad>) batchInputFileService.parse(procurementCardHolderInputFileType, fileByteContent);
        } catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        } catch (ParseException e) {
            LOG.error("Error parsing xml " + e.getMessage());
            throw new RuntimeException("Error parsing xml " + e.getMessage(), e);
        }

        if (pcardHolders == null || pcardHolders.isEmpty()) {
            LOG.warn("No PCard Holders in input file " + fileName);
        }

        loadTransactions((List<ProcurementCardHolderLoad>) pcardHolders);

        LOG.info("Total holders loaded: " + pcardHolders.size());
        return true;
    }
    
    @Override
    public boolean loadFiles() {
        cleanTransactionsTable();

        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(procurementCardHolderInputFileType);

        //process only most recent file set to prevent duplicates
        Map<Date, String> fileNameMap = buildMapForFileNameList(fileNamesToLoad);
        Date holdDate = null;
        for (Iterator<Date> iter2 = fileNameMap.keySet().iterator(); iter2.hasNext();) {
            Date fileDate = (Date) iter2.next();
            if (holdDate == null || holdDate.compareTo(fileDate) < 0) {
                holdDate = fileDate;
            }
        }
        String holdDateString = (String) fileNameMap.get(holdDate);
        
        boolean processSuccess = true;

        for (String inputFileName : fileNamesToLoad) {
            if (inputFileName.contains(holdDateString)) {
                // Removing related .done file right away
                removeDoneFile(inputFileName);
                processSuccess = loadProcurementCardHolderFile(inputFileName);
            }
        }
        //remove any leftover done files
        removeDoneFiles(fileNamesToLoad);
        return processSuccess;
    }
    
    private Map<Date, String> buildMapForFileNameList(List<String> fileNamesToLoad) {
        Map<Date, String> fileNameMap = new HashMap<Date, String>();
        File inputFile = null;
        String shortFileName = null;
        
        for (String inputFileName : fileNamesToLoad) {
            inputFile = new File(inputFileName);
            shortFileName = inputFile.getName();
            // place file date values in map
            fileNameMap.put(Date.valueOf(shortFileName.substring(10, 20)), shortFileName.substring(10, 20));
        }
        return fileNameMap;
    }
    
    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            removeDoneFile(dataFileName);
        }
    }
    
    /**
     * Clears out the associated .done file for the data file about to be processed.
     * 
     * @param dataFileName the name of date file with done file to remove
     */
    protected void removeDoneFile(String dataFileName) {
        File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
        if (doneFile.exists()) {
            try {
                LOG.info("Removing the .done file for " + dataFileName);
                doneFile.delete();
            } catch (SecurityException e) {
                LOG.error("Unable to delete the .done file for " + dataFileName);
                throw new RuntimeException("An error occurred while attempting to remove the done file " + e.getMessage(), e);
            }
        }
    }

    /**
     * Loads all the parsed XML holders into the temp holder table.
     * 
     * @param holders
     *            List of Procurement Cardholders to load.
     */
    protected void loadTransactions(List<ProcurementCardHolderLoad> holders) {
        businessObjectService.save(holders);
    }

    public void setProcurementCardHolderInputFileType(BatchInputFileType procurementCardHolderInputFileType) {
        this.procurementCardHolderInputFileType = procurementCardHolderInputFileType;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }
}
