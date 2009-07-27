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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryTotals;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * Batch input type for the collector job.
 */
public class CollectorXmlInputFileType extends XmlBatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorXmlInputFileType.class);

    protected DateTimeService dateTimeService;
    private CollectorHelperService collectorHelperService;
    
    /**
     * Returns the identifier of the Collector's file type
     * 
     * @return the Collector's file type identifier
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.COLLECTOR_XML_FILE_TYPE_INDENTIFIER;
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
        // this implementation assumes that there is only one batch in the XML file
        CollectorBatch collectorBatch = ((List<CollectorBatch>) parsedFileContents).get(0);
        
        String fileName = "gl_collector_" + collectorBatch.getChartOfAccountsCode() + collectorBatch.getOrganizationCode();
        fileName += "_" + principalName;
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    public boolean validate(Object parsedFileContents) {
        List<CollectorBatch> parsedBatches = (List<CollectorBatch>) parsedFileContents;
        boolean allBatchesValid = true;
        for (CollectorBatch batch : parsedBatches) {
            boolean isValid = collectorHelperService.performValidation(batch);
            if (isValid) {
                isValid = collectorHelperService.checkTrailerTotals(batch, null);
            }
            allBatchesValid &= isValid;
        }
        return allBatchesValid;
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

    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length > 4) {
            return fileNameParts[3];
        }
        return null;
    }
    
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        CollectorBatch batch = (CollectorBatch) super.parse(fileByteContent);
        OriginEntryTotals totals = new OriginEntryTotals();
        totals.addToTotals(batch.getOriginEntries().iterator());
        batch.setOriginEntryTotals(totals);
        return Arrays.asList(batch);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }
}

