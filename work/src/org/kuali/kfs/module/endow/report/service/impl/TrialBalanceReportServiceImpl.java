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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
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
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportDataHolder;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TrialBalanceReportServiceImpl implements TrialBalanceReportService {
       
    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;
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
      
        // Some of kemids returned may be duplicated. Set will remove the duplicated kemids
        Set<String> kemids = new HashSet<String>();
        
        // 4.1.9 - If any of the non-required criteria are left blank, ignore it as a criterion
        // 4.1.8 - Collects all related kemdis regardless of the closed status of the KEMID
        
        if (ObjectUtils.isNotNull(benefittingOrganziationCampusCodes) && !benefittingOrganziationCampusCodes.isEmpty()) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute("benefittingOrganziationCampusCode", benefittingOrganziationCampusCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationChartCodes) && !benefittingOrganziationChartCodes.isEmpty()) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute("benefittingChartCode", benefittingOrganziationChartCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationCodes) && !benefittingOrganziationCodes.isEmpty()) {
            kemids.addAll(kemidBenefittingOrganizationDao.getKemidsByAttribute("benefittingOrgCode", benefittingOrganziationCodes));
        }
        if (ObjectUtils.isNotNull(typeCodes) && !typeCodes.isEmpty()) {        
            kemids.addAll(kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_TYPE_CODE, typeCodes));
        }
        if (ObjectUtils.isNotNull(purposeCodes) && !purposeCodes.isEmpty()) {
            kemids.addAll(kemidDao.getKemidsByAttribute("purposeCode", purposeCodes));
        }
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {        
            kemids.addAll(kemidReportGroupDao.getKemidsByAttribute("combineGroupCode", combineGroupCodes));
        }
        
        if (kemids.size() == 0) {
            return null;
        } else {
            return getTrialBalanceReportsByKemidByIds(new ArrayList<String>(kemids), endowmnetOption);
        }
    }
    
    public String getInstitutionName() {
        return parameterService.getParameterValue(OrganizationOptions.class, ArConstants.INSTITUTION_NAME);
    }
    
    public String getReportRequestor() {
        return GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }
        
    public String getBenefittingCampuses(List<String> campuses) {        
        StringBuffer orgCampuses = new StringBuffer();
        if (ObjectUtils.isNotNull(campuses) && !campuses.isEmpty()) {
            List<String> campusList = kemidBenefittingOrganizationDao.getAttributeValues("benefittingOrganziationCampusCode", campuses);
            for (String campus : campusList) {
                orgCampuses.append(campus).append(" ");
            }
        }         
        return orgCampuses.toString();
    }
    
    public String getBenefittingCharts(List<String> charts) {
        StringBuffer orgCharts = new StringBuffer();
        if (ObjectUtils.isNotNull(charts) && !charts.isEmpty()) {
            List<String> chartList = kemidBenefittingOrganizationDao.getAttributeValues("benefittingChartCode", charts);
            for (String chart : chartList) {
                orgCharts.append(chart).append(" ");
            }
        }         
        return orgCharts.toString();
    }
    
    public String getBenefittingOrganizations(List<String> organizations) {
        StringBuffer orgs = new StringBuffer();
        if (ObjectUtils.isNotNull(organizations) && !organizations.isEmpty()) {
            List<String> organizationList = kemidBenefittingOrganizationDao.getAttributeValues("benefittingOrgCode", organizations);
            for (String org : organizationList) {
                orgs.append(org).append(" ");
            }
        }         
        return orgs.toString();   
    }
    
    public String getKemidTypeCodes(List<String> kemidTypeCodes) {
        StringBuffer typeCodes = new StringBuffer();
        if (ObjectUtils.isNotNull(kemidTypeCodes) && !kemidTypeCodes.isEmpty()) {
            List<String> typeCodeList = kemidDao.getAttributeValues(EndowPropertyConstants.KEMID_TYPE_CODE, kemidTypeCodes);
            for (String typeCode : typeCodeList) {
                typeCodes.append(typeCode).append(" ");
            }
        }         
        return typeCodes.toString();
    }
    
    public String getKemidPurposeCodes(List<String> kemidPurposes) {
        StringBuffer purposes = new StringBuffer();
        if (ObjectUtils.isNotNull(kemidPurposes) && !kemidPurposes.isEmpty()) {
            List<String> purposeList = kemidDao.getAttributeValues("purposeCode", kemidPurposes);
            for (String purpose : purposeList) {
                purposes.append(purpose).append(" ");
            }
        }         
        return purposes.toString();
    }
    
    public String getCombineGroupCodes(List<String> combineGroupCodes) {
        StringBuffer groupCodes = new StringBuffer();
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {
            List<String> groupCodeList = kemidReportGroupDao.getAttributeValues("combineGroupCode", combineGroupCodes);
            for (String groupCode : groupCodeList) {
                groupCodes.append(groupCode).append(" ");
            }
        } 

        return groupCodes.toString();
    }
    
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids) {
        return kemidBenefittingOrganizationDao.getKemidsWithMultipleBenefittingOrganizations(kemids);
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
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
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
