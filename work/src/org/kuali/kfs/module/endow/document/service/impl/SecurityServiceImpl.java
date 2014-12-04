/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class provides service for Security maintenance
 */
@NonTransactional
public class SecurityServiceImpl implements SecurityService {

    private KEMService kemService;
    private BusinessObjectService businessObjectService;
    private SecurityDao securityDao;


    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundControlService#getByPrimaryKey(java.lang.String)
     */
    public Security getByPrimaryKey(String id) {
        Security security = null;
        if (StringUtils.isNotBlank(id)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(Security.class, EndowPropertyConstants.SECURITY_ID)) {
                id = id.toUpperCase();
            }

            criteria.put("id", id);

            security = (Security) businessObjectService.findByPrimaryKey(Security.class, criteria);
        }
        return security;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.SecurityService#updateUnitValue(org.kuali.kfs.module.endow.businessobject.Security,
     *      java.math.BigDecimal, java.sql.Date, java.lang.String)
     */
    public Security updateUnitValue(Security security, BigDecimal newUnitValue, Date newValueDate, String newUnitValueSource) {
        BigDecimal oldUnitValue = security.getUnitValue();
        security.setPreviousUnitValue(oldUnitValue);
        security.setUnitValue(newUnitValue);

        Date oldValuationDate = security.getValuationDate();
        security.setPreviousUnitValueDate(oldValuationDate);
        security.setValuationDate(newValueDate);

        security.setUnitValueSource(newUnitValueSource);

        return security;

    }

    /**
     * This method will update the interest rate or amount
     * 
     * @return Security the update security object
     */
    public Security updateInterestRate(Security security, BigDecimal interestRate) {
        security.setIncomeRate(interestRate);
        return security;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.SecurityService#computeValueBasedOnValuationMethod(org.kuali.kfs.module.endow.businessobject.Security)
     */
    public void computeValueBasedOnValuationMethod(Security security) {
        ClassCode classCode = security.getClassCode();

        if (ObjectUtils.isNotNull(classCode)) {
            if (EndowConstants.ValuationMethod.UNITS.equalsIgnoreCase((classCode.getValuationMethod()))) {
                BigDecimal marketValue = getSecurityMarketValue(security);
                security.setMarketValue(marketValue);
            }
            else if (EndowConstants.ValuationMethod.MARKET.equalsIgnoreCase((classCode.getValuationMethod()))) {
                BigDecimal unitValue = getSecurityUnitValue(security);
                security.setUnitValue(unitValue);
            }
        }

    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.SecurityService#updateIncomeChangeDate(java.lang.String)
     */
    public Security updateIncomeChangeDate(Security security) {
        java.sql.Date currentDate = kemService.getCurrentDate();
        if (ObjectUtils.isNotNull(security)) {
            security.setIncomeChangeDate(currentDate);
        }
        return security;
    }

    /**
     * Computes the market value.
     * 
     * @param security the security for which we calculate the market value
     * @return the computed market value
     */
    public BigDecimal getSecurityMarketValue(Security security) {
        BigDecimal marketValue = BigDecimal.ZERO;
        String securityId = security.getId();

        if (ObjectUtils.isNotNull(securityId)) {
            marketValue = kemService.getMarketValue(securityId);
        }

        return marketValue;
    }


    /**
     * Computes the unit value based on the market value.
     * 
     * @param security the security for which we calculate the unit value
     * @return the computed unit value
     */
    public BigDecimal getSecurityUnitValue(Security security) {
        BigDecimal marketValue = security.getMarketValue();
        BigDecimal unitsHeld = security.getUnitsHeld();
        BigDecimal unitValue = BigDecimal.ZERO;
        ClassCode classCode = security.getClassCode();

        if (ObjectUtils.isNotNull(classCode) && ObjectUtils.isNotNull(unitValue) && ObjectUtils.isNotNull(unitsHeld) && unitsHeld.compareTo(BigDecimal.ZERO) != 0) {
            if (EndowConstants.ClassCodeTypes.BOND.equalsIgnoreCase(classCode.getClassCodeType())) {
                unitValue = KEMCalculationRoundingHelper.divide((marketValue.multiply(new BigDecimal(100))), unitsHeld, EndowConstants.Scale.SECURITY_UNIT_VALUE);
            }
            else {
                unitValue = KEMCalculationRoundingHelper.divide(marketValue, unitsHeld, EndowConstants.Scale.SECURITY_UNIT_VALUE);
            }
        }

        return unitValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.SecurityService#getSecuritiesByClassCodeWithUnitsGreaterThanZero(java.util.List)
     */
    public List<Security> getSecuritiesByClassCodeWithUnitsGreaterThanZero(List<String> classCodes) {

        return securityDao.getSecuritiesByClassCodeWithUnitsGreaterThanZero(classCodes);
    }


    /**
     * Gets the kemService.
     * 
     * @return kemService
     */
    public KEMService getKemService() {
        return kemService;
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
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
     * Sets the securityDao.
     * 
     * @param securityDao
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

}
