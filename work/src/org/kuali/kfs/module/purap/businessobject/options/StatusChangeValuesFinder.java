/*
 * Copyright 2006 The Kuali Foundation
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
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Value Finder for manual Purchase Order Status Changes.
 */
public class StatusChangeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all manual Purchase Order Status Changes.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS, VendorConstants.NONE));
        labels.add(new ConcreteKeyValue(PurapConstants.PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT, "Department"));
        labels.add(new ConcreteKeyValue(PurapConstants.PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR, "Vendor"));
        return labels;
    }
}
