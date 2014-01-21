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
package org.kuali.kfs.module.tem.batch;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportData;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class CreditCardDataXmlInputFileType extends XmlBatchInputFileTypeBase {

    private DateTimeService dateTimeService;
    private String fileNamePrefix;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return TemConstants.CREDIT_CARD_DATA_XML_INPUT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(java.lang.String, java.lang.Object, java.lang.String)
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        StringBuilder fileName = new StringBuilder();

        fileUserIdentifier = StringUtils.deleteWhitespace(fileUserIdentifier);
        fileUserIdentifier = StringUtils.remove(fileUserIdentifier, TemConstants.FILE_NAME_PART_DELIMITER);

        fileName.append(this.getFileNamePrefix()).append(TemConstants.FILE_NAME_PART_DELIMITER);
        fileName.append(principalName).append(TemConstants.FILE_NAME_PART_DELIMITER);
        fileName.append(fileUserIdentifier).append(TemConstants.FILE_NAME_PART_DELIMITER);

        fileName.append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));

        return fileName.toString();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        CreditCardImportData creditCardData = (CreditCardImportData) super.parse(fileByteContent);
        return creditCardData;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getAuthorPrincipalName(java.io.File)
     */
    @Override
    public String getAuthorPrincipalName(File file) {
        return StringUtils.substringBetween(file.getName(), this.getFileNamePrefix(), TemConstants.FILE_NAME_PART_DELIMITER);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return TemKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_CREDIT_CARD_DATA_XML_FILE;
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

    /**
     * Gets the fileNamePrefix attribute.
     * @return Returns the fileNamePrefix.
     */
    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    /**
     * Sets the fileNamePrefix attribute value.
     * @param fileNamePrefix The fileNamePrefix to set.
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

}
