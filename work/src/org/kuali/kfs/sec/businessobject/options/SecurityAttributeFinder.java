/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sec.businessobject.SecurityAttribute;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;


/**
 * Returns list of security Attributes
 */
public class SecurityAttributeFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();

        Collection securityAttributes = SpringContext.getBean(KeyValuesService.class).findAllOrderBy(SecurityAttribute.class, KFSPropertyConstants.ID, true);
        for (Iterator iterator = securityAttributes.iterator(); iterator.hasNext();) {
            SecurityAttribute securityAttribute = (SecurityAttribute) iterator.next();
            activeLabels.add(new ConcreteKeyValue(securityAttribute.getId().toString(), securityAttribute.getName()));
        }

        return activeLabels;
    }
}
