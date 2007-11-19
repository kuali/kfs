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
package org.kuali.module.gl.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MailService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.XMLParseException;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.batch.collector.CollectorStep;
import org.kuali.module.gl.bo.CollectorDetail;
import org.kuali.module.gl.bo.CollectorHeader;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.CollectorDetailService;
import org.kuali.module.gl.service.CollectorHelperService;
import org.kuali.module.gl.service.CollectorScrubberService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.CollectorScrubberStatus;
import org.kuali.module.gl.util.OriginEntryTotals;

/**
 * The base implementation of CollectorHelperService
 * @see org.kuali.module.gl.service.CollectorService
 */
public class CollectorHelperServiceImpl implements CollectorHelperService {
    private static Logger LOG = Logger.getLogger(CollectorHelperServiceImpl.class);

    private static final String CURRENCY_SYMBOL = "$";

    private CollectorDetailService collectorDetailService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private MailService mailService;
    private DateTimeService dateTimeService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType collectorInputFileType;
    private CollectorScrubberService collectorScrubberService;

    /**
     * Parses the given file, validates the batch, stores the entries, and sends email.
     * @param fileName - name of file to load (including path)
     * @param group the group into which to persist the origin entries for the collector batch/file
     * @param collectorReportData the object used to store all of the collector status information for reporting
     * @param collectorScrubberStatuses if the collector scrubber is able to be invoked upon this collector batch, then the status
     *        info of the collector status run is added to the end of this list
     * @return boolean - true if load was successful, false if errors were encountered
     * @see org.kuali.module.gl.service.CollectorService#loadCollectorFile(java.lang.String)
     */
    public boolean loadCollectorFile(String fileName, OriginEntryGroup originEntryGroup, CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses) {
        boolean isValid = true;

        // the batch name is the file name
        // we can't use the global variables map to store errors because multiple collector batches/files run by the same thread
        // if we used the global variables map, all of the parse/validation errors from one file would be retained when
        // parsing/validating the
        // the next file, causing all subsequent files to fail validation. So, instead we do one unique error map per file
        ErrorMap errorMap = collectorReportData.getErrorMapForBatchName(fileName);

        CollectorBatch batch = doCollectorFileParse(fileName, errorMap);

        // terminate if there were parse errors
        if (!errorMap.isEmpty()) {
            isValid = false;
            collectorReportData.markUnparsableBatchNames(fileName);
        }

        if (isValid) {
            batch.setBatchName(fileName);
            collectorReportData.addBatch(batch);
            collectorReportData.setNumInputDetails(batch);
            // check totals
            isValid = checkTrailerTotals(batch, collectorReportData, errorMap);
        }

        // do validation, base collector files rules and total checks
        if (isValid) {
            isValid = performValidation(batch, errorMap);
        }

        if (isValid) {
            CollectorScrubberStatus collectorScrubberStatus = collectorScrubberService.scrub(batch, collectorReportData);
            collectorScrubberStatuses.add(collectorScrubberStatus);
            processInterDepartmentalBillingAmounts(batch);

            // store origin group, entries, and id billings
            batch.setDefaultsAndStore(originEntryGroup, collectorReportData);
            collectorReportData.incrementNumPersistedBatches();

            // mark batch as valid
            collectorReportData.markValidationStatus(batch, true);
        }
        else {
            collectorReportData.incrementNumNonPersistedBatches();

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
     * Calls batch input service to parse the xml contents into an object. Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param fileName the name of the file to parse
     * @param errorMap a map of errors resultant from the parsing
     * @return the CollectorBatch of details parsed from the file
     */
    private CollectorBatch doCollectorFileParse(String fileName, ErrorMap errorMap) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e) {
            LOG.error("file to parse not found " + fileName, e);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e.getMessage(), e);
        }

