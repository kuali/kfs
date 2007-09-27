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
package org.kuali.module.gl.batch.collector;

import java.io.File;
import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileTypeBase;
import org.kuali.module.gl.service.CollectorHelperService;

/**
 * Batch input type for the collector job.
 */
public class CollectorInputFileType extends BatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorInputFileType.class);

    private DateTimeService dateTimeService;
    private CollectorHelperService collectorHelperService;

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.COLLECTOR_FILE_TYPE_INDENTIFIER;
    }

    public String getWorkgroupParameterNamespace() {
        return KFSConstants.SystemGroupParameterNames.COLLECTOR_FILE_TYPE_PARAMETER_NAMESPACE;
    }
    
    public String getWorkgroupParameterComponent() {
        return KFSConstants.SystemGroupParameterNames.COLLECTOR_FILE_TYPE_PARAMETER_COMPONENT;
    }

    /**
     * Builds the file name using the following construction: All collector files start with gl_idbilltrans_ append the chartorg
     * from the batch header append the username of the user who is uploading the file then the user supplied indentifier finally
     * the timestamp
     * 
     * @see org.kuali.kfs.batch.BatchInputFileType#getFileName(org.kuali.core.bo.user.UniversalUser, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(UniversalUser user, Object parsedFileContents, String userIdentifier) {
        CollectorBatch collectorBatch = (CollectorBatch) parsedFileContents;
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();

        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        formatter.setLenient(false);
        formatter.format(currentTimestamp, buf, new FieldPosition(0));

        String fileName = "gl_idbilltrans_" + collectorBatch.getChartOfAccountsCode() + collectorBatch.getOrganizationCode();
        fileName += "_" + user.getPersonUserIdentifier().toLowerCase();
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + buf.toString();
        
        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    /**
     * Verifies user created the file by checking for the username in the file name.
     * 
     * @see org.kuali.kfs.batch.BatchInputFileType#checkAuthorization(org.kuali.core.bo.user.UniversalUser, java.io.File)
     */
    public boolean checkAuthorization(UniversalUser user, File batchFile) {
        boolean isAuthorized = false;

        String userIdentifier = user.getPersonUserIdentifier();
        userIdentifier = StringUtils.remove(userIdentifier, " ");
        
        String[] fileNameParts = StringUtils.split(batchFile.getName(), "_");
        if (fileNameParts.length > 4) {
            if (fileNameParts[3].equalsIgnoreCase(userIdentifier.toLowerCase())) {
                isAuthorized = true;
            }
        }

        return isAuthorized;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        boolean isValid = collectorHelperService.performValidation((CollectorBatch) parsedFileContents);
        if (isValid) {
            isValid = collectorHelperService.checkTrailerTotals((CollectorBatch) parsedFileContents, null);
        }
        
        return isValid;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#getTitleKey()
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


}
