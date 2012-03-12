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
package org.kuali.kfs.pdp.businessobject.defaultvalue;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Returns the next ACH Account identifier available.
 */
public class AchBankDefaultInstitutionCodeFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return PdpConstants.ACH_BANK_DATA_VIEW_CODE_DEFAULT;
    }

}
