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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.report.service.TrialBalanceReportService;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportDataHolder;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TrialBalanceReportServiceImpl extends EndowmentReportServiceImpl implements TrialBalanceReportService {
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportForAllKemids(java.lang.String)
     */
    public List<TrialBalanceReportDataHolder> getTrialBalanceReportForAllKemids(String endownmentOption, String closedIndicator) {
        return getTrialBalanceReportsByKemidByIds(null, endownmentOption, closedIndicator);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportsByKemidByIds(java.util.List, java.lang.String)
     */
    public List<TrialBalanceReportDataHolder> getTrialBalanceReportsByKemidByIds(List<String> kemids, String endownmentOption, String closedIndicator) {
        
        KualiDecimal totalInocmeCashBalance = KualiDecimal.ZERO;
        KualiDecimal totalPrincipalcashBalance = KualiDecimal.ZERO;
        BigDecimal totalKemidTotalMarketValue = BigDecimal.ZERO;
        BigDecimal totalAvailableExpendableFunds = BigDecimal.ZERO;
        BigDecimal totalFyRemainderEstimatedIncome = BigDecimal.ZERO;
        
        List<TrialBalanceReportDataHolder> trialBalanceReportList = new ArrayList<TrialBalanceReportDataHolder>();
        List<KEMID> kemidRecords = kemidDao.getKemidRecordsByIds(kemids, endownmentOption, closedIndicator);
        if (kemidRecords == null || kemidRecords.isEmpty()) {
            return null;
        }
        
        for (KEMID kemidObj : kemidRecords) {
            
            // get the kemid and the title
            TrialBalanceReportDataHolder trialBalanceReport = new TrialBalanceReportDataHolder(); 
            trialBalanceReport.setKemid(kemidObj.getKemid());
            trialBalanceReport.setKemidName(kemidObj.getShortTitle());

            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(EndowPropertyConstants.KEMID, kemidObj.getKemid());
            
            // get income cash balance, principal cash balance            
            KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findByPrimaryKey(KemidCurrentCash.class, primaryKeys);
            if (ObjectUtils.isNotNull(kemidCurrentCash)) {
                trialBalanceReport.setInocmeCashBalance(kemidCurrentCash.getCurrentIncomeCash());
                trialBalanceReport.setPrincipalcashBalance(kemidCurrentCash.getCurrentPrincipalCash());   
                totalInocmeCashBalance = totalInocmeCashBalance.add(kemidCurrentCash.getCurrentIncomeCash());
                totalPrincipalcashBalance = totalPrincipalcashBalance.add(kemidCurrentCash.getCurrentPrincipalCash());
            } else {
                trialBalanceReport.setInocmeCashBalance(KualiDecimal.ZERO);
                trialBalanceReport.setPrincipalcashBalance(KualiDecimal.ZERO);  
            }
            
            // get available expendable funds
            KEMIDCurrentAvailableBalance kemidCurrentAvailableBalance = (KEMIDCurrentAvailableBalance) businessObjectService.findByPrimaryKey(KEMIDCurrentAvailableBalance.class, primaryKeys);
            if (ObjectUtils.isNotNull(kemidCurrentAvailableBalance)) {
                trialBalanceReport.setAvailableExpendableFunds(kemidCurrentAvailableBalance.getAvailableTotalCash());
                totalAvailableExpendableFunds = totalAvailableExpendableFunds.add(kemidCurrentAvailableBalance.getAvailableTotalCash());
            } else {
                trialBalanceReport.setAvailableExpendableFunds(BigDecimal.ZERO);
            }
            
            // get sub total market value, FY remainder estimated income 
            BigDecimal subTotalKemidTotalMarketValue = BigDecimal.ZERO;            
            BigDecimal subTotalRemainderEstimatedIncome = BigDecimal.ZERO;            
            List<CurrentTaxLotBalance> CurrentTaxLotBalanceRecords = (List<CurrentTaxLotBalance>) businessObjectService.findMatching(CurrentTaxLotBalance.class, primaryKeys);
            if (ObjectUtils.isNotNull(CurrentTaxLotBalanceRecords)) {
                for (CurrentTaxLotBalance currentTaxLotBalance : CurrentTaxLotBalanceRecords) {
                    subTotalKemidTotalMarketValue = subTotalKemidTotalMarketValue.add(currentTaxLotBalance.getHoldingMarketValue());
                    subTotalRemainderEstimatedIncome = subTotalRemainderEstimatedIncome.add(currentTaxLotBalance.getRemainderOfFYEstimatedIncome());
                    trialBalanceReport.setKemidTotalMarketValue(subTotalKemidTotalMarketValue);
                    trialBalanceReport.setFyRemainderEstimatedIncome(subTotalRemainderEstimatedIncome);
                }                
            }
            
            // add this new one
            trialBalanceReportList.add(trialBalanceReport);

            // total market value, total FY remainder estimated income
            totalKemidTotalMarketValue = totalKemidTotalMarketValue.add(subTotalKemidTotalMarketValue);
            totalFyRemainderEstimatedIncome = totalFyRemainderEstimatedIncome.add(subTotalRemainderEstimatedIncome);
        }
        
        // add the totals 
        TrialBalanceReportDataHolder trialBalanceReport = new TrialBalanceReportDataHolder(); 
        trialBalanceReport.setKemid("TOTALS");
        trialBalanceReport.setKemidName("");
        trialBalanceReport.setInocmeCashBalance(totalInocmeCashBalance);
        trialBalanceReport.setPrincipalcashBalance(totalPrincipalcashBalance);
        trialBalanceReport.setKemidTotalMarketValue(totalKemidTotalMarketValue);
        trialBalanceReport.setAvailableExpendableFunds(totalAvailableExpendableFunds);
        trialBalanceReport.setFyRemainderEstimatedIncome(totalFyRemainderEstimatedIncome);
        trialBalanceReportList.add(trialBalanceReport);
        
        return trialBalanceReportList;
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getTrialBalanceReportsByOtherCriteria(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String)
     */
    public List<TrialBalanceReportDataHolder> getTrialBalanceReportsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String endowmnetOption, String closedIndicator) {

        List<String> kemids = getKemidsByOtherCriteria(benefittingOrganziationCampusCodes, benefittingOrganziationChartCodes,
                benefittingOrganziationCodes, typeCodes, purposeCodes, combineGroupCodes);
          
//        if (ObjectUtils.isNotNull(benefittingOrganziationCampusCodes) && !benefittingOrganziationCampusCodes.isEmpty()) {
//            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByCampusCode(benefittingOrganziationCampusCodes));
//        }
//        if (ObjectUtils.isNotNull(benefittingOrganziationChartCodes) && !benefittingOrganziationChartCodes.isEmpty()) {
//            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_CHRT_CD, benefittingOrganziationChartCodes));
//        }
//        if (ObjectUtils.isNotNull(benefittingOrganziationCodes) && !benefittingOrganziationCodes.isEmpty()) {
//            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_ORG_CD, benefittingOrganziationCodes));
//        }
//        if (ObjectUtils.isNotNull(typeCodes) && !typeCodes.isEmpty()) {        
//            retainCommonKemids(kemids, kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_TYPE_CODE, typeCodes));
//        }
//        if (ObjectUtils.isNotNull(purposeCodes) && !purposeCodes.isEmpty()) {
//            retainCommonKemids(kemids, kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_PRPS_CD, purposeCodes));
//        }
//        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {        
//            retainCommonKemids(kemids, kemidReportGroupDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCodes));
//        }
        
        // Now that we have all the kemids to be reported by the use of the other criteria, go to get the report data 
        if (kemids.size() == 0) {
            return null;
        } else {            
            return getTrialBalanceReportsByKemidByIds(kemids, endowmnetOption, closedIndicator);
        }
    }
    
}
