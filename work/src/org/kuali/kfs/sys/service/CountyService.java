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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.County;
import org.kuali.rice.kns.bo.BusinessObject;

public interface CountyService {

    /**
     * get a county object based on the given county code, state code and default country code. The default country code is set up
     * in the system.
     * 
     * @param postalStateCode the given state code
     * @param countyCode the given state code
     * @return a county object based on the given county code, state code and default country code
     */
    public County getByPrimaryId(String postalStateCode, String countyCode);

    /**
     * get a county object based on the given county code, state code and country code.
     * 
     * @param postalCountryCode the given country code
     * @param postalStateCode the given state code
     * @param countyCode the given state code
     * @return a county object based on the given county code, state code and country code
     */
    public County getByPrimaryId(String postalCountryCode, String postalStateCode, String countyCode);

    /**
     * get a county object based on the given county code, state code and default country code. The default country code is set up
     * in the system. If the given postal state code and county code are same as those of the given existing county, return the
     * existing county; otherwise, retrieve a county object.
     * 
     * @param postalStateCode the given state code
     * @param countyCode the given state code
     * @param existingCounty the given existing county
     * @return a county object based on the given county code, state code and default country code if necessary
     */
    public County getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalStateCode, String countyCode, County existingCounty);

    /**
     * get a county object based on the given county code, state code and default country code. The default country code is set up
     * in the system. If the given postal country code, state code and county code are same as those of the given existing county,
     * return the existing county; otherwise, retrieve a county object.
     * 
     * @param postalCountryCode the given country code
     * @param postalStateCode the given state code
     * @param countyCode the given state code
     * @param existingCounty the given existing county
     * @return a county object based on the given county code, state code and country code if necessary
     */
    public County getByPrimaryIdIfNecessary(BusinessObject businessObject, String postalCountryCode, String postalStateCode, String countyCode, County existingCounty);
}
