/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * a value finder built from the budget construction appointment funding reason codes
 */
public class AppointmentFundingReasonValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<BudgetConstructionAppointmentFundingReasonCode> reasonCodes = boService.findAll(BudgetConstructionAppointmentFundingReasonCode.class);

        List<KeyValue> reasonCodeKeyLabels = new ArrayList<KeyValue>();
        reasonCodeKeyLabels.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));

        for (BudgetConstructionAppointmentFundingReasonCode reasonCode : reasonCodes) {
            if (!reasonCode.isActive()) {
                continue;
            }

            String code = reasonCode.getAppointmentFundingReasonCode();
            reasonCodeKeyLabels.add(new ConcreteKeyValue(code, code));
        }

        return reasonCodeKeyLabels;
    }
}
