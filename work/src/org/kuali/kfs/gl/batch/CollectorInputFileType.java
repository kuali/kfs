/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * Batch input type for the collector job.
 */
public class CollectorInputFileType extends BatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorInputFileType.class);

    private DateTimeService dateTimeService;
    private CollectorHelperService collectorHelperService;

    /**
     * Returns the identifier of the Collector's file type
     * 
     * @return the Collector's file type identifier
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.COLLECTOR_FILE_TYPE_INDENTIFIER;
    }

    /**
     * Builds the file name using the following construction: All collector files start with gl_idbilltrans_ append the chartorg
     * from the batch header append the username of the user who is uploading the file then the user supplied indentifier finally
     * the timestamp
     * 
     * @param user who uploaded the file
     * @param parsedFileContents represents collector batch object
     * @param userIdentifier user identifier for user who uploaded file
     * @return String returns file name using the convention mentioned in the description
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.bo.Person, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String userIdentifier) {
        CollectorBatch collectorBatch = (CollectorBatch) parsedFileContents;
        
        String fileName = "gl_idbilltrans_" + collectorBatch.getChartOfAccountsCode() + collectorBatch.getOrganizationCode();
        fileName += "_" + principalName;
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    /**
     * Checks that the file contents parsed from the file are valid Collector data
     * 
     * @param parsedFileContents represents collector batch
     * @return true if valid, false if not
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        boolean isValid = collectorHelperService.performValidation((CollectorBatch) parsedFileContents);
        if (isValid) {
            isValid = collectorHelperService.checkTrailerTotals((CollectorBatch) parsedFileContents, null);
        }

        return isValid;
    }

    /**
     * Returns the Collector's title key
     * 
     * @return the title key for the Collector
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_COLLECTOR;
    }

    /**
     * Sets the dateTimeService attribute value.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the collectorService attribute value.
     */
    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }

    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length > 4) {
            return fileNameParts[3];
        }
        return null;
    }
}

