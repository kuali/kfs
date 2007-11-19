/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.exceptions.XMLParseException;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.module.financial.bo.ProcurementCardTransaction;
import org.kuali.module.financial.service.ProcurementCardLoadTransactionsService;

/**
 * This is the default implementation of the ProcurementCardLoadTransactionsService interface.
 * Handles loading, parsing, and storing of incoming procurement card batch files.
 * 
 * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService
 */
public class ProcurementCardLoadTransactionsServiceImpl implements ProcurementCardLoadTransactionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardLoadTransactionsServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType procurementCardInputFileType;

    /**
     * Validates and parses the given file, then stores transactions into a temp table.
     * 
     * @param fileName The name of the file to be parsed.
     * @return This method always returns true.  An exception is thrown if a problem occurs while loading the file.
     * 
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#loadProcurementCardFile()
     */
    public boolean loadProcurementCardFile(String fileName) {
        FileInputStream fileContents;
        try {
            fileContents = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName, e1);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }

        Collection pcardTransactions = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            pcardTransactions = (Collection) batchInputFileService.parse(procurementCardInputFileType, fileByteContent);
        }
        catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (XMLParseException e) {
            LOG.error("Error parsing xml " + e.getMessage());
            throw new RuntimeException("Error parsing xml " + e.getMessage(), e);
        }

        if (pcardTransactions == null || pcardTransactions.isEmpty()) {
            LOG.warn("No PCard transactions in input file " + fileName);
        }

        loadTransactions((List) pcardTransactions);

        LOG.info("Total transactions loaded: " + Integer.toString(pcardTransactions.size()));
        return true;
    }

    /**
     * Calls businessObjectService to remove all the procurement card transaction rows from the transaction load table.
     */
    public void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed XML transactions into the temp transaction table.
     * 
     * @param transactions List of ProcurementCardTransactions to load.
     */
    private void loadTransactions(List transactions) {
        businessObjectService.save(transactions);
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the batchInputFileService attribute value.
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Sets the procurementCardInputFileType attribute value.
     * @param procurementCardInputFileType The procurementCardInputFileType to set.
     */
    public void setProcurementCardInputFileType(BatchInputFileType procurementCardInputFileType) {
        this.procurementCardInputFileType = procurementCardInputFileType;
    }

}