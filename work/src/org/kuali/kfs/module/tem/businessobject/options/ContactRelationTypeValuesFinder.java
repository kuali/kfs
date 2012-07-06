/*
 * Copyright 2010 The Kuali Foundation.
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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.ContactRelationType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class ContactRelationTypeValuesFinder extends KeyValuesBase {

    @Override
    public List getKeyValues() {
        List keyValues = new ArrayList();

        Collection<ContactRelationType> bos = SpringContext.getBean(BusinessObjectService.class).findAll(ContactRelationType.class);

        keyValues.add(new KeyLabelPair("", ""));
        for (ContactRelationType typ : bos) {
            keyValues.add(new KeyLabelPair(typ.getCode(), typ.getName()));
        }

        return keyValues;
    }

}
