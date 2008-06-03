/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.capitalAssetBuilder.batch;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.batch.BatchInputFileTypeBase;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;

/**
 * Batch input type for the pre-asset tagging job.
 */
public class PreAssetTaggingInputFileType extends BatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreAssetTaggingInputFileType.class);

    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return CamsConstants.PRE_ASSET_TAGGING_FILE_TYPE_INDENTIFIER;
    }

    public Class getUploadWorkgroupParameterComponent() {
        return PreAssetTaggingStep.class;
    }

    /**
     * No additional information is added to pre-asset tagging batch files.
     * Builds the file name using the following construction: All pre-asset tagging files start with pre_asset_tagging the username of the user
     * uploading the file append the supplied user identifier finally append the current timestamp
     * 
     * @see org.kuali.kfs.batch.BatchInputFileType#getFileName(org.kuali.core.bo.user.UniversalUser, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(UniversalUser user, Object parsedFileContents, String userIdentifier) {
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();

        String fileName = "pre_asset_tagging_" + user.getPersonUserIdentifier().toLowerCase();
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + dateTimeService.toString(currentTimestamp, "yyyyMMdd_HHmmss");

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }


    /**
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
        return CamsKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PRE_ASSET_TAGGING;
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
