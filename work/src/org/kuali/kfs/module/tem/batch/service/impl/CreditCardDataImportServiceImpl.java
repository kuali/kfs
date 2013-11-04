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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImport;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypes;
import org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService;
import org.kuali.kfs.module.tem.batch.service.DataReportService;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TmProfileAccount;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ObjectUtils;

public class CreditCardDataImportServiceImpl implements CreditCardDataImportService{

    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditCardDataImportServiceImpl.class);

    public final static String REPORT_FILE_NAME_PATTERN = "{0}/{1}_{2}{3}";

    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private TemProfileService temProfileService;
    private TravelExpenseService travelExpenseService;
    private CreditCardAgencyService creditCardAgencyService;
    private DataReportService dataReportService;

    private List<BatchInputFileType> creditCardDataImportFileTypes;
    private String creditCardDataFileErrorDirectory;

    private BusinessObjectReportHelper creditCardDataUploadReportHelper;

    private String creditCardDataReportDirectory;
    private String creditCardDataReportFilePrefix;

    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#importCreditCardData()
     */
    @Override
    public boolean importCreditCardData() {
        boolean success = true;

        for (BatchInputFileType inputFileType : creditCardDataImportFileTypes) {
            List<String> inputFileNames = batchInputFileService.listInputFileNamesWithDoneFile(inputFileType);

            for (String dataFileName : inputFileNames) {
                success &= importCreditCardDataFile(dataFileName, inputFileType);
            }
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#importCreditCardDataFile(java.lang.String, org.kuali.kfs.sys.batch.BatchInputFileType)
     */
    @Override
    public boolean importCreditCardDataFile(String dataFileName, BatchInputFileType inputFileType) {

        try {
            FileInputStream fileContents = new FileInputStream(dataFileName);

            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            CreditCardImportData creditCardData = (CreditCardImportData) batchInputFileService.parse(inputFileType, fileByteContent);
            IOUtils.closeQuietly(fileContents);

            LOG.info("Credit Card Import - validating: " + dataFileName);
            List<CreditCardStagingData> validCreditCardList = validateCreditCardData(creditCardData, dataFileName);
            if (!validCreditCardList.isEmpty()){
                businessObjectService.save(validCreditCardList);
            }
        }
        catch (Exception ex) {
            LOG.error("Failed to process the file : " + dataFileName, ex);
            moveErrorFile(dataFileName, creditCardDataFileErrorDirectory);
            return false;
        }
        finally {
           removeDoneFiles(dataFileName);
        }
        return true;
    }

    public void moveErrorFile(String dataFileName, String creditCardDataFileErrorDirectory) {
        File dataFile = new File(dataFileName);

        if (!dataFile.exists() || !dataFile.canRead()) {
            LOG.error("Cannot find/read data file " + dataFileName);

        }
        else {

            try {
                FileUtils.moveToDirectory(dataFile, new File(creditCardDataFileErrorDirectory), true);
            }
            catch (IOException ex) {
                LOG.error("Cannot move the file:" + dataFile + " to the directory: " + creditCardDataFileErrorDirectory, ex);
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
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#validateCreditCardData(org.kuali.kfs.module.tem.businessobject.CreditCardImportData, java.lang.String)
     */
    @Override
    public List<CreditCardStagingData> validateCreditCardData(CreditCardImportData creditCardList, String dataFileName) {

        PrintStream reportDataStream = dataReportService.getReportPrintStream(getCreditCardDataReportDirectory(), getCreditCardDataReportFilePrefix());
        List<CreditCardStagingData> validData = new ArrayList<CreditCardStagingData>();

        try {
            dataReportService.writeReportHeader(reportDataStream, dataFileName, TemKeyConstants.MESSAGE_CREDIT_CARD_DATA_REPORT_HEADER, getCreditCardDataUploadReportHelper());
            Integer count = 1;
            for(CreditCardStagingData creditCardData: creditCardList.getCreditCardData()){
                LOG.info("Validating credit card import. Record# " + count + " of " + creditCardList.getCreditCardData().size());

                creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_NO_ERROR);
                creditCardData.setImportBy(creditCardList.getImportBy());
                creditCardData.setStagingFileName(StringUtils.substringAfterLast(dataFileName, File.separator));

                List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
                if(validateAndSetCreditCardAgency(creditCardData)){

                    if(creditCardData.getExpenseImport() == ExpenseImport.traveler){
                        TmProfileAccount temProfileAccount  = findTraveler(creditCardData);

                        if(ObjectUtils.isNull(temProfileAccount)){
                            LOG.error("Invalid Traveler in Credit Card Data record.");
                            creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_INVALID_CARD);
                        }
                        else{
                            //Set Traveler Id for UCD
                            if(ObjectUtils.isNull(creditCardData.getTravelerId()) || creditCardData.getTravelerId() == 0){
                                Integer travelerId = new Integer(temProfileAccount.getProfile().getEmployeeId()).intValue();
                                creditCardData.setTravelerId(travelerId);
                            }

                            creditCardData.setTemProfileId(temProfileAccount.getProfileId());

                            //Set expense type code to O-Other if null
                            if(creditCardData.getExpenseTypeCode() == null){
                                creditCardData.setExpenseTypeCode(ExpenseTypes.OTHER);
                            }
                            // write an error if the expense type code is not valid
                            final ExpenseType expenseType = businessObjectService.findBySinglePrimaryKey(ExpenseType.class, creditCardData.getExpenseTypeCode());
                            if (expenseType == null) {
                                LOG.error("Invalid expense type code "+creditCardData.getExpenseTypeCode()+" in Credit Card Data record");
                                creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_INVALID_EXPENSE_TYPE_CODE);
                            }

                            //Set Credit Card Key(traveler Id + Credit Card Agency + Credit Card number
                            creditCardData.setCreditCardKey(creditCardData.getTravelerId() + temProfileAccount.getCreditCardAgency().getCreditCardOrAgencyCode()+ creditCardData.getCreditCardNumber());

                            // need to do the duplicate check at this point, since the CC key is one of the fields being checked
                            if (!isDuplicate(creditCardData, errorMessages)) {
                                creditCardData.setMoveToHistoryIndicator(true);
                                creditCardData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
                                validData.add(creditCardData);
                            }
                        }
                    }
                    else if(creditCardData.getExpenseImport() == ExpenseImport.trip){
                        if (!isDuplicate(creditCardData, errorMessages)) {
                            creditCardData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
                            validData.add(creditCardData);
                        }
                    }
                }else{
                    errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_CREDIT_CARD_DATA_INVALID_CCA));
                }

                //writer to error report
                if (!errorMessages.isEmpty()){
                    dataReportService.writeToReport(reportDataStream, creditCardData, errorMessages, getCreditCardDataUploadReportHelper());
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
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#isDuplicate(org.kuali.kfs.module.tem.businessobject.CreditCardStagingData, java.util.List)
     */
    @Override
    public boolean isDuplicate(CreditCardStagingData creditCardData, List<ErrorMessage> errorMessages){
        Map<String, Object> fieldValues = new HashMap<String, Object>();

        if(StringUtils.isNotEmpty(creditCardData.getCreditCardKey())){
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_KEY, creditCardData.getCreditCardKey());
        }
        if(StringUtils.isNotEmpty(creditCardData.getReferenceNumber())){
            fieldValues.put(TemPropertyConstants.REFERENCE_NUMBER, creditCardData.getReferenceNumber());
        }
        if(ObjectUtils.isNotNull(creditCardData.getTransactionAmount())){
            fieldValues.put(TemPropertyConstants.TRANSACTION_AMOUNT, creditCardData.getTransactionAmount());
        }
        if(ObjectUtils.isNotNull(creditCardData.getTransactionDate())){
            fieldValues.put(TemPropertyConstants.TRANSACTION_DATE, creditCardData.getTransactionDate());
        }
        if(ObjectUtils.isNotNull(creditCardData.getBankPostDate())){
            fieldValues.put(TemPropertyConstants.BANK_POSTED_DATE, creditCardData.getBankPostDate());
        }
        if(StringUtils.isNotEmpty(creditCardData.getMerchantName())){
            fieldValues.put(TemPropertyConstants.MERCHANT_NAME, creditCardData.getMerchantName());
        }
        List<CreditCardStagingData> creditCardDataList = (List<CreditCardStagingData>) businessObjectService.findMatching(CreditCardStagingData.class, fieldValues);

        if (ObjectUtils.isNull(creditCardDataList) || creditCardDataList.size() == 0) {
            return false;
        }
        LOG.error("Found a duplicate entry for credit card. Matching credit card id: " + creditCardDataList.get(0).getId());
        SimpleDateFormat format = new SimpleDateFormat();
        ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_CREDIT_CARD_DATA_DUPLICATE_RECORD,
                creditCardData.getCreditCardKey(), creditCardData.getReferenceNumber(), creditCardData.getTransactionAmount().toString(),
                format.format(creditCardData.getTransactionDate()), format.format(creditCardData.getBankPostDate()), creditCardData.getMerchantName());

        errorMessages.add(error);
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#findTraveler(org.kuali.kfs.module.tem.businessobject.CreditCardStagingData)
     */
    @Override
    public TmProfileAccount findTraveler(CreditCardStagingData creditCardData){
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemPropertyConstants.ACCOUNT_NUMBER, creditCardData.getCreditCardNumber());

        Collection<TmProfileAccount> temProfileAccounts = businessObjectService.findMatching(TmProfileAccount.class, criteria);

        if(ObjectUtils.isNotNull(temProfileAccounts) && temProfileAccounts.size() > 0) {
            return temProfileAccounts.iterator().next();
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#validateAndSetCreditCardAgency(org.kuali.kfs.module.tem.businessobject.CreditCardStagingData)
     */
    @Override
    public boolean validateAndSetCreditCardAgency(CreditCardStagingData creditCardData){
        CreditCardAgency ccAgency = creditCardAgencyService.getCreditCardAgencyByCode(creditCardData.getCreditCardOrAgencyCode());
        if (ObjectUtils.isNull(ccAgency)) {
            LOG.error("Mandatory Field Credit Card Or Agency Code is invalid: " + creditCardData.getCreditCardOrAgencyCode());
            creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_INVALID_CC_AGENCY);
            return false;
        }
        creditCardData.setCreditCardAgency(ccAgency);
        creditCardData.setCreditCardAgencyId(ccAgency.getId());
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#moveCreditCardDataToHistoricalExpenseTable()
     */
    @Override
    public boolean moveCreditCardDataToHistoricalExpenseTable() {

        List<CreditCardStagingData> creditCardData = travelExpenseService.retrieveValidCreditCardData();
        if (ObjectUtils.isNotNull(creditCardData) && creditCardData.size() > 0) {
            for(CreditCardStagingData creditCard: creditCardData){
                LOG.info("Creating historical travel expense for credit card: " + creditCard.getId());
                HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(creditCard);
                businessObjectService.save(expense);

                //Mark as moved to historical
                creditCard.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_MOVED_TO_HISTORICAL);
                LOG.info("Finished creating historical travel expense for credit card: " + creditCard.getId() + " Historical Travel Expense: " + expense.getId());
            }
            businessObjectService.save(creditCardData);
        }

        return true;
    }

    /**
     * Sets the batchInputFileService attribute value.
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Sets the creditCardDataImportFileTypes attribute value.
     * @param creditCardDataImportFileTypes The creditCardDataImportFileTypes to set.
     */
    public void setCreditCardDataImportFileTypes(List<BatchInputFileType> creditCardDataImportFileTypes) {
        this.creditCardDataImportFileTypes = creditCardDataImportFileTypes;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the creditCardDataFileErrorDirectory attribute value.
     * @param creditCardDataFileErrorDirectory The creditCardDataFileErrorDirectory to set.
     */
    public void setCreditCardDataFileErrorDirectory(String creditCardDataFileErrorDirectory) {
        this.creditCardDataFileErrorDirectory = creditCardDataFileErrorDirectory;
    }



    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    /**
     *
     * This method...
     * @param argTravelExpenseService
     */
    public void setTravelExpenseService(TravelExpenseService argTravelExpenseService){
        this.travelExpenseService = argTravelExpenseService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCreditCardAgencyService(CreditCardAgencyService creditCardAgencyService) {
        this.creditCardAgencyService = creditCardAgencyService;
    }

    public void setCreditCardDataUploadReportHelper(BusinessObjectReportHelper creditCardDataUploadReportHelper) {
        this.creditCardDataUploadReportHelper = creditCardDataUploadReportHelper;
    }

    public void setCreditCardDataReportDirectory(String creditCardDataReportDirectory) {
        this.creditCardDataReportDirectory = creditCardDataReportDirectory;
    }

    public void setCreditCardDataReportFilePrefix(String creditCardDataReportFilePrefix) {
        this.creditCardDataReportFilePrefix = creditCardDataReportFilePrefix;
    }

    public BusinessObjectReportHelper getCreditCardDataUploadReportHelper() {
        return creditCardDataUploadReportHelper;
    }

    public String getCreditCardDataReportDirectory() {
        return creditCardDataReportDirectory;
    }

    public String getCreditCardDataReportFilePrefix() {
        return creditCardDataReportFilePrefix;
    }

    public DataReportService getDataReportService() {
        return dataReportService;
    }

    public void setDataReportService(DataReportService dataReportService) {
        this.dataReportService = dataReportService;
    }
}
