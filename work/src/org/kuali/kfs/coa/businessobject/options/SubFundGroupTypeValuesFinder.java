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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.SubFundGroupType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link SubFundGroupType}s)
 */
public class SubFundGroupTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link SubFundGroupType}s using their code as their key, and their code "-" description as the display
     * value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Collection<SubFundGroupType> subFundGroupTypeCodes = SpringContext.getBean(KeyValuesService.class).findAll(SubFundGroupType.class);
        List<KeyValue> subFundGroupTypeKeyLabels = new ArrayList<KeyValue>();
        subFundGroupTypeKeyLabels.add(new ConcreteKeyValue("", ""));
        for (SubFundGroupType element : subFundGroupTypeCodes) {
            if(element.isActive()) {
                subFundGroupTypeKeyLabels.add(new ConcreteKeyValue(element.getSubFundGroupTypeCode(), element.getSubFundGroupTypeCode() + " - " + element.getSubFundGroupTypeDescription()));
            }
        }

        return subFundGroupTypeKeyLabels;
    }

}
