/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides service for Security maintenance
 */
@Transactional
public class HoldingHistoryServiceImpl implements HoldingHistoryService {

    protected BusinessObjectService businessObjectService;
    protected MonthEndDateService monthEndDateService;
    protected HoldingHistoryDao holdingHistoryDao;

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundControlService#getHoldingHistoryBySecuritIdAndMonthEndId(java.lang.String,
     *      KualiInteger)
     */
    public Collection<HoldingHistory> getHoldingHistoryBySecuritIdAndMonthEndId(String securityId, KualiInteger monthEndId) {

        Collection<HoldingHistory> holdingHistory = new ArrayList();

        if (StringUtils.isNotBlank(securityId)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(HoldingHistory.class, EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID)) {
                securityId = securityId.toUpperCase();
            }

            criteria.put(EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, securityId);
            criteria.put(EndowPropertyConstants.MONTH_END_DATE_ID, monthEndId);
            holdingHistory = businessObjectService.findMatching(HoldingHistory.class, criteria);
        }

        return holdingHistory;

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#saveHoldingHistory(HoldingHistory)
     */
    public boolean saveHoldingHistory(HoldingHistory holdingHistoryRecord) {
        boolean success = true;

        try {
            businessObjectService.save(holdingHistoryRecord);
        }
        catch (Exception ex) {
            success = false;
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getKemIdFromHoldingHistory(String)
     */
    public String getKemIdFromHoldingHistory(String securityId) {
        String kemId = "";

        Collection<HoldingHistory> holdingHistory = new ArrayList();

        if (StringUtils.isNotBlank(securityId)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(HoldingHistory.class, EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID)) {
                securityId = securityId.toUpperCase();
            }

            criteria.put(EndowPropertyConstants.HISTORY_VALUE_ADJUSTMENT_SECURITY_ID, securityId);

            holdingHistory = businessObjectService.findMatching(HoldingHistory.class, criteria);
        }

        for (HoldingHistory holdingHistoryRecord : holdingHistory) {
            kemId = holdingHistoryRecord.getKemid();
        }

        return kemId;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getHoldingHistoryForMatchingSecurityClassCode(String)
     *      Get all securityIds for the given securityClassCode from END_SEC_T table.
     */
    public Collection<HoldingHistory> getHoldingHistoryForMatchingSecurityClassCode(String securityClassCode) {
        Collection<HoldingHistory> holdingHistory = new ArrayList();

        Collection<Security> securities = new ArrayList();

        if (StringUtils.isNotBlank(securityClassCode)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(Security.class, EndowPropertyConstants.SECURITY_CLASS_CODE)) {
                securityClassCode = securityClassCode.toUpperCase();
            }
            criteria.put(EndowPropertyConstants.SECURITY_CLASS_CODE, securityClassCode);

            securities = businessObjectService.findMatching(Security.class, criteria);

            for (Security security : securities) {
                criteria.clear();
                criteria.put(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, security.getId());

                holdingHistory.addAll(businessObjectService.findMatching(HoldingHistory.class, criteria));
            }
        }

        return holdingHistory;

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getHoldingHistoryBySecurityId(String)
     */
    public Collection<HoldingHistory> getHoldingHistoryBySecurityId(String securityId) {
        Collection<HoldingHistory> holdingHistory = new ArrayList();

        if (StringUtils.isNotBlank(securityId)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(HoldingHistory.class, EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID)) {
                securityId = securityId.toUpperCase();
            }

            criteria.put(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityId);

            holdingHistory = businessObjectService.findMatching(HoldingHistory.class, criteria);
        }

        return holdingHistory;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getHoldingHistoryForMatchingSecurityClassCodeAndSecurityId(String,
     *      String)
     */
    public Collection<HoldingHistory> getHoldingHistoryForMatchingSecurityClassCodeAndSecurityId(String securityClassCode, String securityId) {
        Collection<HoldingHistory> holdingHistory = new ArrayList();

        holdingHistory = getHoldingHistoryForMatchingSecurityClassCode(securityClassCode);

        holdingHistory.addAll(getHoldingHistoryBySecurityId(securityId));

        return holdingHistory;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getHoldingHistoryByIncomePrincipalIndicator(String)
     */
    public Collection<HoldingHistory> getHoldingHistoryByIncomePrincipalIndicator(String incomePrincipalIndicator) {
        Collection<HoldingHistory> holdingHistory = new ArrayList();

        if (StringUtils.isNotBlank(incomePrincipalIndicator)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(HoldingHistory.class, EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR)) {
                incomePrincipalIndicator = incomePrincipalIndicator.toUpperCase();
            }

            criteria.put(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, incomePrincipalIndicator);

            holdingHistory = businessObjectService.findMatching(HoldingHistory.class, criteria);
        }

        return holdingHistory;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getAllHoldingHistory()
     */
    public Collection<HoldingHistory> getAllHoldingHistory() {

        Collection<HoldingHistory> holdingHistory = new ArrayList();

        holdingHistory = businessObjectService.findAll(HoldingHistory.class);

        return holdingHistory;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getHoldingHistoryTotalHoldingUnits(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public BigDecimal getHoldingHistoryTotalHoldingUnits(FeeMethod feeMethod, String feeMethodCodeForSecurityClassCodes, String feeMethodCodeForSecurityIds) {
        BigDecimal totalHoldingUnits = BigDecimal.ZERO;

        Date lastProcessDate = feeMethod.getFeeLastProcessDate();
        Date mostRecentDate = monthEndDateService.getMostRecentDate();

        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();

        Collection<HoldingHistory> holdingHistoryRecords = holdingHistoryDao.getHoldingHistoryForBlance(feeMethod, feeMethodCodeForSecurityClassCodes, feeMethodCodeForSecurityIds);
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            Date monthEndDate = monthEndDateService.getByPrimaryKey(holdingHistory.getMonthEndDateId());

            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS) && (monthEndDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingUnits = totalHoldingUnits.add(holdingHistory.getUnits());
            }
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS) && (mostRecentDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingUnits = totalHoldingUnits.add(holdingHistory.getUnits());
            }
        }

        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS)) {
            totalHoldingUnits = KEMCalculationRoundingHelper.divide(totalHoldingUnits, BigDecimal.valueOf(holdingHistoryRecords.size()), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }

        return totalHoldingUnits;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingHistoryService#getHoldingHistoryTotalHoldingMarketValue(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public BigDecimal getHoldingHistoryTotalHoldingMarketValue(FeeMethod feeMethod, String feeMethodCodeForSecurityClassCodes, String feeMethodCodeForSecurityIds) {
        BigDecimal totalHoldingMarkteValue = BigDecimal.ZERO;

        Date lastProcessDate = feeMethod.getFeeLastProcessDate();
        Date mostRecentDate = monthEndDateService.getMostRecentDate();

        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();

        Collection<HoldingHistory> holdingHistoryRecords = holdingHistoryDao.getHoldingHistoryForBlance(feeMethod, feeMethodCodeForSecurityClassCodes, feeMethodCodeForSecurityIds);
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            Date monthEndDate = monthEndDateService.getByPrimaryKey(holdingHistory.getMonthEndDateId());
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE) && (monthEndDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingMarkteValue = totalHoldingMarkteValue.add(holdingHistory.getMarketValue());
            }
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE) && (monthEndDate.compareTo(mostRecentDate) > 0)) {
                totalHoldingMarkteValue = totalHoldingMarkteValue.add(holdingHistory.getMarketValue());
            }
        }

        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE)) {
            totalHoldingMarkteValue = KEMCalculationRoundingHelper.divide(totalHoldingMarkteValue, BigDecimal.valueOf(holdingHistoryRecords.size()), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }

        return totalHoldingMarkteValue;
    }


    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the monthEndDateService.
     * 
     * @param monthEndDateService
     */
    public void setMonthEndDateService(MonthEndDateService monthEndDateService) {
        this.monthEndDateService = monthEndDateService;
    }

    /**
     * Sets the holdingHistoryDao.
     * 
     * @param holdingHistoryDao
     */
    public void setHoldingHistoryDao(HoldingHistoryDao holdingHistoryDao) {
        this.holdingHistoryDao = holdingHistoryDao;
    }
}
