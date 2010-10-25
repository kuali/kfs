/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.DataDictionaryService;

public class HoldingHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements HoldingHistoryDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryDaoOjb.class);
    
    protected MonthEndDateService monthEndDateService;
    protected SecurityDao securityDao;
    
    /**
     * Prepares the criteria and selects the records from END_HLDG_HIST_T table
     */
    protected Collection<HoldingHistory> getHoldingHistoryForBlance(FeeMethod feeMethod) {
        Collection<HoldingHistory> holdingHistory = new ArrayList(); 
        
        Collection incomePrincipalValues = new ArrayList();
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
        
        Criteria criteria = new Criteria();
        
        if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME_AND_PRINCIPAL)) {
            criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, incomePrincipalValues);
        }
        else {
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME)) {
                criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
            }
            
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL)) {
                criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
            }
        }

        Collection securityClassCodes =  new ArrayList();
        Collection securityIds = new ArrayList();
        
        if (feeMethod.getFeeByClassCode() && feeMethod.getFeeBySecurityCode()) {
            securityClassCodes = getSecurityClassCodes(feeMethod.getCode());
            securityIds = getSecurityIds(feeMethod.getCode());
            
            securityIds.addAll(securityClassCodes);
            if (securityIds.size() > 0) {
               criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityIds);
            }
        }
        else {
            if (feeMethod.getFeeByTransactionType()) {
                securityClassCodes = getSecurityClassCodes(feeMethod.getCode());
                if (securityClassCodes.size() > 0) {
                   criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityClassCodes);
                }
            }
            
            if (feeMethod.getFeeByETranCode()) {
                securityIds = getSecurityIds(feeMethod.getCode());                
                if (securityIds.size() > 0) {
                    criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityIds);
                }
            }
        }
        
        QueryByCriteria query = QueryFactory.newQuery(HoldingHistory.class, criteria);
            
        holdingHistory = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        
        return holdingHistory;
    }
       
    /**
     * Gets the security codes for a given securityClassCode in END_FEE_CLS_CD_T table
     * @feeMethodCode FEE_MTH
     * @return securityCodes
     */
    protected Collection getSecurityClassCodes(String feeMethodCode) {
        Collection securityClassCodes = new ArrayList();
        Collection<FeeClassCode> feeClassCodes = new ArrayList();        

        if (StringUtils.isNotBlank(feeMethodCode)) {        
            Map<String, String>  crit = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(FeeClassCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCode = feeMethodCode.toUpperCase();
            }
            
            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_CLASS_CODE_INCLUDE, EndowConstants.YES);
            
            QueryByCriteria query = QueryFactory.newQuery(FeeClassCode.class, criteria);
            
            feeClassCodes = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeClassCode feeClassCode : feeClassCodes) {
                Collection <Security> securities = securityDao.getSecuritiesBySecurityClassCode(feeClassCode.getFeeClassCode());
                for (Security security : securities) {
                    securityClassCodes.add(security.getId());
                }
            }
        }
        
        return securityClassCodes;
    }
    
    /**
     * Gets the security ids for a given securityClassCode in END_FEE_SEC_T table
     * @feeMethodCode FEE_MTH
     * @return securityIds
     */
    protected Collection getSecurityIds(String feeMethodCode) {
        Collection securityIds = new ArrayList();
        Collection<FeeSecurity> feeSecuritys = new ArrayList();        

        if (StringUtils.isNotBlank(feeMethodCode)) {        
            Map<String, String>  crit = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(FeeSecurity.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCode = feeMethodCode.toUpperCase();
            }
            
            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_SECURITY_INCLUDE, EndowConstants.YES);
            
            QueryByCriteria query = QueryFactory.newQuery(FeeSecurity.class, criteria);
            
            feeSecuritys = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeSecurity feeSecurity : feeSecuritys) {
                securityIds.add(feeSecurity.getSecurityCode());
            }
        }
        
        return securityIds;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryTotalHoldingUnits(FeeMethod)
     */
    public BigDecimal getHoldingHistoryTotalHoldingUnits(FeeMethod feeMethod) {
        BigDecimal totalHoldingUnits = BigDecimal.ZERO;
        
        Date lastProcessDate = feeMethod.getFeeLastProcessDate();
        Date mostRecentDate = monthEndDateService.getMostRecentDate();
        
        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();
        
        Collection <HoldingHistory> holdingHistoryRecords = getHoldingHistoryForBlance(feeMethod);
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            Date monthEndDate = monthEndDateService.getByPrimaryKey(holdingHistory.getMonthEndDateId());

            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS) && (monthEndDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingUnits.add(holdingHistory.getUnits());    
            }
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS) && (mostRecentDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingUnits.add(holdingHistory.getUnits());    
            }
        }
        
        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS)) {
            totalHoldingUnits = KEMCalculationRoundingHelper.divide(totalHoldingUnits, BigDecimal.valueOf(holdingHistoryRecords.size()), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }
        
        return totalHoldingUnits;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryTotalHoldingMarketValue(FeeMethod)
     */
    public BigDecimal getHoldingHistoryTotalHoldingMarketValue(FeeMethod feeMethod) {
        BigDecimal totalHoldingMarkteValue = new BigDecimal("0");
        
        Date lastProcessDate = feeMethod.getFeeLastProcessDate();
        Date mostRecentDate = monthEndDateService.getMostRecentDate();
        
        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();
        
        Collection <HoldingHistory> holdingHistoryRecords = getHoldingHistoryForBlance(feeMethod);
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            Date monthEndDate = monthEndDateService.getByPrimaryKey(holdingHistory.getMonthEndDateId());
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE) && (monthEndDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingMarkteValue.add(holdingHistory.getMarketValue());    
            }
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE) && (monthEndDate.compareTo(mostRecentDate) > 0)) {
                totalHoldingMarkteValue.add(holdingHistory.getMarketValue());    
            }
        }
        
        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE)) {
            totalHoldingMarkteValue = KEMCalculationRoundingHelper.divide(totalHoldingMarkteValue, BigDecimal.valueOf(holdingHistoryRecords.size()), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }
        
        return totalHoldingMarkteValue;
    }

    /**
     * Gets the monthEndDateService attribute. 
     * @return Returns the monthEndDateService.
     */
    protected MonthEndDateService getMonthEndDateService() {
        return monthEndDateService;
    }

    /**
     * Sets the monthEndDateService attribute value.
     * @param monthEndDateService The monthEndDateService to set.
     */
    public void setMonthEndDateService(MonthEndDateService monthEndDateService) {
        this.monthEndDateService = monthEndDateService;
    }
    
    /**
     * Gets the securityDao attribute. 
     * @return Returns the securityDao.
     */
    protected SecurityDao getSecurityDao() {
        return securityDao;
    }

    /**
     * Sets the securityDao attribute value.
     * @param securityDao The securityDao to set.
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    
}
