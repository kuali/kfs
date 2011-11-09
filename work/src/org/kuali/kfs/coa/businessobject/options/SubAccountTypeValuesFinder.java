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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link SubAccount}s) It only pulls {@link SubAccount}s
 * that are allowed codes from our {@link ParameterService}
 */
public class SubAccountTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates an allowed list of {@link SubAccount}s from the {@link ParameterService}
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     * @see ParameterService
     * @see KFSConstants.ChartApcParms.CG_ALLOWED_SUBACCOUNT_TYPE_CODES
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue("", ""));
        for (String value : KFSConstants.SubAccountType.ELIGIBLE_SUB_ACCOUNT_TYPE_CODES) {
            activeLabels.add(new ConcreteKeyValue(value, value));
        }
        return activeLabels;
    }
}
