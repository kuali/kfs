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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.State;
import org.kuali.kfs.sys.service.CountryService;
import org.kuali.kfs.sys.service.StateService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KualiModuleService;

public class StateServiceImpl implements StateService {
    private static Logger LOG = Logger.getLogger(StateServiceImpl.class);

    private KualiModuleService kualiModuleService;
    private CountryService countryService;

    /**
     * @see org.kuali.kfs.sys.service.StateService#getByPrimaryId(java.lang.String)
     */
    public State getByPrimaryId(String postalStateCode) {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();
        return this.getByPrimaryId(postalCountryCode, postalStateCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public State getByPrimaryId(String postalCountryCode, String postalStateCode) {
        if (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank(postalStateCode)) {
            LOG.info("neither postalCountryCode nor postalStateCode can be empty String.");
            return null;
        }

        Map<String, Object> postalStateMap = new HashMap<String, Object>();
        postalStateMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);
        postalStateMap.put(KFSPropertyConstants.POSTAL_STATE_CODE, postalStateCode);

        return kualiModuleService.getResponsibleModuleService(State.class).getExternalizableBusinessObject(State.class, postalStateMap);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#getByPrimaryIdIfNecessary(org.kuali.rice.kns.bo.BusinessObject, java.lang.String,
     *      org.kuali.kfs.sys.businessobject.State)
     */
    public State getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalStateCode, State existingState) {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();

        return this.getByPrimaryIdIfNecessary(businessObject, postalCountryCode, postalStateCode, existingState);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#getByPrimaryIdIfNecessary(org.kuali.rice.kns.bo.BusinessObject, java.lang.String,
     *      java.lang.String, org.kuali.kfs.sys.businessobject.State)
     */
    public State getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalCountryCode, String postalStateCode, State existingState) {
        if (existingState != null) {
            String existingCountryCode = existingState.getPostalCountryCode();
            String existingPostalStateCode = existingState.getPostalStateCode();
            if (StringUtils.equals(postalCountryCode, existingCountryCode) && StringUtils.equals(postalStateCode, existingPostalStateCode)) {
                return existingState;
            }
        }

        return this.getByPrimaryId(postalCountryCode, postalStateCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#findAllStates()
     */
    public List<State> findAllStates() {
        String postalCountryCode = countryService.getDefaultCountry().getPostalCountryCode();
        return this.findAllStates(postalCountryCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#findAllStates(java.lang.String)
     */
    public List<State> findAllStates(String postalCountryCode) {
        if (StringUtils.isBlank(postalCountryCode)) {
            throw new IllegalArgumentException("The postalCountryCode cannot be empty String.");
        }

        Map<String, Object> postalStateMap = new HashMap<String, Object>();
        postalStateMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);

        return kualiModuleService.getResponsibleModuleService(State.class).getExternalizableBusinessObjectsList(State.class, postalStateMap);
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
