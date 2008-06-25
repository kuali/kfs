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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.PayrateImportLine;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.service.PayrateImportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.service.NonTransactional;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PayrateImportServiceImpl implements PayrateImportService {
    
    private BusinessObjectService businessObjectService;
    private LockService lockService;
    private int importCount;
    private int updateCount;
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#importFile(java.io.InputStream)
     */
    @Transactional
    public StringBuilder importFile(InputStream fileImportStream) {
        this.businessObjectService.delete(new ArrayList<PersistableBusinessObject>(this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class)));
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        StringBuilder messages = new StringBuilder();
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
            messages.append("Import Aborted \n");
            
            return messages;
        }
        if (this.importCount == 0 ) messages.append("No records found to import. \n");
        else messages.append("Import count: " + this.importCount + "\n");
        
        messages.append("Import complete \n");
        
        return messages;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#update()
     */
    @Transactional
    public StringBuilder update(Integer budgetYear, UniversalUser user) {
        StringBuilder messages = new StringBuilder();
        Map<String, PendingBudgetConstructionAppointmentFunding> lockMap = new HashMap<String, PendingBudgetConstructionAppointmentFunding>();
        boolean updateContainsErrors = false;
        this.updateCount = 0;
        
        List<BudgetConstructionPayRateHolding> records = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class);
        
        if ( !getPayRateLock(lockMap, messages, budgetYear, user, records) ) return messages;
        
        for (BudgetConstructionPayRateHolding holdingRecord : records) {
            if (holdingRecord.getAppointmentRequestedPayRate().equals( -1.0)) {
                //TODO: add message constant
                messages.append(holdingRecord.getEmplid() + ", " + holdingRecord.getPositionNumber() + ": no match in payroll. \n");
                updateContainsErrors = true;
                continue;
            } 
            
            List<PendingBudgetConstructionAppointmentFunding> fundingRecords = getFundingRecords(holdingRecord, budgetYear);
            if (fundingRecords.isEmpty()) {
                //TODO: add message constant
                messages.append(holdingRecord.getEmplid() + ", " + holdingRecord.getPositionNumber() + ": no active funding records found. \n");
                updateContainsErrors = true;
                continue;
            }
            
            for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
                KualiInteger annualAmount;
                KualiInteger updateAmount;
                
                if ( !fundingRecord.getAppointmentRequestedPayRate().equals(holdingRecord.getAppointmentRequestedPayRate()) ) {
                    if (fundingRecord.getAppointmentRequestedPayRate().equals(0) || fundingRecord.getAppointmentRequestedPayRate() == null) {
                        messages.append(holdingRecord.getEmplid() + ", " + holdingRecord.getPositionNumber() + ", " + fundingRecord.getChartOfAccountsCode() + ", " + fundingRecord.getAccountNumber() + ", " + fundingRecord.getSubAccountNumber() + ": No update, FTE is zero or blank. \n");
                        updateContainsErrors = true;
                        continue;
                    } 
                    /*Calculate the new annual amount from the requested payrate
                    $$bc_hrly_rate = appt_rqst_pay_rt.ld_bcn_payrt_hldg_t
                    $$bc_annual_amt = $$bc_hrly_rate * %\
                    (((appt_rqst_tm_pct.ld_pndbc_apptfnd_t/100) *   (appt_fnd_mo.ld_pndbc_apptfnd_t/iu_pay_months.ld_bcn_pos_t)) * 2080.0)
                    $$bc_annual_amt = $$bc_annual_amt[round]*/
                    
                    BigDecimal temp1 = fundingRecord.getAppointmentRequestedTimePercent().divide(new BigDecimal(100));
                    BigDecimal temp2 = new BigDecimal((fundingRecord.getAppointmentFundingMonth()/fundingRecord.getBudgetConstructionPosition().getIuPayMonths()) * 2080);
                    
                    annualAmount = new KualiInteger(holdingRecord.getAppointmentRequestedPayRate().multiply(temp1.multiply(temp2)));
                    updateAmount = annualAmount.subtract(fundingRecord.getAppointmentRequestedAmount());
                    
                    BudgetConstructionHeader header = getHeaderRecord(fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber(), budgetYear);
                    if (header == null ) {
                        //TODO: are the parameters for this message correct? (see step 16b on spec).
                        messages.append("Can't find budget doc for " + budgetYear + "," + fundingRecord.getChartOfAccountsCode() + "," + fundingRecord.getAccountNumber() + "," + fundingRecord.getSubAccountNumber() + "\n");
                        messages.append(fundingRecord.getEmplid() + "," + fundingRecord.getPositionNumber() + "," + fundingRecord.getChartOfAccountsCode() + "," + fundingRecord.getAccountNumber() + "," + fundingRecord.getSubAccountNumber() + ": No funding update, error during object level update");
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
                        pendingRecord.setFinancialObjectTypeCode("EX");
                        pendingRecord.setFinancialBeginningBalanceLineAmount(new KualiInteger(0));
                        pendingRecord.setAccountLineAnnualBalanceAmount(updateAmount);
                    }
                    
                    this.businessObjectService.save(pendingRecord);
                    
                    //2PLG
                    //TODO: should I use fundingRecord for this comparison or pendingRecord?
                    if ( !fundingRecord.getAccount().isForContractsAndGrants() && !fundingRecord.getAccount().getSubFundGroupCode().equals("SDCI") ) {
                        PendingBudgetConstructionGeneralLedger plg = findPendingBudgetConstructionGeneralLedger(header, fundingRecord, true);
                        
                        if (plg != null) plg.setAccountLineAnnualBalanceAmount(plg.getAccountLineAnnualBalanceAmount().subtract(updateAmount));
                        else {
                            plg = new PendingBudgetConstructionGeneralLedger();
                            plg.setDocumentNumber(header.getDocumentNumber());
                            plg.setUniversityFiscalYear(fundingRecord.getUniversityFiscalYear());
                            plg.setChartOfAccountsCode(fundingRecord.getChartOfAccountsCode());
                            plg.setAccountNumber(fundingRecord.getAccountNumber());
                            plg.setSubAccountNumber(fundingRecord.getSubAccountNumber());
                            plg.setFinancialObjectCode("2PLG");
                            plg.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                            plg.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
                            plg.setFinancialObjectTypeCode("EX");
                            plg.setFinancialBeginningBalanceLineAmount(new KualiInteger(0));
                            plg.setAccountLineAnnualBalanceAmount(updateAmount.negated());
                        }
                        
                        this.businessObjectService.save(plg);
                    }
                    //TODO: step 17
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
        
        //TODO: when should update be aborted?
        messages.append("Update Aborted, records processed: " + this.updateCount + "\n");
        
        return messages;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#generatePdf(java.lang.StringBuilder, java.io.ByteArrayOutputStream)
     */
    @NonTransactional
    public void generatePdf(List<String> logMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        for (String message : logMessages) {
            document.add(new Paragraph(message));
        }

        document.close();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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
        //TODO: SHOULD I USE CSF FREEZE DATE?
        
        return budgetConstructionPayRateHolding;
    }
    
    private List<PendingBudgetConstructionAppointmentFunding> getFundingRecords(BudgetConstructionPayRateHolding holdingRecord, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        //TODO: get this value from a system parameter
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "2500");
        searchCriteria.put(KFSPropertyConstants.EMPLID, holdingRecord.getEmplid());
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, holdingRecord.getPositionNumber());
        searchCriteria.put(BCPropertyConstants.APPOINTMENT_FUNDING_DELETE_INDICATOR, "N");
        
        return (List<PendingBudgetConstructionAppointmentFunding>) this.businessObjectService.findMatching(PendingBudgetConstructionAppointmentFunding.class, searchCriteria);
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
    
    private boolean getPayRateLock(Map<String, PendingBudgetConstructionAppointmentFunding> lockMap, StringBuilder messages, Integer budgetYear, UniversalUser user, List<BudgetConstructionPayRateHolding> records) {
        Map<String,PendingBudgetConstructionAppointmentFunding> noLockMap = new HashMap<String,PendingBudgetConstructionAppointmentFunding>();
        
        for (BudgetConstructionPayRateHolding record: records) {
            try {
                List<PendingBudgetConstructionAppointmentFunding> fundingRecords = getFundingRecords(record, budgetYear);
                for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
                    BudgetConstructionHeader header = getHeaderRecord(fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber(), budgetYear);
                    String lockingKey = getLockingKeyString(fundingRecord);
                    if ( !lockMap.containsKey(lockingKey) && !noLockMap.containsKey(lockingKey)) {
                        BudgetConstructionLockStatus accountLockStatus = this.lockService.lockAccount(header, user.getPersonUniversalIdentifier());
                        BudgetConstructionLockStatus fundingLockStatus = this.lockService.lockFunding(header, user.getPersonUniversalIdentifier());
                        if ( !accountLockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.SUCCESS) ) {
                            //TODO: add constant
                            messages.append("Batch account lock failed - Budget locks exist. \n");
                            noLockMap.put(lockingKey, fundingRecord);
                        } else if ( !fundingLockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.SUCCESS) ) {
                            //TODO: add error constant
                            messages.append("Batch account lock failed - Funding locks exist. \n");
                            lockMap.remove(lockingKey);
                            noLockMap.put(lockingKey, fundingRecord);
                        } else {
                            lockMap.put(lockingKey, fundingRecord);
                        }
                    }
                }
            }
            
            //TODO: is this right? spec says to clear payrate holding table and return exception file if any errors are encountered.
            catch (RuntimeException e) {
               clearPayrateHoldingTable();
               //TODO: add error constant
               messages.append("Batch account lock failed");
               return false;
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
        //TODO: get this value from a system parameter
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, is2PLG ? "2PLG" : "2500");
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, is2PLG ? KFSConstants.getDashFinancialSubObjectCode() : fundingRecord.getFinancialSubObjectCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, "EX");
        
        return (PendingBudgetConstructionGeneralLedger)this.businessObjectService.findMatching(PendingBudgetConstructionGeneralLedger.class, searchCriteria);
    }
    
    
    private static class DefaultImportFileFormat {
        private static final int[] fieldLengths = new int[] {11, 8, 50, 5, 4, 3, 3, 10, 8};
        //TODO: use constants for field names
        private static final String[] fieldNames = new String[] {"emplid", "positionNumber", "personName", "setidSalary()", "salaryAdministrationPlan", "grade", "positionUnionCode", "appointmentRequestPayRate", "csfFreezeDate"};
    }

    public int getImportCount() {
        return importCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }
    
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }
    
    
}
