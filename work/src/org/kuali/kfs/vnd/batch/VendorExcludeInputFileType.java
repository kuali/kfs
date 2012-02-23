/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.vnd.batch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.businessobject.DebarredVendorDetail;
import org.kuali.rice.kns.service.DateTimeService;

import au.com.bytecode.opencsv.CSVReader;

public class VendorExcludeInputFileType extends BatchInputFileTypeBase{
    
    private static final String FILE_NAME_PREFIX = "epls_debarred_vendors_";
    public static final int[] FIELD_SIZES = {200, 150, 50, 50, 20, 2, 20, 1000, 1000}; //Size limits for the fields
    private String fileTypeIdentifier;
    private DateTimeService dateTimeService;
    
    /**
     * @return the electronic invoice file type identifier
     */
    public String getFileTypeIdentifer() {
        return VendorConstants.VENDOR_EXCLUDE_FILE_TYPE_INDENTIFIER;
    }
    
    /**
     * doesn't do any validation
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        return true;
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return VendorKeyConstants.MESSAGE_BATCH_UPLOAD_VENDOR_EXCLUDE;
    }
    
    /** @see org.kuali.kfs.sys.batch.BatchInputFileType#getAuthorPrincipalName(File)
     */
    public String getAuthorPrincipalName(File file) {
        return null;
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
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
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifuer) {
        String fileName = FILE_NAME_PREFIX;
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
        fileName = org.apache.commons.lang.StringUtils.remove(fileName, " ");

        return fileName;
    }

    public Object parse(byte[] fileByteContent) throws ParseException {
        CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
        List <DebarredVendorDetail> debarredVendors = new ArrayList<DebarredVendorDetail>();
        String[] nextLine;
        DebarredVendorDetail vendor;
        try {
            nextLine = reader.readNext(); // skip first line
            while ((nextLine = reader.readNext()) != null) {
                vendor = new DebarredVendorDetail();
                StringBuffer name = new StringBuffer();
                if (StringUtils.isEmpty(nextLine[0])) {
                    //If the name is empty then there must be a first/middle/last name
                    name.append(StringUtils.remove(nextLine[1], "\"") + " " + StringUtils.remove(nextLine[2], "\""));
                    if (nextLine.length > 3) {
                        name.append(" " + StringUtils.remove(nextLine[3], "\""));
                    }
                    if (nextLine.length > 4) {
                        name.append(" " + StringUtils.remove(nextLine[4], "\""));
                    }
                    if (nextLine.length > 5) {
                        name.append(" " + StringUtils.remove(nextLine[5], "\""));
                    }
                } else {
                    name.append(StringUtils.remove(nextLine[0], "\""));
                }
                vendor.setName(StringUtils.left(name.toString(), FIELD_SIZES[0]));
                if (nextLine.length > 6) {
                    vendor.setAddress1(StringUtils.left(nextLine[6], FIELD_SIZES[1]));
                }
                if (nextLine.length > 7) {
                    vendor.setAddress2(StringUtils.left(nextLine[7], FIELD_SIZES[2]));
                }
                if (nextLine.length > 8) {
                    vendor.setCity(StringUtils.left(nextLine[8], FIELD_SIZES[3]));
                }
                if (nextLine.length > 9) {
                    vendor.setProvince(StringUtils.left(nextLine[9], FIELD_SIZES[4]));
                }
                if (nextLine.length > 10) {
                    vendor.setState(StringUtils.left(nextLine[10], FIELD_SIZES[5]));
                }
                if (nextLine.length > 11) {
                    vendor.setZip(StringUtils.left(nextLine[11], FIELD_SIZES[6]));
                }
                if (nextLine.length > 13) {
                    vendor.setAliases(StringUtils.left(StringUtils.remove(nextLine[13], "\""), FIELD_SIZES[7]));
                }
                vendor.setLoadDate(new Date(new java.util.Date().getTime()));
                if (nextLine.length > 18) {
                    vendor.setDescription(StringUtils.left(nextLine[18], FIELD_SIZES[8]));
                }
                debarredVendors.add(vendor);
            }
        }
        catch (IOException ex) {
            throw new ParseException(ex.getMessage());
        }
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
