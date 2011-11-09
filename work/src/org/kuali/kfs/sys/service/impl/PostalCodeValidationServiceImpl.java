/*
 * Copyright 2007-2009 The Kuali Foundation
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
