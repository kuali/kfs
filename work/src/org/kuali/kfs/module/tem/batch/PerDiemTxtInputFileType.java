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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;
import org.kuali.kfs.module.tem.batch.service.PerDiemFileParsingService;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadService;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class PerDiemTxtInputFileType extends BatchInputFileTypeBase {
    private static Logger LOG = Logger.getLogger(PerDiemTxtInputFileType.class);

    private PerDiemFileParsingService perDiemFileParsingService;
    private DateTimeService dateTimeService;

    private String fileNamePrefix;
    private String deliminator;
    private List<String> perDiemFieldsToPopulate;

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
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return TemConstants.PER_DIEM_INPUT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));

        List<PerDiemForLoad> perDiemList = this.getPerDiemFileParsingService().buildPerDiemsFromFlatFile(reader, this.getDeliminator(), this.getPerDiemFieldsToPopulate());

        PerDiemLoadService perDiemLoadService = SpringContext.getBean(PerDiemLoadService.class);
        perDiemLoadService.updatePerDiem(perDiemList);

        return perDiemList;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(java.lang.String, java.lang.Object)
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {

    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        PerDiemLoadValidationService perDiemLoadValidationService = SpringContext.getBean(PerDiemLoadValidationService.class);
        List<PerDiemForLoad> perDiemList = (List<PerDiemForLoad>)parsedFileContents;

        return perDiemLoadValidationService.validate(perDiemList);
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
        return TemKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PER_DIEM_FILE;
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

    /**
     * Gets the perDiemFileParsingService attribute.
     * @return Returns the perDiemFileParsingService.
     */
    public PerDiemFileParsingService getPerDiemFileParsingService() {
        return perDiemFileParsingService;
    }

    /**
     * Sets the perDiemFileParsingService attribute value.
     * @param perDiemFileParsingService The perDiemFileParsingService to set.
     */
    public void setPerDiemFileParsingService(PerDiemFileParsingService perDiemFileParsingService) {
        this.perDiemFileParsingService = perDiemFileParsingService;
    }

    /**
     * Gets the deliminator attribute.
     * @return Returns the deliminator.
     */
    public String getDeliminator() {
        return deliminator;
    }

    /**
     * Sets the deliminator attribute value.
     * @param deliminator The deliminator to set.
     */
    public void setDeliminator(String deliminator) {
        this.deliminator = deliminator;
    }

    /**
     * Gets the perDiemFieldsToPopulate attribute.
     * @return Returns the perDiemFieldsToPopulate.
     */
    public List<String> getPerDiemFieldsToPopulate() {
        return perDiemFieldsToPopulate;
    }

    /**
     * Sets the perDiemFieldsToPopulate attribute value.
     * @param perDiemFieldsToPopulate The perDiemFieldsToPopulate to set.
     */
    public void setPerDiemFieldsToPopulate(List<String> perDiemFieldsToPopulate) {
        this.perDiemFieldsToPopulate = perDiemFieldsToPopulate;
    }
}
