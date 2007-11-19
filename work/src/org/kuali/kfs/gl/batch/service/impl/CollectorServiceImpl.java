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
package org.kuali.module.gl.service.impl;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.CollectorHelperService;
import org.kuali.module.gl.service.CollectorScrubberService;
import org.kuali.module.gl.service.CollectorService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.RunDateService;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.CollectorScrubberStatus;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of the Collector service
 */
@Transactional
public class CollectorServiceImpl implements CollectorService {
    private static Logger LOG = Logger.getLogger(CollectorServiceImpl.class);

    private CollectorHelperService collectorHelperService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType collectorInputFileType;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private CollectorScrubberService collectorScrubberService;
    private RunDateService runDateService;

    /**
     * performs collection
     * 
     * @return status information related to the collection execution
     */
    public CollectorReportData performCollection() {
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(collectorInputFileType);
        List<String> processedFiles = new ArrayList();

        Date executionDate = dateTimeService.getCurrentSqlDate();

        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());
        OriginEntryGroup group = originEntryGroupService.createGroup(runDate, OriginEntrySource.COLLECTOR, true, false, true);
        CollectorReportData collectorReportData = new CollectorReportData();
        List<CollectorScrubberStatus> collectorScrubberStatuses = new ArrayList<CollectorScrubberStatus>();

        try {
            for (String inputFileName : fileNamesToLoad) {
                boolean processSuccess = false;
                try {
                    LOG.info("Collecting file: " + inputFileName);
                    processSuccess = collectorHelperService.loadCollectorFile(inputFileName, group, collectorReportData, collectorScrubberStatuses);
                }
                catch (RuntimeException e) {
                    LOG.error("Caught exception trying to load collector file: " + inputFileName, e);
                }
                processedFiles.add(inputFileName);
            }
            updateCollectorReportDataWithExecutionStatistics(collectorReportData, collectorScrubberStatuses);
        }
        finally {
            collectorScrubberService.removeTempGroups(collectorScrubberStatuses);
            group.setProcess(true);
            originEntryGroupService.save(group);
            removeDoneFiles(processedFiles);
        }
        return collectorReportData;
    }

    /**
     * Clears out associated .done files for the processed data files.
     * @param dataFileNames the name of files with done files to remove
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCollectorInputFileType(BatchInputFileType collectorInputFileType) {
        this.collectorInputFileType = collectorInputFileType;
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
        Collection<OriginEntryGroup> inputGroups = new ArrayList<OriginEntryGroup>();

        // NOTE: this implementation does not support the use of multiple origin entry group service/origin entry services
        for (CollectorScrubberStatus collectorScrubberStatus : collectorScrubberStatuses) {
            inputGroups.add(collectorScrubberStatus.getValidGroup());
        }

        if (inputGroups.size() > 0 && collectorScrubberStatuses.size() > 0) {
            OriginEntryService collectorScrubberOriginEntryService = collectorScrubberStatuses.get(0).getOriginEntryService();
            LedgerEntryHolder ledgerEntryHolder = collectorScrubberOriginEntryService.getSummaryByGroupId(inputGroups);
            collectorReportData.setLedgerEntryHolder(ledgerEntryHolder);
        }
    }

    public RunDateService getRunDateService() {
        return runDateService;
    }

    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }
}
