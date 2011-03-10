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

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.report.service.AssetStatementReportService;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder;
import org.kuali.kfs.module.endow.report.util.EndowmentReportFooterDataHolder;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder.ReportGroupData;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetStatementReportServiceImpl extends EndowmentReportServiceImpl implements AssetStatementReportService {

    protected DateTimeService dateTimeService;
    protected KEMService kemService;
    protected HoldingHistoryDao holdingHistoryDao;
    //protected KemidHistoricalCashDao kemidHistoricalCashDao;
        
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportForAllKemids(java.lang.String)
     */
    public List<AssetStatementReportDataHolder> getAssetStatementReportForAllKemids(String monthEndDate, String endowmentOption, String reportOption, String closedIndicator) {
        return getAssetStatementReportsByKemidByIds(null, monthEndDate, endowmentOption, reportOption, closedIndicator);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.AssetStatementReportService#getAssetStatementReportsByKemidByIds(java.util.List, java.lang.String)
     */
    public List<AssetStatementReportDataHolder> getAssetStatementReportsByKemidByIds(List<String> kemids, String monthEndDate, String endowmentOption, String reportOption, String closedIndicator) {
        
        List<AssetStatementReportDataHolder> assetStatementReportList = new ArrayList<AssetStatementReportDataHolder>();

        Date date = convertStringToDate(monthEndDate);
        if (ObjectUtils.isNull(date)) {
            return null;
        }

        // get objects used in common
        List<String> kemidsSelcted = kemidDao.getKemidsByAttributeWithEndowmentOption(EndowPropertyConstants.KEMID, kemids, endowmentOption, closedIndicator);
        // filter kemids that do not exist in historical cash
        //kemidsSelcted = getKemidsInHistoryCash(kemidsSelcted, monthEndDate);
        //List<KEMID> kemidRecords = kemidDao.getKemidRecordsByIds(kemids, endowmentOption, closedIndicator);        
        MonthEndDate endingMED = getMonthEndDate(convertStringToDate(monthEndDate));
        KualiInteger medId = endingMED.getMonthEndDateId();
        List<KemidHistoricalCash> historyCashRecords = getKemidHistoricalCashRecords(kemidsSelcted, medId);
        // For Total report
        //BigDecimal totalHistoryIncomeCash = getTotalHistoryCash(historyCashRecords, IncomePrincipalIndicator.INCOME);
        //BigDecimal totalHostoryPrincipalCash = getTotalHistoryCash(historyCashRecords, IncomePrincipalIndicator.PRINCIPAL);     
        
        if ("Y".equalsIgnoreCase(endowmentOption)) {
            // for endowment
            for (KemidHistoricalCash historyCash : historyCashRecords) {
                
                AssetStatementReportDataHolder assetStatementReport = new AssetStatementReportDataHolder();
                
                // get related objects
                //KemidHistoricalCash historyCash = getKemidHistoricalCash(kemidOjb.getKemid(), medId);
                //if (historyCash == null) continue;
                KEMID kemidOjb = getKemid(historyCash.getKemid());
                List<HoldingHistory> holdingHistoryRecordsForIncome = holdingHistoryDao.getHoldingHistoryByKemidIdAndMonthEndIdAndIpInd(historyCash.getKemid(), medId, IncomePrincipalIndicator.INCOME);
                List<HoldingHistory> holdingHistoryRecordsForPrincipal = holdingHistoryDao.getHoldingHistoryByKemidIdAndMonthEndIdAndIpInd(historyCash.getKemid(), medId, IncomePrincipalIndicator.PRINCIPAL);
                
                // create a data holder
                // work only with the kemids that exist in holding history
                if ((holdingHistoryRecordsForIncome != null && !holdingHistoryRecordsForIncome.isEmpty()) || (holdingHistoryRecordsForPrincipal != null && !holdingHistoryRecordsForPrincipal.isEmpty())) {
                    AssetStatementReportDataHolder dataHolder = new AssetStatementReportDataHolder();
                    dataHolder.setInstitution(getInstitutionName());
                    dataHolder.setKemid(historyCash.getKemid());
                    dataHolder.setKemidLongTitle(kemidOjb.getLongTitle());
                    dataHolder.setMonthEndDate(monthEndDate);
                    
                    dataHolder.setHistoryIncomeCash(historyCash.getHistoricalIncomeCash().bigDecimalValue());
                    dataHolder.setHistoryPrincipalCash(historyCash.getHistoricalPrincipalCash().bigDecimalValue());
                    
                    // for income
                    if (holdingHistoryRecordsForIncome != null) {
                        for (HoldingHistory holdingHistory : holdingHistoryRecordsForIncome) {
                            Security security = getSecurityById(holdingHistory.getSecurityId());
                            SecurityReportingGroup securityReportingGroup = getSecurityReportingGroupBySecurityId(holdingHistory.getSecurityId());
                            ReportGroupData rgIncome = dataHolder.createReportGroupData(securityReportingGroup, security, IncomePrincipalIndicator.INCOME);
        
                            rgIncome.addSumOfUnits(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_UNITS, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.INCOME));
                            rgIncome.addSumOfMarketValue(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_MARKET_VALUE, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.INCOME));
                            rgIncome.addSumOfEstimatedIncome(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.INCOME));
                            rgIncome.addSumOfRemainderOfFYEstimated(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_REMAIDER_OF_FY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.INCOME));
                            rgIncome.addSumOfNextFYEstimatedIncome(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_NEXT_FY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.INCOME));
                        }
                    }
                    
                    // for principal
                    if (holdingHistoryRecordsForPrincipal != null) {
                        for (HoldingHistory holdingHistory : holdingHistoryRecordsForPrincipal) {
                            Security security = getSecurityById(holdingHistory.getSecurityId());
                            SecurityReportingGroup securityReportingGroup = getSecurityReportingGroupBySecurityId(holdingHistory.getSecurityId());
                            ReportGroupData rgPrincipal = dataHolder.createReportGroupData(securityReportingGroup, security, IncomePrincipalIndicator.PRINCIPAL);
                        
                            rgPrincipal.addSumOfUnits(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_UNITS, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.PRINCIPAL));
                            rgPrincipal.addSumOfMarketValue(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_MARKET_VALUE, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.PRINCIPAL));
                            rgPrincipal.addSumOfEstimatedIncome(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.PRINCIPAL));
                            rgPrincipal.addSumOfRemainderOfFYEstimated(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_REMAIDER_OF_FY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.PRINCIPAL));
                            rgPrincipal.addSumOfNextFYEstimatedIncome(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_NEXT_FY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), IncomePrincipalIndicator.PRINCIPAL));                
                        }
                    }
                    
                    // TODO: need beneffiting organization info 
                    // add footer data
                    if ("D".equalsIgnoreCase(reportOption)) {
                        EndowmentReportFooterDataHolder footerDataHolder = new EndowmentReportFooterDataHolder();
                        footerDataHolder.setReference(kemidOjb.getKemid());
                        footerDataHolder.setEstablishedDate(kemidOjb.getDateEstablished().toString());
                        footerDataHolder.setKemidType(kemidOjb.getTypeCode());
                        footerDataHolder.setKemidPurpose(kemidOjb.getPurposeCode());
                        footerDataHolder.setReportRunDate(kemService.getCurrentDate().toString());
                        footerDataHolder.setCampusName("UMD");
                        footerDataHolder.setChartName("BL");
                        footerDataHolder.setOrganizationName("");
                        footerDataHolder.setBenefittingPercent("");
                        dataHolder.setFooter(footerDataHolder);
                    }
                    
                    // add this new one
                    assetStatementReportList.add(dataHolder);
                }
            }
            
        } else if ("N".equalsIgnoreCase(endowmentOption)) {
            // for non-endowed
            for (KemidHistoricalCash historyCash : historyCashRecords) {
                
                AssetStatementReportDataHolder assetStatementReport = new AssetStatementReportDataHolder();
                
                // get related objects
                //KemidHistoricalCash historyCash = getKemidHistoricalCash(kemidOjb.getKemid(), medId);
                //if (historyCash == null) continue;
                KEMID kemidOjb = getKemid(historyCash.getKemid());
                List<HoldingHistory> holdingHistoryRecords = holdingHistoryDao.getHoldingHistoryByKemidIdAndMonthEndIdAndIpInd(historyCash.getKemid(), medId, "");
                
                // work only with the kemids that exist in holding history
                if (holdingHistoryRecords != null && !holdingHistoryRecords.isEmpty()) {
                    // create a data holder
                    AssetStatementReportDataHolder dataHolder = new AssetStatementReportDataHolder();
                    dataHolder.setInstitution(getInstitutionName());
                    dataHolder.setKemid(historyCash.getKemid());
                    dataHolder.setKemidLongTitle(kemidOjb.getLongTitle());
                    dataHolder.setMonthEndDate(monthEndDate);
                    
                    dataHolder.setHistoryIncomeCash(historyCash.getHistoricalIncomeCash().bigDecimalValue());
                    dataHolder.setHistoryPrincipalCash(historyCash.getHistoricalPrincipalCash().bigDecimalValue());
                    
                    // use income for non-endowed
                    for (HoldingHistory holdingHistory : holdingHistoryRecords) {
                        Security security = getSecurityById(holdingHistory.getSecurityId());
                        SecurityReportingGroup securityReportingGroup = getSecurityReportingGroupBySecurityId(holdingHistory.getSecurityId());
                        ReportGroupData rgIncome = dataHolder.createReportGroupData(securityReportingGroup, security, IncomePrincipalIndicator.INCOME);
    
                        rgIncome.addSumOfUnits(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_UNITS, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), ""));
                        rgIncome.addSumOfMarketValue(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_MARKET_VALUE, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), ""));
                        rgIncome.addSumOfEstimatedIncome(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), ""));
                        rgIncome.addSumOfRemainderOfFYEstimated(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_REMAIDER_OF_FY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), ""));
                        rgIncome.addSumOfNextFYEstimatedIncome(holdingHistoryDao.getSumOfHoldginHistoryAttribute(EndowPropertyConstants.HOLDING_HISTORY_NEXT_FY_ESTIMATED_INCOME, kemidOjb.getKemid(), medId, holdingHistory.getSecurityId(), ""));
                    }
                    
                    // TODO: need beneffiting organization info 
                    // add footer data
                    if ("D".equalsIgnoreCase(reportOption)) {
                        EndowmentReportFooterDataHolder footerDataHolder = new EndowmentReportFooterDataHolder();
                        footerDataHolder.setReference(kemidOjb.getKemid());
                        footerDataHolder.setEstablishedDate(kemidOjb.getDateEstablished().toString());
                        footerDataHolder.setKemidType(kemidOjb.getTypeCode());
                        footerDataHolder.setKemidPurpose(kemidOjb.getPurposeCode());
                        footerDataHolder.setReportRunDate(kemService.getCurrentDate().toString());
                        footerDataHolder.setCampusName("UMD");
                        footerDataHolder.setChartName("BL");
                        footerDataHolder.setOrganizationName("");
                        footerDataHolder.setBenefittingPercent("");
                        dataHolder.setFooter(footerDataHolder);
                    }
                    
                    // add this new one
                    assetStatementReportList.add(dataHolder);
                }
            }    
        } 
        
        return assetStatementReportList;      
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.AssetStatementReportService#getAssetStatementReportsByOtherCriteria(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String)
     */
    public List<AssetStatementReportDataHolder> getAssetStatementReportsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String monthEndDate, String endowmnetOption, 
            String reportOption, String closedIndicator) {
        
        List<String> kemids = getKemidsByOtherCriteria(benefittingOrganziationCampusCodes, benefittingOrganziationChartCodes,
                benefittingOrganziationCodes, typeCodes, purposeCodes, combineGroupCodes);         
        
        // Now that we have all the kemids to be reported by the use of the other criteria, go to get the report data 
        if (kemids.size() == 0) {
            return null;
        } else {            
            return getAssetStatementReportsByKemidByIds(kemids, monthEndDate, endowmnetOption, reportOption, closedIndicator);
        }
    }

    protected SecurityReportingGroup getSecurityReportingGroupBySecurityId(String securityId) {

        // get Security
        Security security = getSecurityById(securityId);
        // get class code
        ClassCode classCode = getClassCodeById(security.getSecurityClassCode());
        // get security report group
        SecurityReportingGroup securityReportingGroup = getSecurityReportingGroup(classCode.getSecurityReportingGrp());
        
        return securityReportingGroup;        
    }
    
    protected Security getSecurityById(String securityId) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.SECURITY_ID, securityId);
        return (Security) businessObjectService.findByPrimaryKey(Security.class, primaryKeys);
    }
    
    protected ClassCode getClassCodeById(String classCode) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, classCode);
        return (ClassCode) businessObjectService.findByPrimaryKey(ClassCode.class, primaryKeys);
    }
    
    protected SecurityReportingGroup getSecurityReportingGroup(String reportGroup) {
        Map<String,String> primaryKeys = new HashMap<String,String>();
        primaryKeys.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, reportGroup);
        return (SecurityReportingGroup) businessObjectService.findByPrimaryKey(SecurityReportingGroup.class, primaryKeys);

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
    
    protected List<KemidHistoricalCash> getKemidHistoricalCashRecords(List<String> kemids, KualiInteger medId) {
        return kemidHistoricalCashDao.getHistoricalCashRecords(kemids, medId);
    }
    
    protected BigDecimal getTotalHistoryCash(List<KemidHistoricalCash> historyCashRecords, String ipInd) {
        BigDecimal total = BigDecimal.ZERO;
        for (KemidHistoricalCash historicalCash : historyCashRecords) {
            if ("I".equalsIgnoreCase(ipInd)) {
                total = total.add(historicalCash.getHistoricalIncomeCash().bigDecimalValue());
            } else {
                total = total.add(historicalCash.getHistoricalPrincipalCash().bigDecimalValue());
            }
        }
        return total;
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

    public void setHoldingHistoryDao(HoldingHistoryDao holdingHistoryDao) {
        this.holdingHistoryDao = holdingHistoryDao;
    }

    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

}