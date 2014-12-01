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
