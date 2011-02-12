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
package org.kuali.kfs.module.external.kc.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;


public interface UnitService {
    public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("unitName","unitNumber","parentUnitNumber","organizationId");
    
    ContractsAndGrantsUnit getUnit(String unitNumber);

    List lookupUnits(Map<String, String> searchCriteria);

    /* Parents will be in order of proximity, ie parent at index 0, grandparent at index 1, etc */
    List<String> getParentUnits(String unitNumber);
    
    /**
     * Gets the wsdlLocation attribute. 
     * @return Returns the wsdlLocation.
     */
    public String getWsdlLocation();


    /**
     * Sets the wsdlLocation attribute value.
     * @param wsdlLocation The wsdlLocation to set.
     */
    public void setWsdlLocation(String wsdlLocation);
}
