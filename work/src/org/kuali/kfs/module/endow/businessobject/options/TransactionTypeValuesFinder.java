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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.core.util.KeyLabelPair;

public class TransactionTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> keyValues = new ArrayList();
        List<String> types = SpringContext.getBean(ParameterService.class).getParameterValues(EndowmentRecurringCashTransfer.class, EndowConstants.EndowmentSystemParameter.ENDOWMENT_RECURRING_CASH_TRANSFER_DOCUMENT_TYPES);

        for (String type : types) {
            keyValues.add(new KeyLabelPair(type, type));
        }

        return keyValues;
    }

}
