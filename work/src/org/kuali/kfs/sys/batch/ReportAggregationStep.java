/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportAggregatorService;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class ReportAggregationStep extends AbstractStep {
    protected String outputFilePath;
    protected String outputFilePrefix;
    protected String outputFileSuffix;
    protected ReportAggregatorService reportAggregatorService;
    protected String inputFilePath;
    protected String inputFilePrefix;
    protected String inputFileSuffix;
    
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        File outputFile = new File(generateOutputFileName());
        List<File> inputFiles = retrieveFilesToAggregate();
        reportAggregatorService.aggregateReports(outputFile, inputFiles);
        return true;
    }

    protected String generateOutputFileName() {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Date now = dateTimeService.getCurrentDate();
        return outputFilePath + File.separator + outputFilePrefix + dateTimeService.toDateTimeStringForFilename(now) + outputFileSuffix;
    }
    
    protected List<File> retrieveFilesToAggregate() {
        File inputDirectory = new File(inputFilePath);
        if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
            throw new RuntimeException(inputFilePath + " does not exist or is not a directory.");
        }
        FileFilter filter = FileFilterUtils.andFileFilter(
                new PrefixFileFilter(inputFilePrefix), new SuffixFileFilter(inputFileSuffix));
        
        List<File> fileList = Arrays.asList(inputDirectory.listFiles(filter));
        
        Collections.sort(fileList);
        
        return fileList;
    }
    
    /**
     * Sets the outputFilePath attribute value.
     * @param outputFilePath The outputFilePath to set.
     */
    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    /**
     * Sets the outputFilePrefix attribute value.
     * @param outputFilePrefix The outputFilePrefix to set.
     */
    public void setOutputFilePrefix(String outputFilePrefix) {
        this.outputFilePrefix = outputFilePrefix;
    }

    /**
     * Sets the outputFileSuffix attribute value.
     * @param outputFileSuffix The outputFileSuffix to set.
     */
    public void setOutputFileSuffix(String outputFileSuffix) {
        this.outputFileSuffix = outputFileSuffix;
    }

    /**
     * Sets the reportAggregatorService attribute value.
     * @param reportAggregatorService The reportAggregatorService to set.
     */
    public void setReportAggregatorService(ReportAggregatorService reportAggregatorService) {
        this.reportAggregatorService = reportAggregatorService;
    }

    /**
     * Sets the inputFilePath attribute value.
     * @param inputFilePath The inputFilePath to set.
     */
    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    /**
     * Sets the inputFilePrefix attribute value.
     * @param inputFilePrefix The inputFilePrefix to set.
     */
    public void setInputFilePrefix(String inputFilePrefix) {
        this.inputFilePrefix = inputFilePrefix;
    }

    /**
     * Sets the inputFileSuffix attribute value.
     * @param inputFileSuffix The inputFileSuffix to set.
     */
    public void setInputFileSuffix(String inputFileSuffix) {
        this.inputFileSuffix = inputFileSuffix;
    }
}
