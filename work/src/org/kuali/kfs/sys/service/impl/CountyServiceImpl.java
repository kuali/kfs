/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.County;
import org.kuali.kfs.sys.service.CountryService;
import org.kuali.kfs.sys.service.CountyService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KualiModuleService;

/**
 * This class...
 */
public class CountyServiceImpl implements CountyService {
    private static Logger LOG = Logger.getLogger(CountyServiceImpl.class);

    private CountryService countryService;
    private KualiModuleService kualiModuleService;

    /**
     * @see org.kuali.kfs.sys.service.CountyService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public County getByPrimaryId(String postalStateCode, String countyCode) {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();

        return this.getByPrimaryId(postalCountryCode, postalStateCode, countyCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountyService#getByPrimaryId(java.lang.String, java.lang.String, java.lang.String)
     */
    public County getByPrimaryId(String postalCountryCode, String postalStateCode, String countyCode) {
        if (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank(postalStateCode) || StringUtils.isBlank(countyCode)) {
            LOG.info("PostalCountryCode, postalStateCode and countycode can be empty String.");
            return null;
        }

        Map<String, Object> countyMap = new HashMap<String, Object>();
        countyMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);
        countyMap.put(KFSPropertyConstants.STATE_CODE, postalStateCode);
        countyMap.put(KFSPropertyConstants.COUNTY_CODE, countyCode);

        return kualiModuleService.getResponsibleModuleService(County.class).getExternalizableBusinessObject(County.class, countyMap);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountyService#getByPrimaryIdIfNecessary(java.lang.String, java.lang.String,
     *      org.kuali.kfs.sys.businessobject.County)
     */
    public County getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalStateCode, String countyCode, County existingCounty) {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();

        return this.getByPrimaryIdIfNecessary(businessObject, postalCountryCode, postalStateCode, countyCode, existingCounty);
    }

    /**
     * @see org.kuali.kfs.sys.service.CountyService#getByPrimaryIdIfNecessary(java.lang.String, java.lang.String, java.lang.String,
     *      org.kuali.kfs.sys.businessobject.County)
     */
    public County getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalCountryCode, String postalStateCode, String countyCode, County existingCounty) {
        if (existingCounty != null) {
            String existingCountryCode = existingCounty.getPostalCountryCode();
            String existingStateCode = existingCounty.getStateCode();
            String existingCountyCode = existingCounty.getCountyCode();

            if (StringUtils.equals(postalCountryCode, existingCountryCode) && StringUtils.equals(postalStateCode, existingStateCode) && StringUtils.equals(countyCode, existingCountyCode)) {
                return existingCounty;
            }
        }

        return this.getByPrimaryId(postalCountryCode, postalStateCode, countyCode);
    }

    /**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Sets the countryService attribute value.
     * 
     * @param countryService The countryService to set.
     */
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }
}
