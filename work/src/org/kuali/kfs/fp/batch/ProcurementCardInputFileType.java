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
package org.kuali.module.financial.batch.pcard;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.batch.BatchInputFileTypeBase;

/**
 * Batch input type for the procurement card job.
 */
public class ProcurementCardInputFileType extends BatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardInputFileType.class);

    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.PCDO_FILE_TYPE_INDENTIFIER;
    }
    
    public String getWorkgroupParameterComponent() {
        return KFSConstants.SystemGroupParameterNames.PCDO_FILE_TYPE_PARAMETER_COMPONENT;
    }

    public String getWorkgroupParameterNamespace() {
        return KFSConstants.SystemGroupParameterNames.PCDO_FILE_TYPE_PARAMETER_NAMESPACE;
    }

    /**
     * No additional information is added to procurment card batch files.
     * 
     * @see org.kuali.kfs.batch.BatchInputFileType#getFileName(org.kuali.core.bo.user.UniversalUser, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(UniversalUser user, Object parsedFileContents, String userIdentifier) {
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();

        String fileName = "pcdo_"  + user.getPersonUserIdentifier().toLowerCase();
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + dateTimeService.toString(currentTimestamp, "yyyyMMdd_HHmmss");
        
        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }


    /**
     * Builds the file name using the following construction: All pcdo files start with pcdo_ append the username of the user
     * uploading the file append the supplied user identifier finally append the current timestamp
     * 
     * @see org.kuali.kfs.batch.BatchInputFileType#checkAuthorization(org.kuali.core.bo.user.UniversalUser, java.io.File)
     */
    public boolean checkAuthorization(UniversalUser user, File batchFile) {
        return true;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PCDO;
    }

    /**
     * Gets the dateTimeService attribute.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
