/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.service.ProcurementCardLoadTransactionsService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * This step will call a service method to load the kuali pcard xml file into the transaction table. Validates the data before the
 * load. Functions performed by this step: 1) Lookup path and filename from APC for the pcard input file 2) Load the pcard xml file
 * 3) Parse each transaction and validate against the data dictionary 4) Clean fp_prcrmnt_card_trn_mt from the previous run 5) Load
 * new transactions into fp_prcrmnt_card_trn_mt 6) Rename input file using the current date (backup) RESTART: All functions performed
 * within a single transaction. Step can be restarted as needed.
 */
public class ProcurementCardLoadStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardLoadStep.class);

    protected ProcurementCardLoadTransactionsService procurementCardLoadTransactionsService;
    protected BatchInputFileService batchInputFileService;
    protected BatchInputFileType procurementCardInputFileType;
    protected ReportWriterService reportWriterService;

    /**
     * custom get requiredDirectoryNames step- assign the passed in procurementInputFileType to the batchInputFileType
     * 
     * @see org.kuali.kfs.sys.batch.AbstractStep#prepareStepDirectory()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        setBatchInputFileType(procurementCardInputFileType);
        return super.getRequiredDirectoryNames();
    }
    
    /**
     * Controls the procurement card process.
     */
    public boolean execute(String jobName, Date jobRunDate) {
        procurementCardLoadTransactionsService.cleanTransactionsTable();

        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(procurementCardInputFileType);
        ((WrappingBatchService) reportWriterService).initialize();
        
        boolean processSuccess = true;
        List<String> processedFiles = new ArrayList();
        for (String inputFileName : fileNamesToLoad) {
            processSuccess = procurementCardLoadTransactionsService.loadProcurementCardFile(inputFileName, reportWriterService);
            if (processSuccess) {
                processedFiles.add(inputFileName);
            }
        }
        ((WrappingBatchService) reportWriterService).destroy();
        
        removeDoneFiles(fileNamesToLoad);

        return processSuccess;
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    protected void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    /**
     * Sets the batchInputFileService attribute value.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Sets the procurementCardInputFileType attribute value.
     */
    public void setProcurementCardInputFileType(BatchInputFileType procurementCardInputFileType) {
        this.procurementCardInputFileType = procurementCardInputFileType;
    }

    /**
     * Sets the procurementCardLoadTransactionsService attribute value.
     */
    public void setProcurementCardLoadTransactionsService(ProcurementCardLoadTransactionsService procurementCardLoadTransactionsService) {
        this.procurementCardLoadTransactionsService = procurementCardLoadTransactionsService;
    }

    /**
     * 
     * @return
     */
    public ReportWriterService getReportWriterService() {
        return reportWriterService;
    }

    /**
     * 
     * @param reportWriterService
     */
    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }
}
