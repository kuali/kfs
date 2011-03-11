/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.EndowConstants.EndowmentTransactionTypeCodes;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.report.service.TransactionSummaryReportService;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ContributionsDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ExpensesDataHolder;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionSummaryReportServiceImpl extends EndowmentReportServiceImpl implements TransactionSummaryReportService {

    protected DateTimeService dateTimeService;
    protected TransactionArchiveDao transactionArchiveDao;
    protected HoldingHistoryDao holdingHistoryDao;
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportForAllKemids(java.lang.String)
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportForAllKemids(String beginningDate, String endingDate, String endowmentOption, String closedIndicator) {
        return getTransactionSummaryReportsByKemidByIds(null, beginningDate, endingDate, endowmentOption, closedIndicator);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByKemidByIds(java.util.List, java.lang.String)
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportsByKemidByIds(List<String> kemids, String beginningDate, String endingDate, String endowmentOption, String closedIndicator) {
        
        List<TransactionSummaryReportDataHolder> transactionSummaryReportList = new ArrayList<TransactionSummaryReportDataHolder>();

        Date beginDate = convertStringToDate(beginningDate);
        Date endDate = convertStringToDate(endingDate);        
        if (ObjectUtils.isNull(beginDate) || ObjectUtils.isNull(beginDate)) {
            return null;
        }
        //gather kemids based on the user selection of endowmentOption and closed indicators...
        kemids = getKemidsBasedOnUserSelection(kemids, endowmentOption, closedIndicator);
        
        //eliminate kemids that are not in 
        kemids = getKemidsInHistoryCash(kemids, beginningDate, endingDate);
        
        MonthEndDate beginningMED = getPreviousMonthEndDate(convertStringToDate(beginningDate));
        MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));
        
        for (String kemid : kemids) {
            TransactionSummaryReportDataHolder transactionSummaryReportDataHolder = new TransactionSummaryReportDataHolder();
            
            List<HoldingHistory> holdingHistoryList = holdingHistoryDao.getHoldingHistoryByKemid(kemid);

            getHistoryCashAmounts(transactionSummaryReportDataHolder, kemid, beginningMED.getMonthEndDateId(), endingMED.getMonthEndDateId());
            
            for (HoldingHistory holdingHistory : holdingHistoryList) {
                //if month end beginning date
                if (holdingHistory.getMonthEndDateId().equals(beginningMED.getMonthEndDateId())) {
                    if (holdingHistory.getIncomePrincipalIndicator().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                        transactionSummaryReportDataHolder.setIncomeBeginningMarketValue(transactionSummaryReportDataHolder.getIncomeBeginningMarketValue().add(holdingHistory.getMarketValue()));
                    }
                    if (holdingHistory.getIncomePrincipalIndicator().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                        transactionSummaryReportDataHolder.setPrincipalBeginningMarketValue(transactionSummaryReportDataHolder.getPrincipalBeginningMarketValue().add(holdingHistory.getMarketValue()));
                    }
                }
                // if month end ending date ..
                if (holdingHistory.getMonthEndDateId().equals(endingMED.getMonthEndDateId())) {
                    if (holdingHistory.getIncomePrincipalIndicator().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                        transactionSummaryReportDataHolder.setIncomeEndingMarketValue(transactionSummaryReportDataHolder.getIncomeEndingMarketValue().add(holdingHistory.getMarketValue()));
                    }
                    if (holdingHistory.getIncomePrincipalIndicator().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                        transactionSummaryReportDataHolder.setPrincipalEndingMarketValue(transactionSummaryReportDataHolder.getPrincipalEndingMarketValue().add(holdingHistory.getMarketValue()));
                    }
                }
                
                transactionSummaryReportDataHolder.setNext12MonthsEstimatedIncome(transactionSummaryReportDataHolder.getNext12MonthsEstimatedIncome().add(holdingHistory.getEstimatedIncome()));
                transactionSummaryReportDataHolder.setRemainderOfFYEstimatedIncome(transactionSummaryReportDataHolder.getRemainderOfFYEstimatedIncome().add(holdingHistory.getRemainderOfFYEstimatedIncome()));
                transactionSummaryReportDataHolder.setNextFYEstimatedIncome(transactionSummaryReportDataHolder.getNextFYEstimatedIncome().add(holdingHistory.getNextFYEstimatedIncome()));
            }
            
            //get the transaction archive records now....
            List<TransactionArchive> transactionArchives = transactionArchiveDao.getTransactionArchivesByKemid(kemid, beginDate, endDate);

            for (TransactionArchive transactionArchive : transactionArchives) {
                transactionArchive.refreshNonUpdateableReferences();
                
                //income type code...
                if (transactionArchive.getEtranObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
                    getTransactionArchiveTotalsForIncomeTypeCode(transactionSummaryReportDataHolder, transactionArchive);
                }    
                //expense type code...
                if (transactionArchive.getEtranObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE)) {
                    getTransactionArchiveTotalsForExpenseTypeCode(transactionSummaryReportDataHolder, transactionArchive);
                }    
            }
            
            //setup the report header information....
            transactionSummaryReportDataHolder.setInstitution(getInstitutionName());
            transactionSummaryReportDataHolder.setKemid(kemid);
            transactionSummaryReportDataHolder.setKemidLongTitle(getKemid(kemid).getLongTitle());
            transactionSummaryReportDataHolder.setBeginningDate(beginningDate);
            transactionSummaryReportDataHolder.setEndingDate(endingDate);
            
            //setup the report footer information....need to do this..
            // add this new one
            transactionSummaryReportList.add(transactionSummaryReportDataHolder);
        }
        
        return transactionSummaryReportList;
    }

    /**
     * Method to retrieve the records from END_HIST_CSH_T table for the given
     * kemid and retrieve the the income and principal cash amounts.
     * @param kemid
     * @param historyIncomeCash
     * @param historyPrincipalCash
     * @param beginningMED
     * @param endingMED
     */
    protected void getHistoryCashAmounts(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder, String kemid, KualiInteger beginningMed, KualiInteger endingMed) {
        List<String> kemids = new ArrayList();
        kemids.add(kemid);
        
        List<KemidHistoricalCash> kemidHistoryCash = kemidHistoricalCashDao.getHistoricalCashRecords(kemids, beginningMed, endingMed);
        
        for (KemidHistoricalCash historyCash : kemidHistoryCash) {
            if (historyCash.getMonthEndDateId().equals(beginningMed)) {
                transactionSummaryReportDataHolder.setIncomeBeginningMarketValue(historyCash.getHistoricalIncomeCash().bigDecimalValue());
                transactionSummaryReportDataHolder.setPrincipalBeginningMarketValue(historyCash.getHistoricalPrincipalCash().bigDecimalValue());
            }
            if (historyCash.getMonthEndDateId().equals(endingMed)) {
                transactionSummaryReportDataHolder.setIncomeEndingMarketValue(historyCash.getHistoricalIncomeCash().bigDecimalValue());
                transactionSummaryReportDataHolder.setPrincipalEndingMarketValue(historyCash.getHistoricalPrincipalCash().bigDecimalValue());
            }
        }
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByOtherCriteria(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String)
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String beginningDate, String endingDate, String endowmnetOption, String closedIndicator) {
        
        List<String> kemids = getKemidsByOtherCriteria(benefittingOrganziationCampusCodes, benefittingOrganziationChartCodes,
                benefittingOrganziationCodes, typeCodes, purposeCodes, combineGroupCodes);        
        
        // Now that we have all the kemids to be reported by the use of the other criteria, go to get the report data 
        if (kemids.size() == 0) {
            return null;
        } else {            
            return getTransactionSummaryReportsByKemidByIds(kemids, beginningDate, endingDate, endowmnetOption, closedIndicator);
        }
    }

    /**
     * Method to look into each transaction archive record and gather income or principal amounts.
     * 
     * @param transactionSummaryReportDataHolder
     * @param transactionArchive
     */
    protected void getTransactionArchiveTotalsForIncomeTypeCode(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder, TransactionArchive transactionArchive) {
        transactionArchive.refreshNonUpdateableReferences();
        BigDecimal transactionSecurityCost = BigDecimal.ZERO;
        
        ContributionsDataHolder contributionsData = transactionSummaryReportDataHolder.new ContributionsDataHolder();
        contributionsData.setContributionsDescription(transactionArchive.getDescription());
        
        //get the transaction archive data totals....
        List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
        for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
            transactionSecurityCost = transactionSecurityCost.add(archiveSecurity.getHoldingCost());
        }
        //income type code...
        if (transactionArchive.getEtranObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                contributionsData.setIncomeContributions(transactionSecurityCost.add(contributionsData.getIncomeContributions().add(transactionArchive.getIncomeCashAmount())));
            }
            
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                contributionsData.setPrincipalContributions(transactionSecurityCost.add(contributionsData.getPrincipalContributions().add(transactionArchive.getPrincipalCashAmount())));
            }
        }
        
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            contributionsData.setIncomeContributions(transactionSecurityCost.add(contributionsData.getIncomeContributions().add(transactionArchive.getIncomeCashAmount())));
        }
        
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
            contributionsData.setPrincipalContributions(transactionSecurityCost.add(contributionsData.getPrincipalContributions().add(transactionArchive.getPrincipalCashAmount())));
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForContributions().add(contributionsData);
    }
    
    /**
     * Method to look into each transaction archive record and gather income or principal amounts.
     * 
     * @param transactionSummaryReportDataHolder
     * @param transactionArchive
     */
    protected void getTransactionArchiveTotalsForExpenseTypeCode(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder, TransactionArchive transactionArchive) {
        BigDecimal transactionSecurityCost = BigDecimal.ZERO;
        
        ExpensesDataHolder expensesDataHolder = transactionSummaryReportDataHolder.new ExpensesDataHolder();
        expensesDataHolder.setExpensesDescription(transactionArchive.getDescription());
        
        //get the transaction archive data totals....
        List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
        for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
            transactionSecurityCost = transactionSecurityCost.add(archiveSecurity.getHoldingCost());
        }
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            expensesDataHolder.setIncomeExpenses(transactionSecurityCost.add(expensesDataHolder.getIncomeExpenses().add(transactionArchive.getIncomeCashAmount())));
        }
        
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
            expensesDataHolder.setPrincipalExpenses(transactionSecurityCost.add(expensesDataHolder.getPrincipalExpenses().add(transactionArchive.getPrincipalCashAmount())));
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForExpenses().add(expensesDataHolder);
    }
    
    protected KEMID getKemid(String kemid) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.KEMID, kemid);
        return (KEMID) businessObjectService.findByPrimaryKey(KEMID.class, primaryKeys);
    }
    
    
    protected KemidHistoricalCash getKemidHistoricalCash(String kemid, KualiInteger medId) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, medId);
        return (KemidHistoricalCash) businessObjectService.findByPrimaryKey(KemidHistoricalCash.class, primaryKeys);
    }
 
    protected String convertDateToString(Date date) {        
        return dateTimeService.toDateString(date);
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setTransactionArchiveDao(TransactionArchiveDao transactionArchiveDao) {
        this.transactionArchiveDao = transactionArchiveDao;
    }
    
    protected TransactionArchiveDao getTransactionArchiveDao() {
        return transactionArchiveDao;
    }

    /**
     * gets attribute holdingHistoryDao
     * @return holdingHistoryDao
     */
    protected HoldingHistoryDao getHoldingHistoryDao() {
        return holdingHistoryDao;
    }

    /**
     * sets attribute holdingHistoryDao
     */
    public void setHoldingHistoryDao(HoldingHistoryDao holdingHistoryDao) {
        this.holdingHistoryDao = holdingHistoryDao;
    }
}