        CollectorBatch parsedObject = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(inputStream);
            parsedObject = (CollectorBatch) batchInputFileService.parse(collectorInputFileType, fileByteContent);
        }
        catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (XMLParseException e1) {
            LOG.error("errors parsing xml " + e1.getMessage(), e1);
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e1.getMessage() });
        }

        return parsedObject;
    }


    /**
     * Validates the contents of a parsed file.
     * 
     * @param batch - batch to validate
     * @return boolean - true if validation was OK, false if there were errors
     * @see org.kuali.module.gl.service.CollectorHelperService#performValidation(org.kuali.module.gl.batch.collector.CollectorBatch)
     */
    public boolean performValidation(CollectorBatch batch) {
        return performValidation(batch, GlobalVariables.getErrorMap());
    }

    /**
     * Performs the following checks on the collector batch: Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param batch - batch to validate
     * @param errorMap the map into which to put errors encountered during validation
     * @return boolean - true if validation was successful, false it not
     */
    protected boolean performValidation(CollectorBatch batch, ErrorMap errorMap) {
        boolean valid = true;

        boolean performDuplicateHeaderCheck = parameterService.getIndicatorParameter(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_PERFORM_DUPLICATE_HEADER_CHECK);
        if (performDuplicateHeaderCheck) {
            valid = duplicateHeaderCheck(batch, errorMap);
        }
        if (valid) {
            valid = checkForMixedDocumentTypes(batch, errorMap);
        }

        if (valid) {
            valid = checkForMixedBalanceTypes(batch, errorMap);
        }

        if (valid) {
            valid = checkDetailKeys(batch, errorMap);
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

            BalanceTyp balanceTyp = new BalanceTyp();
            balanceTyp.setFinancialBalanceTypeCode(balanceTypeCode);
            balanceTyp = (BalanceTyp) SpringContext.getBean(BusinessObjectService.class).retrieve(balanceTyp);
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
    protected void negateAmountIfNecessary(CollectorDetail collectorDetail, BalanceTyp balanceTyp, ObjectType objectType, CollectorBatch batch) {
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
    private boolean duplicateHeaderCheck(CollectorBatch batch, ErrorMap errorMap) {
        boolean validHeader = true;

        CollectorHeader foundHeader = batch.retrieveDuplicateHeader();

        if (foundHeader != null) {
            LOG.error("batch header was matched to a previously loaded batch");
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.DUPLICATE_BATCH_HEADER);

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
    private boolean checkForMixedDocumentTypes(CollectorBatch batch, ErrorMap errorMap) {
        boolean docTypesNotMixed = true;

        Set batchDocumentTypes = new HashSet();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            batchDocumentTypes.add(entry.getFinancialDocumentTypeCode());
        }

        if (batchDocumentTypes.size() > 1) {
            LOG.error("mixed document types found in batch");
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES);

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
    private boolean checkForMixedBalanceTypes(CollectorBatch batch, ErrorMap errorMap) {
        boolean balanceTypesNotMixed = true;

        Set balanceTypes = new HashSet();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            balanceTypes.add(entry.getFinancialBalanceTypeCode());
        }

        if (balanceTypes.size() > 1) {
            LOG.error("mixed balance types found in batch");
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_BALANCE_TYPES);

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
    private boolean checkDetailKeys(CollectorBatch batch, ErrorMap errorMap) {
        boolean detailKeysFound = true;

        // build a Set of keys from the gl entries to compare with
        Set glEntryKeys = new HashSet();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            glEntryKeys.add(generateOriginEntryMatchingKey(entry, ", "));
        }

        for (CollectorDetail collectorDetail : batch.getCollectorDetails()) {
            String collectorDetailKey = generateCollectorDetailMatchingKey(collectorDetail, ", ");
            if (!glEntryKeys.contains(collectorDetailKey)) {
                LOG.error("found detail key without a matching gl entry key " + collectorDetailKey);
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY, collectorDetailKey);

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
    private String generateOriginEntryMatchingKey(OriginEntryFull entry, String delimiter) {
        return StringUtils.join(new String[] { entry.getUniversityFiscalYear().toString(), entry.getUniversityFiscalPeriodCode(), entry.getChartOfAccountsCode(), entry.getAccountNumber(), entry.getSubAccountNumber(), entry.getFinancialObjectCode(), entry.getFinancialSubObjectCode(), entry.getFinancialBalanceTypeCode(), entry.getFinancialObjectTypeCode(), entry.getDocumentNumber(), entry.getFinancialDocumentTypeCode(), entry.getFinancialSystemOriginationCode() }, delimiter);
    }

    /**
     * Generates a String representation of the CollectorDetail's primary key
     * 
     * @param collectorDetail collector detail to get key from
     * @param delimiter the String delimiter to separate parts of the key
     * @return the key as a String
     */
    private String generateCollectorDetailMatchingKey(CollectorDetail collectorDetail, String delimiter) {
        return StringUtils.join(new String[] { collectorDetail.getUniversityFiscalYear().toString(), collectorDetail.getUniversityFiscalPeriodCode(), collectorDetail.getChartOfAccountsCode(), collectorDetail.getAccountNumber(), collectorDetail.getSubAccountNumber(), collectorDetail.getFinancialObjectCode(), collectorDetail.getFinancialSubObjectCode(), collectorDetail.getFinancialBalanceTypeCode(), collectorDetail.getFinancialObjectTypeCode(), collectorDetail.getDocumentNumber(), collectorDetail.getFinancialDocumentTypeCode(), collectorDetail.getFinancialSystemOriginationCode() }, delimiter);
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param batch batch to check totals for
     * @param collectorReportData collector report data (optional)
     * @see org.kuali.module.gl.service.CollectorHelperService#checkTrailerTotals(org.kuali.module.gl.batch.collector.CollectorBatch,
     *      org.kuali.module.gl.util.CollectorReportData)
     */
    public boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData) {
        return checkTrailerTotals(batch, collectorReportData, GlobalVariables.getErrorMap());
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param batch - batch to check totals for
     * @return boolean - true if validation was successful, false it not
     */
    protected boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData, ErrorMap errorMap) {
        boolean trailerTotalsMatch = true;

        int actualRecordCount = batch.getOriginEntries().size() + batch.getCollectorDetails().size();
        if (actualRecordCount != batch.getTotalRecords()) {
            LOG.error("trailer check on total count did not pass, expected count: " + String.valueOf(batch.getTotalRecords()) + ", actual count: " + String.valueOf(actualRecordCount));
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_COUNTNOMATCH, String.valueOf(batch.getTotalRecords()), String.valueOf(actualRecordCount));

            trailerTotalsMatch = false;
        }

        OriginEntryTotals totals = new OriginEntryTotals();
        totals.addToTotals(batch.getOriginEntries().iterator());

        if (collectorReportData != null) {
            collectorReportData.setOriginEntryTotals(batch, totals);
        }

        if (batch.getOriginEntries().size() == 0) {
            if (!KualiDecimal.ZERO.equals(batch.getTotalAmount())) {
                LOG.error("trailer total should be zero when there are no origin entries");
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNT_SHOULD_BE_ZERO);
            }
            return false;
        }

        // retrieve document types that balance by equal debits and credits
        String[] documentTypes = parameterService.getParameterValues(CollectorStep.class, KFSConstants.SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES).toArray(new String[] {});

        boolean equalDebitCreditTotal = false;
        for (int i = 0; i < documentTypes.length; i++) {
            String documentType = StringUtils.remove(documentTypes[i], "*");
            if (batch.getOriginEntries().get(0).getFinancialDocumentTypeCode().startsWith(documentType.toUpperCase()) && KFSConstants.BALANCE_TYPE_ACTUAL.equals(batch.getOriginEntries().get(0).getFinancialBalanceTypeCode())) {
                equalDebitCreditTotal = true;
            }
        }

        if (equalDebitCreditTotal) {
            // credits must equal debits must equal total trailer amount
            if (!totals.getCreditAmount().equals(totals.getDebitAmount()) || !totals.getCreditAmount().equals(batch.getTotalAmount())) {
                LOG.error("trailer check on total amount did not pass, debit should equal credit, should equal trailer total");
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH1, totals.getCreditAmount().toString(), totals.getDebitAmount().toString(), batch.getTotalAmount().toString());
                trailerTotalsMatch = false;
            }
        }
        else {
            // credits plus debits plus other amount must equal trailer
            KualiDecimal totalGlEntries = totals.getCreditAmount().add(totals.getDebitAmount()).add(totals.getOtherAmount());
            if (!totalGlEntries.equals(batch.getTotalAmount())) {
                LOG.error("trailer check on total amount did not pass, sum of gl entry amounts should equal trailer total");
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH2, totalGlEntries.toString(), batch.getTotalAmount().toString());
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
        return configurationService.getPropertyString(KFSConstants.GL_COLLECTOR_STAGING_DIRECTORY);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCollectorInputFileType(BatchInputFileType collectorInputFileType) {
        this.collectorInputFileType = collectorInputFileType;
    }

    /**
     * Sets the collectorScrubberService attribute value.
     * 
     * @param collectorScrubberService The collectorScrubberService to set.
     */
    public void setCollectorScrubberService(CollectorScrubberService collectorScrubberService) {
        this.collectorScrubberService = collectorScrubberService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
