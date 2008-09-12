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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.State;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.StateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants.FINANCIAL_SYSTEM_ALL;
import org.kuali.rice.kns.service.BusinessObjectService;

public class StateServiceImpl implements StateService {

    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.sys.service.StateService#getByPrimaryId(java.lang.String)
     */
    public State getByPrimaryId(String postalStateCode) {
        String postalCountryCode = parameterService.getParameterValue(FINANCIAL_SYSTEM_ALL.class, KFSConstants.CoreApcParms.DEFAULT_COUNTRY);
        return this.getByPrimaryId(postalCountryCode, postalStateCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public State getByPrimaryId(String postalCountryCode, String postalStateCode) {
        if (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank(postalStateCode)) {
            //throw new IllegalArgumentException("neither postalCountryCode nor postalStateCode can be empty String.");
            return null;
        }

        Map<String, String> postalStateMap = new HashMap<String, String>();
        postalStateMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);
        postalStateMap.put(KFSPropertyConstants.POSTAL_STATE_CODE, postalStateCode);

        return (State) businessObjectService.findByPrimaryKey(State.class, postalStateMap);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#findAllStates()
     */
    public List<State> findAllStates() {
        String postalCountryCode = parameterService.getParameterValue(FINANCIAL_SYSTEM_ALL.class, KFSConstants.CoreApcParms.DEFAULT_COUNTRY);
        return this.findAllStates(postalCountryCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.StateService#findAllStates(java.lang.String)
     */
    public List<State> findAllStates(String postalCountryCode) {
        if (StringUtils.isBlank(postalCountryCode)) {
            throw new IllegalArgumentException("The postalCountryCode cannot be empty String.");
        }

        Map<String, String> postalStateMap = new HashMap<String, String>();
        postalStateMap.put(KFSPropertyConstants.POSTAL_COUNTRY_CODE, postalCountryCode);

        return (List<State>) businessObjectService.findMatching(State.class, postalStateMap);
    }
    
    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
