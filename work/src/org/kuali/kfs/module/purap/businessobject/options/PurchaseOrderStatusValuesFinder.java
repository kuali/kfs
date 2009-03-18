/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderStatus;
import org.kuali.kfs.module.purap.businessobject.Status;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * Value Finder for Purchase Order Statuses.
 */
public class PurchaseOrderStatusValuesFinder extends PurApStatusKeyValuesBase {

    /**
     * Overide this method to sort the PO statuses for proper display. 
     * 
     * @see org.kuali.kfs.module.purap.businessobject.options.PurApStatusKeyValuesBase#getKeyValues()
     */
    public List getKeyValues() {
        // get all PO statuses
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<Status> statuses = boService.findAll(getStatusClass());
        
        Set<String> incompleteCodes = PurchaseOrderStatuses.INCOMPLETE_STATUSES;
        Set<String> completeCodes = PurchaseOrderStatuses.COMPLETE_STATUSES;

        List<Status> incompleteStatus = new ArrayList<Status>();
        List<Status> completeStatus = new ArrayList<Status>();
        List<Status> unsorted = new ArrayList<Status>();
        
        for (Status status : statuses) {
            if (incompleteCodes.contains(status.getStatusCode())) {
                incompleteStatus.add(status);
            } else if (completeCodes.contains(status.getStatusCode())) {
                completeStatus.add(status);
            } else {
                unsorted.add(status);
            }
        }
        
        Comparator<Status> comparator = new Status();
        Collections.sort(incompleteStatus, comparator);
        Collections.sort(completeStatus, comparator);
        Collections.sort(unsorted, comparator);
           
        // generate output
        List labels = new ArrayList();
        
        labels.add(new KeyLabelPair("INCOMPLETE", "Incomplete Statuses"));
        for (Status status : incompleteStatus) {
            labels.add(new KeyLabelPair(status.getStatusCode(), "- "+status.getStatusDescription()));
        }

        labels.add(new KeyLabelPair("COMPLETE", "CompleteStatuses"));
        for (Status status : completeStatus) {
            labels.add(new KeyLabelPair(status.getStatusCode(), "- "+status.getStatusDescription()));
        }
        
        if (!unsorted.isEmpty())
            labels.add(new KeyLabelPair("UNSORTED", "Unsorted Statuses"));
        for (Status status : unsorted) {
            labels.add(new KeyLabelPair(status.getStatusCode(), "- "+status.getStatusDescription()));
        }
        
        
        return labels;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.businessobject.options.PurApStatusKeyValuesBase#getStatusClass()
     */
    @Override
    public Class getStatusClass() {
        return PurchaseOrderStatus.class;
    }
    
    
}
