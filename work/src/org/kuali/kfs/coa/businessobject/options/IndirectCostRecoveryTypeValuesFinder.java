/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link ICRTypeCode}s)
 */
public class IndirectCostRecoveryTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link ICRTypeCode}s using their code as their key, and their code "-" name as the display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<IndirectCostRecoveryType> codes = (List<IndirectCostRecoveryType>) SpringContext.getBean(KeyValuesService.class).findAll(IndirectCostRecoveryType.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( codes == null ) {
            codes = new ArrayList<IndirectCostRecoveryType>(0);
        } else {
            codes = new ArrayList<IndirectCostRecoveryType>( codes );
        }

        // sort using comparator.
        Collections.sort(codes, new IndirectCostRecoveryTypeCodeComparator());

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        for (IndirectCostRecoveryType icrType : codes) {
            if(icrType.isActive()) {
                labels.add(new ConcreteKeyValue(icrType.getCode(), icrType.getCodeAndDescription()));
            }
        }

        return labels;
    }

}
