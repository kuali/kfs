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
package org.kuali.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.util.KRADPropertyConstants;

/**
 * Values finder for <code>ContractManager</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.ContractManager
 */
public class ContractManagerValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Map fieldValues = new HashMap();
        fieldValues.put(KRADPropertyConstants.ACTIVE, true);
        Collection codes = boService.findMatching(ContractManager.class, fieldValues);
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            ContractManager ContractManager = (ContractManager) iter.next();
            labels.add(new ConcreteKeyValue(ContractManager.getContractManagerCode().toString(), ContractManager.getContractManagerName()));
        }

        return labels;
    }

}
