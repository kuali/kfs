/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.SubContractor;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

/**
 *
 */
public class SubcontractorRule extends MaintenanceDocumentRuleBase {

    protected SubContractor newSubcontractor;

    /**
     * This method has been overridden to add some additional validation checks to the {@link Subcontractor} maintenance document.
     *
     * @param maintenanceDocument - document to be tested
     * @return whether maintenance doc passes
     * @throws org.kuali.rice.krad.exception.ValidationException
     */
    @Override
    protected boolean validateMaintenanceDocument(MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        success = super.validateMaintenanceDocument(maintenanceDocument);

        newSubcontractor = (SubContractor) super.getNewBo();
        success &= validateCountryCode(newSubcontractor.getSubcontractorCountryCode());
        success &= validateStateCode(newSubcontractor.getSubcontractorCountryCode(), newSubcontractor.getSubcontractorStateCode());

        return success;
    }


    /**
     * This method retrieves the entered state code and checks that this value is valid by comparing it against known values in the
     * SH_STATE_T database table.
     *
     * @param stateCode
     * @return Whether state code entered is valid
     */
    protected boolean validateStateCode(String countryCode, String stateCode) {
        boolean valid = true;

        // Perform lookup for state code provided
        if ( StringUtils.isNotBlank(stateCode) && StringUtils.isNotBlank(countryCode) ) {
            State state = SpringContext.getBean(StateService.class).getState(countryCode, stateCode);

            // If no values returned, state code is invalid, throw error
            if (state== null) {
                putFieldError("subcontractorStateCode", KFSKeyConstants.ERROR_STATE_CODE_INVALID, stateCode);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * This method retrieves the entered country code and checks that this value is valid by comparing it against known values in
     * the SH_COUNTRY_T database table.
     *
     * @param countryCode
     * @return Whether country code entered is valid.
     */
    protected boolean validateCountryCode(String countryCode) {
        boolean valid = true;

        if ( StringUtils.isNotBlank(countryCode) ) {
            Country country = SpringContext.getBean(CountryService.class).getCountry(countryCode);

            // If no values returned, country code is invalid, throw error
            if (country == null) {
                putFieldError("subcontractorCountryCode", KFSKeyConstants.ERROR_COUNTRY_CODE_INVALID, countryCode);
                valid = false;
            }
        }

        return valid;
    }

}
