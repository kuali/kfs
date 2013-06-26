/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateImportDao;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.PayrateImportService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.exception.BudgetConstructionLockUnavailableException;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.ExternalizedMessageWrapper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PayrateImportServiceImpl implements PayrateImportService {

    protected BusinessObjectService businessObjectService;
    protected LockService lockService;
    protected int importCount;
    protected int updateCount;
    protected OptionsService optionsService;
    protected PayrateImportDao payrateImportDao;
    protected BudgetDocumentService budgetDocumentService;
    protected SalarySettingService salarySettingService;

    /**
     *
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#importFile(java.io.InputStream)
     */
    @Transactional
    public boolean importFile(InputStream fileImportStream, List<ExternalizedMessageWrapper> messageList, String principalId) {
        Map payRateHoldingPersonUniversalIdentifierKey = new HashMap();
        payRateHoldingPersonUniversalIdentifierKey.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalId);

        this.businessObjectService.deleteMatching(BudgetConstructionPayRateHolding.class, payRateHoldingPersonUniversalIdentifierKey);

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        this.importCount = 0;

        try {
            while(fileReader.ready()) {
                BudgetConstructionPayRateHolding budgetConstructionPayRateHolding = new BudgetConstructionPayRateHolding();
                String line = fileReader.readLine();
                ObjectUtil.convertLineToBusinessObject(budgetConstructionPayRateHolding, line, DefaultImportFileFormat.fieldLengths, Arrays.asList(DefaultImportFileFormat.fieldNames));
                budgetConstructionPayRateHolding.setPrincipalId(principalId);
                budgetConstructionPayRateHolding.setAppointmentRequestedPayRate(budgetConstructionPayRateHolding.getAppointmentRequestedPayRate().movePointLeft(2));
                businessObjectService.save(budgetConstructionPayRateHolding);
                this.importCount++;
            }
        }
        catch (Exception e) {
            this.businessObjectService.deleteMatching(BudgetConstructionPayRateHolding.class, payRateHoldingPersonUniversalIdentifierKey);
            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_IMPORT_ABORTED));

            return false;
        }

        if (importCount == 0 ) {
            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_NO_IMPORT_RECORDS));
        }
        else {
            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_COUNT, String.valueOf(importCount)));
        }

        return true;
    }

    /**
     *
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#update()
     */
    @Transactional
    public void update(Integer budgetYear, Person user, List<ExternalizedMessageWrapper> messageList, String principalId) {
        List<PendingBudgetConstructionAppointmentFunding> lockedFundingRecords = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        boolean updateContainsErrors = false;
        this.updateCount = 0;

        Map payRateHoldingPersonUniversalIdentifierKey = new HashMap();
        payRateHoldingPersonUniversalIdentifierKey.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalId);
        List<BudgetConstructionPayRateHolding> records = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findMatching(BudgetConstructionPayRateHolding.class, payRateHoldingPersonUniversalIdentifierKey);

        if ( !getPayrateLock(lockedFundingRecords, messageList, budgetYear, user, records) ) {
            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_UPDATE_ABORTED, String.valueOf(this.updateCount)));
            doRollback();
            return;
        }

        Collection<String> biweeklyPayObjectCodes = BudgetParameterFinder.getBiweeklyPayObjectCodes();
        for (BudgetConstructionPayRateHolding holdingRecord : records) {
            if (holdingRecord.getAppointmentRequestedPayRate().equals( -1.0)) {
                messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_IMPORT_NO_PAYROLL_MATCH, holdingRecord.getEmplid(), holdingRecord.getPositionNumber()));
                updateContainsErrors = true;
                continue;
            }

            List<PendingBudgetConstructionAppointmentFunding> fundingRecords = this.payrateImportDao.getFundingRecords(holdingRecord, budgetYear, biweeklyPayObjectCodes);
            if (fundingRecords.isEmpty()) {
                messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_NO_ACTIVE_FUNDING_RECORDS, holdingRecord.getEmplid(), holdingRecord.getPositionNumber()));
                updateContainsErrors = true;
                continue;
            }

            for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {

                if ( !fundingRecord.getAppointmentRequestedPayRate().equals(holdingRecord.getAppointmentRequestedPayRate()) ) {
                    if (fundingRecord.getAppointmentRequestedFteQuantity().equals(0) || fundingRecord.getAppointmentRequestedFteQuantity() == null) {
                        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_NO_UPDATE_FTE_ZERO_OR_BLANK, holdingRecord.getEmplid(), holdingRecord.getPositionNumber(), fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber()));
                        updateContainsErrors = true;
                        continue;
                    }

                    BigDecimal fteQty = salarySettingService.calculateFteQuantity(fundingRecord.getBudgetConstructionPosition().getIuPayMonths(), fundingRecord.getAppointmentFundingMonth(), fundingRecord.getAppointmentRequestedTimePercent());
                    BigDecimal annualWorkingHours = BigDecimal.valueOf(BudgetParameterFinder.getAnnualWorkingHours());
                    BigDecimal totalPayHoursForYear = fteQty.multiply(annualWorkingHours);
                    KualiInteger annualAmount = new KualiInteger(holdingRecord.getAppointmentRequestedPayRate().multiply(totalPayHoursForYear));
                    KualiInteger updateAmount = annualAmount.subtract(fundingRecord.getAppointmentRequestedAmount());

                    BudgetConstructionHeader header = budgetDocumentService.getBudgetConstructionHeader(fundingRecord);
                    if (header == null ) {
                        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_NO_BUDGET_DOCUMENT, budgetYear.toString(), fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber()));
                        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_OBJECT_LEVEL_ERROR, fundingRecord.getEmplid(), fundingRecord.getPositionNumber(), fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber()));
                        updateContainsErrors = true;
                        continue;
                    }

                    // update or create pending budget GL record and  plug line
                    budgetDocumentService.updatePendingBudgetGeneralLedger(fundingRecord, updateAmount);
                    if (updateAmount.isNonZero()) {
                        budgetDocumentService.updatePendingBudgetGeneralLedgerPlug(fundingRecord, updateAmount.negated());
                    }

                    fundingRecord.setAppointmentRequestedPayRate(holdingRecord.getAppointmentRequestedPayRate());
                    fundingRecord.setAppointmentRequestedAmount(annualAmount);
                    this.businessObjectService.save(fundingRecord);
                }
            }
            this.updateCount ++;
        }

        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.MSG_PAYRATE_IMPORT_UPDATE_COMPLETE, String.valueOf(updateCount)));

        for (PendingBudgetConstructionAppointmentFunding recordToUnlock : lockedFundingRecords) {
            this.lockService.unlockAccount(budgetDocumentService.getBudgetConstructionHeader(recordToUnlock));
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#generatePdf(java.lang.StringBuilder, java.io.ByteArrayOutputStream)
     */
    @NonTransactional
    public void generatePdf(List<ExternalizedMessageWrapper> logMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        for (ExternalizedMessageWrapper messageWrapper : logMessages) {
            String message;
            if (messageWrapper.getParams().length == 0 ) {
                message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(messageWrapper.getMessageKey());
            }
            else {
                String temp = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(messageWrapper.getMessageKey());
                message = MessageFormat.format(temp, (Object[])messageWrapper.getParams());
            }
            document.add(new Paragraph(message));
        }

        document.close();
    }

    /**
     * Sets the business object service
     *
     * @param businessObjectService
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * sets lock service
     *
     * @param lockService
     */
    @NonTransactional
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    /**
     * sets option service
     *
     * @param optionsService
     */
    @NonTransactional
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * sets payrate import dao
     *
     * @param payrateImportDao
     */
    @NonTransactional
    public void setPayrateImportDao(PayrateImportDao payrateImportDao) {
        this.payrateImportDao = payrateImportDao;
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    @NonTransactional
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * Sets the salarySettingService attribute.
     *
     * @param salarySettingService The salarySettingService to set.
     */
    @NonTransactional
    public void setSalarySettingService(SalarySettingService salarySettingService) {
        this.salarySettingService = salarySettingService;
    }

    /**
     * Creates the locking key to use in retrieving account locks
     *
     * @param record
     * @return
     */
    protected String getLockingKeyString(PendingBudgetConstructionAppointmentFunding record) {
        return record.getUniversityFiscalYear() + "-" + record.getChartOfAccountsCode() + "-" + record.getAccountNumber() + "-" + record.getSubAccountNumber();
    }

    /**
     * Retrieves Account locks for payrate import records
     *
     * @param lockMap
     * @param messageList
     * @param budgetYear
     * @param user
     * @param records
     * @return
     */
    @Transactional
    protected boolean getPayrateLock(List<PendingBudgetConstructionAppointmentFunding> lockedRecords, List<ExternalizedMessageWrapper> messageList, Integer budgetYear, Person user, List<BudgetConstructionPayRateHolding> records) {
        Collection<String> biweeklyPayObjectCodes = BudgetParameterFinder.getBiweeklyPayObjectCodes();

        for (BudgetConstructionPayRateHolding record: records) {
            List<PendingBudgetConstructionAppointmentFunding> fundingRecords = this.payrateImportDao.getFundingRecords(record, budgetYear, biweeklyPayObjectCodes);
            try {
                lockedRecords.addAll(this.lockService.lockPendingBudgetConstructionAppointmentFundingRecords(fundingRecords, user));
            } catch(BudgetConstructionLockUnavailableException e) {
                BudgetConstructionLockStatus lockStatus = e.getLockStatus();
                if ( lockStatus.getLockStatus().equals(BCConstants.LockStatus.BY_OTHER) ) {
                    messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_ACCOUNT_LOCK_EXISTS));

                    return false;
                } else if ( lockStatus.getLockStatus().equals(BCConstants.LockStatus.FLOCK_FOUND) ) {
                    messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_FUNDING_LOCK_EXISTS));

                    return false;
                } else if ( !lockStatus.getLockStatus().equals(BCConstants.LockStatus.SUCCESS) ) {
                    messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_BATCH_ACCOUNT_LOCK_FAILED));
                    return false;
                }
          }

        }

        return true;
    }

    /**
     * File format for payrate import file
     *
     */
    protected static class DefaultImportFileFormat {
        private static final int[] fieldLengths = new int[] {11, 8, 50, 5, 4, 3, 3, 10, 8};
        private static final String[] fieldNames = new String[] {KFSPropertyConstants.EMPLID, KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.PERSON_NAME, BCPropertyConstants.SET_SALARY_ID, BCPropertyConstants.SALARY_ADMINISTRATION_PLAN, BCPropertyConstants.GRADE, "unionCode", BCPropertyConstants.APPOINTMENT_REQUESTED_PAY_RATE, BCPropertyConstants.CSF_FREEZE_DATE};
    }

    /**
     * If retrieving budget locks fails, this method rolls back previous changes
     *
     */
    protected void doRollback() {
        PlatformTransactionManager transactionManager = SpringContext.getBean(PlatformTransactionManager.class);
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(defaultTransactionDefinition);
        transactionManager.rollback( transactionStatus );

    }

}

