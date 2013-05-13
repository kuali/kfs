/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.vnd.batch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.businessobject.DebarredVendorDetail;
import org.kuali.rice.core.api.datetime.DateTimeService;

import au.com.bytecode.opencsv.CSVReader;

public class VendorExcludeInputFileType extends BatchInputFileTypeBase{
    private static final Logger LOG = Logger.getLogger(VendorExcludeInputFileType.class);

    private static final String FILE_NAME_PREFIX = "epls_debarred_vendors_";
    public static final int[] FIELD_SIZES = {200, 150, 50, 50, 20, 2, 20, 1000, 1000}; //Size limits for the fields
    private String fileTypeIdentifier;
    private DateTimeService dateTimeService;

    /**
     * @return the electronic invoice file type identifier
     */
    @Override
    public String getFileTypeIdentifer() {
        return VendorConstants.VENDOR_EXCLUDE_FILE_TYPE_INDENTIFIER;
    }

    /**
     * doesn't do any validation
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return VendorKeyConstants.MESSAGE_BATCH_UPLOAD_VENDOR_EXCLUDE;
    }

    /** @see org.kuali.kfs.sys.batch.BatchInputFileType#getAuthorPrincipalName(File)
     */
    @Override
    public String getAuthorPrincipalName(File file) {
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {
        // does nothing
    }

    /**
     * @param principalName - not used
     * @param parsedFileContents List - not used
     * @param fileUserIdentifier - not used
     * @return file name prefix appended by current date
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(String, Object, String)
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifuer) {
        String fileName = FILE_NAME_PREFIX;
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
        fileName = org.apache.commons.lang.StringUtils.remove(fileName, " ");

        return fileName;
    }

    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        LOG.info("Parsing Vendor Exclude Input File ...");
        
        // create CSVReader, using conventional separator, quote, null escape char, skip first line, use strict quote, ignore leading white space 
        int skipLine = 1; // skip the first line, which is the header line
        Reader inReader = new InputStreamReader(new ByteArrayInputStream(fileByteContent));
        CSVReader reader = new CSVReader(inReader, ',', '"', Character.MIN_VALUE, skipLine, true, true);
        
        List <DebarredVendorDetail> debarredVendors = new ArrayList<DebarredVendorDetail>();
        String[] nextLine;
        DebarredVendorDetail vendor;
        int lineNumber = skipLine; 
        
        try {           
            while ((nextLine = reader.readNext()) != null) {
                lineNumber++; 
                LOG.debug("Line " + lineNumber + ": " + nextLine[0]);
                
                vendor = new DebarredVendorDetail();
                boolean emptyLine = true; 
                
                // this should never happen, as for an empty line, CSVReader.readNext returns a string array with an empty string as the only element 
                // but just in case somehow a zero sized array is returned, we skip it.
                if (nextLine.length == 0) {
                    continue;                   
                }

                StringBuffer name = new StringBuffer();  
                // if the name field is not empty, use that as vendor name 
                if (StringUtils.isNotEmpty(nextLine[0])) {                    
                    name.append(nextLine[0]);
                }
                // otherwise, there should be a first/middle/last name, which we concatenate into vendor name
                else {
                    if (nextLine.length > 1 && !StringUtils.isNotEmpty(nextLine[1])) {
                        name.append(" " + nextLine[1]);
                    }
                    if (nextLine.length > 2 && !StringUtils.isNotEmpty(nextLine[2])) {
                        name.append(" " + nextLine[2]);
                    }
                    if (nextLine.length > 3 && !StringUtils.isNotEmpty(nextLine[3])) {
                        name.append(" " + nextLine[3]);
                    }
                    if (nextLine.length > 4 && !StringUtils.isNotEmpty(nextLine[4])) {
                        name.append(" " + nextLine[4]);
                    }
                    if (nextLine.length > 5 && StringUtils.isNotEmpty(nextLine[5])) {
                        name.append(" " + nextLine[5]);
                    }
                }
                if (StringUtils.isNotEmpty(name.toString())) {
                    vendor.setName(StringUtils.left(name.toString(), FIELD_SIZES[0]));
                    emptyLine = false;
                }
                
                if (nextLine.length > 6 && StringUtils.isNotEmpty(nextLine[6])) {
                    vendor.setAddress1(StringUtils.left(nextLine[6], FIELD_SIZES[1]));
                    emptyLine = false;
                }
                if (nextLine.length > 7 && StringUtils.isNotEmpty(nextLine[7])) {
                    vendor.setAddress2(StringUtils.left(nextLine[7], FIELD_SIZES[2]));
                    emptyLine = false;
                }
                if (nextLine.length > 8 && StringUtils.isNotEmpty(nextLine[8])) {
                    vendor.setCity(StringUtils.left(nextLine[8], FIELD_SIZES[3]));
                    emptyLine = false;
                }
                if (nextLine.length > 9 && StringUtils.isNotEmpty(nextLine[9])) {
                    vendor.setProvince(StringUtils.left(nextLine[9], FIELD_SIZES[4]));
                    emptyLine = false;
                }
                if (nextLine.length > 10 && StringUtils.isNotEmpty(nextLine[10])) {
                    vendor.setState(StringUtils.left(nextLine[10], FIELD_SIZES[5]));
                    emptyLine = false;
                }
                if (nextLine.length > 11 && StringUtils.isNotEmpty(nextLine[11])) {
                    vendor.setZip(StringUtils.left(nextLine[11], FIELD_SIZES[6]));
                    emptyLine = false;
                }
                if (nextLine.length > 13 && StringUtils.isNotEmpty(nextLine[13])) {
                    vendor.setAliases(StringUtils.left(StringUtils.remove(nextLine[13], "\""), FIELD_SIZES[7]));
                    emptyLine = false;
                }
                if (nextLine.length > 18 && StringUtils.isNotEmpty(nextLine[18])) {
                    vendor.setDescription(StringUtils.left(nextLine[18], FIELD_SIZES[8]));
                    emptyLine = false;
                }
                
                if (emptyLine) {
                    /* give warnings on a line that doesn't have any useful vendor info
                    LOG.warn("Note: line " + lineNumber + " in the Vendor Exclude Input File is skipped since all parsed fields are empty.");
                    */
                    // throw parser exception on a line that doesn't have any useful vendor info.
                    // Since the file usually doesn't contain empty lines or lines with empty fields, this happening usually is a good indicator that 
                    // some line ahead has wrong data format, for ex, missing a quote on a field, which could mess up the following fields and lines. 
                    throw new ParseException("Line " + lineNumber + " in the Vendor Exclude Input File contains no valid field or only empty fields within quote pairs. Please check the lines ahead to see if any field is missing quotes.");
                }
                else {
                    vendor.setLoadDate(new Date(new java.util.Date().getTime()));
                    debarredVendors.add(vendor);
                }
            }
        }
        catch (IOException ex) {
            throw new ParseException("Error reading Vendor Exclude Input File at line " + lineNumber + ": " + ex.getMessage());
        }
        
        LOG.info("Total number of lines read from Vendor Exclude Input File: " + lineNumber);
        LOG.info("Total number of vendors parsed from Vendor Exclude Input File: " + debarredVendors.size());
        return debarredVendors;
    }

    /**
     * Gets the fileTypeIdentifier attribute.
     * @return Returns the fileTypeIdentifier.
     */
    public String getFileTypeIdentifier() {
        return fileTypeIdentifier;
    }

    /**
     * Sets the fileTypeIdentifier attribute value.
     * @param fileTypeIdentifier The fileTypeIdentifier to set.
     */
    public void setFileTypeIdentifier(String fileTypeIdentifier) {
        this.fileTypeIdentifier = fileTypeIdentifier;
    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
