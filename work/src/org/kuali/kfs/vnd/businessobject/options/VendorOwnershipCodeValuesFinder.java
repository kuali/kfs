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
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.OwnershipType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Values Finder for <code>OwnershipType</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.OwnershipType
 */
public class VendorOwnershipCodeValuesFinder extends KeyValuesBase {

    /***
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        return getKeyValues(true);
    }

    /***
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#getKeyValues(boolean)
     */
    public List getKeyValues(boolean active){
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes;
        List labels = new ArrayList();
        if(active){
            codes = boService.findAll(OwnershipType.class);
            labels.add(new ConcreteKeyValue("", ""));
        } else
            codes = boService.findAllInactive(OwnershipType.class);
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            OwnershipType ot = (OwnershipType) iter.next();
            labels.add(new ConcreteKeyValue(ot.getVendorOwnershipCode(), ot.getVendorOwnershipDescription()));
        }
        return labels;
    }

}
