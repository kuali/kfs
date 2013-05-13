/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class defines a values finder for Type Restriction Code.
 */
public class TypeRestrictionCodeWithoutPermanentlyRestrictedValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<TypeRestrictionCode> codes = boService.findAll(TypeRestrictionCode.class);
        List<KeyValue> labels = new ArrayList<KeyValue>();
        
        labels.add(new ConcreteKeyValue("", ""));
        for (Iterator<TypeRestrictionCode> iter = codes.iterator(); iter.hasNext();) {
            TypeRestrictionCode typeRestrictionCode = (TypeRestrictionCode) iter.next();
            if (!typeRestrictionCode.getCode().equalsIgnoreCase(EndowConstants.TypeRestrictionPresetValueCodes.PERMANENT_TYPE_RESTRICTION_CODE)) {
                labels.add(new ConcreteKeyValue(typeRestrictionCode.getCode(), typeRestrictionCode.getName()));
            }
        }

        return labels;
    }   
}
