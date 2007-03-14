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
package org.kuali.module.cg.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Subcontractor;

/**
 * This class...
 */
public class SubcontractorRule extends MaintenanceDocumentRuleBase {

    private Subcontractor newSubcontractor;
    
    /**
     * This method has been overridden to add some additional validation checks to the Subcontractor maintenance document.  
     * 
     * @param maintenanceDocument - document to be tested
     * @return whether maintenance doc passes
     * @throws ValidationException
     */
    @Override
    protected boolean validateMaintenanceDocument(MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        success = super.validateMaintenanceDocument(maintenanceDocument);

        newSubcontractor = (Subcontractor) super.getNewBo();
        success &= validateStateCode(newSubcontractor.getSubcontractorStateCode());
        success &= validateCountryCode(newSubcontractor.getSubcontractorCountryCode());

        return success;
    }

    
    /**
     * 
     * This method retrieves the entered state code and checks that this value is valid by comparing it against
     * known values in the SH_STATE_T database table.
     * 
     * @param stateCode
     * @return Whether state code entered is valid
     */
    private boolean validateStateCode(String stateCode) {
        boolean valid = true;

        // Create a query to do a lookup on.
        Map criteria = new HashMap();
        List<String> criteriaValues = new ArrayList<String>();
        criteriaValues.add(stateCode);
        criteria.put("postalStateCode", criteriaValues);

        // Perform lookup for state code provided
        List boList = (List) SpringServiceLocator.getBusinessObjectService().findMatching(State.class, criteria);

        // If no values returned, state code is invalid, throw error
        if(boList.size() < 1) {
            putFieldError("subcontractorStateCode", KeyConstants.ERROR_STATE_CODE_INVALID, stateCode);            
            valid = false;
        }

        return valid;
    }
    
    /**
     * 
     * This method retrieves the entered country code and checks that this value is valid by comparing it against
     * known values in the SH_COUNTRY_T database table.
     * 
     * @param countryCode
     * @return Whether country code entered is valid.
     */
    private boolean validateCountryCode(String countryCode) {
        boolean valid = true;

        // Create a query to do a lookup on.
        Map criteria = new HashMap();
        
        List<String> criteriaValues = new ArrayList<String>();
        criteriaValues.add(countryCode);
        criteria.put("postalCountryCode", criteriaValues);

        // Perform lookup for country code provided
        List boList = (List) SpringServiceLocator.getBusinessObjectService().findMatching(Country.class, criteria);

        // If no values returned, country code is invalid, throw error
        if(boList.size() < 1) {
            putFieldError("subcontractorCountryCode", KeyConstants.ERROR_COUNTRY_CODE_INVALID, countryCode);            
            valid = false;
        }

        return valid;
    }
    
}
