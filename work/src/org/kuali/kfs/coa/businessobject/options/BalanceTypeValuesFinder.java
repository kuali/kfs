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

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of balance type value pairs.
 */
public class BalanceTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link BalanceTyp) with their code as their key and display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Collection<BalanceType> balanceTypeCodeCollection = SpringContext.getBean(BalanceTypeService.class).getAllBalanceTypes();
        List<KeyValue> balanceTypeCodes = new ArrayList<KeyValue>();
        balanceTypeCodes.add(new ConcreteKeyValue("", ""));

        for (BalanceType balanceType : balanceTypeCodeCollection) {
            if(balanceType.isActive()) {
                balanceTypeCodes.add(new ConcreteKeyValue(balanceType.getCode(), balanceType.getCodeAndDescription()));
            }
        }
        return balanceTypeCodes;
    }
}
