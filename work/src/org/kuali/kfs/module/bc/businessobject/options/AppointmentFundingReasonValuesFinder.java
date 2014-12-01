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
