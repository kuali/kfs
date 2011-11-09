/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.batch.service.CollectorScrubberService;
import org.kuali.kfs.gl.batch.service.CollectorService;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.InitiateDirectoryBase;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of the Collector service
 */
@Transactional
public class CollectorServiceImpl extends InitiateDirectoryBase implements CollectorService {
    private static Logger LOG = Logger.getLogger(CollectorServiceImpl.class);

    private CollectorHelperService collectorHelperService;
    private BatchInputFileService batchInputFileService;
    private List<BatchInputFileType> collectorInputFileTypes;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private CollectorScrubberService collectorScrubberService;
    private RunDateService runDateService;
    private String batchFileDirectoryName;
    
    private ReportWriterService collectorReportWriterService;

    /**
     * performs collection
     * 
     * @return status information related to the collection execution
     */
    public CollectorReportData performCollection() {
        
        //add a step to check for directory paths
        prepareDirectories(getRequiredDirectoryNames());
        
        List<String> processedFiles = new ArrayList<String>();
        Date executionDate = dateTimeService.getCurrentSqlDate();

        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());
        CollectorReportData collectorReportData = new CollectorReportData();
        List<CollectorScrubberStatus> collectorScrubberStatuses = new ArrayList<CollectorScrubberStatus>();

        String collectorFinalOutputFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_OUTPUT + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
      
        PrintStream collectorFinalOutputFilePs = null;
        BufferedReader inputFileReader = null;
        try {
            collectorFinalOutputFilePs = new PrintStream(collectorFinalOutputFileName);
                
        } catch (FileNotFoundException e) {
            throw new RuntimeException("writing all collector result files to output file process Stopped: " + e.getMessage(), e);
        }

        for (BatchInputFileType collectorInputFileType : collectorInputFileTypes) {
            List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(collectorInputFileType);
            for (String inputFileName : fileNamesToLoad) {
                boolean processSuccess = false;
                
                LOG.info("Collecting file: " + inputFileName);
                processSuccess = collectorHelperService.loadCollectorFile(inputFileName, collectorReportData, collectorScrubberStatuses, collectorInputFileType, collectorFinalOutputFilePs);
                processedFiles.add(inputFileName);
                if (processSuccess) {
                    renameCollectorScrubberFiles();
                }
                collectorReportData.getLoadedfileNames().add(inputFileName);
                
            }
            updateCollectorReportDataWithExecutionStatistics(collectorReportData, collectorScrubberStatuses);
        }

        collectorScrubberService.removeTempGroups(collectorScrubberStatuses);
        collectorFinalOutputFilePs.close();
            
        return collectorReportData;
    }

    /**
     * Clears out associated .done files for the processed data files.
     * @param dataFileNames the name of files with done files to remove
     */
    protected void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }
    
    protected void renameCollectorScrubberFiles() {
        String filePath = batchFileDirectoryName + File.separator;
        List<String> fileNameList = new ArrayList<String>();
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_BACKUP_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_INPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_VALID_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_EXPIRED_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_SORTED_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_ERROR_OUTPUT_FILE);
        
        for (String fileName : fileNameList){
            File file = new File(filePath + fileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            if (file.exists()) {
                String changedFileName = filePath + fileName + "." + getDateTimeService().toDateTimeStringForFilename(dateTimeService.getCurrentDate());
                file.renameTo(new File(changedFileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        }
    }
    
    public void finalizeCollector(CollectorReportData collectorReportData){
        // remove all done files for processed files
        removeDoneFiles( (List) collectorReportData.getLoadedfileNames());
        
        // create a done file for collector gl output
        String collectorFinalOutputDoneFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_OUTPUT + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
        File doneFile = new File (collectorFinalOutputDoneFileName);
        if (!doneFile.exists()){
            try {
                doneFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error creating collector done file", e);
            }
        }
    }
    

    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCollectorInputFileTypes(List<BatchInputFileType> collectorInputFileTypes) {
        this.collectorInputFileTypes = collectorInputFileTypes;
    }

    /**
     * Gets the originEntryGroupService attribute.
     * 
     * @return Returns the originEntryGroupService.
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the collectorScrubberService attribute.
     * 
     * @return Returns the collectorScrubberService.
     */
    public CollectorScrubberService getCollectorScrubberService() {
        return collectorScrubberService;
    }

    /**
     * Sets the collectorScrubberService attribute value.
     * 
     * @param collectorScrubberService The collectorScrubberService to set.
     */
    public void setCollectorScrubberService(CollectorScrubberService collectorScrubberService) {
        this.collectorScrubberService = collectorScrubberService;
    }

    /**
     * Adds execution statistics to the Collector run
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @param collectorScrubberStatuses a List of CollectorScrubberStatus records
     */
    protected void updateCollectorReportDataWithExecutionStatistics(CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses) {
    }

    public RunDateService getRunDateService() {
        return runDateService;
    }

    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Sets the collectorReportWriterService attribute value.
     * @param collectorReportWriterService The collectorReportWriterService to set.
     */
    public void setCollectorReportWriterService(ReportWriterService collectorReportWriterService) {
        this.collectorReportWriterService = collectorReportWriterService;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.impl.InitiateDirectoryImpl#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        List<String> requiredDirectoryList = new ArrayList<String>();
        for (BatchInputFileType batchInputFile : collectorInputFileTypes){
            requiredDirectoryList.add(batchInputFile.getDirectoryPath());
        }
        return requiredDirectoryList;
    }
}
