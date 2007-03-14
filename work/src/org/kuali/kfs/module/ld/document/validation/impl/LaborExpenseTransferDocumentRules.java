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
package org.kuali.module.labor.rules;

import static org.kuali.Constants.BALANCE_TYPE_ACTUAL;
import static org.kuali.Constants.BALANCE_TYPE_A21;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;

/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 * 
 * 
 */
public class LaborExpenseTransferDocumentRules extends AccountingDocumentRuleBase {

    // LLPE Constants
    public static final class LABOR_LEDGER_PENDING_ENTRY_CODE {
        public static final String NO = "N";
        public static final String YES = "Y";
        public static final String BLANK_PROJECT_STRING = "----------"; // Max length is 10 for this field
        public static final String BLANK_SUB_OBJECT_CODE = "---"; // Max length is 3 for this field
        public static final String BLANK_SUB_ACCOUNT_NUMBER = "-----"; // Max length is 5 for this field
        public static final String BLANK_OBJECT_CODE = "----"; // Max length is 4 for this field
        public static final String BLANK_OBJECT_TYPE_CODE = "--"; // Max length is 4 for this field
        public static final String BLANK_POSITION_NUMBER = "--------"; // Max length is 8 for this field
        public static final String BLANK_EMPL_ID = "-----------"; // Max length is 11 for this field
        public static final String LL_PE_OFFSET_STRING = "TP Generated Offset";
        public static final int LLPE_DESCRIPTION_MAX_LENGTH = 40;
    }

    public LaborExpenseTransferDocumentRules() {
    }   
    
