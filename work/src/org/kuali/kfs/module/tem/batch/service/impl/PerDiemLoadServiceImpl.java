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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemConstants.PerDiemParameter;
import org.kuali.kfs.module.tem.batch.PerDiemLoadStep;
import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadService;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * implement the service methods that can parse and load federal per diem records, which can be downloaded from
 * http://www.defensetravel.dod.mil/site/perdiemFiles.cfm
 * http://www.defensetravel.dod.mil/Docs/perdiem/browse/Allowances/Per_Diem_Rates/Relational/ This implementation can applied onto
 * the TXT and XML files in ZIP file, which includes the file format definition as well.
 */
public class PerDiemLoadServiceImpl implements PerDiemLoadService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PerDiemLoadServiceImpl.class);

    public final static String REPORT_FILE_NAME_PATTERN = "{0}/{1}_{2}{3}";

    private PerDiemService perDiemService;

    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private PerDiemLoadValidationService perDiemLoadValidationService;
    private BusinessObjectReportHelper perDiemUploadReportHelper;

    private BatchInputFileService batchInputFileService;
    private List<BatchInputFileType> perDiemInputFileTypes;

    private String perDiemFileErrorDirectory;
    private String perDiemReportDirectory;
    private String perDiemReportFilePrefix;

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemLoadService#loadPerDiem()
     */
    @Override
    public boolean loadPerDiem() {
        boolean success = true;

        for (BatchInputFileType inputFileType : perDiemInputFileTypes) {
            List<String> inputFileNames = batchInputFileService.listInputFileNamesWithDoneFile(inputFileType);

            for (String dataFileName : inputFileNames) {
                success &= this.loadPerDiem(dataFileName, inputFileType);
            }
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemLoadService#loadPerDiem(java.lang.String,
     *      org.kuali.kfs.sys.batch.BatchInputFileType)
     */
    @Override
    @Transactional
    public boolean loadPerDiem(String dataFileName, BatchInputFileType inputFileType) {
        String fileExtension = "." + inputFileType.getFileExtension();
        String doneFileName = this.getCompanionFileName(dataFileName, fileExtension, TemConstants.DONE_FILE_SUFFIX);
        File doneFile = this.getFileByAbsolutePath(doneFileName);

        try {
            FileInputStream fileContents = new FileInputStream(dataFileName);
            LOG.info("Process Per Diem file [" + dataFileName + "]");
            
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            List<PerDiemForLoad> perDiemList = (List<PerDiemForLoad>) batchInputFileService.parse(inputFileType, fileByteContent);
            IOUtils.closeQuietly(fileContents);
            
            List<PerDiemForLoad> validPerDiemList = this.validatePerDiem(perDiemList, dataFileName);
            
            boolean isAllValid = validPerDiemList.size() == perDiemList.size();
            if (!isAllValid && this.isRejectAllWhenError()) {
                String error = "The per diem records to be loaded are rejected due to data problem. Please check the per diem load report.";
                throw new RuntimeException(error);
            }
            
            boolean isDeactivatePerDiem = this.isDeactivatePerDiem();
            List<PerDiem> perDiemsForExpriation =  this.getPerDiemService().retrieveExpireDeactivatePreviousPerDiem(validPerDiemList, isDeactivatePerDiem);

            businessObjectService.save(perDiemsForExpriation);
            businessObjectService.save(validPerDiemList);
        }
        catch (Exception ex) {
            LOG.error("Failed to process the file : " + dataFileName, ex);
            this.moveErrorFile(dataFileName, this.getPerDiemFileErrorDirectory());
            
            throw new RuntimeException("Failed to process the file : " + dataFileName, ex);
        }
        finally {
            boolean doneFileDeleted = doneFile.delete();
        }

        return true;
    }

    protected boolean isDeactivatePerDiem() {
        return this.getParameterService().getIndicatorParameter(PerDiemLoadStep.class, PerDiemParameter.PREVIOUS_PER_DIEM_DEACTIVATION_IND_PARAM_NAME);
    }

    /**
     * determine whether the whole per diem file has to be rejected whenever an error occurs 
     */
    protected boolean isRejectAllWhenError() {
        return this.getParameterService().getIndicatorParameter(PerDiemLoadStep.class, PerDiemParameter.REJECT_FILE_WHEN_ERROR_IND_PARAM_NAME);
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemLoadService#updatePerDiem(java.util.List)
     */
    @Override
    public void updatePerDiem(List<PerDiemForLoad> perDiemList) {
        int lineNumber = 0;
        for (PerDiemForLoad perDiemForLoad : perDiemList) {
            this.updatePerDiem(perDiemForLoad);
            perDiemForLoad.setLineNumber(++lineNumber);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemLoadService#updatePerDiem(org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad)
     */
    @Override
    public void updatePerDiem(PerDiemForLoad perDiem) {       
        Date effectiveDate = this.getEffectiveDateFromString(perDiem);
        perDiem.setEffectiveFromDate(effectiveDate);       

        String seasonBeginMonthAndDay = perDiem.getSeasonBeginDateAsString();
        perDiem.setSeasonBeginMonthAndDay(seasonBeginMonthAndDay);
        
        Date loadDate = this.getDateTimeService().getCurrentSqlDate();
        perDiem.setLoadDate(loadDate);
        
        String primaryDestination = perDiem.getPrimaryDestination();
        if(StringUtils.isBlank(primaryDestination)){
            perDiem.setPrimaryDestination(StringUtils.EMPTY);
        }
        
        String county = perDiem.getCounty();
        if(StringUtils.isBlank(county)){
            perDiem.setCounty(StringUtils.EMPTY);
        }        

        this.getPerDiemService().updateTripType(perDiem);
        this.getPerDiemService().breakDownMealsIncidental(perDiem);
    }

    /**
     * get the session begin date built from the given date string
     * 
     * @return the session begin date built from the given date string
     */
    protected Date getSessionBeginDateFromString(PerDiemForLoad perDiem) {
        String seasonBeginDateAsString = perDiem.getSeasonBeginDateAsString();

        return buildDate(perDiem, seasonBeginDateAsString);
    }   

    /**
     * get the session end date built from the given date string
     * 
     * @return the session end date built from the given date string
     */
    protected Date getSessionEndDateFromString(PerDiemForLoad perDiem) {
        String seasonEndDateAsString = perDiem.getSeasonEndDateAsString();

        return buildDate(perDiem, seasonEndDateAsString);
    }

    /**
     * This method...
     * @param perDiem
     * @return
     */
    protected Date buildDate(PerDiemForLoad perDiem, String seasonDateAsString) {
        int effectiveYear = this.getEffectiveYear(perDiem);
        
        Date effectiveDate = perDiem.getEffectiveFromDate();
        
        Date seasonDate = this.getDateFromString(seasonDateAsString, effectiveYear);
        int difference = this.getDateTimeService().dateDiff(effectiveDate, seasonDate, true);
        if(difference <= 0){
            DateUtils.addYears(seasonDate, 1);
        }
        return seasonDate;
    }

    /**
     * get the effective date built from the given date string
     * 
     * @return the effective date built from the given date string
     */
    protected Date getEffectiveDateFromString(PerDiemForLoad perDiem) {
        int effectiveYear = this.getDateTimeService().getCurrentCalendar().get(Calendar.YEAR);

        return this.getDateFromString(perDiem.getEffectiveDateAsString(), effectiveYear);
    }
    
    /**
     * get the expiration date built from the given date string
     * 
     * @return the expiration date built from the given date string
     */
    protected Date getExpirationDateFromString(PerDiemForLoad perDiem) {
        int effectiveYear = this.getDateTimeService().getCurrentCalendar().get(Calendar.YEAR);

        return this.getDateFromString(perDiem.getExpirationDateAsString(), effectiveYear);
    }

    /**
     * get date from the given date string
     * 
     * @param dateAsString the given date string
     * @return date built from the given date string
     */ 
    protected Date getDateFromString(String dateAsString, int effectiveYear) {
        String localDateAsString = completeDateString(dateAsString, effectiveYear);

        return this.convertDateFrom(localDateAsString);
    }    

    /**
     * complete the date string if the year is missing
     */
    protected String completeDateString(String dateString, int effectiveYear) {
        if (StringUtils.isNotEmpty(dateString) && StringUtils.countMatches(dateString, TemConstants.DATE_FIELD_SEPARATOR) >= 2) {
            return dateString;
        }

        return dateString + TemConstants.DATE_FIELD_SEPARATOR + effectiveYear;
    }

    /**
     * convert the given string into date
     */
    protected Date convertDateFrom(String dateString) {
        try {
            java.util.Date localDate = TemConstants.SIMPLE_DATE_FORMAT.parse(dateString);

            return DateUtils.convertToSqlDate(localDate);
        }
        catch (ParseException ex) {
            throw new RuntimeException("The date " + dateString + " must be formatted as " + TemConstants.DATE_FORMAT_STRING, ex);
        }
    }
    
    /**
     * get the year of the per diem in effect
     */
    protected int getEffectiveYear(PerDiemForLoad perDiem){
        Date effectiveDate = perDiem.getEffectiveFromDate();
        
        return this.getDateTimeService().getCurrentCalendar().get(Calendar.YEAR);
    }

    /**
     * move the given file to the specified directory
     * 
     * @param fileName the given file name
     * @param directory the specified directory
     */
    protected void moveErrorFile(String fileName, String directory) {
        File dataFile = this.getFileByAbsolutePath(fileName);

        if (dataFile != null && dataFile.exists()) {
            File errorDirectory = this.getFileByAbsolutePath(directory);

            try {
                FileUtils.moveToDirectory(dataFile, errorDirectory, true);
            }
            catch (IOException ex) {
                LOG.error("Cannot move the file:" + fileName + " to the directory: " + perDiemFileErrorDirectory, ex);
            }
        }
    }

    /**
     * get the file based on the given absolute file path
     * 
     * @param absolutePath the given absolute file path
     * @return the file with the given absolute file path
     */
    protected File getFileByAbsolutePath(String absolutePath) {
        File dataFile = new File(absolutePath);

        if (!dataFile.exists() || !dataFile.canRead()) {
            LOG.error("Cannot find/read data file " + absolutePath);
            return null;
        }

        return dataFile;
    }

    /**
     * generate the name of a companion file of the given file with the given companion file extension
     * 
     * @param fileAbsPath the given file
     * @param fileExtension the extension of the given file
     * @param companionFileExtension the given companion file extension
     * @return the name of a companion file of the given file with the given companion file extension
     */
    protected String getCompanionFileName(String fileAbsPath, String fileExtension, String companionFileExtension) {
        return StringUtils.removeEnd(fileAbsPath, fileExtension).concat(companionFileExtension);
    }

    /**
     * validate the given per diem records
     * 
     * @return the valid per diem records
     */
    protected <T extends PerDiem> List<T> validatePerDiem(List<T> perDiemList, String fileName) {
        List<T> validPerDiems = new ArrayList<T>();

        PrintStream reportDataStream = this.getReportPrintStream();

        this.writeReportHeader(reportDataStream, fileName);

        for (T perDiem : perDiemList) {
            List<Message> errorMessage = this.getPerDiemLoadValidationService().validate(perDiem);

            if (ObjectUtils.isNotNull(errorMessage) && !errorMessage.isEmpty()) {
                this.writeErrorToReport(reportDataStream, perDiem, errorMessage);
            }
            else{
                validPerDiems.add(perDiem);
            }
        }

        if (reportDataStream != null) {
            reportDataStream.flush();
            reportDataStream.close();
        }

        return validPerDiems;
    }

    /**
     * write report header
     */
    protected void writeReportHeader(PrintStream reportDataStream, String fileName) {
        StringBuilder header = new StringBuilder();
        header.append(MessageBuilder.buildMessageWithPlaceHolder(TemKeyConstants.MESSAGE_PER_DIEM_REPORT_HEADER, BusinessObjectReportHelper.LINE_BREAK, fileName));
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        header.append(BusinessObjectReportHelper.LINE_BREAK);

        Map<String, String> tableDefinition = this.getPerDiemUploadReportHelper().getTableDefinition();
        String tableHeaderFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY);

        header.append(tableHeaderFormat);

        reportDataStream.print(header);
    }

    /**
     * write the error message to the given print stream
     */
    protected <T extends PerDiem> void writeErrorToReport(PrintStream reportDataStream, T perDiem, List<Message> errorMessage) {
        String reportEntry = this.formatMessage(perDiem, errorMessage);

        reportDataStream.println(reportEntry);
    }

    /**
     * build message body with the given per diem and message list
     */
    protected <T extends PerDiem> String formatMessage(T perDiem, List<Message> messageList) {
        StringBuilder body = new StringBuilder();

        Map<String, String> tableDefinition = this.getPerDiemUploadReportHelper().getTableDefinition();
        String tableCellFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY);

        List<String> propertyList = this.getPerDiemUploadReportHelper().getTableCellValues(perDiem, false);

        String fieldLine = String.format(tableCellFormat, propertyList.toArray());
        body.append(fieldLine);

        for (Message message : messageList) {
            body.append("\t**  ").append(message).append(BusinessObjectReportHelper.LINE_BREAK);
        }

        return body.toString();
    }

    /**
     * get print stream for report
     */
    protected PrintStream getReportPrintStream() {
        String dateTime = dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentSqlDate());
        String reportFileName = MessageFormat.format(REPORT_FILE_NAME_PATTERN, this.getPerDiemReportDirectory(), this.getPerDiemReportFilePrefix(), dateTime, TemConstants.TEXT_FILE_SUFFIX);

        File outputfile = new File(reportFileName);

        try {
            return new PrintStream(outputfile);
        }
        catch (FileNotFoundException e) {
            String errorMessage = "Cannot find the output file: " + reportFileName;

            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Gets the perDiemService attribute.
     * 
     * @return Returns the perDiemService.
     */
    public PerDiemService getPerDiemService() {
        return perDiemService;
    }

    /**
     * Sets the perDiemService attribute value.
     * 
     * @param perDiemService The perDiemService to set.
     */
    public void setPerDiemService(PerDiemService perDiemService) {
        this.perDiemService = perDiemService;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the perDiemFileErrorDirectory attribute.
     * 
     * @return Returns the perDiemFileErrorDirectory.
     */
    public String getPerDiemFileErrorDirectory() {
        return perDiemFileErrorDirectory;
    }

    /**
     * Sets the perDiemFileErrorDirectory attribute value.
     * 
     * @param perDiemFileErrorDirectory The perDiemFileErrorDirectory to set.
     */
    public void setPerDiemFileErrorDirectory(String perDiemFileErrorDirectory) {
        this.perDiemFileErrorDirectory = perDiemFileErrorDirectory;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the perDiemLoadValidationService attribute.
     * 
     * @return Returns the perDiemLoadValidationService.
     */
    public PerDiemLoadValidationService getPerDiemLoadValidationService() {
        return perDiemLoadValidationService;
    }

    /**
     * Sets the perDiemLoadValidationService attribute value.
     * 
     * @param perDiemLoadValidationService The perDiemLoadValidationService to set.
     */
    public void setPerDiemLoadValidationService(PerDiemLoadValidationService perDiemLoadValidationService) {
        this.perDiemLoadValidationService = perDiemLoadValidationService;
    }

    /**
     * Gets the perDiemReportDirectory attribute.
     * 
     * @return Returns the perDiemReportDirectory.
     */
    public String getPerDiemReportDirectory() {
        return perDiemReportDirectory;
    }

    /**
     * Sets the perDiemReportDirectory attribute value.
     * 
     * @param perDiemReportDirectory The perDiemReportDirectory to set.
     */
    public void setPerDiemReportDirectory(String perDiemReportDirectory) {
        this.perDiemReportDirectory = perDiemReportDirectory;
    }

    /**
     * Gets the perDiemReportFilePrefix attribute.
     * 
     * @return Returns the perDiemReportFilePrefix.
     */
    public String getPerDiemReportFilePrefix() {
        return perDiemReportFilePrefix;
    }

    /**
     * Sets the perDiemReportFilePrefix attribute value.
     * 
     * @param perDiemReportFilePrefix The perDiemReportFilePrefix to set.
     */
    public void setPerDiemReportFilePrefix(String perDiemReportFilePrefix) {
        this.perDiemReportFilePrefix = perDiemReportFilePrefix;
    }

    /**
     * Gets the perDiemUploadReportHelper attribute.
     * 
     * @return Returns the perDiemUploadReportHelper.
     */
    public BusinessObjectReportHelper getPerDiemUploadReportHelper() {
        return perDiemUploadReportHelper;
    }

    /**
     * Sets the perDiemUploadReportHelper attribute value.
     * 
     * @param perDiemUploadReportHelper The perDiemUploadReportHelper to set.
     */
    public void setPerDiemUploadReportHelper(BusinessObjectReportHelper perDiemUploadReportHelper) {
        this.perDiemUploadReportHelper = perDiemUploadReportHelper;
    }

    /**
     * Gets the batchInputFileService attribute.
     * 
     * @return Returns the batchInputFileService.
     */
    public BatchInputFileService getBatchInputFileService() {
        return batchInputFileService;
    }

    /**
     * Sets the batchInputFileService attribute value.
     * 
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Gets the perDiemInputFileTypes attribute.
     * 
     * @return Returns the perDiemInputFileTypes.
     */
    public List<BatchInputFileType> getPerDiemInputFileTypes() {
        return perDiemInputFileTypes;
    }

    /**
     * Sets the perDiemInputFileTypes attribute value.
     * 
     * @param perDiemInputFileTypes The perDiemInputFileTypes to set.
     */
    public void setPerDiemInputFileTypes(List<BatchInputFileType> perDiemInputFileTypes) {
        this.perDiemInputFileTypes = perDiemInputFileTypes;
    }
}
