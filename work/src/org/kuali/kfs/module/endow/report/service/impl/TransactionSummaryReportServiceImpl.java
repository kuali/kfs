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
import org.kuali.kfs.module.endow.EndowConstants.EndowmentTransactionTypeCodes;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
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
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.CashTransfersDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ContributionsDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ExpensesDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.SecurityTransfersDataHolder;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionSummaryReportServiceImpl extends EndowmentReportServiceImpl implements TransactionSummaryReportService {

    protected TransactionArchiveDao transactionArchiveDao;
    protected HoldingHistoryDao holdingHistoryDao;
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportForAllKemids(java.lang.String)
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportForAllKemids(String beginningDate, String endingDate, String endowmentOption, String closedIndicator, String reportOption) {
        return getTransactionSummaryReportsByKemidByIds(null, beginningDate, endingDate, endowmentOption, closedIndicator, reportOption);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByKemidByIds(java.util.List, java.lang.String)
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportsByKemidByIds(List<String> kemids, String beginningDate, String endingDate, String endowmentOption, String closedIndicator, String reportOption) {
        
        List<TransactionSummaryReportDataHolder> transactionSummaryReportList = new ArrayList<TransactionSummaryReportDataHolder>();

        Date beginDate = convertStringToDate(beginningDate);
        Date endDate = convertStringToDate(endingDate);        
        if (ObjectUtils.isNull(beginDate) || ObjectUtils.isNull(beginDate)) {
            return null;
        }
        //gather kemids based on the user selection of endowmentOption and closed indicators...
        kemids = getKemidsBasedOnUserSelection(kemids, endowmentOption, closedIndicator);
        
        if (kemids.size() == 0) {
            return null;
        }
        
        //eliminate kemids that are not in 
        kemids = getKemidsInHistoryCash(kemids, beginningDate, endingDate);
        
        if (kemids.size() == 0) {
            return null;
        }
        
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
                
                //start calculating change in market value. It will also be calculated during transaction archive records calculations
                transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeEndingMarketValue().subtract(transactionSummaryReportDataHolder.getIncomeBeginningMarketValue()));
                transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalEndingMarketValue().subtract(transactionSummaryReportDataHolder.getPrincipalBeginningMarketValue()));
                
                transactionSummaryReportDataHolder.setNext12MonthsEstimatedIncome(transactionSummaryReportDataHolder.getNext12MonthsEstimatedIncome().add(holdingHistory.getEstimatedIncome()));
                transactionSummaryReportDataHolder.setRemainderOfFYEstimatedIncome(transactionSummaryReportDataHolder.getRemainderOfFYEstimatedIncome().add(holdingHistory.getRemainderOfFYEstimatedIncome()));
                transactionSummaryReportDataHolder.setNextFYEstimatedIncome(transactionSummaryReportDataHolder.getNextFYEstimatedIncome().add(holdingHistory.getNextFYEstimatedIncome()));
            }
            
            //get the transaction archive records now....
            List<TransactionArchive> transactionArchives = transactionArchiveDao.getTransactionArchivesByKemid(kemid, beginDate, endDate);

            for (TransactionArchive transactionArchive : transactionArchives) {
                transactionArchive.refreshNonUpdateableReferences();
                
                if (EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER.equalsIgnoreCase(transactionArchive.getTypeCode()) ||
                        EndowConstants.DocumentTypeNames.ENDOWMENT_SECURITY_TRANSFER.equalsIgnoreCase(transactionArchive.getTypeCode())) {
                    //cash transfers...
                    if (EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER.equalsIgnoreCase(transactionArchive.getTypeCode())) {
                        getTransactionArchiveTotalsForCashTransfers(transactionSummaryReportDataHolder, transactionArchive);
                    }
                    //security tranfers....
                    if (EndowConstants.DocumentTypeNames.ENDOWMENT_SECURITY_TRANSFER.equalsIgnoreCase(transactionArchive.getTypeCode())) {
                        getTransactionArchiveTotalsForSecurityTransfers(transactionSummaryReportDataHolder, transactionArchive);
                    }
                } 
                else {
                    //income type code...
                    if (transactionArchive.getEtranObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
                        getTransactionArchiveTotalsForIncomeTypeCode(transactionSummaryReportDataHolder, transactionArchive);
                    }    
                    //expense type code...
                    if (transactionArchive.getEtranObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE)) {
                        getTransactionArchiveTotalsForExpenseTypeCode(transactionSummaryReportDataHolder, transactionArchive);
                    }
                }
            }
            
            //combine the similar etrancode records in the list for
            //contributions, expenses, cash transfers, and security transfers....
            if (transactionSummaryReportDataHolder.getReportGroupsForContributions().size() > 0) {
                combineContributionsData(transactionSummaryReportDataHolder);
            }
            
            if (transactionSummaryReportDataHolder.getReportGroupsForExpenses().size() > 0) {
                combineExpensesData(transactionSummaryReportDataHolder);
            }
            
            if (transactionSummaryReportDataHolder.getReportGroupsForCashTransfers().size() > 0) {
                combineCashTransfersData(transactionSummaryReportDataHolder);
            }
            
            if (transactionSummaryReportDataHolder.getReportGroupsForSecurityTransfers().size() > 0) {
                combineSecurityTransfersData(transactionSummaryReportDataHolder);
            }
            
            //setup the report header information....
            transactionSummaryReportDataHolder.setInstitution(getInstitutionName());
            transactionSummaryReportDataHolder.setKemid(kemid);
            
            KEMID kemidObj = getKemid(kemid);
            
            transactionSummaryReportDataHolder.setKemidLongTitle(kemidObj.getLongTitle());
            transactionSummaryReportDataHolder.setBeginningDate(beginningDate);
            transactionSummaryReportDataHolder.setEndingDate(endingDate);
            
            //setup the report footer information...Only for Detail report option
            if (!reportOption.equalsIgnoreCase(EndowConstants.EndowmentReport.TOTAL)) {
                transactionSummaryReportDataHolder.setFooter(createFooterData(kemidObj));
            }

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
        List<String> kemids = new ArrayList<String>();
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
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String beginningDate, String endingDate, String endowmnetOption, String closedIndicator, String reportOption) {
        
        List<String> kemids = getKemidsByOtherCriteria(benefittingOrganziationCampusCodes, benefittingOrganziationChartCodes,
                benefittingOrganziationCodes, typeCodes, purposeCodes, combineGroupCodes);        
        
        // Now that we have all the kemids to be reported by the use of the other criteria, go to get the report data 
        if (kemids.size() == 0) {
            return null;
        } else {            
            return getTransactionSummaryReportsByKemidByIds(kemids, beginningDate, endingDate, endowmnetOption, closedIndicator, reportOption);
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
        
        BigDecimal transactionSecurityCostIncome = BigDecimal.ZERO;
        BigDecimal transactionSecurityCostPrincipal = BigDecimal.ZERO;
        
        //get the transaction archive data totals....
        List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
        for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                transactionSecurityCostIncome = transactionSecurityCostIncome.add(archiveSecurity.getHoldingCost());
            }
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                transactionSecurityCostPrincipal = transactionSecurityCostPrincipal.add(archiveSecurity.getHoldingCost());
            }
        }
        
        ContributionsDataHolder contributionsData = transactionSummaryReportDataHolder.new ContributionsDataHolder();
        contributionsData.setContributionsDescription(transactionArchive.getEtranObj().getName());
        
        //get the transaction archive data totals....
     //   List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
     //   for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
     //       transactionSecurityCost = transactionSecurityCost.add(archiveSecurity.getHoldingCost());
     //   }
        //income type code...
        if (transactionArchive.getEtranObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                contributionsData.setIncomeContributions(transactionSecurityCostIncome.add(contributionsData.getIncomeContributions().add(transactionArchive.getIncomeCashAmount())));
                transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(contributionsData.getIncomeContributions()));
            }
            
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                contributionsData.setPrincipalContributions(transactionSecurityCostPrincipal.add(contributionsData.getPrincipalContributions().add(transactionArchive.getPrincipalCashAmount())));
                transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(contributionsData.getPrincipalContributions()));
            }
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
        BigDecimal transactionSecurityCostIncome = BigDecimal.ZERO;
        BigDecimal transactionSecurityCostPrincipal = BigDecimal.ZERO;
        
        //get the transaction archive data totals....
        List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
        for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                transactionSecurityCostIncome = transactionSecurityCostIncome.add(archiveSecurity.getHoldingCost());
            }
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                transactionSecurityCostPrincipal = transactionSecurityCostPrincipal.add(archiveSecurity.getHoldingCost());
            }
        }
        
        ExpensesDataHolder expensesDataHolder = transactionSummaryReportDataHolder.new ExpensesDataHolder();
        expensesDataHolder.setExpensesDescription(transactionArchive.getEtranObj().getName());
        
        //get the transaction archive data totals....
     //   List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
     //   for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
     //       transactionSecurityCost = transactionSecurityCost.add(archiveSecurity.getHoldingCost());
    //    }
        
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            expensesDataHolder.setIncomeExpenses(transactionSecurityCostIncome.add(expensesDataHolder.getIncomeExpenses().add(transactionArchive.getIncomeCashAmount())));
            transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(expensesDataHolder.getIncomeExpenses()));
        }
            
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
            expensesDataHolder.setPrincipalExpenses(transactionSecurityCostPrincipal.add(expensesDataHolder.getPrincipalExpenses().add(transactionArchive.getPrincipalCashAmount())));
            transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(expensesDataHolder.getPrincipalExpenses()));
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForExpenses().add(expensesDataHolder);
    }
    
    /**
     * Cash Transfers.....
     * 
     * @param transactionSummaryReportDataHolder
     * @param transactionArchive
     */
    protected void getTransactionArchiveTotalsForCashTransfers(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder, TransactionArchive transactionArchive) {
        CashTransfersDataHolder cashTransfersDataHolder = transactionSummaryReportDataHolder.new CashTransfersDataHolder();
        cashTransfersDataHolder.setCashTransfersDescription(transactionArchive.getEtranObj().getName());
        
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            cashTransfersDataHolder.setIncomeCashTransfers(cashTransfersDataHolder.getIncomeCashTransfers().add(transactionArchive.getIncomeCashAmount()));
            transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(cashTransfersDataHolder.getIncomeCashTransfers()));
        }
            
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
            cashTransfersDataHolder.setPrincipalCashTransfers(cashTransfersDataHolder.getPrincipalCashTransfers().add(transactionArchive.getPrincipalCashAmount()));
            transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(cashTransfersDataHolder.getPrincipalCashTransfers()));
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForCashTransfers().add(cashTransfersDataHolder);
    }
    
    /**
     * Security Transfers.....
     * 
     * @param transactionSummaryReportDataHolder
     * @param transactionArchive
     */
    protected void getTransactionArchiveTotalsForSecurityTransfers(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder, TransactionArchive transactionArchive) {
        SecurityTransfersDataHolder securityTransfersDataHolder = transactionSummaryReportDataHolder.new SecurityTransfersDataHolder();
        securityTransfersDataHolder.setSecurityTransfersDescription(transactionArchive.getEtranObj().getName());
        
        BigDecimal transactionSecurityCostIncome = BigDecimal.ZERO;
        BigDecimal transactionSecurityCostPrincipal = BigDecimal.ZERO;
        
        //get the transaction archive data totals....
        List<TransactionArchiveSecurity> archiveSecurities = transactionArchive.getArchiveSecurities();
        for (TransactionArchiveSecurity archiveSecurity : archiveSecurities) {
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                transactionSecurityCostIncome = transactionSecurityCostIncome.add(archiveSecurity.getHoldingCost());
            }
            if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                transactionSecurityCostPrincipal = transactionSecurityCostPrincipal.add(archiveSecurity.getHoldingCost());
            }
        }
        
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            securityTransfersDataHolder.setIncomeSecurityTransfers(securityTransfersDataHolder.getIncomeSecurityTransfers().add(transactionSecurityCostIncome));
            transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(securityTransfersDataHolder.getIncomeSecurityTransfers()));
        }
            
        if (transactionArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
            securityTransfersDataHolder.setPrincipalSecurityTransfers(securityTransfersDataHolder.getPrincipalSecurityTransfers().add(transactionSecurityCostPrincipal));
            transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(securityTransfersDataHolder.getPrincipalSecurityTransfers()));
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForSecurityTransfers().add(securityTransfersDataHolder);
    }

    /**
     * Helper method to first retrieve all the contributions records and go over all of them
     * to combine based on same description of the contributions.  The combined records are then
     * added to the data holder.
     * 
     * @param transactionSummaryReportDataHolder
     */
    protected void combineContributionsData(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        String contributionsDescription = null;
        BigDecimal incomeContributionsAmount = BigDecimal.ZERO;
        BigDecimal PrincipalContributionsAmount = BigDecimal.ZERO;
        
        List<ContributionsDataHolder> contributionsDataList = new ArrayList<ContributionsDataHolder>();
        
        ContributionsDataHolder combinedContributionsData = transactionSummaryReportDataHolder.new ContributionsDataHolder();
        
        List<ContributionsDataHolder> contributionsData = transactionSummaryReportDataHolder.getReportGroupsForContributions();

        for (ContributionsDataHolder contributionData : contributionsData) {
            if (contributionsDescription == null) {
                contributionsDescription = contributionData.getContributionsDescription();
            }
            //same description == same etran code so combine the totals.....
            if (contributionsDescription.equals(contributionData.getContributionsDescription())) {
                incomeContributionsAmount = incomeContributionsAmount.add(contributionData.getIncomeContributions());
                PrincipalContributionsAmount = PrincipalContributionsAmount.add(contributionData.getPrincipalContributions());
            }
            else { //write out the record....
                combinedContributionsData.setContributionsDescription(contributionsDescription);
                combinedContributionsData.setIncomeContributions(incomeContributionsAmount);
                combinedContributionsData.setPrincipalContributions(PrincipalContributionsAmount);
                transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeContributionsAmount));
                transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(PrincipalContributionsAmount));
                contributionsDataList.add(combinedContributionsData);
                
                //create a new holder...
                incomeContributionsAmount = BigDecimal.ZERO;
                PrincipalContributionsAmount = BigDecimal.ZERO;
                combinedContributionsData = transactionSummaryReportDataHolder.new ContributionsDataHolder();                
                contributionsDescription = contributionData.getContributionsDescription();
                incomeContributionsAmount = incomeContributionsAmount.add(contributionData.getIncomeContributions());
                PrincipalContributionsAmount = PrincipalContributionsAmount.add(contributionData.getPrincipalContributions());
            }
        }
        
        //add the last data holder....
        combinedContributionsData.setContributionsDescription(contributionsDescription);
        combinedContributionsData.setIncomeContributions(incomeContributionsAmount);
        combinedContributionsData.setPrincipalContributions(PrincipalContributionsAmount);
        transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeContributionsAmount));
        transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(PrincipalContributionsAmount));
        contributionsDataList.add(combinedContributionsData);

        //now remove the current list of contributions and add the newly created combined list.
        transactionSummaryReportDataHolder.getReportGroupsForContributions().removeAll(contributionsData);
        transactionSummaryReportDataHolder.getReportGroupsForContributions().addAll(contributionsDataList);
    }
    
    /**
     * Helper method to first retrieve all the expenses records and go over all of them
     * to combine based on same description of the expenses.  The combined records are then
     * added to the data holder.
     * 
     * @param transactionSummaryReportDataHolder
     */
    protected void combineExpensesData(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        String expensesDescription = null;
        BigDecimal incomeExpensesAmount = BigDecimal.ZERO;
        BigDecimal PrincipalExpensesAmount = BigDecimal.ZERO;
        
        List<ExpensesDataHolder> expensesDataList = new ArrayList<ExpensesDataHolder>();
        
        ExpensesDataHolder combinedExpensesData = transactionSummaryReportDataHolder.new ExpensesDataHolder();
        
        List<ExpensesDataHolder> expensesData = transactionSummaryReportDataHolder.getReportGroupsForExpenses();

        for (ExpensesDataHolder expenseData : expensesData) {
            if (expensesDescription == null) {
                expensesDescription = expenseData.getExpensesDescription();
            }
            //same description == same etran code so combine the totals.....
            if (expensesDescription.equals(expenseData.getExpensesDescription())) {
                incomeExpensesAmount = incomeExpensesAmount.add(expenseData.getIncomeExpenses());
                PrincipalExpensesAmount = PrincipalExpensesAmount.add(expenseData.getPrincipalExpenses());
            }
            else { //write out the record....
                combinedExpensesData.setExpensesDescription(expensesDescription);
                combinedExpensesData.setIncomeExpenses(incomeExpensesAmount);
                combinedExpensesData.setPrincipalExpenses(PrincipalExpensesAmount);
                transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeExpensesAmount));
                transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(PrincipalExpensesAmount));
                expensesDataList.add(combinedExpensesData);
                
                //create a new holder...
                incomeExpensesAmount = BigDecimal.ZERO;
                PrincipalExpensesAmount = BigDecimal.ZERO;
                combinedExpensesData = transactionSummaryReportDataHolder.new ExpensesDataHolder();                
                expensesDescription = expenseData.getExpensesDescription();
                incomeExpensesAmount = incomeExpensesAmount.add(expenseData.getIncomeExpenses());
                PrincipalExpensesAmount = PrincipalExpensesAmount.add(expenseData.getPrincipalExpenses());
            }
        }
        
        //add the last data holder....
        combinedExpensesData.setExpensesDescription(expensesDescription);
        combinedExpensesData.setIncomeExpenses(incomeExpensesAmount);
        combinedExpensesData.setPrincipalExpenses(PrincipalExpensesAmount);
        transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeExpensesAmount));
        transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(PrincipalExpensesAmount));
        expensesDataList.add(combinedExpensesData);

        //now remove the current list of contributions and add the newly created combined list.
        transactionSummaryReportDataHolder.getReportGroupsForExpenses().removeAll(expensesData);
        transactionSummaryReportDataHolder.getReportGroupsForExpenses().addAll(expensesDataList);
    }

    /**
     * Helper method to first retrieve all the Cash Transfers records and go over all of them
     * to combine based on same description of the expenses.  The combined records are then
     * added to the data holder.
     * 
     * @param transactionSummaryReportDataHolder
     */
    protected void combineCashTransfersData(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        String cashTransfersDescription = null;
        BigDecimal incomeCashTransfersAmount = BigDecimal.ZERO;
        BigDecimal PrincipalCashTransfersAmount = BigDecimal.ZERO;
        
        List<CashTransfersDataHolder> cashTransfersDataList = new ArrayList<CashTransfersDataHolder>();
        
        CashTransfersDataHolder combinedCashTransfersData = transactionSummaryReportDataHolder.new CashTransfersDataHolder();
        
        List<CashTransfersDataHolder> cashTransfersData = transactionSummaryReportDataHolder.getReportGroupsForCashTransfers();

        for (CashTransfersDataHolder cashTransferData : cashTransfersData) {
            if (cashTransfersDescription == null) {
                cashTransfersDescription = cashTransferData.getCashTransfersDescription();
            }
            //same description == same etran code so combine the totals.....
            if (cashTransfersDescription.equals(cashTransferData.getCashTransfersDescription())) {
                incomeCashTransfersAmount = incomeCashTransfersAmount.add(cashTransferData.getIncomeCashTransfers());
                PrincipalCashTransfersAmount = PrincipalCashTransfersAmount.add(cashTransferData.getPrincipalCashTransfers());
            }
            else { //write out the record....
                combinedCashTransfersData.setCashTransfersDescription(cashTransfersDescription);
                combinedCashTransfersData.setIncomeCashTransfers(incomeCashTransfersAmount);
                combinedCashTransfersData.setPrincipalCashTransfers(PrincipalCashTransfersAmount);
                transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeCashTransfersAmount));
                transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(PrincipalCashTransfersAmount));
                cashTransfersDataList.add(combinedCashTransfersData);
                
                //create a new holder...
                incomeCashTransfersAmount = BigDecimal.ZERO;
                PrincipalCashTransfersAmount = BigDecimal.ZERO;
                combinedCashTransfersData = transactionSummaryReportDataHolder.new CashTransfersDataHolder();                
                cashTransfersDescription = cashTransferData.getCashTransfersDescription();
                incomeCashTransfersAmount = incomeCashTransfersAmount.add(cashTransferData.getIncomeCashTransfers());
                PrincipalCashTransfersAmount = PrincipalCashTransfersAmount.add(cashTransferData.getPrincipalCashTransfers());
            }
        }
        
        //add the last data holder....
        combinedCashTransfersData.setCashTransfersDescription(cashTransfersDescription);
        combinedCashTransfersData.setIncomeCashTransfers(incomeCashTransfersAmount);
        combinedCashTransfersData.setPrincipalCashTransfers(PrincipalCashTransfersAmount);
        transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeCashTransfersAmount));
        transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(PrincipalCashTransfersAmount));
        cashTransfersDataList.add(combinedCashTransfersData);

        //now remove the current list of contributions and add the newly created combined list.
        transactionSummaryReportDataHolder.getReportGroupsForCashTransfers().removeAll(cashTransfersData);
        transactionSummaryReportDataHolder.getReportGroupsForCashTransfers().addAll(cashTransfersDataList);
    }
    
    /**
     * Helper method to first retrieve all the Security Transfers records and go over all of them
     * to combine based on same description of the expenses.  The combined records are then
     * added to the data holder.
     * 
     * @param transactionSummaryReportDataHolder
     */
    protected void combineSecurityTransfersData(TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        String securityTransfersDescription = null;
        BigDecimal incomeSecurityTransfersAmount = BigDecimal.ZERO;
        BigDecimal principalSecurityTransfersAmount = BigDecimal.ZERO;
        
        List<SecurityTransfersDataHolder> securityTransfersDataList = new ArrayList<SecurityTransfersDataHolder>();
        
        SecurityTransfersDataHolder combinedSecurityTransfersData = transactionSummaryReportDataHolder.new SecurityTransfersDataHolder();
        
        List<SecurityTransfersDataHolder> securityTransfersData = transactionSummaryReportDataHolder.getReportGroupsForSecurityTransfers();

        for (SecurityTransfersDataHolder securityTransferData : securityTransfersData) {
            if (securityTransfersDescription == null) {
                securityTransfersDescription = securityTransferData.getSecurityTransfersDescription();
            }
            //same description == same etran code so combine the totals.....
            if (securityTransfersDescription.equals(securityTransferData.getSecurityTransfersDescription())) {
                incomeSecurityTransfersAmount = incomeSecurityTransfersAmount.add(securityTransferData.getIncomeSecurityTransfers());
                principalSecurityTransfersAmount = principalSecurityTransfersAmount.add(securityTransferData.getPrincipalSecurityTransfers());
            }
            else { //write out the record....
                combinedSecurityTransfersData.setSecurityTransfersDescription(securityTransfersDescription);
                combinedSecurityTransfersData.setIncomeSecurityTransfers(incomeSecurityTransfersAmount);
                combinedSecurityTransfersData.setPrincipalSecurityTransfers(principalSecurityTransfersAmount);
                transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeSecurityTransfersAmount));
                transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(principalSecurityTransfersAmount));
                securityTransfersDataList.add(combinedSecurityTransfersData);
                
                //create a new holder...
                incomeSecurityTransfersAmount = BigDecimal.ZERO;
                principalSecurityTransfersAmount = BigDecimal.ZERO;
                combinedSecurityTransfersData = transactionSummaryReportDataHolder.new SecurityTransfersDataHolder();                
                securityTransfersDescription = securityTransferData.getSecurityTransfersDescription();
                incomeSecurityTransfersAmount = incomeSecurityTransfersAmount.add(securityTransferData.getIncomeSecurityTransfers());
                principalSecurityTransfersAmount = principalSecurityTransfersAmount.add(securityTransferData.getPrincipalSecurityTransfers());
            }
        }
        
        //add the last data holder....
        combinedSecurityTransfersData.setSecurityTransfersDescription(securityTransfersDescription);
        combinedSecurityTransfersData.setIncomeSecurityTransfers(incomeSecurityTransfersAmount);
        combinedSecurityTransfersData.setPrincipalSecurityTransfers(principalSecurityTransfersAmount);
        transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().subtract(incomeSecurityTransfersAmount));
        transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().subtract(principalSecurityTransfersAmount));
        securityTransfersDataList.add(combinedSecurityTransfersData);

        //now remove the current list of contributions and add the newly created combined list.
        transactionSummaryReportDataHolder.getReportGroupsForSecurityTransfers().removeAll(securityTransfersData);
        transactionSummaryReportDataHolder.getReportGroupsForSecurityTransfers().addAll(securityTransfersDataList);
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
