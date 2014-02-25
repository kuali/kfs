/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.InvoiceTransmissionMethod;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * ValuesFinder for Method of Invoice Transmission.
 */
public class InvoiceTransmissionMethodValuesFinder extends KeyValuesBase {

    protected List<KeyValue> keyValues = new ArrayList();

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {

        List<InvoiceTransmissionMethod> boList = (List<InvoiceTransmissionMethod>) SpringContext.getBean(KeyValuesService.class).findAll(InvoiceTransmissionMethod.class);
        keyValues.add(new ConcreteKeyValue("", ""));
        for (InvoiceTransmissionMethod element : boList) {
            if (element.isActive()) {
                keyValues.add(new ConcreteKeyValue(element.getInvoiceTransmissionMethodCode(), element.getInvoiceTransmissionMethodDescription()));
            }
        }
        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#clearInternalCache()
     */
    @Override
    public void clearInternalCache() {
        keyValues = null;
    }
}
