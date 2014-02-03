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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImport;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.module.tem.batch.service.DataReportService;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.businessobject.AgencyImportData;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.defaultvalue.NextAgencyStagingDataIdFinder;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

public class AgencyDataImportServiceImpl implements AgencyDataImportService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyDataImportServiceImpl.class);

    public final static String REPORT_FILE_NAME_PATTERN = "{0}/{1}_{2}{3}";

    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private ExpenseImportByTravelerService expenseImportByTravelerService;
    private ExpenseImportByTripService expenseImportByTripService;
    private TravelExpenseService travelExpenseService;
    private static ConfigurationService configurationService;
    private TravelDocumentService travelDocumentService;

    private List<BatchInputFileType> agencyDataImportFileTypes;
    private String agencyDataFileErrorDirectory;
    private BusinessObjectReportHelper agencyDataTravelerUploadReportHelper;
    private BusinessObjectReportHelper agencyDataTripUploadReportHelper;
    private DateTimeService dateTimeService;
    private DataReportService dataReportService;
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

        try {
            FileInputStream fileContents = new FileInputStream(dataFileName);

            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            AgencyImportData agencyData = (AgencyImportData) batchInputFileService.parse(inputFileType, fileByteContent);
            IOUtils.closeQuietly(fileContents);

            LOG.info("Agency Import - validating: " + dataFileName);

            List<AgencyStagingData> validAgencyList = validateAgencyData(agencyData, dataFileName);

            boolean isAllValid = validAgencyList.size() == agencyData.getAgencyStagingData().size();
            if (!isAllValid) {
                String error = "The agency data records to be loaded are rejected due to data problem. Please check the agency data report.";
                moveErrorFile(dataFileName, this.getAgencyDataFileErrorDirectory());
            }
            businessObjectService.save(validAgencyList);
        }
        catch (Exception ex) {
            LOG.error("Failed to process the file : " + dataFileName, ex);
            moveErrorFile(dataFileName, this.getAgencyDataFileErrorDirectory());
            return false;
        }
        finally {
         // boolean doneFileDeleted = doneFile.delete();
            removeDoneFiles(dataFileName);

        }
        return true;
    }


    public void moveErrorFile(String dataFileName, String agencyDataFileErrordirectory) {
        File dataFile = new File(dataFileName);

        if (!dataFile.exists() || !dataFile.canRead()) {
            LOG.error("Cannot find/read data file " + dataFileName);

        }
        else {

            try {
                FileUtils.moveToDirectory(dataFile, new File(agencyDataFileErrordirectory), true);
            }
            catch (IOException ex) {
                LOG.error("Cannot move the file:" + dataFile + " to the directory: " + agencyDataFileErrordirectory, ex);
            }
        }
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    protected void removeDoneFiles(String dataFileName) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + TemConstants.DONE_FILE_SUFFIX);
            if (doneFile.exists()) {
                doneFile.delete();
            }

    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyImportData, java.lang.String)
     */
    @Override
    public List<AgencyStagingData> validateAgencyData(AgencyImportData agencyImportData, String dataFileName) {
        PrintStream reportDataStream = dataReportService.getReportPrintStream(getAgencyDataReportDirectory(), getAgencyDataReportFilePrefix());

        BusinessObjectReportHelper reportHelper = getReportHelper(ExpenseImport.getExpenseImportByCode(agencyImportData.getImportBy()));
        HashMap<String, AgencyStagingData> validAgencyStagingDataMap = new HashMap<String, AgencyStagingData>();
        try {
            dataReportService.writeReportHeader(reportDataStream, dataFileName, TemKeyConstants.MESSAGE_AGENCY_DATA_REPORT_HEADER, reportHelper);
            int count = 1;

            List<AgencyStagingData> importedAgencyStagingDataList = agencyImportData.getAgencyStagingData();
            int listSize = importedAgencyStagingDataList.size();
            LOG.info("Validating agency import by traveler: importing "+ listSize +" records");

            NextAgencyStagingDataIdFinder idFinder = new NextAgencyStagingDataIdFinder();
            for (AgencyStagingData importedAgencyStagingData : importedAgencyStagingDataList) {
                String key = null;
                importedAgencyStagingData.setId(Integer.valueOf(idFinder.getValue()));
                importedAgencyStagingData.setImportBy(agencyImportData.getImportBy());
                importedAgencyStagingData.setStagingFileName(StringUtils.substringAfterLast(dataFileName, File.separator));

                String itineraryData = importedAgencyStagingData.getItineraryDataString();

                AgencyStagingData validAgencyStagingData = null;
                List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

                // validate by Traveler ID
                if (importedAgencyStagingData.getExpenseImport() == ExpenseImport.traveler) {


                     key = importedAgencyStagingData.getTravelerId() + "~" + itineraryData + "~" +
                         importedAgencyStagingData.getCreditCardOrAgencyCode() + "~" + importedAgencyStagingData.getTransactionPostingDate() + "~" +
                         importedAgencyStagingData.getTripExpenseAmount() + "~" + importedAgencyStagingData.getTripInvoiceNumber();

                    LOG.info("Validating agency import by traveler. Record# " + count + " of " + listSize);

                    if(!validAgencyStagingDataMap.containsKey(key)){
                        errorMessages.addAll(expenseImportByTravelerService.validateAgencyData(importedAgencyStagingData));

                        //no errors- load the data
                        if (errorMessages.isEmpty()) {
                            validAgencyStagingDataMap.put(key,importedAgencyStagingData);
                        }
                        else {
                        //if there are errors check for required fields missing or duplicate data- load anything else
                            String errorCode = importedAgencyStagingData.getErrorCode();
                            if (ObjectUtils.isNotNull(errorCode) &&
                                    !StringUtils.equals(TemConstants.AgencyStagingDataErrorCodes.AGENCY_DUPLICATE_DATA, errorCode)) {

                                validAgencyStagingDataMap.put(key,importedAgencyStagingData);
                            }
                        }
                    }
                    else {
                        // duplicate record found in the file
                        ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRAVELER_DUPLICATE_RECORD,
                                importedAgencyStagingData.getTravelerId(), itineraryData, importedAgencyStagingData.getCreditCardOrAgencyCode(),
                                importedAgencyStagingData.getTransactionPostingDate().toString(), importedAgencyStagingData.getTripExpenseAmount().toString(), importedAgencyStagingData.getTripInvoiceNumber());
                            errorMessages.add(error);
                    }
                }
                // validate by Trip ID
                else if (importedAgencyStagingData.getExpenseImport() == ExpenseImport.trip) {

                    key = importedAgencyStagingData.getTravelerId() + "~" + importedAgencyStagingData.getTripId() + "~" + importedAgencyStagingData.getCreditCardOrAgencyCode()
                            + "~" + importedAgencyStagingData.getTransactionPostingDate() + "~" + importedAgencyStagingData.getTripExpenseAmount() ;

                    LOG.info("Validating agency import by trip. Record# " + count + " of " + listSize);

                    if(!validAgencyStagingDataMap.containsKey(key)){
                        errorMessages.addAll(expenseImportByTripService.validateAgencyData(importedAgencyStagingData));

                        //no errors- load the data
                        if (errorMessages.isEmpty()) {
                            validAgencyStagingDataMap.put(key,importedAgencyStagingData);
                        }
                        else {
                        //if there are errors check for required fields missing or duplicate data- load anything else
                            String errorCode = importedAgencyStagingData.getErrorCode();
                            if (ObjectUtils.isNotNull(errorCode) &&
                                    !StringUtils.equals(TemConstants.AgencyStagingDataErrorCodes.AGENCY_DUPLICATE_DATA, errorCode)) {

                                validAgencyStagingDataMap.put(key,importedAgencyStagingData);
                            }
                        }
                    }
                    else {
                        // duplicate record found in the file.
                        ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRIP_DUPLICATE_RECORD,
                                importedAgencyStagingData.getTripId(), importedAgencyStagingData.getCreditCardOrAgencyCode(),
                                importedAgencyStagingData.getTransactionPostingDate().toString(), importedAgencyStagingData.getTripExpenseAmount().toString(), itineraryData);
                            errorMessages.add(error);
                    }
                }

                //writer to error report
                if (!errorMessages.isEmpty()){
                    dataReportService.writeToReport(reportDataStream, importedAgencyStagingData, errorMessages, reportHelper);
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

        ArrayList<AgencyStagingData> validAgencyRecords = new ArrayList<AgencyStagingData>() ;
        for(Map.Entry<String , AgencyStagingData> entry : validAgencyStagingDataMap.entrySet()) {
            validAgencyRecords.add(entry.getValue());
        }

        return validAgencyRecords;
    }


    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#moveAgencyDataToHistoricalExpenseTable()
     */
    @Override
    public boolean moveAgencyDataToHistoricalExpenseTable() {

        LOG.info("Starting Agency Expense Distribution/Reconciliation Process");
        List<AgencyStagingData> agencyData = travelExpenseService.retrieveValidAgencyData();
        if (ObjectUtils.isNotNull(agencyData) && agencyData.size() > 0) {

            //set up map for keeping track of sequence helpers per Trip Id. Sequence helper is not needed for ImportBy=TRV
            Map<String,GeneralLedgerPendingEntrySequenceHelper> sequenceHelperMap = new HashMap<String,GeneralLedgerPendingEntrySequenceHelper>();
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = null;

            for (AgencyStagingData agency : agencyData) {

                if (agency.getExpenseImport() == ExpenseImport.trip) {
                    sequenceHelper = getGeneralLedgerPendingEntrySequenceHelper(agency, sequenceHelperMap);
                }
                boolean result = processAgencyStagingExpense(agency, sequenceHelper);
                getBusinessObjectService().save(agency);
                LOG.info("Agency Data Id: "+ agency.getId() + (result ? " was":" was not") + " processed.");
            }
        }

        LOG.info("Finished Agency Expense Distribution/Reconciliation Process");
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#processAgencyStagingExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Transactional
    @Override
    public boolean processAgencyStagingExpense(AgencyStagingData agency, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean result = false;
        if (agency.getExpenseImport() == ExpenseImport.traveler) {
            result = expenseImportByTravelerService.distributeExpense(agency);

        }

        if (agency.getExpenseImport() == ExpenseImport.trip) {
            result = expenseImportByTripService.reconciliateExpense(agency, sequenceHelper);
        }

        return result;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.AgencyDataImportService#matchExpenses()
     */
    @Override
    public boolean matchExpenses() {
        LOG.info("Starting Agency Reconciliation Match Process");

        Set<Integer> visitedIds = new HashSet<Integer>();
        List<AgencyStagingData> agencyData = travelExpenseService.retrieveValidAgencyDataByImportType(ExpenseImportTypes.IMPORT_BY_TRIP);
        if (ObjectUtils.isNotNull(agencyData) && agencyData.size() > 0) {
            Map<String, GeneralLedgerPendingEntrySequenceHelper> sequenceHelperMap = new HashMap<String, GeneralLedgerPendingEntrySequenceHelper>();
            int count = 1;
            for (AgencyStagingData agency : agencyData) {
                if (!visitedIds.contains(agency.getId())) {
                    LOG.info("Matching agency data record# " + count + " of " + agencyData.size());
                    boolean result = expenseImportByTripService.reconciliateExpense(agency, getGeneralLedgerPendingEntrySequenceHelper(agency, sequenceHelperMap));
                    LOG.info("Agency Data Id: "+ agency.getId() + (result ? " was":" was not") +" reconciled.");
                    visitedIds.add(agency.getId());
                }
                count++;
            }
        }

        LOG.info("Finished Agency Reconciliation Match Process");
        return true;
    }

    protected GeneralLedgerPendingEntrySequenceHelper getGeneralLedgerPendingEntrySequenceHelper(AgencyStagingData agencyStagingData, Map<String,GeneralLedgerPendingEntrySequenceHelper> sequenceHelperMap) {
        String tripId = agencyStagingData.getTripId();
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = sequenceHelperMap.get(tripId);
        if (ObjectUtils.isNull(sequenceHelper)) {

            Collection<GeneralLedgerPendingEntry> glpes = getGeneralLedgerPendingEntriesForDocumentNumber(agencyStagingData);
            if (ObjectUtils.isNotNull(glpes) && !glpes.isEmpty()) {

                Integer maxSequenceNumber = 0;
                for(GeneralLedgerPendingEntry glpe : glpes) {
                    Integer sequenceNumber = glpe.getTransactionLedgerEntrySequenceNumber();
                    maxSequenceNumber = (maxSequenceNumber < sequenceNumber ? sequenceNumber : maxSequenceNumber);
                }
                sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(maxSequenceNumber++);
            }
            else {
                sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            }

            sequenceHelperMap.put(tripId, sequenceHelper);
        }

        return sequenceHelper;
    }

    /**
     * Gets the currently existing GLPEs for the document we're going to add GLPEs to
     * @param agencyStagingData
     * @return
     */
    @Override
    public Collection<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntriesForDocumentNumber(AgencyStagingData agencyStagingData) {
        Collection<GeneralLedgerPendingEntry> glpes = null;

        TravelDocument travelDocument = getTravelDocumentService().getTravelDocument(agencyStagingData.getTripId());
        if (ObjectUtils.isNotNull(travelDocument)) {
            Map<String,Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, travelDocument.getDocumentNumber());
            glpes = businessObjectService.findMatching(GeneralLedgerPendingEntry.class, fieldValues);
        }

        return glpes;
    }

    protected BusinessObjectReportHelper getReportHelper(ExpenseImport importType){
        BusinessObjectReportHelper reportHelper = getAgencyDataTravelerUploadReportHelper();
        if(ExpenseImport.traveler == importType){
            reportHelper = getAgencyDataTravelerUploadReportHelper();
        }
        else if(ExpenseImport.trip == importType){
            reportHelper = getAgencyDataTripUploadReportHelper();
        }
        return reportHelper;
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

    private static ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    public void setDataReportService(DataReportService dataReportService) {
        this.dataReportService = dataReportService;
    }

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

}
