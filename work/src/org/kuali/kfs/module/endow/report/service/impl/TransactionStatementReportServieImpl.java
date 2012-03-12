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

import org.kuali.kfs.module.endow.EndowConstants.TransactionSubTypeCode;
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
import org.kuali.kfs.module.endow.report.util.TransactionStatementReportDataHolder.TransactionArchiveInfo;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionStatementReportServieImpl extends EndowmentReportServiceImpl implements TransactionStatementReportService {

    protected TransactionArchiveDao transactionArchiveDao;
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportForAllKemids(java.lang.String)
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportForAllKemids(String beginningDate, String endingDate, String endowmentOption, String closedIndicator) {
        return getTransactionStatementReportsByKemidByIds(null, beginningDate, endingDate, endowmentOption, closedIndicator);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByKemidByIds(java.util.List, java.lang.String)
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportsByKemidByIds(List<String> kemids, String beginningDate, String endingDate, String endowmentOption, String closedIndicator) {
        
        Date beginDate = convertStringToDate(beginningDate);
        Date endDate = convertStringToDate(endingDate);    
        MonthEndDate beginningMED = getPreviousMonthEndDate(convertStringToDate(beginningDate));
        MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));
        
        if (ObjectUtils.isNull(beginDate) || ObjectUtils.isNull(beginDate)) {
            return null;
        }
        // get objects used in common
        List<String> kemidsSelected = kemidDao.getKemidsByAttributeWithEndowmentOption(EndowPropertyConstants.KEMID, kemids, endowmentOption, closedIndicator);
        if (kemidsSelected == null || kemidsSelected.isEmpty()) {
            return null;
        }
        
        //remove any kemids that do not exist in the END_HIST_CSH_T
        kemids = getKemidsInHistoryCash(kemidsSelected, beginningDate, endingDate);
        if (kemids == null) {
            return null;
        }
        
        List<TransactionStatementReportDataHolder> transactionStatementReportList = new ArrayList<TransactionStatementReportDataHolder>();

        for (String kemid : kemids) {
            
            // get related objects
            KEMID kemidOjb = getKemid(kemid);            
            KemidHistoricalCash beginningHistoryCash = getKemidHistoricalCash(kemid, beginningMED.getMonthEndDateId());
            KemidHistoricalCash endingHistoryCash = getKemidHistoricalCash(kemid, endingMED.getMonthEndDateId());
            
            // get all the cash transactions meeting the given conditions
            List<TransactionArchive> transactionArchiveList = transactionArchiveDao.getTransactionArchiveByKemidsAndPostedDate(kemid, endowmentOption, beginDate, endDate, closedIndicator, TransactionSubTypeCode.CASH);
            if (transactionArchiveList == null && transactionArchiveList == null) {
                continue;
            }
            
            TransactionStatementReportDataHolder dataHolder = new TransactionStatementReportDataHolder();
            
            // the header info            
            dataHolder.setInstitution(getInstitutionName());
            dataHolder.setKemid(kemid);
            dataHolder.setKemidLongTitle(kemidOjb.getLongTitle());
            dataHolder.setBeginningDate(beginningDate);
            dataHolder.setEndingDate(endingDate);
            if (beginningHistoryCash == null) {
                dataHolder.setBeginningIncomeCash(BigDecimal.ZERO);
                dataHolder.setBeginningPrincipalCash(BigDecimal.ZERO);                
            } else {
                dataHolder.setBeginningIncomeCash(beginningHistoryCash.getHistoricalIncomeCash().bigDecimalValue());
                dataHolder.setBeginningPrincipalCash(beginningHistoryCash.getHistoricalPrincipalCash().bigDecimalValue());
            }
            if (endingHistoryCash == null) {
                dataHolder.setEndingIncomeCash(BigDecimal.ZERO);
                dataHolder.setEndingPrincipalCash(BigDecimal.ZERO);
            } else {
                dataHolder.setEndingIncomeCash(endingHistoryCash.getHistoricalIncomeCash().bigDecimalValue());
                dataHolder.setEndingPrincipalCash(endingHistoryCash.getHistoricalPrincipalCash().bigDecimalValue());
            }
            
            // populate all transaction info into the data holder
            for (TransactionArchive transactionArchive : transactionArchiveList) {
                
                TransactionArchiveSecurity transactionArchiveSecurity = getTransactionArchiveSecurity(transactionArchive);
                Security security = getSecurity(transactionArchive);
                EndowmentTransactionCode endowmentTransactionCode = getEndowmentTransactionCode(transactionArchive.getEtranCode()); 
                
                // create TransactionArchiveInfo and add it to the list
                TransactionArchiveInfo transactionArchiveInfo = dataHolder.createTransactionArchiveInfo(); 
                
                // body info            
                transactionArchiveInfo.setPostedDate(convertDateToString(transactionArchive.getPostedDate()));
                transactionArchiveInfo.setDocumentName(transactionArchive.getTypeCode());
                transactionArchiveInfo.setEtranCode(transactionArchive.getEtranCode());
                transactionArchiveInfo.setTransactionDesc(transactionArchive.getDescription());
                
                if (ObjectUtils.isNotNull(security)) {
                    transactionArchiveInfo.setTransactionSecurity(security.getDescription());
                }
                if (ObjectUtils.isNotNull(transactionArchiveSecurity)) {
                    transactionArchiveInfo.setTransactionSecurityUnits(transactionArchiveSecurity.getUnitsHeld());
                    transactionArchiveInfo.setTransactionSecurityUnitValue(transactionArchiveSecurity.getUnitValue());
                }                
                transactionArchiveInfo.setTransactionIncomeCash(transactionArchive.getIncomeCashAmount());
                transactionArchiveInfo.setTransactionPrincipalCash(transactionArchive.getPrincipalCashAmount());
                
                if (ObjectUtils.isNotNull(endowmentTransactionCode)) {
                    transactionArchiveInfo.setEtranCodeDesc(endowmentTransactionCode.getName());
                }
            }
            
            // add footer data
            dataHolder.setFooter(createFooterData(kemidOjb));
            
            // add the new data holder
            transactionStatementReportList.add(dataHolder);
        }
        
        return transactionStatementReportList;
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TransactionStatementReportService#getTransactionStatementReportsByOtherCriteria(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String)
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String beginningDate, String endingDate, String endowmnetOption, String closedIndicator) {
        
        List<String> kemids = getKemidsByOtherCriteria(benefittingOrganziationCampusCodes, benefittingOrganziationChartCodes,
                benefittingOrganziationCodes, typeCodes, purposeCodes, combineGroupCodes);        
        
        // Now that we have all the kemids to be reported by the use of the other criteria, go to get the report data 
        if (kemids.size() == 0) {
            return null;
        } else {            
            return getTransactionStatementReportsByKemidByIds(kemids, beginningDate, endingDate, endowmnetOption, closedIndicator);
        }
    }

    /**
     * Gets a Kemid object
     * 
     * @param kemid
     * @return
     */
    protected KEMID getKemid(String kemid) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.KEMID, kemid);
        return (KEMID) businessObjectService.findByPrimaryKey(KEMID.class, primaryKeys);
    }
    
    /**
     * Gets a transaction archive object
     * 
     * @param transactionArchive
     * @return
     */
    protected TransactionArchiveSecurity  getTransactionArchiveSecurity(TransactionArchive transactionArchive) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionArchive.getDocumentNumber());
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionArchive.getLineNumber());
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionArchive.getLineTypeCode());
        return (TransactionArchiveSecurity) businessObjectService.findByPrimaryKey(TransactionArchiveSecurity.class, primaryKeys);        
    }
    
    /**
     * Gets a security object
     * 
     * @param transactionArchive
     * @return
     */
    protected Security getSecurity(TransactionArchive transactionArchive) {
        TransactionArchiveSecurity transactionArchiveSecurity = getTransactionArchiveSecurity(transactionArchive);
        if (transactionArchiveSecurity != null) {
            return transactionArchiveSecurity.getSecurity();
        } else {
            return null;
        }        
    }
    
    /**
     * Gets an transaction code object
     * 
     * @param etranCcode
     * @return
     */
    protected EndowmentTransactionCode getEndowmentTransactionCode(String etranCcode) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, etranCcode);
        return (EndowmentTransactionCode) businessObjectService.findByPrimaryKey(EndowmentTransactionCode.class, primaryKeys);
    }
    
    /**
     * Gets a historical cash object
     * 
     * @param kemid
     * @param medId
     * @return
     */
    protected KemidHistoricalCash getKemidHistoricalCash(String kemid, KualiInteger medId) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, medId);
        return (KemidHistoricalCash) businessObjectService.findByPrimaryKey(KemidHistoricalCash.class, primaryKeys);
    }
 
    /**
     * Gets the previous month end date object
     * @see org.kuali.kfs.module.endow.report.service.impl.EndowmentReportServiceImpl#getPreviousMonthEndDate(java.sql.Date)
     */
    protected MonthEndDate getPreviousMonthEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return getMonthEndDate(new java.sql.Date(calendar.getTimeInMillis()));
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.impl.EndowmentReportServiceImpl#getMonthEndDate(java.sql.Date)
     */
    protected MonthEndDate getMonthEndDate(Date date) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.MONTH_END_DATE, date);
        return (MonthEndDate) businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeys);
    }
    
    /**
     * Converts string to date
     * @see org.kuali.kfs.module.endow.report.service.impl.EndowmentReportServiceImpl#convertStringToDate(java.lang.String)
     */
    protected Date convertStringToDate(String stringDate) {        
        Date date = null;
        try {
            date = dateTimeService.convertToSqlDate(stringDate);
        } catch (ParseException e) {
            return null;
        }        
        return date;
    }
    
    /** 
     * Convert date to string
     * 
     * @param date
     * @return
     */
    protected String convertDateToString(Date date) {        
        return dateTimeService.toDateString(date);
    }

    /**
     * Sets transaction archive dao
     * 
     * @param transactionArchiveDao
     */
    public void setTransactionArchiveDao(TransactionArchiveDao transactionArchiveDao) {
        this.transactionArchiveDao = transactionArchiveDao;
    }
    
}
