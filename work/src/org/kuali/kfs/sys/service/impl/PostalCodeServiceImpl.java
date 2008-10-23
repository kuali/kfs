/*
 * Copyright 2007 The Kuali Foundation.
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
import org.kuali.kfs.sys.businessobject.PostalCode;
import org.kuali.kfs.sys.service.CountryService;
import org.kuali.kfs.sys.service.PostalCodeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KualiModuleService;

public class PostalCodeServiceImpl implements PostalCodeService {
    private static Logger LOG = Logger.getLogger(PostalCodeServiceImpl.class);

    private CountryService countryService;
    private KualiModuleService kualiModuleService;

    /**
     * @see org.kuali.kfs.sys.service.PostalCodeService#getByPrimaryId(java.lang.String)
     */
    public PostalCode getByPrimaryId(String postalZipCode) {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();

        return this.getByPrimaryId(postalCountryCode, postalZipCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.PostalCodeService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public PostalCode getByPrimaryId(String postalCountryCode, String postalCode) {
        if (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank(postalCode)) {
            LOG.info("neither postalCountryCode nor postalCode can be empty String.");
            return null;
        }

        Map<String, Object> postalCodeMap = new HashMap<String, Object>();
        postalCodeMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);
        postalCodeMap.put(KFSPropertyConstants.POSTAL_CODE, postalCode);

        return kualiModuleService.getResponsibleModuleService(PostalCode.class).getExternalizableBusinessObject(PostalCode.class, postalCodeMap);
    }

    /**
     * @see org.kuali.kfs.sys.service.PostalCodeService#getByPrimaryIdIfNecessary(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, org.kuali.kfs.sys.businessobject.PostalCode)
     */
    public PostalCode getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalCode, PostalCode existingPostalCode) {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();

        return this.getByPrimaryIdIfNecessary(businessObject, postalCountryCode, postalCode, existingPostalCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.PostalCodeService#getByPrimaryIdIfNecessary(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, java.lang.String, org.kuali.kfs.sys.businessobject.PostalCode)
     */
    public PostalCode getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalCountryCode, String postalCode, PostalCode existingPostalCode) {
        if (existingPostalCode != null) {
            String existingCountryCode = existingPostalCode.getPostalCountryCode();
            String existingPostalZipCode = existingPostalCode.getPostalCode();
            if (StringUtils.equals(postalCountryCode, existingCountryCode) && StringUtils.equals(postalCode, existingPostalZipCode)) {
                return existingPostalCode;
            }
        }

        return this.getByPrimaryId(postalCountryCode, postalCode);
    }

    /**
     * Sets the countryService attribute value.
     * 
     * @param countryService The countryService to set.
     */
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
