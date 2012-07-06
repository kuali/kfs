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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AdvancePaymentReason;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AdvancePaymentReasonValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> list = new ArrayList<KeyLabelPair>();
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        fieldValues.put("active", "Y");
        BusinessObjectService service = (BusinessObjectService)SpringContext.getService("businessObjectService");
        List<AdvancePaymentReason> reasons = (List<AdvancePaymentReason>) service.findMatching(AdvancePaymentReason.class, fieldValues);
        for (AdvancePaymentReason reason : reasons){
            list.add(new KeyLabelPair(reason.getCode(), reason.getCode() + " - " + reason.getDescription()));
        }
        return list;
    }
}
