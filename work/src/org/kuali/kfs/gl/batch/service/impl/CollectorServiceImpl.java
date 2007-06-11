/*
 * Copyright 2006 The Kuali Foundation.
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MailService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.ParameterGroups;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.exceptions.XMLParseException;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.bo.CollectorHeader;
import org.kuali.module.gl.bo.InterDepartmentalBilling;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.service.CollectorService;
import org.kuali.module.gl.service.InterDepartmentalBillingService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.springframework.beans.factory.BeanFactory;

/**
 * @see org.kuali.module.gl.service.CollectorService
 */
public class CollectorServiceImpl implements CollectorService {
    private static Logger LOG = Logger.getLogger(CollectorServiceImpl.class);

    private InterDepartmentalBillingService interDepartmentalBillingService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private KualiConfigurationService kualiConfigurationService;
    private MailService mailService;
    private DateTimeService dateTimeService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType collectorInputFileType;
    private BeanFactory beanFactory;

    /**
     * Parses the given file, validates the batch, stores the entries, and sends email.
     * 
     * @see org.kuali.module.gl.service.CollectorService#loadCollectorFile(java.lang.String)
     */
    public boolean loadCollectorFile(String fileName) {
        boolean isValid = true;

        CollectorBatch batch = doCollectorFileParse(fileName);

        // terminate if there were parse errors
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            isValid = false;
        }

        // do validation, base collector files rules and total checks
        if (isValid) {
            isValid = performValidation(batch);
        }

        if (isValid) {
            // check totals
            isValid = checkTrailerTotals(batch);
        }

        if (isValid) {
            // store origin group, entries, and id billings
            batch.setDefaultsAndStore();
        }

        List<String> errorMessages = translateErrorsFromGlobalVariables();
        sendEmail(errorMessages, batch);