    protected boolean AddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);
    }
    
    /**
     * Overriding hook into generate general ledger pending entries, but calling a method
     * to generate labor ledger pending entries.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.AccountingDocument, org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateGeneralLedgerPendingEntries(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {        
        return true;
        //return processGenerateLaborLedgerPendingEntries(accountingDocument, accountingLine, sequenceHelper);
    }

    /**
     * This method is the starting point for creating labor ledger pending entries.
     * The logic used to create the LLPEs resides in this method.
     *  
     * @param accountingDocument
     * @param accountingLine
     * @param sequenceHelper
     * @return
     */
    public boolean processGenerateLaborLedgerPendingEntries(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper){
        boolean success = true;

        ExpenseTransferAccountingLine al = (ExpenseTransferAccountingLine)accountingLine;
        Collection<PositionObjectBenefit> positionObjectBenefits;
        
        //setup default values, so they don't have to be set multiple times
        PendingLedgerEntry defaultEntry = new PendingLedgerEntry();        
        populateDefaultLaborLedgerPendingEntry(accountingDocument, accountingLine, defaultEntry);

        //Generate orig entry
        PendingLedgerEntry originalEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
        success &= processOriginalLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, originalEntry);
    
        // increment the sequence counter
        sequenceHelper.increment();
        
        //if the AL's pay FY and period do not match the University fiscal year and period
        if( isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(accountingDocument, accountingLine) ){    
            //Generate A21
            PendingLedgerEntry a21Entry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
            success &= processA21LaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, a21Entry);            
        }
        
        //Generate A21 rev
        PendingLedgerEntry a21RevEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
        success &= processA21RevLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, a21RevEntry);

        //TODO: Figure out how to determine what exactly is a salary object code
        //if AL object code is a salary object code
        if(true){
            //get benefits
            positionObjectBenefits = SpringServiceLocator.getLaborPositionObjectBenefitService().getPositionObjectBenefits(al.getPayrollEndDateFiscalYear(), al.getChartOfAccountsCode(), al.getFinancialObjectCode());            
            
            //for each row in the ld_lbr_obj_bene_t table for the labor ledger AL's pay FY, chart and object code            
            for (PositionObjectBenefit pob : positionObjectBenefits){

                //fringe benefit code
                String fringeBenefitObjectCode = pob.getBenefitsCalculation().getPositionFringeBenefitObjectCode();
                
                //calculate the benefit amount (ledger amt * (benfit pct/100) )
                KualiDecimal benefitAmount = pob.getBenefitsCalculation().getPositionFringeBenefitPercent();                
                benefitAmount = benefitAmount.divide(new KualiDecimal(100));
                benefitAmount = benefitAmount.multiply(al.getAmount());
                
                //Generate Benefit
                PendingLedgerEntry benefitEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
                success &= processBenefitLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, benefitEntry, benefitAmount, fringeBenefitObjectCode);                    
                
                //if the AL's pay FY and period do not match the University fiscal year and period
                if( isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(accountingDocument, accountingLine) ){
                    //Generate Benefit A21
                    PendingLedgerEntry benefitA21Entry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
                    success &= processBenefitA21LaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, benefitA21Entry, benefitAmount, fringeBenefitObjectCode);                    
                }
                
                //Generate Benefit A21 rev
                PendingLedgerEntry benefitA21RevEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
                success &= processBenefitA21RevLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, benefitA21RevEntry, benefitAmount, fringeBenefitObjectCode);                
            }
            
        }                    

        return true;
    }

    /**
     * This method compares the pay fiscal year and period from the
     * accounting line and the university values.  A true is returned
     * if the values match.
     *      
     * @param transactionalDocument
     * @param accountingLine
     * @return
     */
    private boolean isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(AccountingDocument accountingDocument, AccountingLine accountingLine){
        boolean success = true;
        
        AccountingPeriod ap = accountingDocument.getAccountingPeriod();
        ExpenseTransferAccountingLine al = (ExpenseTransferAccountingLine)accountingLine;
        
        //if the AL's pay FY and period do not match the University fiscal year and period
        if( !(ap.getUniversityFiscalYear().equals(al.getPayrollEndDateFiscalYear()) &&
             ap.getUniversityFiscalPeriodCode().equals(al.getPayrollEndDateFiscalPeriodCode()) ) ){
            success = false;
        }
        
        return success;
    }

    /**
     * This method populates common fields amongst the different LLPE use cases.
     *      
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @param originalEntry
     */
    private void populateDefaultLaborLedgerPendingEntry(AccountingDocument transactionalDocument, AccountingLine accountingLine, PendingLedgerEntry defaultEntry){

        //the same across all types
        ObjectCode objectCode = accountingLine.getObjectCode();
        if (ObjectUtils.isNull(objectCode)) {
            accountingLine.refreshReferenceObject("objectCode");
        }
        defaultEntry.setFinancialObjectTypeCode(accountingLine.getObjectCode().getFinancialObjectTypeCode());
        defaultEntry.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(transactionalDocument.getClass()));
        defaultEntry.setFinancialSystemOriginationCode(SpringServiceLocator.getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode());
        defaultEntry.setDocumentNumber(accountingLine.getDocumentNumber());
        defaultEntry.setTransactionLedgerEntryDescription(getEntryValue(accountingLine.getFinancialDocumentLineDescription(), transactionalDocument.getDocumentHeader().getFinancialDocumentDescription()));                
        defaultEntry.setOrganizationDocumentNumber(transactionalDocument.getDocumentHeader().getOrganizationDocumentNumber());
        defaultEntry.setFinancialDocumentReversalDate(null);
        defaultEntry.setReferenceFinancialSystemOriginationCode(null);
        defaultEntry.setReferenceFinancialDocumentNumber(null);
        defaultEntry.setReferenceFinancialDocumentTypeCode(null);
                              
    }

    protected boolean processOriginalLaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry originalEntry) {        
        
        boolean success = true;
        
        // populate the entry
        populateOriginalLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, originalEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeOriginalLaborLedgerPendingEntry(accountingDocument, accountingLine, originalEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(originalEntry);

        return success;
    }

    protected boolean customizeOriginalLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry originalEntry) {
        return true;
    }

 
    protected void populateOriginalLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry originalEntry) {        

        originalEntry.setUniversityFiscalYear(null);
        originalEntry.setUniversityFiscalPeriodCode(null);
        originalEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        originalEntry.setAccountNumber(accountingLine.getAccountNumber());
        originalEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        originalEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        originalEntry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        originalEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        originalEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        originalEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        originalEntry.setTransactionDebitCreditCode( accountingLine.isSourceAccountingLine() ? Constants.GL_CREDIT_CODE : Constants.GL_DEBIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        originalEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        originalEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        originalEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        originalEntry.setPositionNumber( ((ExpenseTransferAccountingLine)accountingLine).getPositionNumber() );
        originalEntry.setEmplid( ((ExpenseTransferAccountingLine)accountingLine).getEmplid() );
        originalEntry.setPayrollEndDateFiscalYear( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        originalEntry.setPayrollEndDateFiscalPeriodCode( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        originalEntry.setTransactionTotalHours( ((ExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        originalEntry.setReferenceFinancialSystemOriginationCode(null);
        originalEntry.setReferenceFinancialDocumentNumber(null);
        originalEntry.setReferenceFinancialDocumentTypeCode(null);
                      
        // TODO wait for core budget year data structures to be put in place
        // originalEntry.setBudgetYear(accountingLine.getBudgetYear());
        // originalEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);
    }
    
    protected boolean processA21LaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry a21Entry) {        

        boolean success = true;
        
        // populate the entry
        populateA21LaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, a21Entry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeA21LaborLedgerPendingEntry(accountingDocument, accountingLine, a21Entry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(a21Entry);

        return success;
    }

    protected boolean customizeA21LaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry a21Entry) {
        return true;
    }

    protected void populateA21LaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry a21Entry) {        
        a21Entry.setUniversityFiscalYear(null);
        a21Entry.setUniversityFiscalPeriodCode(null);
        a21Entry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        a21Entry.setAccountNumber(accountingLine.getAccountNumber());
        a21Entry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        a21Entry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        a21Entry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        a21Entry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        a21Entry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        a21Entry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        a21Entry.setTransactionDebitCreditCode( accountingLine.isSourceAccountingLine() ? Constants.GL_DEBIT_CODE : Constants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        a21Entry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        a21Entry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        a21Entry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        a21Entry.setPositionNumber( ((ExpenseTransferAccountingLine)accountingLine).getPositionNumber() );
        a21Entry.setEmplid( ((ExpenseTransferAccountingLine)accountingLine).getEmplid() );
        a21Entry.setPayrollEndDateFiscalYear( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        a21Entry.setPayrollEndDateFiscalPeriodCode( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        a21Entry.setTransactionTotalHours( ((ExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        a21Entry.setReferenceFinancialSystemOriginationCode(null);
        a21Entry.setReferenceFinancialDocumentNumber(null);
        a21Entry.setReferenceFinancialDocumentTypeCode(null);
    }

    protected boolean processA21RevLaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry a21RevEntry) {        

        boolean success = true;
        
        // populate the entry
        populateA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, a21RevEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, a21RevEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(a21RevEntry);
        
        return success;
    }

    protected boolean customizeA21RevLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry a21RevEntry) {
        return true;
    }

    protected void populateA21RevLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry a21RevEntry) {
        
        ExpenseTransferAccountingLine al = (ExpenseTransferAccountingLine)accountingLine;
        
        a21RevEntry.setUniversityFiscalYear(al.getPayrollEndDateFiscalYear());
        a21RevEntry.setUniversityFiscalPeriodCode(al.getPayrollEndDateFiscalPeriodCode());
        a21RevEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        a21RevEntry.setAccountNumber(accountingLine.getAccountNumber());
        a21RevEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        a21RevEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        a21RevEntry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        a21RevEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        a21RevEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        a21RevEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        a21RevEntry.setTransactionDebitCreditCode( accountingLine.isSourceAccountingLine() ? Constants.GL_CREDIT_CODE : Constants.GL_DEBIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        a21RevEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        a21RevEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        a21RevEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        a21RevEntry.setPositionNumber( ((ExpenseTransferAccountingLine)accountingLine).getPositionNumber() );
        a21RevEntry.setEmplid( ((ExpenseTransferAccountingLine)accountingLine).getEmplid() );
        a21RevEntry.setPayrollEndDateFiscalYear( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        a21RevEntry.setPayrollEndDateFiscalPeriodCode( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        a21RevEntry.setTransactionTotalHours( ((ExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        a21RevEntry.setReferenceFinancialSystemOriginationCode(null);
        a21RevEntry.setReferenceFinancialDocumentNumber(null);
        a21RevEntry.setReferenceFinancialDocumentTypeCode(null);
    }

    protected boolean processBenefitLaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitEntry, benefitAmount, fringeBenefitObjectCode);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitLaborLedgerPendingEntry(accountingDocument, accountingLine, benefitEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(benefitEntry);

        return success;
    }

    protected boolean customizeBenefitLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry benefitEntry) {
        return true;
    }

    protected void populateBenefitLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {       
        benefitEntry.setUniversityFiscalYear(null);
        benefitEntry.setUniversityFiscalPeriodCode(null);
        
        //special handling
        benefitEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        benefitEntry.setAccountNumber(accountingLine.getAccountNumber());

        //set benefit amount and fringe object code
        benefitEntry.setTransactionLedgerEntryAmount(benefitAmount);
        benefitEntry.setFinancialObjectCode(fringeBenefitObjectCode);        

        benefitEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));        
        benefitEntry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        benefitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));            
        benefitEntry.setTransactionDebitCreditCode( accountingLine.isSourceAccountingLine() ? Constants.GL_CREDIT_CODE : Constants.GL_DEBIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        benefitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        benefitEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        benefitEntry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitEntry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitEntry.setPayrollEndDateFiscalYear( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        benefitEntry.setPayrollEndDateFiscalPeriodCode( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        benefitEntry.setTransactionTotalHours( ((ExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        benefitEntry.setReferenceFinancialSystemOriginationCode(null);
        benefitEntry.setReferenceFinancialDocumentNumber(null);
        benefitEntry.setReferenceFinancialDocumentTypeCode(null);        
    }

    protected boolean processBenefitA21LaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitA21Entry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitA21LaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitA21Entry, benefitAmount, fringeBenefitObjectCode);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitA21LaborLedgerPendingEntry(accountingDocument, accountingLine, benefitA21Entry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(benefitA21Entry);

        return success;
    }

    protected boolean customizeBenefitA21LaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry benefitA21Entry) {
        return true;
    }

    protected void populateBenefitA21LaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitA21Entry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {        
        benefitA21Entry.setUniversityFiscalYear(null);
        benefitA21Entry.setUniversityFiscalPeriodCode(null);
        
        //special handling
        benefitA21Entry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        benefitA21Entry.setAccountNumber(accountingLine.getAccountNumber());

        //set benefit amount and fringe object code
        benefitA21Entry.setTransactionLedgerEntryAmount(benefitAmount);
        benefitA21Entry.setFinancialObjectCode(fringeBenefitObjectCode);        

        benefitA21Entry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));        
        benefitA21Entry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitA21Entry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        benefitA21Entry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));            
        benefitA21Entry.setTransactionDebitCreditCode( accountingLine.isSourceAccountingLine() ? Constants.GL_DEBIT_CODE : Constants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        benefitA21Entry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitA21Entry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        benefitA21Entry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        benefitA21Entry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitA21Entry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitA21Entry.setPayrollEndDateFiscalYear( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        benefitA21Entry.setPayrollEndDateFiscalPeriodCode( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        benefitA21Entry.setTransactionTotalHours( ((ExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        benefitA21Entry.setReferenceFinancialSystemOriginationCode(null);
        benefitA21Entry.setReferenceFinancialDocumentNumber(null);
        benefitA21Entry.setReferenceFinancialDocumentTypeCode(null);        
    }

    protected boolean processBenefitA21RevLaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitA21RevEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitA21RevEntry, benefitAmount, fringeBenefitObjectCode);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, benefitA21RevEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(benefitA21RevEntry);

        return success;
    }

    protected boolean customizeBenefitA21RevLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry benefitA21RevEntry) {
        return true;
    }

    protected void populateBenefitA21RevLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitA21RevEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {        
        benefitA21RevEntry.setUniversityFiscalYear(null);
        benefitA21RevEntry.setUniversityFiscalPeriodCode(null);
        
        //special handling
        benefitA21RevEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        benefitA21RevEntry.setAccountNumber(accountingLine.getAccountNumber());

        //set benefit amount and fringe object code
        benefitA21RevEntry.setTransactionLedgerEntryAmount(benefitAmount);
        benefitA21RevEntry.setFinancialObjectCode(fringeBenefitObjectCode);        

        benefitA21RevEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));        
        benefitA21RevEntry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitA21RevEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        benefitA21RevEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));            
        benefitA21RevEntry.setTransactionDebitCreditCode( accountingLine.isSourceAccountingLine() ? Constants.GL_DEBIT_CODE : Constants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime());
        benefitA21RevEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitA21RevEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        benefitA21RevEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        benefitA21RevEntry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitA21RevEntry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitA21RevEntry.setPayrollEndDateFiscalYear( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalYear() );
        benefitA21RevEntry.setPayrollEndDateFiscalPeriodCode( ((ExpenseTransferAccountingLine)accountingLine).getPayrollEndDateFiscalPeriodCode() );
        benefitA21RevEntry.setTransactionTotalHours( ((ExpenseTransferAccountingLine)accountingLine).getPayrollTotalHours() );
        
        benefitA21RevEntry.setReferenceFinancialSystemOriginationCode(null);
        benefitA21RevEntry.setReferenceFinancialDocumentNumber(null);
        benefitA21RevEntry.setReferenceFinancialDocumentTypeCode(null);        
    }

    protected boolean processBenefitClearingLaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry benefitClearingEntry) {        

        boolean success = true;
        
        // populate the entry
        populateBenefitClearingLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitClearingEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitClearingLaborLedgerPendingEntry(accountingDocument, accountingLine, benefitClearingEntry);

        // add the new entry to the document now
        ((SalaryExpenseTransferDocument)accountingDocument).getLaborLedgerPendingEntries().add(benefitClearingEntry);

        return success;
    }

    protected boolean customizeBenefitClearingLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, PendingLedgerEntry benefitClearingEntry) {
        return true;
    }

    protected void populateBenefitClearingLaborLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PendingLedgerEntry benefitClearingEntry) {        
    }

    /**
     * This is responsible for properly negating the sign on an accounting line's amount when its associated document is an error
     * correction.
     * 
     * @param accountingDocument
     * @param accountingLine
     */
    private final void handleDocumentErrorCorrection(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        // If the document corrects another document, make sure the accounting line has the correct sign.
        if ((null == accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isNegative()) || (null != accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isPositive())) {
            accountingLine.setAmount(accountingLine.getAmount().multiply(new KualiDecimal(1)));
        }
    }

    /**
     * Applies the given flexible offset account to the given offset entry. Does nothing if flexibleOffsetAccount is null or its COA
     * and account number are the same as the offset entry's.
     * 
     * @param flexibleOffsetAccount may be null
     * @param offsetEntry may be modified
     */
    private static void flexOffsetAccountIfNecessary(OffsetAccount flexibleOffsetAccount, PendingLedgerEntry offsetEntry) {
        if (flexibleOffsetAccount == null) {
            return; // They are not required and may also be disabled.
        }
        String flexCoa = flexibleOffsetAccount.getFinancialOffsetChartOfAccountCode();
        String flexAccountNumber = flexibleOffsetAccount.getFinancialOffsetAccountNumber();
        if (flexCoa.equals(offsetEntry.getChartOfAccountsCode()) && flexAccountNumber.equals(offsetEntry.getAccountNumber())) {
            return; // no change, so leave sub-account as is
        }
        if (ObjectUtils.isNull(flexibleOffsetAccount.getFinancialOffsetAccount())) {
            throw new ReferentialIntegrityException("flexible offset account " + flexCoa + "-" + flexAccountNumber);
        }
        offsetEntry.setChartOfAccountsCode(flexCoa);
        offsetEntry.setAccountNumber(flexAccountNumber);
        // COA and account number are part of the sub-account's key, so the original sub-account would be invalid.
        offsetEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
    }
    

    public boolean isDebit(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isDebit = false;
        if (accountingLine.isSourceAccountingLine()) {
            isDebit = IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingDocument, accountingLine);
        }
        else if (accountingLine.isTargetAccountingLine()) {
            isDebit = !IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingDocument, accountingLine);
        }
        else {
            throw new IllegalStateException(IsDebitUtils.isInvalidLineTypeIllegalArgumentExceptionMessage);
        }

        return isDebit;
    }

    public boolean isCredit(AccountingLine accountingLine, AccountingDocument accountingDocument) {
        return false;
    }
 
    /**
     * util class that contains common algorithms for determining debit amounts
     * 
     * 
     */
    protected static class IsDebitUtils {
        protected static final String isDebitCalculationIllegalStateExceptionMessage = "an invalid debit/credit check state was detected";
        protected static final String isErrorCorrectionIllegalStateExceptionMessage = "invalid (error correction) document not allowed";
        protected static final String isInvalidLineTypeIllegalArgumentExceptionMessage = "invalid accounting line type";

        /**
         * 
         * @param debitCreditCode
         * @return true if debitCreditCode equals the the debit constant
         */
        static boolean isDebitCode(String debitCreditCode) {
            return StringUtils.equals(Constants.GL_DEBIT_CODE, debitCreditCode);
        }

        /**
         * <ol>
         * <li>object type is included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> (isIncome || isLiability) && (lineAmount > 0)
         * <li> (isExpense || isAsset) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> (isIncome || isLiability) && (lineAmount < 0)
         * <li> (isExpense || isAsset) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> document isErrorCorrection
         * <li> lineAmount == 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         * 
         */
        static boolean isDebitConsideringType(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();

            // income/liability
            if (rule.isIncomeOrLiability(accountingLine)) {
                isDebit = !isPositiveAmount;
            }
            // expense/asset
            else {
                if (rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>object type is not included in determining if a line is debit or credit.
         * <li>accounting line section (source/target) is not included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> none
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> (isIncome || isLiability || isExpense || isAsset) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> lineAmount <= 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        static boolean isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            // non error correction
            if (!isErrorCorrection(accountingDocument)) {
                boolean isPositiveAmount = amount.isPositive();
                // isDebit if income/liability/expense/asset and line amount is positive
                if (isPositiveAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                    isDebit = true;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // error correction
            else {
                boolean isNegativeAmount = amount.isNegative();
                // isDebit if income/liability/expense/asset and line amount is negative
                if (isNegativeAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                    isDebit = false;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }

            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) type is included in determining if a line is debit or credit.
         * <li> zero line amounts are never allowed
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
         * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
         * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> lineAmount == 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         * 
         */
        static boolean isDebitConsideringSection(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();
            // source line
            if (accountingLine.isSourceAccountingLine()) {
                // income/liability/expense/asset
                if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = !isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // target line
            else {
                if (accountingLine.isTargetAccountingLine()) {
                    if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                        isDebit = isPositiveAmount;
                    }
                    else {
                        throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                    }
                }
                else {
                    throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                }
            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) and object type is included in determining if a line is debit or credit.
         * <li> negative line amounts are <b>Only</b> allowed during error correction
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> isSourceLine && (isExpense || isAsset) && (lineAmount > 0)
         * <li> isTargetLine && (isIncome || isLiability) && (lineAmount > 0)
         * <li> isErrorCorrection && isSourceLine && (isIncome || isLiability) && (lineAmount < 0)
         * <li> isErrorCorrection && isTargetLine && (isExpense || isAsset) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> isSourceLine && (isIncome || isLiability) && (lineAmount > 0)
         * <li> isTargetLine && (isExpense || isAsset) && (lineAmount > 0)
         * <li> isErrorCorrection && (isExpense || isAsset) && (lineAmount < 0)
         * <li> isErrorCorrection && (isIncome || isLiability) && (lineAmount < 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> !isErrorCorrection && !(lineAmount > 0)
         * <li> isErrorCorrection && !(lineAmount < 0)
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        static boolean isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            // non error correction
            if (!isErrorCorrection(accountingDocument)) {
                boolean isPositiveAmount = amount.isPositive();
                // only allow amount >0
                if (!isPositiveAmount) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
                // source line
                if (accountingLine.isSourceAccountingLine()) {
                    isDebit = rule.isIncomeOrLiability(accountingLine);
                }
                // target line
                else {
                    if (accountingLine.isTargetAccountingLine()) {
                        isDebit = rule.isExpenseOrAsset(accountingLine);
                    }
                    else {
                        throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                    }
                }
            }
            // error correction document
            else {
                boolean isNegativeAmount = amount.isNegative();
                if (!isNegativeAmount) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
                // source line
                if (accountingLine.isSourceAccountingLine()) {
                    isDebit = rule.isExpenseOrAsset(accountingLine);
                }
                // target line
                else {
                    if (accountingLine.isTargetAccountingLine()) {
                        isDebit = rule.isIncomeOrLiability(accountingLine);
                    }
                    else {
                        throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                    }
                }
            }

            return isDebit;
        }

        /**
         * throws an <code>IllegalStateException</code> if the document is an error correction. otherwise does nothing
         * 
         * @param rule
         * @param accountingDocument
         */
        static void disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument) {
            if (isErrorCorrection(accountingDocument)) {
                throw new IllegalStateException(isErrorCorrectionIllegalStateExceptionMessage);
            }
        }
        
        /**
         * Convience method for determine if a document is an error correction document.
         * 
         * @param accountingDocument
         * @return true if document is an error correct
         */
        static boolean isErrorCorrection(AccountingDocument accountingDocument) {
            boolean isErrorCorrection = false;

            String correctsDocumentId = accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber();
            if (StringUtils.isNotBlank(correctsDocumentId)) {
                isErrorCorrection = true;
            }

            return isErrorCorrection;
        }

    }
    
    /**
     * Verify that the selected employee does not have other pending salary transfers that have
     * not been processed.
     * 
     * @param Employee ID
     * @return true if the employee does not have any pending salary transfers.
     */
    public boolean validatePendingSalaryTransfer(String emplid) {
        
        // We must not have any pending labor ledger entries
        if (SpringServiceLocator.getLaborLedgerPendingEntryService().hasPendingLaborLedgerEntry(emplid)) {
           reportError(Constants.EMPLOYEE_LOOKUP_ERRORS,KeyConstants.Labor.PENDING_SALARY_TRANSFER_ERROR, emplid);
           return false;
        }      
        return true;
 
    }
}