/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AdvancePaymentReason;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class AdvancePaymentReasonValuesFinder extends KeyValuesBase {
    protected static volatile KeyValuesService keyValuesService;

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> list = new ArrayList<KeyValue>();

        Collection<AdvancePaymentReason> reasons = getKeyValuesService().findAll(AdvancePaymentReason.class);
        list.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        for (AdvancePaymentReason reason : reasons){
            list.add(new ConcreteKeyValue(reason.getCode(), reason.getCode() + " - " + reason.getDescription()));
        }
        return list;
    }

    protected KeyValuesService getKeyValuesService() {
        if (keyValuesService == null) {
            keyValuesService = SpringContext.getBean(KeyValuesService.class);
        }
        return keyValuesService;
    }
}
