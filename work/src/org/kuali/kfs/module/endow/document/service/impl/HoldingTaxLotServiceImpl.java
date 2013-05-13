/*
 * Copyright 2010 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@Transactional
public class HoldingTaxLotServiceImpl implements HoldingTaxLotService {
    protected HoldingTaxLotDao holdingTaxLotDao;
    protected BusinessObjectService businessObjectService;
    protected SecurityService securityService;
    protected ClassCodeService classCodeService;
    protected KEMService kEMService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getByPrimaryKey(java.lang.String, java.lang.String,
     *      java.lang.String, int, java.lang.String)
     */
    public HoldingTaxLot getByPrimaryKey(String kemid, String securityId, String registrationCode, int lotNumber, String ipIndicator) {
        Map<String, String> primaryKeys = new HashMap<String, String>();

        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_NUMBER, String.valueOf(lotNumber));
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (HoldingTaxLot) businessObjectService.findByPrimaryKey(HoldingTaxLot.class, primaryKeys);

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLots(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public List<HoldingTaxLot> getAllTaxLots(String kemid, String securityId, String registrationCode, String ipIndicator) {
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (List<HoldingTaxLot>) businessObjectService.findMatching(HoldingTaxLot.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLots()
     */
    public List<HoldingTaxLot> getAllTaxLots() {
        return (List<HoldingTaxLot>) businessObjectService.findAll(HoldingTaxLot.class);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLotsOrderByAcquiredDate(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public List<HoldingTaxLot> getAllTaxLotsOrderByAcquiredDate(String kemid, String securityId, String registrationCode, String ipIndicator, boolean sortAscending) {
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (List<HoldingTaxLot>) businessObjectService.findMatchingOrderBy(HoldingTaxLot.class, criteria, EndowPropertyConstants.HOLDING_TAX_LOT_ACQUIRED_DATE, sortAscending);
    }

    /**
     * Gets all tax lots on the following criteria: kemId and IPIndicator.
     * 
     * @param kemid
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLotsByKemIdAdndIPIndicator(String kemid, String ipIndicator) {
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (List<HoldingTaxLot>) businessObjectService.findMatching(HoldingTaxLot.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLotsWithPositiveUnits(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public List<HoldingTaxLot> getAllTaxLotsWithPositiveUnits(String kemid, String securityId, String registrationCode, String ipIndicator) {
        return (List<HoldingTaxLot>) holdingTaxLotDao.getAllTaxLotsWithPositiveUnits(kemid, securityId, registrationCode, ipIndicator);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLotsWithPositiveCost(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public List<HoldingTaxLot> getAllTaxLotsWithPositiveCost(String kemid, String securityId, String registrationCode, String ipIndicator) {
        return (List<HoldingTaxLot>) holdingTaxLotDao.getAllTaxLotsWithPositiveCost(kemid, securityId, registrationCode, ipIndicator);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getClassCodeType(String) Gets class code type based on
     *      securityId. Based on security ID, you search END_SEC_T Table to get END_SEC_T:SEC_CLS_CD, then, based on class code, you
     *      search END_CLS_CD_T, to get END_CLS_CD_T:CLS_CD_TYP
     * @param id
     * @return class code type
     */
    public String getClassCodeType(String securityId) {
        String classCodeType = null;

        Security security = securityService.getByPrimaryKey(securityId);

        if (ObjectUtils.isNull(security)) {
            throw new RuntimeException("Object Null: Unable to get Security Object for Security Id: " + securityId);
        }

        ClassCode classCode = classCodeService.getByPrimaryKey(security.getSecurityClassCode());

        return classCode.getClassCodeType();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getMarketValueForCashEquivalentsForAvailableIncomeCash()
     *      The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Cash Equivalents (C), and with the
     *      HLDG_IP_IND equal to I.
     * @return marketValue
     */
    public BigDecimal getMarketValueForCashEquivalentsForAvailableIncomeCash(String kemId) {
        BigDecimal marketValue = BigDecimal.ZERO;

        List<HoldingTaxLot> holdingTaxLots = getAllTaxLotsByKemIdAdndIPIndicator(kemId, EndowConstants.IncomePrincipalIndicator.INCOME);
        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            // using sec_cd, get the class type code....
            if (getClassCodeType(holdingTaxLot.getSecurityId()).equalsIgnoreCase(EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS)) {
                marketValue = marketValue.add(holdingTaxLot.getMarketValue());
            }
        }

        return marketValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getMarketValueForPooledInvestmentForAvailableIncomeCash()
     *      The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Pooled Investment (P) and with the
     *      HLDG_IP_IND equal to I times the value in the Available Cash Percent institutional parameter (accounts for only a
     *      percentage of the market value allowing for pricing changes).
     * @return marketValue
     */
    public BigDecimal getMarketValueForPooledInvestmentForAvailableIncomeCash(String kemId) {
        BigDecimal marketValue = BigDecimal.ZERO;

        BigDecimal availableCashPercent = kEMService.getAvailableCashPercent();

        List<HoldingTaxLot> holdingTaxLots = getAllTaxLotsByKemIdAdndIPIndicator(kemId, EndowConstants.IncomePrincipalIndicator.INCOME);
        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            // using sec_cd, get the class type code and if class code type = POOLED_INVESTMENT then multiply the market value with
            // percent
            if (getClassCodeType(holdingTaxLot.getSecurityId()).equalsIgnoreCase(EndowConstants.ClassCodeTypes.POOLED_INVESTMENT)) {
                marketValue = marketValue.add(KEMCalculationRoundingHelper.multiply(holdingTaxLot.getMarketValue(), availableCashPercent, EndowConstants.Scale.SECURITY_MARKET_VALUE));
            }
        }

        return marketValue;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getMarketValueForCashEquivalentsForAvailablePrincipalCash()
     *      The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Cash Equivalents (C), and with the
     *      HLDG_IP_IND equal to P.
     * @return marketValue
     */
    public BigDecimal getMarketValueForCashEquivalentsForAvailablePrincipalCash(String kemId) {
        BigDecimal marketValue = BigDecimal.ZERO;

        List<HoldingTaxLot> holdingTaxLots = getAllTaxLotsByKemIdAdndIPIndicator(kemId, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            // using sec_cd, get the class type code....
            if (getClassCodeType(holdingTaxLot.getSecurityId()).equalsIgnoreCase(EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS)) {
                marketValue = marketValue.add(holdingTaxLot.getMarketValue());
            }
        }

        return marketValue;

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getMarketValueForPooledInvestmentForAvailablePrincipalCash()
     *      The Market Value of the KEMID END_HLDG_TAX_LOT_T records with a CLS_CD_TYP of Pooled Investment (P) and with the
     *      HLDG_IP_IND equal to I times the value in the Available Cash Percent institutional parameter (accounts for only a
     *      percentage of the market value allowing for pricing changes).
     * @return marketValue
     */
    public BigDecimal getMarketValueForPooledInvestmentForAvailablePrincipalCash(String kemId) {
        BigDecimal marketValue = BigDecimal.ZERO;

        BigDecimal availableCashPercent = kEMService.getAvailableCashPercent();

        List<HoldingTaxLot> holdingTaxLots = getAllTaxLotsByKemIdAdndIPIndicator(kemId, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            // using sec_cd, get the class type code and if class code type = POOLED_INVESTMENT then multiply the market value with
            // percent
            if (getClassCodeType(holdingTaxLot.getSecurityId()).equalsIgnoreCase(EndowConstants.ClassCodeTypes.POOLED_INVESTMENT)) {
                marketValue = marketValue.add(KEMCalculationRoundingHelper.multiply(holdingTaxLot.getMarketValue(), availableCashPercent, EndowConstants.Scale.SECURITY_MARKET_VALUE));
            }
        }

        return marketValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(java.lang.String)
     */
    public List<HoldingTaxLot> getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(String securityId) {

        return holdingTaxLotDao.getTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(securityId);

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(java.lang.String)
     */
    public List<HoldingTaxLot> getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(String securityId) {
        return holdingTaxLotDao.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(securityId);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#removeAllHoldingTaxLots()
     */
    public boolean removeAllHoldingTaxLots() {
        boolean success = true;
        
        List<HoldingTaxLot> allTaxLots = getAllTaxLots();
        for (HoldingTaxLot holdingTaxLot : allTaxLots) {
            businessObjectService.delete(holdingTaxLot);
        }
        
        return success;
    }
    
    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the holdingTaxLotDao.
     * 
     * @return holdingTaxLotDao
     */
    protected HoldingTaxLotDao getHoldingTaxLotDao() {
        return holdingTaxLotDao;
    }

    /**
     * Sets the holdingTaxLotDao.
     * 
     * @param holdingTaxLotDao
     */
    public void setHoldingTaxLotDao(HoldingTaxLotDao holdingTaxLotDao) {
        this.holdingTaxLotDao = holdingTaxLotDao;
    }

    /**
     * Gets the securityService.
     * 
     * @return securityService
     */
    protected SecurityService getSecurityService() {
        return securityService;
    }

    /**
     * Sets the securityService.
     * 
     * @param securityService
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Gets the classCodeService.
     * 
     * @return classCodeService
     */
    protected ClassCodeService getClassCodeService() {
        return classCodeService;
    }

    /**
     * Sets the classCodeService.
     * 
     * @param classCodeService
     */
    public void setClassCodeService(ClassCodeService classCodeService) {
        this.classCodeService = classCodeService;
    }

    /**
     * gets the kEMService.
     * 
     * @param kEMService
     */
    protected KEMService getkEMService() {
        return kEMService;
    }

    /**
     * Sets the kEMService.
     * 
     * @param kEMService
     */
    public void setkEMService(KEMService kEMService) {
        this.kEMService = kEMService;
    }

}
