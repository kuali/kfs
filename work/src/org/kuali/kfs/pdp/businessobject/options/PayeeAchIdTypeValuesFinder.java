/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PayeeType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public class PayeeAchIdTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyLabelPair> getKeyValues() {
        List<PayeeType> boList = (List) SpringContext.getBean(KeyValuesService.class).findAll(PayeeType.class);
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));
        for (PayeeType element : boList) {
            if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(element.getCode()) || PdpConstants.PayeeIdTypeCodes.ENTITY_ID.equals(element.getCode())) {
                keyValues.add(new KeyLabelPair(element.getCode(), element.getName()));
            }
        }

        return keyValues;

    }

}
