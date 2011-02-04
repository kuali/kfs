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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao;
import org.kuali.kfs.module.endow.dataaccess.KemidDao;
import org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.report.service.TrialBalanceReportService;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportDataHolder;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TrialBalanceReportServiceImpl implements TrialBalanceReportService {
       
    protected BusinessObjectService businessObjectService;
    protected KEMService kemService;
    protected KemidDao kemidDao;
    protected KemidBenefittingOrganizationDao kemidBenefittingOrganizationDao;
    protected KemidReportGroupDao kemidReportGroupDao;
    
    public List<TrialBalanceReportDataHolder> getTrialBalanceReportForAllKemids(String endownmentOption) {
        return getTrialBalanceReportsByKemidByIds(new ArrayList<String>(), endownmentOption);
    }
    
    public List<TrialBalanceReportDataHolder> getTrialBalanceReportsByKemidByIds(List<String> kemids, String endownmentOption) {
        
        KualiDecimal totalInocmeCashBalance = KualiDecimal.ZERO;
        KualiDecimal totalPrincipalcashBalance = KualiDecimal.ZERO;
        BigDecimal totalKemidTotalMarketValue = BigDecimal.ZERO;
        BigDecimal totalAvailableExpendableFunds = BigDecimal.ZERO;
        BigDecimal totalFyRemainderEstimatedIncome = BigDecimal.ZERO;
        
        List<TrialBalanceReportDataHolder> trialBalanceReportList = new ArrayList<TrialBalanceReportDataHolder>();
        List<KEMID> kemidRecords = kemidDao.getKemidRecordsByIds(kemids, endownmentOption, kemService.getCurrentDate());
        for (KEMID kemidObj : kemidRecords) {
            
            TrialBalanceReportDataHolder trialBalanceReport = new TrialBalanceReportDataHolder(); 
            trialBalanceReport.setKemid(kemidObj.getKemid());
            trialBalanceReport.setKemidName(kemidObj.getShortTitle());

            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(EndowPropertyConstants.KEMID, kemidObj.getKemid());
            
            // get current cash            
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
            
            // get available cash
            KEMIDCurrentAvailableBalance kemidCurrentAvailableBalance = (KEMIDCurrentAvailableBalance) businessObjectService.findByPrimaryKey(KEMIDCurrentAvailableBalance.class, primaryKeys);
            if (ObjectUtils.isNotNull(kemidCurrentAvailableBalance)) {
                trialBalanceReport.setAvailableExpendableFunds(kemidCurrentAvailableBalance.getAvailableTotalCash());
                totalAvailableExpendableFunds = totalAvailableExpendableFunds.add(kemidCurrentAvailableBalance.getAvailableTotalCash());
            } else {
                trialBalanceReport.setAvailableExpendableFunds(BigDecimal.ZERO);
            }
            
            // get current tax lot balance
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
            
            // add the new one
            trialBalanceReportList.add(trialBalanceReport);

            totalKemidTotalMarketValue = totalKemidTotalMarketValue.add(subTotalKemidTotalMarketValue);
            totalFyRemainderEstimatedIncome = totalFyRemainderEstimatedIncome.add(subTotalRemainderEstimatedIncome);
        }
        
        // add the total row
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

    public List<TrialBalanceReportDataHolder> getTrialBalanceReportsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes, String endowmnetOption) {
      
        List<String> kemids = new ArrayList<String>();
        
        if (ObjectUtils.isNotNull(benefittingOrganziationCampusCodes)) {
            //kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute("benefittingOrganziationCampusCode", benefittingOrganziationCampusCode));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationChartCodes)) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute("benefittingChartCode", benefittingOrganziationChartCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationCodes)) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute("benefittingOrgCode", benefittingOrganziationCodes));
        }
        if (ObjectUtils.isNotNull(typeCodes)) {        
            kemids.addAll(kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_TYPE_CODE, typeCodes));
        }
        if (ObjectUtils.isNotNull(purposeCodes)) {
            kemids.addAll(kemidDao.getKemidsByAttribute("purposeCode", purposeCodes));
        }
        if (ObjectUtils.isNotNull(combineGroupCodes)) {        
            kemids.addAll(kemidReportGroupDao.getKemidsByAttribute("combineGroupCode", combineGroupCodes));
        }
        
        return getTrialBalanceReportsByKemidByIds(kemids, endowmnetOption); 
    }
    
    /**
     * 
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * 
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * 
     */
    public void setKemidDao(KemidDao kemidDao) {
        this.kemidDao = kemidDao;
    }

    /**
     * 
     */
    public void setKemidBenefittingOrganizationDao(KemidBenefittingOrganizationDao kemidBenefittingOrganizationDao) {
        this.kemidBenefittingOrganizationDao = kemidBenefittingOrganizationDao;
    }

    /**
     * 
     */
    public void setKemidReportGroupDao(KemidReportGroupDao kemidReportGroupDao) {
        this.kemidReportGroupDao = kemidReportGroupDao;
    }
    
}
