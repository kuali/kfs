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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.report.service.TransactionStatementReportService;
import org.kuali.kfs.module.endow.report.util.TransactionStatementReportDataHolder;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionStatementReportServieImpl extends EndowmentReportServiceImpl implements TransactionStatementReportService {

    protected DateTimeService dateTimeService;
    protected TransactionArchiveDao transactionArchiveDao;
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportForAllKemids(java.lang.String)
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportForAllKemids(String beginningDate, String endingDate, String endowmentOption) {
        return getTransactionStatementReportsByKemidByIds(new ArrayList<String>(), beginningDate, endingDate, endowmentOption);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByKemidByIds(java.util.List, java.lang.String)
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportsByKemidByIds(List<String> kemids, String beginningDate, String endingDate, String endowmentOption) {
        
        List<TransactionStatementReportDataHolder> transactionStatementReportList = new ArrayList<TransactionStatementReportDataHolder>();

        Date beginDate = convertStringToDate(beginningDate);
        Date endDate = convertStringToDate(endingDate);        
        if (ObjectUtils.isNull(beginDate) || ObjectUtils.isNull(beginDate)) {
            return null;
        }
        
        List<TransactionArchive> transactionArchiveRecords = transactionArchiveDao.getTransactionArchiveByKemidsAndPostedDate(kemids, endowmentOption, beginDate, endDate);
        if (transactionArchiveRecords == null) {
            return null;
        }
        
        for (TransactionArchive transactionArchive : transactionArchiveRecords) {
            
            TransactionStatementReportDataHolder transactionStatementReport = new TransactionStatementReportDataHolder();
            
            // get related objects
            KEMID kemid = getKemid(transactionArchive.getKemid());
            TransactionArchiveSecurity transactionArchiveSecurity = getTransactionArchiveSecurity(transactionArchive);
            Security security = getSecurity(transactionArchive);
            EndowmentTransactionCode endowmentTransactionCode = getEndowmentTransactionCode(transactionArchive.getEtranCode());
            MonthEndDate beginningMED = getPreviousMonthEndDate(convertStringToDate(beginningDate));
            MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));
            KemidHistoricalCash beginningHistoryCash = getKemidHistoricalCash(transactionArchive.getKemid(), beginningMED.getMonthEndDateId());
            KemidHistoricalCash endingHistoryCash = getKemidHistoricalCash(transactionArchive.getKemid(), endingMED.getMonthEndDateId());
            
            // the header info            
            transactionStatementReport.setInstitution(getInstitutionName());
            transactionStatementReport.setKemid(transactionArchive.getKemid());
            transactionStatementReport.setKemidLongTitle(kemid.getLongTitle());
            transactionStatementReport.setBeginningDate(beginningDate);
            transactionStatementReport.setEndingDate(endingDate);
            
            // body info            
            if (ObjectUtils.isNotNull(transactionArchive)) {
                transactionStatementReport.setPostedDate(convertDateToString(transactionArchive.getPostedDate()));
                transactionStatementReport.setDocumentName(transactionArchive.getTypeCode());
                transactionStatementReport.setEtranCode(transactionArchive.getEtranCode());
                transactionStatementReport.setTransactionDesc(transactionArchive.getDescription());
                
                transactionStatementReport.setIncomeAmount(transactionArchive.getIncomeCashAmount());
                transactionStatementReport.setPrincipalAmount(transactionArchive.getPrincipalCashAmount());
            } 
            
            if (ObjectUtils.isNotNull(security)) {
                transactionStatementReport.setTransactionSecurity(security.getDescription());
            } 
            
            if (ObjectUtils.isNotNull(transactionArchiveSecurity)) {
                transactionStatementReport.setTransactionSecurityUnits(transactionArchiveSecurity.getUnitsHeld());
                transactionStatementReport.setTransactionSecurityUnitValue(transactionArchiveSecurity.getUnitValue());
            }
            
            if (ObjectUtils.isNotNull(endowmentTransactionCode)) {
                transactionStatementReport.setEtranCodeDesc(endowmentTransactionCode.getName());
            }
            
            if (ObjectUtils.isNotNull(beginningHistoryCash)) {
                transactionStatementReport.setHistoryIncomeCash1(beginningHistoryCash.getHistoricalIncomeCash().bigDecimalValue());
                transactionStatementReport.setHistoryPrincipalCash1(beginningHistoryCash.getHistoricalPrincipalCash().bigDecimalValue());
            } else {
                transactionStatementReport.setHistoryIncomeCash1(BigDecimal.ZERO);
                transactionStatementReport.setHistoryPrincipalCash1(BigDecimal.ZERO);
            }
            if (ObjectUtils.isNotNull(endingHistoryCash)) {
                transactionStatementReport.setHistoryIncomeCash2(endingHistoryCash.getHistoricalIncomeCash().bigDecimalValue());
                transactionStatementReport.setHistoryPrincipalCash2(endingHistoryCash.getHistoricalPrincipalCash().bigDecimalValue());                
            } else {
                transactionStatementReport.setHistoryIncomeCash2(BigDecimal.ZERO);
                transactionStatementReport.setHistoryPrincipalCash2(BigDecimal.ZERO);                
            }
                                    
            // add this new one
            transactionStatementReportList.add(transactionStatementReport);
        }
        
        return transactionStatementReportList;
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByOtherCriteria(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String)
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String beginningDate, String endingDate, String endowmnetOption) {
      
        // Some of kemids returned may be duplicated. Set will remove the duplicated kemids
        Set<String> kemids = new HashSet<String>();
        
        // 4.1.9 - If any of the non-required criteria are left blank, ignore it as a criterion
        // 4.1.8 - Collects all related kemids regardless of the closed status of the KEMID
        
        if (ObjectUtils.isNotNull(benefittingOrganziationCampusCodes) && !benefittingOrganziationCampusCodes.isEmpty()) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByCampusCode(benefittingOrganziationCampusCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationChartCodes) && !benefittingOrganziationChartCodes.isEmpty()) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_CHRT_CD, benefittingOrganziationChartCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationCodes) && !benefittingOrganziationCodes.isEmpty()) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_ORG_CD, benefittingOrganziationCodes));
        }
        if (ObjectUtils.isNotNull(typeCodes) && !typeCodes.isEmpty()) {        
            kemids.addAll(kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_TYPE_CODE, typeCodes));
        }
        if (ObjectUtils.isNotNull(purposeCodes) && !purposeCodes.isEmpty()) {
            kemids.addAll(kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_PRPS_CD, purposeCodes));
        }
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {        
            kemids.addAll(kemidReportGroupDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCodes));
        }
        
        // Now that we have all the kemids to be reported by the use of the other criteria, go to get the report data 
        if (kemids.size() == 0) {
            return null;
        } else {            
            return getTransactionStatementReportsByKemidByIds(new ArrayList<String>(kemids), beginningDate, endingDate, endowmnetOption);
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
    
}
