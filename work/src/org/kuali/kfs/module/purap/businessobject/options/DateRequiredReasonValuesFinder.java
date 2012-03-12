/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Value Finder for Date Required Reasons.
 */
public class DateRequiredReasonValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Date Required Reasons.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes = boService.findAll(DeliveryRequiredDateReason.class);
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            DeliveryRequiredDateReason reason = (DeliveryRequiredDateReason) iter.next();
            labels.add(new ConcreteKeyValue(reason.getDeliveryRequiredDateReasonCode(), reason.getDeliveryRequiredDateReasonDescription()));
        }

        return labels;
    }
}
