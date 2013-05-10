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
