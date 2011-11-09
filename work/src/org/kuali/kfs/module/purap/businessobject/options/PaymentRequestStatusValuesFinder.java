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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PaymentRequestStatus;
import org.kuali.kfs.module.purap.businessobject.Status;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Value Finder for Payment Request Statuses.
 */
public class PaymentRequestStatusValuesFinder extends PurApStatusKeyValuesBase {

    /**
     * Overide this method to sort the PO statuses for proper display. 
     * 
     * @see org.kuali.kfs.module.purap.businessobject.options.PurApStatusKeyValuesBase#getKeyValues()
     */
    public List getKeyValues() {
        // get all PO statuses
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<Status> statuses = boService.findAll(getStatusClass());
        
        // sort the statuses according to their codes alphabetically
        int ns = statuses.size();
        Status[] sortStatuses = new Status[ns];
        if ( ns > 0 ) {
            sortStatuses = (Status[])statuses.toArray(sortStatuses);
            Arrays.sort(sortStatuses, sortStatuses[0]);       
        }        
        
        // generate output
        List labels = new ArrayList();
        for (Status status : sortStatuses) {
            labels.add(new ConcreteKeyValue(status.getStatusCode(), status.getStatusDescription()));
        }
        return labels;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.businessobject.options.PurApStatusKeyValuesBase#getStatusClass()
     */
    @Override
    public Class getStatusClass() {
        return PaymentRequestStatus.class;
    }
}
