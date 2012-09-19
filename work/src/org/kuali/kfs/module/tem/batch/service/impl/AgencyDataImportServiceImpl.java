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
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImport;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.batch.service.TemBatchService;
import org.kuali.kfs.module.tem.businessobject.AgencyImportData;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;

public class AgencyDataImportServiceImpl implements AgencyDataImportService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyDataImportServiceImpl.class);
    
    public final static String REPORT_FILE_NAME_PATTERN = "{0}/{1}_{2}{3}";
    
    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private ExpenseImportByTravelerService expenseImportByTravelerService;
    private ExpenseImportByTripService expenseImportByTripService;
    private TemBatchService temBatchService;
    private TravelExpenseService travelExpenseService;
    
    private List<BatchInputFileType> agencyDataImportFileTypes;
    private String agencyDataFileErrorDirectory;
    private BusinessObjectReportHelper agencyDataTravelerUploadReportHelper;
    private BusinessObjectReportHelper agencyDataTripUploadReportHelper;
    private DateTimeService dateTimeService;
    private String agencyDataReportDirectory;
    private String agencyDataReportFilePrefix;

    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#importAgencyData()
     */
    @Override
    public boolean importAgencyData() {
        
        boolean success = true;

        LOG.info("Starting Agency Import Process");

        for (BatchInputFileType inputFileType : agencyDataImportFileTypes) {
            List<String> inputFileNames = batchInputFileService.listInputFileNamesWithDoneFile(inputFileType);

            for (String dataFileName : inputFileNames) {
                success &= importAgencyDataFile(dataFileName, inputFileType);
            }
        }

        LOG.info("Finished Agency Import Process");
        return success;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#importAgencyDataFile(java.lang.String, org.kuali.kfs.sys.batch.BatchInputFileType)
     */
    @Override
    public boolean importAgencyDataFile(String dataFileName, BatchInputFileType inputFileType) {
        
        String fileExtension = "." + inputFileType.getFileExtension();
        String doneFileName = temBatchService.getCompanionFileName(dataFileName, fileExtension, TemConstants.DONE_FILE_SUFFIX);
        File doneFile = temBatchService.getFileByAbsolutePath(doneFileName);

        try {
            FileInputStream fileContents = new FileInputStream(dataFileName);

            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            AgencyImportData agencyData = (AgencyImportData) batchInputFileService.parse(inputFileType, fileByteContent);
            IOUtils.closeQuietly(fileContents);
            
            LOG.info("Agency Import - validating: " + dataFileName);

            List<AgencyStagingData> validAgencyList = validateAgencyData(agencyData, dataFileName);
            
            boolean isAllValid = validAgencyList.size() == agencyData.getAgencies().size();
            if (!isAllValid) {
                String error = "The agency data records to be loaded are rejected due to data problem. Please check the agency data report.";
            }
            businessObjectService.save(validAgencyList);
        }
        catch (Exception ex) {
            LOG.error("Failed to process the file : " + dataFileName, ex);
            temBatchService.moveErrorFile(dataFileName, this.getAgencyDataFileErrorDirectory(), agencyDataFileErrorDirectory);
            return false;
        }
        finally {
            boolean doneFileDeleted = doneFile.delete();
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyImportData, java.lang.String)
     */
    @Override
    public List<AgencyStagingData> validateAgencyData(AgencyImportData agencyData, String dataFileName) {
        PrintStream reportDataStream = this.getReportPrintStream();

        List<AgencyStagingData> validData;
        try {
            this.writeReportHeader(reportDataStream, dataFileName, agencyData.getImportBy());
            validData = new ArrayList<AgencyStagingData>();
            int count = 1;
            for (AgencyStagingData agency : agencyData.getAgencies()) {
                agency.setImportBy(agencyData.getImportBy());
                AgencyStagingData validAgency = null;
                List<String> errorMessages = new ArrayList<String>();

                // validate by Traveler ID
                if (agency.getExpenseImport() == ExpenseImport.traveler) {
                    LOG.info("Validating agency import by traveler. Record# " + count + " of " + agencyData.getAgencies().size());
                    expenseImportByTravelerService.setErrorMessages(errorMessages);
                    if (expenseImportByTravelerService.areMandatoryFieldsPresent(agency)) {
                        validAgency = expenseImportByTravelerService.validateAgencyData(agency);
                    }
                    errorMessages = expenseImportByTravelerService.getErrorMessages();
                }

                // validate by Trip ID
                if (agency.getExpenseImport() == ExpenseImport.trip) {
                    LOG.info("Validating agency import by trip. Record# " + count + " of " + agencyData.getAgencies().size());
                    expenseImportByTripService.setErrorMessages(errorMessages);
                    if (expenseImportByTripService.areMandatoryFieldsPresent(agency)) {
                        validAgency = expenseImportByTripService.validateAgencyData(agency);
                    }
                    errorMessages = expenseImportByTripService.getErrorMessages();
                }

                if (ObjectUtils.isNotNull(validAgency)) {
                    validData.add(validAgency);
                }
                if (errorMessages.size() > 0) {
                    writeErrorToReport(reportDataStream, agency, getMessageAsString(errorMessages), agency.getExpenseImport());
                }
                count++;

            }
        }
        finally {
            if (reportDataStream != null) {
                reportDataStream.flush();
                reportDataStream.close();
            }
        }
        return validData;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#moveAgencyDataToHistoricalExpenseTable()
     */
    @Override
    public boolean moveAgencyDataToHistoricalExpenseTable() {
        
        LOG.info("Starting Agency Expense Distribution/Reconciliation Process");

        List<AgencyStagingData> agencyData = travelExpenseService.retrieveValidAgencyData();
        if (ObjectUtils.isNotNull(agencyData) && agencyData.size() > 0) {
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            for (AgencyStagingData agency : agencyData) {
                processAgencyStagingExpense(agency, sequenceHelper);
            }
        }
        businessObjectService.save(agencyData);

        LOG.info("Finished Agency Expense Distribution/Reconciliation Process");
        return true;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#processAgencyStagingExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public void processAgencyStagingExpense(AgencyStagingData agency, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (agency.getExpenseImport() == ExpenseImport.traveler) {
            expenseImportByTravelerService.distributeExpense(agency, sequenceHelper);
        }

        if (agency.getExpenseImport() == ExpenseImport.trip) {
            expenseImportByTripService.reconciliateExpense(agency, sequenceHelper);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#matchExpenses()
     */
    @Override
    public boolean matchExpenses() {
        LOG.info("Starting Agency Reconciliation Match Process");

        List<AgencyStagingData> agencyData = travelExpenseService.retrieveValidAgencyDataByImportType(ExpenseImportTypes.IMPORT_BY_TRIP);
        if (ObjectUtils.isNotNull(agencyData) && agencyData.size() > 0) {
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            int count = 1;
            for (AgencyStagingData agency : agencyData) {
                LOG.info("Matching agency data record# " + count + " of " + agencyData.size());
                expenseImportByTripService.reconciliateExpense(agency, sequenceHelper);
                count++;         
            }
        }
        businessObjectService.save(agencyData);

        LOG.info("Finished Agency Reconciliation Match Process");
        return true;
    }

    
    /**
     * Build error message stringn out of message list
     * 
     * @param errorMessages
     * @return
     */
    private String getMessageAsString(List<String> messages){
        
        final String separator = " ";
        StrBuilder builder = new StrBuilder();
        builder.appendWithSeparators(messages, separator);
       return  builder.toString();
    }
    
    /**
     * Write to error report
     * 
     * @param reportDataStream
     * @param AgencyStagingData
     * @param errors
     * @param importBy
     */
    protected <T extends AgencyStagingData> void writeErrorToReport(PrintStream reportDataStream,T AgencyStagingData, String errors, ExpenseImport importType) {
        
        String reportEntry = formatMessage(AgencyStagingData, errors, importType);
        reportDataStream.println(reportEntry);
    }
    
    /**
     * 
     * @param agencyStagingData
     * @param errors
     * @param importBy
     * @return
     */
    protected <T extends AgencyStagingData> String formatMessage(T agencyStagingData, String errors, ExpenseImport importType) {
        StringBuilder body = new StringBuilder();
        Map<String, String> tableDefinition=new LinkedHashMap<String, String>();
        List<String> propertyList =new ArrayList<String>();
        if(importType == ExpenseImport.traveler){
            tableDefinition = this.getAgencyDataTravelerUploadReportHelper().getTableDefinition();
            propertyList = this.getAgencyDataTravelerUploadReportHelper().getTableCellValues(agencyStagingData, false);
        }
        if(importType == ExpenseImport.trip){
            tableDefinition = this.getAgencyDataTripUploadReportHelper().getTableDefinition();
            propertyList = this.getAgencyDataTripUploadReportHelper().getTableCellValues(agencyStagingData, false);
        }
        
        String tableCellFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY);
        String fieldLine = String.format(tableCellFormat, propertyList.toArray());
        body.append(fieldLine);
        body.append("\t**  ").append(errors).append(BusinessObjectReportHelper.LINE_BREAK);

        return body.toString();
    }
    
    /**
     * 
     * @param reportDataStream
     * @param fileName
     * @param importBy
     */
    protected void writeReportHeader(PrintStream reportDataStream, String fileName, String importBy) {
        StringBuilder header = new StringBuilder();
        header.append(MessageBuilder.buildMessageWithPlaceHolder(TemKeyConstants.MESSAGE_AGENCY_DATA_REPORT_HEADER, BusinessObjectReportHelper.LINE_BREAK, fileName));
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        header.append(BusinessObjectReportHelper.LINE_BREAK);
        Map<String, String> tableDefinition=new LinkedHashMap<String, String>();;
        if(importBy.equals(ExpenseImportTypes.IMPORT_BY_TRAVELLER)){
            tableDefinition = getAgencyDataTravelerUploadReportHelper().getTableDefinition();
        }
        if(importBy.equals(ExpenseImportTypes.IMPORT_BY_TRIP)){
           tableDefinition = getAgencyDataTripUploadReportHelper().getTableDefinition();
        }
        String tableHeaderFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY);

        header.append(tableHeaderFormat);

        reportDataStream.print(header);
    }
    
    /**
     * get print stream for report
     */
    protected PrintStream getReportPrintStream() {
        String dateTime = dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentSqlDate());
        String reportFileName = MessageFormat.format(REPORT_FILE_NAME_PATTERN, this.getAgencyDataReportDirectory(), this.getAgencyDataReportFilePrefix(), dateTime, TemConstants.TEXT_FILE_SUFFIX);

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
     * Gets the batchInputFileService attribute. 
     * @return Returns the batchInputFileService.
     */
    public BatchInputFileService getBatchInputFileService() {
        return batchInputFileService;
    }

    /**
     * Sets the batchInputFileService attribute value.
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Gets the agencyDataImportFileTypes attribute. 
     * @return Returns the agencyDataImportFileTypes.
     */
    public List<BatchInputFileType> getAgencyDataImportFileTypes() {
        return agencyDataImportFileTypes;
    }

    /**
     * Sets the agencyDataImportFileTypes attribute value.
     * @param agencyDataImportFileTypes The agencyDataImportFileTypes to set.
     */
    public void setAgencyDataImportFileTypes(List<BatchInputFileType> agencyDataImportFileTypes) {
        this.agencyDataImportFileTypes = agencyDataImportFileTypes;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the agencyDataFileErrorDirectory attribute. 
     * @return Returns the agencyDataFileErrorDirectory.
     */
    public String getAgencyDataFileErrorDirectory() {
        return agencyDataFileErrorDirectory;
    }

    /**
     * Sets the agencyDataFileErrorDirectory attribute value.
     * @param agencyDataFileErrorDirectory The agencyDataFileErrorDirectory to set.
     */
    public void setAgencyDataFileErrorDirectory(String agencyDataFileErrorDirectory) {
        this.agencyDataFileErrorDirectory = agencyDataFileErrorDirectory;
    }

    /**
     * Gets the expenseImportByTravelerService attribute. 
     * @return Returns the expenseImportByTravelerService.
     */
    public ExpenseImportByTravelerService getExpenseImportByTravelerService() {
        return expenseImportByTravelerService;
    }

    /**
     * Sets the expenseImportByTravelerService attribute value.
     * @param expenseImportByTravelerService The expenseImportByTravelerService to set.
     */
    public void setExpenseImportByTravelerService(ExpenseImportByTravelerService expenseImportByTravelerService) {
        this.expenseImportByTravelerService = expenseImportByTravelerService;
    }

    /**
     * Gets the expenseImportByTripService attribute. 
     * @return Returns the expenseImportByTripService.
     */
    public ExpenseImportByTripService getExpenseImportByTripService() {
        return expenseImportByTripService;
    }

    /**
     * Sets the expenseImportByTripService attribute value.
     * @param expenseImportByTripService The expenseImportByTripService to set.
     */
    public void setExpenseImportByTripService(ExpenseImportByTripService expenseImportByTripService) {
        this.expenseImportByTripService = expenseImportByTripService;
    }

    /**
     * Gets the temBatchService attribute. 
     * @return Returns the temBatchService.
     */
    public TemBatchService getTemBatchService() {
        return temBatchService;
    }

    /**
     * Sets the temBatchService attribute value.
     * @param temBatchService The temBatchService to set.
     */
    public void setTemBatchService(TemBatchService temBatchService) {
        this.temBatchService = temBatchService;
    }

    /**
     * Gets the travelExpenseService attribute. 
     * @return Returns the travelExpenseService.
     */
    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    /**
     * Sets the travelExpenseService attribute value.
     * @param travelExpenseService The travelExpenseService to set.
     */
    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

    public BusinessObjectReportHelper getAgencyDataTravelerUploadReportHelper() {
        return agencyDataTravelerUploadReportHelper;
    }

    public void setAgencyDataTravelerUploadReportHelper(BusinessObjectReportHelper agencyDataTravelerUploadReportHelper) {
        this.agencyDataTravelerUploadReportHelper = agencyDataTravelerUploadReportHelper;
    }

    public BusinessObjectReportHelper getAgencyDataTripUploadReportHelper() {
        return agencyDataTripUploadReportHelper;
    }

    public void setAgencyDataTripUploadReportHelper(BusinessObjectReportHelper agencyDataTripUploadReportHelper) {
        this.agencyDataTripUploadReportHelper = agencyDataTripUploadReportHelper;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public String getAgencyDataReportDirectory() {
        return agencyDataReportDirectory;
    }

    public void setAgencyDataReportDirectory(String agencyDataReportDirectory) {
        this.agencyDataReportDirectory = agencyDataReportDirectory;
    }

    public String getAgencyDataReportFilePrefix() {
        return agencyDataReportFilePrefix;
    }

    public void setAgencyDataReportFilePrefix(String agencyDataReportFilePrefix) {
        this.agencyDataReportFilePrefix = agencyDataReportFilePrefix;
    }
    private static KualiConfigurationService configurationService;
    private static KualiConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(KualiConfigurationService.class);
        }
        return configurationService;
    }

}
