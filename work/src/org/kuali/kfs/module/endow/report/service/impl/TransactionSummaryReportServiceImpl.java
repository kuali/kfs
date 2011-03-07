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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.report.service.TransactionSummaryReportService;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder;
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
        
        List<TransactionSummaryReportDataHolder> transactionStatementReportList = new ArrayList<TransactionSummaryReportDataHolder>();

        Date beginDate = convertStringToDate(beginningDate);
        Date endDate = convertStringToDate(endingDate);        
        if (ObjectUtils.isNull(beginDate) || ObjectUtils.isNull(beginDate)) {
            return null;
        }
        
        kemids = getKemidsInHistoryCash(kemids, beginningDate, endingDate);
        
        MonthEndDate beginningMED = getPreviousMonthEndDate(convertStringToDate(beginningDate));
        MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));
        
        for (String kemid : kemids) {
            TransactionSummaryReportDataHolder transactionSummaryReportDataHolder = new TransactionSummaryReportDataHolder();
            BigDecimal principalBeginningMarketValue = BigDecimal.ZERO;
            
            List<HoldingHistory> holdingHistoryList = holdingHistoryDao.getHoldingHistoryByKemid(kemid);
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
        }
        
        List<TransactionArchive> transactionArchiveRecords = transactionArchiveDao.getTransactionArchiveByKemidsAndPostedDate(kemids, endowmentOption, beginDate, endDate, closedIndicator);
        if (transactionArchiveRecords == null) {
            return null;
        }
        
        for (TransactionArchive transactionArchive : transactionArchiveRecords) {
            
            TransactionSummaryReportDataHolder transactionSummaryReport = new TransactionSummaryReportDataHolder();
            
            // get related objects
            KEMID kemid = getKemid(transactionArchive.getKemid());
            TransactionArchiveSecurity transactionArchiveSecurity = getTransactionArchiveSecurity(transactionArchive);
            Security security = getSecurity(transactionArchive);
            EndowmentTransactionCode endowmentTransactionCode = getEndowmentTransactionCode(transactionArchive.getEtranCode());
            KemidHistoricalCash beginningHistoryCash = getKemidHistoricalCash(transactionArchive.getKemid(), beginningMED.getMonthEndDateId());
            KemidHistoricalCash endingHistoryCash = getKemidHistoricalCash(transactionArchive.getKemid(), endingMED.getMonthEndDateId());
            
            if (beginningHistoryCash == null && endingHistoryCash == null) {
                continue;
            }
            
            // the header info            
            transactionSummaryReport.setInstitution(getInstitutionName());
            transactionSummaryReport.setKemid(transactionArchive.getKemid());
            transactionSummaryReport.setKemidLongTitle(kemid.getLongTitle());
            transactionSummaryReport.setBeginningDate(beginningDate);
            transactionSummaryReport.setEndingDate(endingDate);
            
            // body info            
                                    
            // add this new one
            transactionStatementReportList.add(transactionSummaryReport);
        }
        
        return transactionStatementReportList;
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

    protected KEMID getKemid(String kemid) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.KEMID, kemid);
        return (KEMID) businessObjectService.findByPrimaryKey(KEMID.class, primaryKeys);
    }
    
    protected TransactionArchiveSecurity  getTransactionArchiveSecurity(TransactionArchive transactionArchive) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionArchive.getDocumentNumber());
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionArchive.getLineNumber());
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionArchive.getLineTypeCode());
        return (TransactionArchiveSecurity) businessObjectService.findByPrimaryKey(TransactionArchiveSecurity.class, primaryKeys);        
    }
    
    protected Security getSecurity(TransactionArchive transactionArchive) {
        TransactionArchiveSecurity transactionArchiveSecurity = getTransactionArchiveSecurity(transactionArchive);
        return transactionArchiveSecurity.getSecurity();        
    }
    
    protected EndowmentTransactionCode getEndowmentTransactionCode(String etranCcode) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, etranCcode);
        return (EndowmentTransactionCode) businessObjectService.findByPrimaryKey(EndowmentTransactionCode.class, primaryKeys);
    }
    
    protected KemidHistoricalCash getKemidHistoricalCash(String kemid, KualiInteger medId) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, medId);
        return (KemidHistoricalCash) businessObjectService.findByPrimaryKey(KemidHistoricalCash.class, primaryKeys);
    }
 
    protected MonthEndDate getPreviousMonthEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return getMonthEndDate(new java.sql.Date(calendar.getTimeInMillis()));
    }
    
    protected MonthEndDate getMonthEndDate(Date date) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.MONTH_END_DATE, date);
        return (MonthEndDate) businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeys);
    }
    
    protected Date convertStringToDate(String stringDate) {        
        Date date = null;
        try {
            date = dateTimeService.convertToSqlDate(stringDate);
        } catch (ParseException e) {
        }        
        return date;
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
