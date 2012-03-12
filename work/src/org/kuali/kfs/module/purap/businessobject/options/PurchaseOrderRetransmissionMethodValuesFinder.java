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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderTransmissionMethod;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Value Finder for Purchase Order Retransmission Methods.
 */
public class PurchaseOrderRetransmissionMethodValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Purchase Order Retransmission Methods.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<PurchaseOrderTransmissionMethod> codes = boService.findAll(PurchaseOrderTransmissionMethod.class);
        String retransmitTypes = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurchaseOrderDocument.class,PurapParameterConstants.PURAP_PO_RETRANSMIT_TRANSMISSION_METHOD_TYPES);
        List<KeyValue> labels = new ArrayList<KeyValue>();
        if (retransmitTypes != null){
            for (PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod : codes) {
                if (StringUtils.contains(retransmitTypes, StringUtils.left(purchaseOrderTransmissionMethod.getPurchaseOrderTransmissionMethodCode(),4)) &&
                        purchaseOrderTransmissionMethod.isDisplayToUser()) {
                    labels.add(new ConcreteKeyValue(purchaseOrderTransmissionMethod.getPurchaseOrderTransmissionMethodCode(), purchaseOrderTransmissionMethod.getPurchaseOrderTransmissionMethodDescription()));
                }
            }
        }
        return labels;
    }
}
