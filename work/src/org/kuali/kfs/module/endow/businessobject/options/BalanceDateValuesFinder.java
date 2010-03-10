/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.core.util.KeyLabelPair;

public class BalanceDateValuesFinder extends KeyValuesBase {

    public List getKeyValues() {

        List labels = new ArrayList();
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        // set the balance date to be the current process date
        // TODO Parameter Component will have to change
        String currentProcessDateString = parameterService.getParameterValue(PooledFundValue.class, EndowConstants.EndowmentSystemParameter.CURRENT_PROCESS_DATE);

        labels.add(new KeyLabelPair("", currentProcessDateString));

        return labels;
    }

}
