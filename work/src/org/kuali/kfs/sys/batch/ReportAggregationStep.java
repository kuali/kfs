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
