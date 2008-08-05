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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionDuration;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class...
 */
public class BudgetConstructionDurationValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<BudgetConstructionDuration> budgetConstructionDurationCodes = boService.findAll(BudgetConstructionDuration.class);

        List<KeyLabelPair> durationKeyLabels = new ArrayList<KeyLabelPair>();
        for (BudgetConstructionDuration budgetConstructionDurationCode : budgetConstructionDurationCodes) {
            String code = budgetConstructionDurationCode.getAppointmentDurationCode();
            
            if(StringUtils.equals(code, BCConstants.AppointmentFundingDurationCodes.NONE.durationCode)) {
                durationKeyLabels.add(0, new KeyLabelPair(code, code));
            }
            else {
                durationKeyLabels.add(new KeyLabelPair(code, code));
            }
        }

        return durationKeyLabels;
    }
}
