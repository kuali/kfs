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
package org.kuali.kfs.sys.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

/**
 * Service implementation for the PostalCodeBase structure. This is the default implementation, that is delivered with Kuali.
 */

@NonTransactional
public class PostalCodeValidationServiceImpl implements PostalCodeValidationService {

    public boolean validateAddress(String postalCountryCode, String stateCode, String postalCode, String statePropertyConstant, String postalCodePropertyConstant) {
        boolean valid = true;

        if (StringUtils.equals(KFSConstants.COUNTRY_CODE_UNITED_STATES, postalCountryCode)) {

            if (StringUtils.isBlank(stateCode)) {
                valid &= false;
                if (StringUtils.isNotBlank(statePropertyConstant)) {
                    GlobalVariables.getMessageMap().putError(statePropertyConstant, KFSKeyConstants.ERROR_US_REQUIRES_STATE);
                }
            }

            if (StringUtils.isBlank(postalCode)) {
                valid &= false;
                if (StringUtils.isNotBlank(postalCodePropertyConstant)) {
                    GlobalVariables.getMessageMap().putError(postalCodePropertyConstant, KFSKeyConstants.ERROR_US_REQUIRES_ZIP);
                }
            }
            else {
                ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
                if (!zipPattern.matches(StringUtils.defaultString(postalCode))) {
                    valid &= false;
                    if (StringUtils.isNotBlank(postalCodePropertyConstant)) {
                        GlobalVariables.getMessageMap().putError(postalCodePropertyConstant, KFSKeyConstants.ERROR_POSTAL_CODE_INVALID);
                    }
                }
            }

        }

        // verify state code exist
        if (StringUtils.isNotBlank(postalCountryCode) && StringUtils.isNotBlank(stateCode)) {
            State state = SpringContext.getBean(StateService.class).getState(postalCountryCode, stateCode);
            if (state == null) {
                GlobalVariables.getMessageMap().putError(statePropertyConstant, KFSKeyConstants.ERROR_STATE_CODE_INVALID, stateCode);
            }
        }
        
        return valid;
    }


}
