/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCParameterKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.PayrateImportLine;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateImportDao;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.service.PayrateImportService;
import org.kuali.kfs.module.bc.util.ExternalizedMessageWrapper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PayrateImportServiceImpl implements PayrateImportService {
    
    private BusinessObjectService businessObjectService;
    private LockService lockService;
    private int importCount;
    private int updateCount;
    private BudgetParameterService budgetParameterService;
    private OptionsService optionsService;
    private PayrateImportDao payrateImportDao;
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#importFile(java.io.InputStream)
     */
    @Transactional
    public List<ExternalizedMessageWrapper> importFile(InputStream fileImportStream) {
        clearPayrateHoldingTable();
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        List<ExternalizedMessageWrapper> messageList = new ArrayList<ExternalizedMessageWrapper>();
        this.importCount = 0;
        
        try {
            while(fileReader.ready()) {
                PayrateImportLine temp = new PayrateImportLine();
                String line = fileReader.readLine();
                ObjectUtil.convertLineToBusinessObject(temp, line, DefaultImportFileFormat.fieldLengths, Arrays.asList(DefaultImportFileFormat.fieldNames));
                BudgetConstructionPayRateHolding budgetConstructionPayRateHolding = createHoldingRecord(temp);
                businessObjectService.save(budgetConstructionPayRateHolding);
                this.importCount++;
            }
        }
        catch (Exception e) {
            clearPayrateHoldingTable();
            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_IMPORT_ABORTED));
            
            return messageList;
        }

        return messageList;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#update()
     */
    @Transactional
    public List<ExternalizedMessageWrapper> update(Integer budgetYear, UniversalUser user) {
        List<ExternalizedMessageWrapper> messageList = new ArrayList<ExternalizedMessageWrapper>();
        Map<String, PendingBudgetConstructionAppointmentFunding> lockMap = new HashMap<String, PendingBudgetConstructionAppointmentFunding>();
        boolean updateContainsErrors = false;
        this.updateCount = 0;
        
        List<BudgetConstructionPayRateHolding> records = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class);
        
        if ( !getPayrateLock(lockMap, messageList, budgetYear, user, records) ) {
            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_UPDATE_ABORTED, String.valueOf(this.updateCount)));
            doRollback();
            return messageList;
        }
        
        for (BudgetConstructionPayRateHolding holdingRecord : records) {
            if (holdingRecord.getAppointmentRequestedPayRate().equals( -1.0)) {
                messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_IMPORT_NO_PAYROLL_MATCH, holdingRecord.getEmplid(), holdingRecord.getPositionNumber()));
                updateContainsErrors = true;
                continue;
            } 
            
            List<PendingBudgetConstructionAppointmentFunding> fundingRecords = this.payrateImportDao.getFundingRecords(holdingRecord, budgetYear, budgetParameterService.getParameterValues(BudgetConstructionPayRateHolding.class, BCParameterKeyConstants.BIWEEKLY_PAY_OBJECT_CODES));
            if (fundingRecords.isEmpty()) {
                messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_NO_ACTIVE_FUNDING_RECORDS, holdingRecord.getEmplid(), holdingRecord.getPositionNumber()));
                updateContainsErrors = true;
                continue;
            }
            
            for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
                
                if ( !fundingRecord.getAppointmentRequestedPayRate().equals(holdingRecord.getAppointmentRequestedPayRate()) ) {
                    if (fundingRecord.getAppointmentRequestedPayRate().equals(0) || fundingRecord.getAppointmentRequestedPayRate() == null) {
                        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_NO_UPDATE_FTE_ZERO_OR_BLANK, holdingRecord.getEmplid(), holdingRecord.getPositionNumber(), fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber()));
                        updateContainsErrors = true;
                        continue;
                    } 
                    
                    BigDecimal temp1 = fundingRecord.getAppointmentRequestedTimePercent().divide(new BigDecimal(100));
                    BigDecimal temp2 = new BigDecimal((fundingRecord.getAppointmentFundingMonth()/fundingRecord.getBudgetConstructionPosition().getIuPayMonths()) * 2080);
                    
                    KualiInteger annualAmount = new KualiInteger(holdingRecord.getAppointmentRequestedPayRate().multiply(temp1.multiply(temp2)));
                    KualiInteger updateAmount = annualAmount.subtract(fundingRecord.getAppointmentRequestedAmount());
                    
                    BudgetConstructionHeader header = getHeaderRecord(fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber(), budgetYear);
                    if (header == null ) {
                        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_NO_BUDGET_DOCUMENT, budgetYear.toString(), fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber()));
                        messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_OBJECT_LEVEL_ERROR, fundingRecord.getEmplid(), fundingRecord.getPositionNumber(), fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber()));
                        updateContainsErrors = true;
                        continue;
                    }
                    
                    PendingBudgetConstructionGeneralLedger pendingRecord = findPendingBudgetConstructionGeneralLedger(header, fundingRecord, false);
                    if (pendingRecord != null) pendingRecord.setAccountLineAnnualBalanceAmount(pendingRecord.getAccountLineAnnualBalanceAmount().add(updateAmount));
                    else {
                        pendingRecord = new PendingBudgetConstructionGeneralLedger();
                        pendingRecord.setDocumentNumber(header.getDocumentNumber());
                        pendingRecord.setUniversityFiscalYear(fundingRecord.getUniversityFiscalYear());
                        pendingRecord.setChartOfAccountsCode(fundingRecord.getChartOfAccountsCode());
                        pendingRecord.setAccountNumber(fundingRecord.getAccountNumber());
                        pendingRecord.setSubAccountNumber(fundingRecord.getSubAccountNumber());
                        pendingRecord.setFinancialObjectCode(fundingRecord.getFinancialObjectCode());
                        pendingRecord.setFinancialSubObjectCode(fundingRecord.getFinancialSubObjectCode());
                        pendingRecord.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
                        pendingRecord.setFinancialObjectTypeCode(optionsService.getOptions(budgetYear).getFinObjTypeExpenditureexpCd());
                        pendingRecord.setFinancialBeginningBalanceLineAmount(new KualiInteger(0));
                        pendingRecord.setAccountLineAnnualBalanceAmount(updateAmount);
                    }
                    
                    this.businessObjectService.save(pendingRecord);
                    
                    if ( !fundingRecord.getAccount().isForContractsAndGrants() && !fundingRecord.getAccount().getSubFundGroupCode().equals(this.budgetParameterService.getParameterValues(BudgetConstructionPayRateHolding.class, BCParameterKeyConstants.GENERATE_2PLG_SUB_FUND_GROUPS).get(0)) ) {
                        PendingBudgetConstructionGeneralLedger plg = findPendingBudgetConstructionGeneralLedger(header, fundingRecord, true);
                        
                        if (plg != null) plg.setAccountLineAnnualBalanceAmount(plg.getAccountLineAnnualBalanceAmount().subtract(updateAmount));
                        else {
                            plg = new PendingBudgetConstructionGeneralLedger();
                            plg.setDocumentNumber(header.getDocumentNumber());
                            plg.setUniversityFiscalYear(fundingRecord.getUniversityFiscalYear());
                            plg.setChartOfAccountsCode(fundingRecord.getChartOfAccountsCode());
                            plg.setAccountNumber(fundingRecord.getAccountNumber());
                            plg.setSubAccountNumber(fundingRecord.getSubAccountNumber());
                            plg.setFinancialObjectCode(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG);
                            plg.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                            plg.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
                            plg.setFinancialObjectTypeCode(optionsService.getOptions(budgetYear).getFinObjTypeExpenditureexpCd());
                            plg.setFinancialBeginningBalanceLineAmount(new KualiInteger(0));
                            plg.setAccountLineAnnualBalanceAmount(updateAmount.negated());
                        }
                        
                        this.businessObjectService.save(plg);
                    }
                    
                    fundingRecord.setAppointmentRequestedPayRate(holdingRecord.getAppointmentRequestedPayRate());
                    fundingRecord.setAppointmentRequestedAmount(annualAmount);
                    this.businessObjectService.save(fundingRecord);
                }
            }
            this.updateCount ++;
        }
        
        Set<String> locks = lockMap.keySet();
        for (String lockingKey : locks) {
            PendingBudgetConstructionAppointmentFunding recordToUnlock = lockMap.get(lockingKey);
            this.lockService.unlockAccount(getHeaderRecord(recordToUnlock.getChartOfAccountsCode(), recordToUnlock.getAccountNumber(), recordToUnlock.getSubAccountNumber(), budgetYear));
            this.lockService.unlockFunding(recordToUnlock.getChartOfAccountsCode(), recordToUnlock.getAccountNumber(), recordToUnlock.getSubAccountNumber(), budgetYear, user.getPersonUniversalIdentifier());
        }
        
        return messageList;
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
            if (messageWrapper.getParams().length == 0 ) message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(messageWrapper.getMessageKey());
            else {
                String temp = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(messageWrapper.getMessageKey());
                message = MessageFormat.format(temp, messageWrapper.getParams());
            }
            document.add(new Paragraph(message));
        }

        document.close();
    }
    
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    @NonTransactional
    public int getImportCount() {
        return importCount;
    }

    @NonTransactional
    public int getUpdateCount() {
        return updateCount;
    }
    
    @NonTransactional
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }
    
    @NonTransactional
    public BudgetParameterService getBudgetParameterService() {
        return budgetParameterService;
    }
    
    @NonTransactional
    public void setBudgetParameterService(BudgetParameterService budgetParameterService) {
        this.budgetParameterService = budgetParameterService;
    }
    
    @NonTransactional
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
    
    @NonTransactional
    public void setPayrateImportDao(PayrateImportDao payrateImportDao) {
        this.payrateImportDao = payrateImportDao;
    }
    
    private BudgetConstructionPayRateHolding createHoldingRecord(PayrateImportLine record) {
        BudgetConstructionPayRateHolding budgetConstructionPayRateHolding = new BudgetConstructionPayRateHolding();
        
        budgetConstructionPayRateHolding.setEmplid(record.getEmplid());
        budgetConstructionPayRateHolding.setPositionNumber(record.getPositionNumber());
        budgetConstructionPayRateHolding.setPersonName(record.getPersonName());
        budgetConstructionPayRateHolding.setSetidSalary(record.getSetidSalary());
        budgetConstructionPayRateHolding.setSalaryAdministrationPlan(record.getSalaryAdministrationPlan());
        budgetConstructionPayRateHolding.setGrade(record.getGrade());
        budgetConstructionPayRateHolding.setUnionCode(record.getPositionUnionCode());
        budgetConstructionPayRateHolding.setAppointmentRequestedPayRate(record.getAppointmentRequestPayRate().divide(new BigDecimal(100)));
        
        return budgetConstructionPayRateHolding;
    }
    
    private String getLockingKeyString(PendingBudgetConstructionAppointmentFunding record) {
        return record.getUniversityFiscalYear() + "-" + record.getChartOfAccountsCode() + "-" + record.getAccountNumber() + "-" + record.getSubAccountNumber();
    }
    
    private BudgetConstructionHeader getHeaderRecord(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer budgetYear) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        
        BudgetConstructionHeader header = (BudgetConstructionHeader)this.businessObjectService.findByPrimaryKey(BudgetConstructionHeader.class, searchCriteria);
        
        return header;
    }
    
    private void clearPayrateHoldingTable() {
        List<BudgetConstructionPayRateHolding> records = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class);
        for (BudgetConstructionPayRateHolding record : records) {
            this.businessObjectService.delete(record);
        }
    }
    
    private boolean getPayrateLock(Map<String, PendingBudgetConstructionAppointmentFunding> lockMap, List<ExternalizedMessageWrapper> messageList, Integer budgetYear, UniversalUser user, List<BudgetConstructionPayRateHolding> records) {
        Map<String,PendingBudgetConstructionAppointmentFunding> noLockMap = new HashMap<String,PendingBudgetConstructionAppointmentFunding>();
        
        for (BudgetConstructionPayRateHolding record: records) {
                List<PendingBudgetConstructionAppointmentFunding> fundingRecords = this.payrateImportDao.getFundingRecords(record, budgetYear, budgetParameterService.getParameterValues(BudgetConstructionPayRateHolding.class, BCParameterKeyConstants.BIWEEKLY_PAY_OBJECT_CODES));
                for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
                    BudgetConstructionHeader header = getHeaderRecord(fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber(), budgetYear);
                    String lockingKey = getLockingKeyString(fundingRecord);
                    if ( !lockMap.containsKey(lockingKey) && !noLockMap.containsKey(lockingKey)) {
                        BudgetConstructionLockStatus lockStatus = this.lockService.lockAccount(header, user.getPersonUniversalIdentifier());
                        if ( lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.BY_OTHER) ) {
                            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_ACCOUNT_LOCK_EXISTS));
                            return false;
                        } else if ( lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.FLOCK_FOUND) ) {
                            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_FUNDING_LOCK_EXISTS));
                            return false;
                        } else if ( !lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.SUCCESS) ) {
                            messageList.add(new ExternalizedMessageWrapper(BCKeyConstants.ERROR_PAYRATE_BATCH_ACCOUNT_LOCK_FAILED));
                            return false;
                        } else {
                            lockMap.put(lockingKey, fundingRecord);
                        }
                    }
                }
            
        }
        
        return true;
    }
    
    private PendingBudgetConstructionGeneralLedger findPendingBudgetConstructionGeneralLedger(BudgetConstructionHeader header, PendingBudgetConstructionAppointmentFunding fundingRecord, boolean is2PLG) {
        Map searchCriteria = new HashMap();
        
        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, header.getDocumentNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fundingRecord.getUniversityFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, fundingRecord.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, fundingRecord.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, fundingRecord.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, is2PLG ? KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG : budgetParameterService.getParameterValues(BudgetConstructionPayRateHolding.class, "BIWEEKLY_PAY_OBJECT_CODES"));
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, is2PLG ? KFSConstants.getDashFinancialSubObjectCode() : fundingRecord.getFinancialSubObjectCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, optionsService.getOptions(fundingRecord.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd());
        
        return (PendingBudgetConstructionGeneralLedger)this.businessObjectService.findByPrimaryKey(PendingBudgetConstructionGeneralLedger.class, searchCriteria);
    }
    
    private static class DefaultImportFileFormat {
        private static final int[] fieldLengths = new int[] {11, 8, 50, 5, 4, 3, 3, 10, 8};
        //TODO: use constants for field names
        private static final String[] fieldNames = new String[] {"emplid", "positionNumber", "personName", "setidSalary", "salaryAdministrationPlan", "grade", "positionUnionCode", "appointmentRequestPayRate", "csfFreezeDate"};
    }

    
    /**
     * If retrieving budget locks fails, this method rolls back previous changes
     * 
     */
    private void doRollback() {
        PlatformTransactionManager transactionManager = SpringContext.getBean(PlatformTransactionManager.class);
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(defaultTransactionDefinition);
        transactionManager.rollback( transactionStatus );

    }
    
}
