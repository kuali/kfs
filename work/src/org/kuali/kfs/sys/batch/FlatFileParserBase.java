/*
 * Copyright 2012 The Kuali Foundation.
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Implementation of BatchInputFileType which parses flat files
 */
public class FlatFileParserBase extends BatchInputFileTypeBase {
    protected static Logger LOG = Logger.getLogger(FlatFileParserBase.class);
    protected FlatFileSpecification flatFileSpecification;
    protected String fileNamePrefix;
    protected DateTimeService dateTimeService;
    protected String fileTypeIdentifier;
    protected String titleKey;
    protected FlatFileDataHandler processor;

    /**
     * Returns the name of an uploaded file
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        String fileName = processor.getFileName(principalName, parsedFileContents, fileUserIdentifier);
        if(StringHelper.isNullOrEmpty(fileName)) {
            fileName = getFileNamePrefix();
            fileName += principalName;
            if (org.apache.commons.lang.StringUtils.isNotBlank(fileUserIdentifier)) {
                fileName += "_" + fileUserIdentifier;
            }
            fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

            // remove spaces in filename
            fileName = org.apache.commons.lang.StringUtils.remove(fileName, " ");
        }
        return fileName;
    }

    /**
     * All foundation developers are brilliant at spelling!
     *
     * @param fileTypeIdentifier the file identifier to set
     */
    public void setFileTypeIdentifer(String fileTypeIdentifier) {
        this.fileTypeIdentifier = fileTypeIdentifier;
    }

    /**
     * The correctly spelled setter name - just to avoid injection confusion
     *
     * @param fileTypeIdentifier the file type identifier
     */
    public void setFileTypeIdentifier(String fileTypeIdentifier) {
        this.fileTypeIdentifier = fileTypeIdentifier;
    }

    /**
     * Sets the key of the message to show in the title bar of the upload page for this file
     * @param titleKey
     */
    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    /**
     * @return a FlatFileDataValidator associated with this instance of the FixedWidthFlatFileParserBase
     */
    public FlatFileDataHandler getProcessor() {
        return processor;
    }

    /**
     * Sets the processor which will validate and process the file once all flat file data has been parsed
     * @param processor the implementation of FlatFileDataHandler to utilize
     */
    public void setProcessor(FlatFileDataHandler processor) {
        this.processor = processor;
    }

    /**
     * Reads each line of the flat file and uses the injected FlatFileSpecification to parse into an object graph
     * @param fileByteContent the contents file to parse
     * @return an object graph of parsed objects
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
        String lineToParse;
        Object returnObject = null;
        FlatFileParseTracker tracker = SpringContext.getBean(FlatFileParseTracker.class);
        tracker.initialize(flatFileSpecification);
        int  lineNumber = 1;
        try {
            while ((lineToParse = bufferedFileReader.readLine()) != null) {
                Object parseIntoObject = tracker.getObjectToParseInto(lineToParse);

                if (parseIntoObject != null) {
                    FlatFileObjectSpecification parseSpecification = flatFileSpecification.getObjectSpecification(parseIntoObject.getClass());

                    if (parseSpecification.getParseProperties() != null && !parseSpecification.getParseProperties().isEmpty()) {
                        flatFileSpecification.parseLineIntoObject(parseSpecification, lineToParse, parseIntoObject, lineNumber);
                        tracker.completeLineParse();
                    }
                }
               lineNumber++;
            }
            returnObject = tracker.getParsedObjects();


        }
        catch (Exception e) {
            LOG.error(e.getMessage() + " happend in parsing file content ", e);
            throw new ParseException(e.getMessage());
        }

        return returnObject;
    }

    /**
     * Calls the processor if it is available
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {
        if (getProcessor() != null) {
            getProcessor().process(fileName, parsedFileContents);
        }
    }

    /**
     * @return currently always returns true; do we want to extend that sometime?
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        if (getProcessor() == null) {
            return true;
        }
        return getProcessor().validate(parsedFileContents);
    }

    /**
     * Sets the FlatFileSpecification that instructs how to carry out the parse
     * @param flatFileClassIdentifier the FlatFileSpecification that instructs how to carry out the parse
     */
    public void setFlatFileSpecification(AbstractFlatFileSpecificationBase flatFileClassIdentifier) {
        this.flatFileSpecification = flatFileClassIdentifier;
    }

    /**
     * Sets the prefix of the file name which this parser reads
     * @param fileNamePrefix
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    /**
     * Sets an implementation of the DateTimeService for use in parsing
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Determines the principal name of the author of a file from the file name
     * @param file the file to determine the author of
     * @return the principal name of the author
     */
    @Override
    public String getAuthorPrincipalName(File file) {
        return org.apache.commons.lang.StringUtils.substringBetween(file.getName(), getFileNamePrefix(), "_");
    }

    /**
     * @return the identifier for flat files of this type
     */
    @Override
    public String getFileTypeIdentifer() {
        return fileTypeIdentifier;
    }

    /**
     * @return the prefix of the name of flat files of this type
     */
    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    /**
     * @return the key of the message to be used as the title of the upload screen of this file type
     */
    @Override
    public String getTitleKey() {
        return titleKey;
    }
}
