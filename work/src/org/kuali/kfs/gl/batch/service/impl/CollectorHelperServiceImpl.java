/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.batch.service.CollectorScrubberService;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.CollectorHeader;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.report.PreScrubberReportData;
import org.kuali.kfs.gl.service.CollectorDetailService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The base implementation of CollectorHelperService
 * @see org.kuali.kfs.gl.batch.service.CollectorService
 */
public class CollectorHelperServiceImpl implements CollectorHelperService {
    private static Logger LOG = Logger.getLogger(CollectorHelperServiceImpl.class);

    private static final String CURRENCY_SYMBOL = "$";

    private CollectorDetailService collectorDetailService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private ParameterService parameterService;
    private ConfigurationService configurationService;
    private DateTimeService dateTimeService;
    private BatchInputFileService batchInputFileService;
    private CollectorScrubberService collectorScrubberService;
    private AccountService accountService;
    private PreScrubberService preScrubberService;
    private String batchFileDirectoryName;
    
    /**
     * Parses the given file, validates the batch, stores the entries, and sends email.
     * @param fileName - name of file to load (including path)
     * @param group the group into which to persist the origin entries for the collector batch/file
     * @param collectorReportData the object used to store all of the collector status information for reporting
     * @param collectorScrubberStatuses if the collector scrubber is able to be invoked upon this collector batch, then the status
     *        info of the collector status run is added to the end of this list
     * @param the output stream to which to store origin entries that properly pass validation
     * @return boolean - true if load was successful, false if errors were encountered
     * @see org.kuali.kfs.gl.batch.service.CollectorService#loadCollectorFile(java.lang.String)
     */
    public boolean loadCollectorFile(String fileName, CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses, BatchInputFileType collectorInputFileType, PrintStream originEntryOutputPs) {
        boolean isValid = true;

        MessageMap fileMessageMap = collectorReportData.getMessageMapForFileName(fileName);
        
        List<CollectorBatch> batches = doCollectorFileParse(fileName, fileMessageMap, collectorInputFileType, collectorReportData);
        for (int i = 0; i < batches.size(); i++) {
            CollectorBatch collectorBatch = batches.get(i);

            collectorBatch.setBatchName(fileName + " Batch " + String.valueOf(i + 1));
            collectorReportData.addBatch(collectorBatch);
            
            isValid &= loadCollectorBatch(collectorBatch, fileName, i + 1, collectorReportData, collectorScrubberStatuses, collectorInputFileType, originEntryOutputPs);
        }
        return isValid;
    }
        
