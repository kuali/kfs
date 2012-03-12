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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.Status;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Base Value Finder for Purchasing / Accounts Payable Statuses.
 */
public class PurApStatusKeyValuesBase extends KeyValuesBase {

    /**
     * Returns code/description pairs of all PurAp Statuses.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<Status> statuses = boService.findAll(getStatusClass());
        List labels = new ArrayList();
        for (Status status : statuses) {
            labels.add(new ConcreteKeyValue(status.getStatusCode(), status.getStatusDescription()));
        }
        return labels;
    }

    /**
     * Returns Status class for this Value Finder.
     */
    public Class getStatusClass() {
        // method must be overriden
        throw new RuntimeException("getStatusClass() method must be overridden to be used");
    }
}