        return isValid;
    }

    /**
     * Calls batch input service to parse the xml contents into an object. Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param fileName
     */
    private CollectorBatch doCollectorFileParse(String fileName) {

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
            GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e1.getMessage() });
        }

        return parsedObject;
    }

    /**
     * Performs the following checks on the collector batch: Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param batch - batch to validate
     * @return boolean - true if validation was successful, false it not
     */
    public boolean performValidation(CollectorBatch batch) {
        boolean valid = true;

        boolean performDuplicateHeaderCheck = kualiConfigurationService.getApplicationParameterIndicator(ParameterGroups.COLLECTOR_SECURITY_GROUP_NAME, SystemGroupParameterNames.COLLECTOR_PERFORM_DUPLICATE_HEADER_CHECK);
        if (performDuplicateHeaderCheck) {
            valid = duplicateHeaderCheck(batch);
        }

        if (valid) {
            valid = checkForMixedDocumentTypes(batch);
        }

        if (valid) {
            valid = checkForMixedBalanceTypes(batch);
        }

        if (valid) {
            valid = checkDetailKeys(batch);
        }

        return valid;
    }

    /**
     * Checks header against previously loaded batch headers for a duplicate submission.
     * 
     * @param batch - batch to check
     * @return true if header if OK, false if header was used previously
     */
    private boolean duplicateHeaderCheck(CollectorBatch batch) {
        boolean validHeader = true;

        CollectorHeader batchHeader = batch.createCollectorHeader();
        CollectorHeader foundHeader = (CollectorHeader) SpringServiceLocator.getBusinessObjectService().retrieve(batchHeader);

        if (foundHeader != null) {
            LOG.error("batch header was matched to a previously loaded batch");
            GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.DUPLICATE_BATCH_HEADER);

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
    private boolean checkForMixedDocumentTypes(CollectorBatch batch) {
        boolean docTypesNotMixed = true;

        Set batchDocumentTypes = new HashSet();
        for (OriginEntry entry : batch.getOriginEntries()) {
            batchDocumentTypes.add(entry.getFinancialDocumentTypeCode());
        }

        if (batchDocumentTypes.size() > 1) {
            LOG.error("mixed document types found in batch");
            GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES);

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
    private boolean checkForMixedBalanceTypes(CollectorBatch batch) {
        boolean balanceTypesNotMixed = true;

        Set balanceTypes = new HashSet();
        for (OriginEntry entry : batch.getOriginEntries()) {
            balanceTypes.add(entry.getFinancialBalanceTypeCode());
        }

        if (balanceTypes.size() > 1) {
            LOG.error("mixed balance types found in batch");
            GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_BALANCE_TYPES);

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
    private boolean checkDetailKeys(CollectorBatch batch) {
        boolean detailKeysFound = true;

        // build a Set of keys from the gl entries to compare with
        Set glEntryKeys = new HashSet();
        for (OriginEntry entry : batch.getOriginEntries()) {
            glEntryKeys.add(StringUtils.join(new String[] { entry.getChartOfAccountsCode(), entry.getAccountNumber(), entry.getSubAccountNumber(), entry.getFinancialObjectCode(), entry.getFinancialSubObjectCode() }, ","));
        }

        for (InterDepartmentalBilling idBilling : batch.getIdBillings()) {
            String idBillingKey = StringUtils.join(new String[] { idBilling.getChartOfAccountsCode(), idBilling.getAccountNumber(), idBilling.getSubAccountNumber(), idBilling.getFinancialObjectCode(), idBilling.getFinancialSubObjectCode() }, ",");
            if (!glEntryKeys.contains(idBillingKey)) {
                LOG.error("found detail key without a matching gl entry key " + idBillingKey);
                GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY, idBillingKey);

                detailKeysFound = false;
                break;
            }
        }

        return detailKeysFound;
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.errorMap
     * 
     * @param batch - batch to check totals for
     * @return boolean - true if validation was successful, false it not
     */
    public boolean checkTrailerTotals(CollectorBatch batch) {
        boolean trailerTotalsMatch = true;

        int actualRecordCount = batch.getOriginEntries().size() + batch.getIdBillings().size();
        if (actualRecordCount != batch.getTotalRecords()) {
            LOG.error("trailer check on total count did not pass, expected count: " + String.valueOf(batch.getTotalRecords()) + ", actual count: " + String.valueOf(actualRecordCount));
            GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_COUNTNOMATCH, String.valueOf(batch.getTotalRecords()), String.valueOf(actualRecordCount));

            trailerTotalsMatch = false;
        }

        KualiDecimal creditAmount = new KualiDecimal(0);
        KualiDecimal debitAmount = new KualiDecimal(0);
        KualiDecimal otherAmount = new KualiDecimal(0);
        for (OriginEntry entry : batch.getOriginEntries()) {
            if (KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
                creditAmount = creditAmount.add(entry.getTransactionLedgerEntryAmount());
            }
            else if (KFSConstants.GL_DEBIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
                debitAmount = debitAmount.add(entry.getTransactionLedgerEntryAmount());
            }
            else {
                otherAmount = otherAmount.add(entry.getTransactionLedgerEntryAmount());
            }
        }

        // retrieve document types that balance by equal debits and credits
        String[] documentTypes = kualiConfigurationService.getApplicationParameterValues(ParameterGroups.COLLECTOR_SECURITY_GROUP_NAME, SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES);

        boolean equalDebitCreditTotal = false;
        for (int i = 0; i < documentTypes.length; i++) {
            String documentType = StringUtils.remove(documentTypes[i], "*");
            if (batch.getOriginEntries().get(0).getFinancialDocumentTypeCode().startsWith(documentType.toUpperCase()) && KFSConstants.BALANCE_TYPE_ACTUAL.equals(batch.getOriginEntries().get(0).getFinancialBalanceTypeCode())) {
                equalDebitCreditTotal = true;
            }
        }

        if (equalDebitCreditTotal) {
            // credits must equal debits must equal total trailer amount
            if (!creditAmount.equals(debitAmount) || !creditAmount.equals(batch.getTotalAmount())) {
                LOG.error("trailer check on total amount did not pass, debit should equal credit, should equal trailer total");
                GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH1, creditAmount.toString(), debitAmount.toString(), batch.getTotalAmount().toString());
            }
        }
        else {
            // credits plus debits plus other amount must equal trailer
            KualiDecimal totalGlEntries = creditAmount.add(debitAmount).add(otherAmount);
            if (!totalGlEntries.equals(batch.getTotalAmount())) {
                LOG.error("trailer check on total amount did not pass, sum of gl entry amounts should equal trailer total");
                GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH2, totalGlEntries.toString(), batch.getTotalAmount().toString());
            }
        }

        return trailerTotalsMatch;
    }

    /**
     * Sends email with results of the batch processing.
     * 
     * @param eerrorMessages - list of error messages that were encountered during processing
     */
    private void sendEmail(List<String> errorMessages, CollectorBatch batch) {
        LOG.debug("sendNoteEmails() starting");
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        
        String subject = kualiConfigurationService.getApplicationParameterValue(ParameterGroups.COLLECTOR_SECURITY_GROUP_NAME, SystemGroupParameterNames.COLLECTOR_EMAIL_SUBJECT_PARAMETER_NAME);
        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }
        message.setSubject(subject);

        String body = createMessageBody(errorMessages, batch);
        message.setMessage(body);
        message.addToAddress(batch.getWorkgroupName());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    private String createMessageBody(List<String> errorMessages, CollectorBatch batch) {
        StringBuffer body = new StringBuffer();

        body.append("Header Information:\n\n");
        if (!GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML)) {
            body.append("Chart: " + batch.getChartOfAccountsCode() + "\n");
            body.append("Org: " + batch.getOrganizationCode() + "\n");
            body.append("Contact: " + batch.getPersonUserID() + "\n");
            body.append("Email: " + batch.getWorkgroupName() + "\n");
            body.append("File Date: " + batch.getTransmissionDate() + "\n\n");
            body.append("Summary Totals:\n");

            // SUMMARY TOTALS HERE
            body.append("    Total Records: " + String.valueOf(batch.getTotalRecords().intValue()) + "\n");
            body.append("    Total Amount: " + batch.getTotalAmount() + "\n\n");
        }
        body.append("Reported Errors:\n");

        // ERRORS GO HERE
        if (errorMessages.isEmpty()) {
            body.append("----- NO ERRORS TO REPORT -----\nFiles have been added to the system.");
        }
        else {
            for (String currentMessage : errorMessages) {
                body.append(currentMessage + "\n");
            }
            body.append("\n----- THE RECORDS WERE NOT SAVED TO THE DATABASE -----");
        }
        return body.toString();
    }

    /**
     * Builds actual error message from error key and parameters.
     * 
     * @return List<String> of error message text
     */
    private List<String> translateErrorsFromGlobalVariables() {
        List<String> collectorErrors = new ArrayList();

        for (Iterator iter = GlobalVariables.getErrorMap().getPropertiesWithErrors().iterator(); iter.hasNext();) {
            String errorKey = (String) iter.next();

            for (Iterator iter2 = GlobalVariables.getErrorMap().getMessages(errorKey).iterator(); iter2.hasNext();) {
                ErrorMessage errorMessage = (ErrorMessage) iter2.next();
                String messageText = kualiConfigurationService.getPropertyString(errorMessage.getErrorKey());
                collectorErrors.add(MessageFormat.format(messageText, (Object[]) errorMessage.getMessageParameters()));
            }
        }

        return collectorErrors;
    }

    public void setInterDepartmentalBillingService(InterDepartmentalBillingService interDepartmentalBillingService) {
        this.interDepartmentalBillingService = interDepartmentalBillingService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public String getStagingDirectory() {
        return kualiConfigurationService.getPropertyString(KFSConstants.GL_COLLECTOR_STAGING_DIRECTORY);
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

}