    protected boolean loadCollectorBatch(CollectorBatch batch, String fileName, int batchIndex, CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses, BatchInputFileType collectorInputFileType, PrintStream originEntryOutputPs) {
        boolean isValid = true;
        
        MessageMap messageMap = batch.getMessageMap();
        // terminate if there were parse errors
        if (messageMap.hasErrors()) {
            isValid = false;
        }

        if (isValid) {
            collectorReportData.setNumInputDetails(batch);
            // check totals
            isValid = checkTrailerTotals(batch, collectorReportData, messageMap);
        }

        // do validation, base collector files rules and total checks
        if (isValid) {
            isValid = performValidation(batch, messageMap);
        }

        if (isValid) {
            // mark batch as valid
            collectorReportData.markValidationStatus(batch, true);
            
            prescrubParsedCollectorBatch(batch, collectorReportData);
            
            String collectorFileDirectoryName = collectorInputFileType.getDirectoryPath();
            // create a input file for scrubber
            String collectorInputFileNameForScrubber = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            PrintStream inputFilePs = null;
            try {
                inputFilePs = new PrintStream(collectorInputFileNameForScrubber);
            
                for (OriginEntryFull entry : batch.getOriginEntries()){
                    inputFilePs.printf("%s\n", entry.getLine());    
                }
            } catch (IOException e) {
                throw new RuntimeException("loadCollectorFile Stopped: " + e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(inputFilePs);
            }
            
            CollectorScrubberStatus collectorScrubberStatus = collectorScrubberService.scrub(batch, collectorReportData, collectorFileDirectoryName);
            collectorScrubberStatuses.add(collectorScrubberStatus);
            processInterDepartmentalBillingAmounts(batch);

            // store origin group, entries, and collector detairs
            String collectorDemergerOutputFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;  
            batch.setDefaultsAndStore(collectorReportData, collectorDemergerOutputFileName, originEntryOutputPs);
            collectorReportData.incrementNumPersistedBatches();
        }
        else {
            collectorReportData.incrementNumNonPersistedBatches();
            collectorReportData.incrementNumNotPersistedOriginEntryRecords(batch.getOriginEntries().size());
            collectorReportData.incrementNumNotPersistedCollectorDetailRecords(batch.getCollectorDetails().size());
            // mark batch as invalid
            collectorReportData.markValidationStatus(batch, false);
        }

        return isValid;
    }

    /**
     * After a parse error, tries to go through the file to see if the email address can be determined. This method will not throw
     * an exception.
     * 
     * It's not doing much right now, just returning null
     * 
     * @param fileName the name of the file that a parsing error occurred on
     * @return the email from the file
     */
    protected String attemptToParseEmailAfterParseError(String fileName) {
        return null;
    }

    /**
     * Calls batch input service to parse the xml contents into an object. Any errors will be contained in GlobalVariables.MessageMap
     * 
     * @param fileName the name of the file to parse
     * @param MessageMap a map of errors resultant from the parsing
     * @return the CollectorBatch of details parsed from the file
     */
    protected List<CollectorBatch> doCollectorFileParse(String fileName, MessageMap messageMap, BatchInputFileType collectorInputFileType, CollectorReportData collectorReportData) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e) {
            LOG.error("file to parse not found " + fileName, e);
            collectorReportData.markUnparsableFileNames(fileName);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e.getMessage(), e);
        }
        catch (RuntimeException e) {
            collectorReportData.markUnparsableFileNames(fileName);
            throw e;
        }

        List<CollectorBatch> parsedObject = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(inputStream);
            parsedObject = (List<CollectorBatch>) batchInputFileService.parse(collectorInputFileType, fileByteContent);
        }
        catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            collectorReportData.markUnparsableFileNames(fileName);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (ParseException e1) {
            LOG.error("errors parsing file " + e1.getMessage(), e1);
            collectorReportData.markUnparsableFileNames(fileName);
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e1.getMessage() });
        }
        catch (RuntimeException e) {
            collectorReportData.markUnparsableFileNames(fileName);
            throw e;
        }

        return parsedObject;
    }

    protected void prescrubParsedCollectorBatch(CollectorBatch collectorBatch, CollectorReportData collectorReportData) {
        if (preScrubberService.deriveChartOfAccountsCodeIfSpaces()) {
            PreScrubberReportData preScrubberReportData = collectorReportData.getPreScrubberReportData();
            
            int inputRecords = collectorBatch.getOriginEntries().size();
            Set<String> noChartCodesCache = new HashSet<String>();
            Set<String> multipleChartCodesCache = new HashSet<String>();
            Map<String, String> accountNumberToChartCodeCache = new HashMap<String, String>();
            
            Iterator<?> originEntryAndDetailIterator = IteratorUtils.chainedIterator(collectorBatch.getOriginEntries().iterator(), collectorBatch.getCollectorDetails().iterator());
            while (originEntryAndDetailIterator.hasNext()) {
                Object originEntryOrDetail = originEntryAndDetailIterator.next();
                if (StringUtils.isBlank(extractChartOfAccountsCode(originEntryOrDetail))) {
                    String accountNumber = extractAccountNumber(originEntryOrDetail);
                    
                    boolean nonExistent = false;
                    boolean multipleFound = false;
                    String chartOfAccountsCode = null;
                    
                    if (noChartCodesCache.contains(accountNumber)) {
                        nonExistent = true;
                    }
                    else if (multipleChartCodesCache.contains(accountNumber)) {
                        multipleFound = true;
                    }
                    else if (accountNumberToChartCodeCache.containsKey(accountNumber)) {
                        chartOfAccountsCode = accountNumberToChartCodeCache.get(accountNumber);
                    }
                    else {
                        Collection<Account> accounts = accountService.getAccountsForAccountNumber(accountNumber);
                        if (accounts.size() == 1) {
                            chartOfAccountsCode = accounts.iterator().next().getChartOfAccountsCode();
                            accountNumberToChartCodeCache.put(accountNumber, chartOfAccountsCode);
                        }
                        else if (accounts.size() == 0) {
                            noChartCodesCache.add(accountNumber);
                            nonExistent = true;
                        }
                        else {
                            multipleChartCodesCache.add(accountNumber);
                            multipleFound = true;
                        }
                    }
                    
                    if (!nonExistent && !multipleFound) {
                        setChartOfAccountsCode(originEntryOrDetail, chartOfAccountsCode);
                    }
                }
            }
            
            preScrubberReportData.getAccountsWithMultipleCharts().addAll(multipleChartCodesCache);
            preScrubberReportData.getAccountsWithNoCharts().addAll(noChartCodesCache);
            preScrubberReportData.setInputRecords(preScrubberReportData.getInputRecords() + inputRecords);
            preScrubberReportData.setOutputRecords(preScrubberReportData.getOutputRecords() + inputRecords);
        }
    }

    protected String extractChartOfAccountsCode(Object originEntryOrDetail) {
        if (originEntryOrDetail instanceof OriginEntryInformation)
            return ((OriginEntryInformation) originEntryOrDetail).getChartOfAccountsCode(); 
        return ((CollectorDetail) originEntryOrDetail).getChartOfAccountsCode();
    }
    
    protected String extractAccountNumber(Object originEntryOrDetail) {
        if (originEntryOrDetail instanceof OriginEntryInformation)
            return ((OriginEntryInformation) originEntryOrDetail).getAccountNumber(); 
        return ((CollectorDetail) originEntryOrDetail).getAccountNumber();
    }
    
    protected void setChartOfAccountsCode(Object originEntryOrDetail, String chartOfAccountsCode) {
        if (originEntryOrDetail instanceof OriginEntryInformation)
            ((OriginEntryInformation) originEntryOrDetail).setChartOfAccountsCode(chartOfAccountsCode);
        else
            ((CollectorDetail) originEntryOrDetail).setChartOfAccountsCode(chartOfAccountsCode);
    }
    
    /**
     * Validates the contents of a parsed file.
     * 
     * @param batch - batch to validate
     * @return boolean - true if validation was OK, false if there were errors
     * @see org.kuali.kfs.gl.batch.service.CollectorHelperService#performValidation(org.kuali.kfs.gl.batch.CollectorBatch)
     */
    public boolean performValidation(CollectorBatch batch) {
        return performValidation(batch, GlobalVariables.getMessageMap());
    }

    /**
     * Performs the following checks on the collector batch: Any errors will be contained in GlobalVariables.MessageMap
     * 
     * @param batch - batch to validate
     * @param MessageMap the map into which to put errors encountered during validation
     * @return boolean - true if validation was successful, false it not
     */
    protected boolean performValidation(CollectorBatch batch, MessageMap messageMap) {
        boolean valid = performCollectorHeaderValidation(batch, messageMap);
        
        performUppercasing(batch);

        boolean performDuplicateHeaderCheck = parameterService.getParameterValueAsBoolean(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_PERFORM_DUPLICATE_HEADER_CHECK);
        if (valid && performDuplicateHeaderCheck) {
            valid = duplicateHeaderCheck(batch, messageMap);
        }
        if (valid) {
            valid = checkForMixedDocumentTypes(batch, messageMap);
        }

        if (valid) {
            valid = checkForMixedBalanceTypes(batch, messageMap);
        }

        if (valid) {
            valid = checkDetailKeys(batch, messageMap);
        }

        return valid;
    }
    
    /**
     * Uppercases sub-account, sub-object, and project fields
     * 
     * @param batch CollectorBatch with data to uppercase
     */
    protected void performUppercasing(CollectorBatch batch) {
        for (OriginEntryFull originEntry : batch.getOriginEntries()) {
            if (StringUtils.isNotBlank(originEntry.getSubAccountNumber())) {
                originEntry.setSubAccountNumber(originEntry.getSubAccountNumber().toUpperCase());
            }

            if (StringUtils.isNotBlank(originEntry.getFinancialSubObjectCode())) {
                originEntry.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode().toUpperCase());
            }

            if (StringUtils.isNotBlank(originEntry.getProjectCode())) {
                originEntry.setProjectCode(originEntry.getProjectCode().toUpperCase());
            }
        }

        for (CollectorDetail detail : batch.getCollectorDetails()) {
            if (StringUtils.isNotBlank(detail.getSubAccountNumber())) {
                detail.setSubAccountNumber(detail.getSubAccountNumber().toUpperCase());
            }

            if (StringUtils.isNotBlank(detail.getFinancialSubObjectCode())) {
                detail.setFinancialSubObjectCode(detail.getFinancialSubObjectCode().toUpperCase());
            }
        }
    }

    protected boolean performCollectorHeaderValidation(CollectorBatch batch, MessageMap messageMap) {
        if (batch.isHeaderlessBatch()) {
            // if it's a headerless batch, don't validate the header, but it's still an error
            return false;
        }
        boolean valid = true;
        if (StringUtils.isBlank(batch.getChartOfAccountsCode())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_CHART_CODE_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getOrganizationCode())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_ORGANIZATION_CODE_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getCampusCode())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_CAMPUS_CODE_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getPhoneNumber())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_PHONE_NUMBER_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getMailingAddress())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_MAILING_ADDRESS_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getDepartmentName())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_DEPARTMENT_NAME_REQUIRED);
        }
        return valid;
    }

    /**
     * Modifies the amounts in the ID Billing Detail rows, depending on specific business rules. For this default implementation,
     * see the {@link #negateAmountIfNecessary(InterDepartmentalBilling, BalanceTyp, ObjectType, CollectorBatch)} method to see how
     * the billing detail amounts are modified.
     * 
     * @param batch a CollectorBatch to process
     */
    protected void processInterDepartmentalBillingAmounts(CollectorBatch batch) {
        for (CollectorDetail collectorDetail : batch.getCollectorDetails()) {
            String balanceTypeCode = getBalanceTypeCode(collectorDetail, batch);

            BalanceType balanceTyp = new BalanceType();
            balanceTyp.setFinancialBalanceTypeCode(balanceTypeCode);
            balanceTyp = (BalanceType) SpringContext.getBean(BusinessObjectService.class).retrieve(balanceTyp);
            if (balanceTyp == null) {
                // no balance type in db
                LOG.info("No balance type code found for ID billing record. " + collectorDetail);
                continue;
            }

            collectorDetail.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            if (collectorDetail.getFinancialObject() == null) {
                // no object code in db
                LOG.info("No object code found for ID billing record. " + collectorDetail);
                continue;
            }
            ObjectType objectType = collectorDetail.getFinancialObject().getFinancialObjectType();

            /** Commented out for KULRNE-5922 */
            // negateAmountIfNecessary(collectorDetail, balanceTyp, objectType, batch);
        }
    }

    /**
     * Negates the amount of the internal departmental billing detail record if necessary. For this default implementation, if the
     * balance type's offset indicator is yes and the object type has a debit indicator, then the amount is negated.
     * 
     * @param collectorDetail the collector detail
     * @param balanceTyp the balance type
     * @param objectType the object type
     * @param batch the patch to which the interDepartmentalBilling parameter belongs
     */
    protected void negateAmountIfNecessary(CollectorDetail collectorDetail, BalanceType balanceTyp, ObjectType objectType, CollectorBatch batch) {
        if (balanceTyp != null && objectType != null) {
            if (balanceTyp.isFinancialOffsetGenerationIndicator()) {
                if (KFSConstants.GL_DEBIT_CODE.equals(objectType.getFinObjectTypeDebitcreditCd())) {
                    KualiDecimal amount = collectorDetail.getCollectorDetailItemAmount();
                    amount = amount.negated();
                    collectorDetail.setCollectorDetailItemAmount(amount);
                }
            }
        }
    }

    /**
     * Returns the balance type code for the interDepartmentalBilling record. This default implementation will look into the system
     * parameters to determine the balance type
     * 
     * @param interDepartmentalBilling a inter departmental billing detail record
     * @param batch the batch to which the interDepartmentalBilling billing belongs
     * @return the balance type code for the billing detail
     */
    protected String getBalanceTypeCode(CollectorDetail collectorDetail, CollectorBatch batch) {
        return collectorDetail.getFinancialBalanceTypeCode();
    }

    /**
     * Checks header against previously loaded batch headers for a duplicate submission.
     * 
     * @param batch - batch to check
     * @return true if header if OK, false if header was used previously
     */
    protected boolean duplicateHeaderCheck(CollectorBatch batch, MessageMap messageMap) {
        boolean validHeader = true;

        CollectorHeader foundHeader = batch.retrieveDuplicateHeader();

        if (foundHeader != null) {
            LOG.error("batch header was matched to a previously loaded batch");
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.DUPLICATE_BATCH_HEADER);

            validHeader = false;
        }

        return validHeader;
    }

    /**
     * Iterates through the origin entries and builds a map on the document types. Then checks there was only one document type
     * found.
     * 
     * @param batch - batch to check document types
     * @return true if there is only one document type, false if multiple document types were found.
     */
    protected boolean checkForMixedDocumentTypes(CollectorBatch batch, MessageMap messageMap) {
        boolean docTypesNotMixed = true;

        Set<String> batchDocumentTypes = new HashSet<String>();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            batchDocumentTypes.add(entry.getFinancialDocumentTypeCode());
        }

        if (batchDocumentTypes.size() > 1) {
            LOG.error("mixed document types found in batch");
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES);

            docTypesNotMixed = false;
        }

        return docTypesNotMixed;
    }

    /**
     * Iterates through the origin entries and builds a map on the balance types. Then checks there was only one balance type found.
     * 
     * @param batch - batch to check balance types
     * @return true if there is only one balance type, false if multiple balance types were found
     */
    protected boolean checkForMixedBalanceTypes(CollectorBatch batch, MessageMap messageMap) {
        boolean balanceTypesNotMixed = true;

        Set<String> balanceTypes = new HashSet<String>();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            balanceTypes.add(entry.getFinancialBalanceTypeCode());
        }

        if (balanceTypes.size() > 1) {
            LOG.error("mixed balance types found in batch");
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_BALANCE_TYPES);

            balanceTypesNotMixed = false;
        }

        return balanceTypesNotMixed;
    }

    /**
     * Verifies each detail (id billing) record key has an corresponding gl entry in the same batch. The key is built by joining the
     * values of chart of accounts code, account number, sub account number, object code, and sub object code.
     * 
     * @param batch - batch to validate
     * @return true if all detail records had matching keys, false otherwise
     */
    protected boolean checkDetailKeys(CollectorBatch batch, MessageMap messageMap) {
        boolean detailKeysFound = true;

        // build a Set of keys from the gl entries to compare with
        Set<String> glEntryKeys = new HashSet<String>();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            glEntryKeys.add(generateOriginEntryMatchingKey(entry, ", "));
        }

        for (CollectorDetail collectorDetail : batch.getCollectorDetails()) {
            String collectorDetailKey = generateCollectorDetailMatchingKey(collectorDetail, ", ");
            if (!glEntryKeys.contains(collectorDetailKey)) {
                LOG.error("found detail key without a matching gl entry key " + collectorDetailKey);
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY, collectorDetailKey);

                detailKeysFound = false;
            }
        }

        return detailKeysFound;
    }

    /**
     * Generates a String representation of the OriginEntryFull's primary key
     * 
     * @param entry origin entry to get key from
     * @param delimiter the String delimiter to separate parts of the key
     * @return the key as a String
     */
    protected String generateOriginEntryMatchingKey(OriginEntryFull entry, String delimiter) {
        return StringUtils.join(new String[] { ObjectUtils.isNull(entry.getUniversityFiscalYear()) ? "" : entry.getUniversityFiscalYear().toString(), entry.getUniversityFiscalPeriodCode(), entry.getChartOfAccountsCode(), entry.getAccountNumber(), entry.getSubAccountNumber(), entry.getFinancialObjectCode(), entry.getFinancialSubObjectCode(), entry.getFinancialObjectTypeCode(), entry.getDocumentNumber(), entry.getFinancialDocumentTypeCode(), entry.getFinancialSystemOriginationCode() }, delimiter);
    }

    /**
     * Generates a String representation of the CollectorDetail's primary key
     * 
     * @param collectorDetail collector detail to get key from
     * @param delimiter the String delimiter to separate parts of the key
     * @return the key as a String
     */
    protected String generateCollectorDetailMatchingKey(CollectorDetail collectorDetail, String delimiter) {
        return StringUtils.join(new String[] { ObjectUtils.isNull(collectorDetail.getUniversityFiscalYear()) ? "" : collectorDetail.getUniversityFiscalYear().toString(), collectorDetail.getUniversityFiscalPeriodCode(), collectorDetail.getChartOfAccountsCode(), collectorDetail.getAccountNumber(), collectorDetail.getSubAccountNumber(), collectorDetail.getFinancialObjectCode(), collectorDetail.getFinancialSubObjectCode(), collectorDetail.getFinancialObjectTypeCode(), collectorDetail.getDocumentNumber(), collectorDetail.getFinancialDocumentTypeCode(), collectorDetail.getFinancialSystemOriginationCode() }, delimiter);
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.MessageMap
     * 
     * @param batch batch to check totals for
     * @param collectorReportData collector report data (optional)
     * @see org.kuali.kfs.gl.batch.service.CollectorHelperService#checkTrailerTotals(org.kuali.kfs.gl.batch.CollectorBatch,
     *      org.kuali.kfs.gl.report.CollectorReportData)
     */
    public boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData) {
        return checkTrailerTotals(batch, collectorReportData, GlobalVariables.getMessageMap());
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.MessageMap
     * 
     * @param batch - batch to check totals for
     * @return boolean - true if validation was successful, false it not
     */
    protected boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData, MessageMap messageMap) {
        boolean trailerTotalsMatch = true;

        int actualRecordCount = batch.getOriginEntries().size() + batch.getCollectorDetails().size();
        if (actualRecordCount != batch.getTotalRecords()) {
            LOG.error("trailer check on total count did not pass, expected count: " + String.valueOf(batch.getTotalRecords()) + ", actual count: " + String.valueOf(actualRecordCount));
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_COUNTNOMATCH, String.valueOf(batch.getTotalRecords()), String.valueOf(actualRecordCount));
            trailerTotalsMatch = false;
        }

        OriginEntryTotals totals = batch.getOriginEntryTotals();
        
        if (batch.getOriginEntries().size() == 0) {
            if (!KualiDecimal.ZERO.equals(batch.getTotalAmount())) {
                LOG.error("trailer total should be zero when there are no origin entries");
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNT_SHOULD_BE_ZERO);
            }
            return false;
        }

        // retrieve document types that balance by equal debits and credits
        Collection<String> documentTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(CollectorStep.class, KFSConstants.SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES) );

        boolean equalDebitCreditTotal = false;
        for ( String documentType : documentTypes ) {
            documentType = StringUtils.remove(documentType, "*").toUpperCase();
            if (batch.getOriginEntries().get(0).getFinancialDocumentTypeCode().startsWith(documentType) 
                    && KFSConstants.BALANCE_TYPE_ACTUAL.equals(batch.getOriginEntries().get(0).getFinancialBalanceTypeCode())) {
                equalDebitCreditTotal = true;
            }
        }

        if (equalDebitCreditTotal) {
            // credits must equal debits must equal total trailer amount
            if (!totals.getCreditAmount().equals(totals.getDebitAmount()) || !totals.getCreditAmount().equals(batch.getTotalAmount())) {
                LOG.error("trailer check on total amount did not pass, debit should equal credit, should equal trailer total");
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH1, totals.getCreditAmount().toString(), totals.getDebitAmount().toString(), batch.getTotalAmount().toString());
                trailerTotalsMatch = false;
            }
        }
        else {
            // credits plus debits plus other amount must equal trailer
            KualiDecimal totalGlEntries = totals.getCreditAmount().add(totals.getDebitAmount()).add(totals.getOtherAmount());
            if (!totalGlEntries.equals(batch.getTotalAmount())) {
                LOG.error("trailer check on total amount did not pass, sum of gl entry amounts should equal trailer total");
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH2, totalGlEntries.toString(), batch.getTotalAmount().toString());
                trailerTotalsMatch = false;
            }
        }

        return trailerTotalsMatch;
    }

    public void setCollectorDetailService(CollectorDetailService collectorDetailService) {
        this.collectorDetailService = collectorDetailService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    /**
     * Returns the name of the directory where Collector files are saved
     * 
     * @return the name of the staging directory
     */
    public String getStagingDirectory() {
        return configurationService.getPropertyValueAsString(KFSConstants.GL_COLLECTOR_STAGING_DIRECTORY);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Sets the collectorScrubberService attribute value.
     * 
     * @param collectorScrubberService The collectorScrubberService to set.
     */
    public void setCollectorScrubberService(CollectorScrubberService collectorScrubberService) {
        this.collectorScrubberService = collectorScrubberService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the preScrubberService attribute value.
     * @param preScrubberService The preScrubberService to set.
     */
    public void setPreScrubberService(PreScrubberService preScrubberService) {
        this.preScrubberService = preScrubberService;
    }
}
