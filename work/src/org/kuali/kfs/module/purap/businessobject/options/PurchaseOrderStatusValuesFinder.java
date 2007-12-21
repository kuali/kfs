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
package org.kuali.module.purap.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.PurchaseOrderStatus;
import org.kuali.module.purap.bo.Status;

/**
 * Value Finder for Purchase Order Statuses.
 */
public class PurchaseOrderStatusValuesFinder extends PurApStatusKeyValuesBase {

    /**
     * Overide this method to sort the PO statuses for proper display. 
     * 
     * @see org.kuali.module.purap.lookup.keyvalues.PurApStatusKeyValuesBase#getKeyValues()
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
        
        /*
        // since workflow reads and displays the statuses in the "right and down" order,
        // while the user wants to read the statuses in the "down and over" order,
        // we need to reorder the status list to feed the workflow 
        // so that they come out right as the user wants 
        statuses.clear();
        int ncol = 3; // the number of columns when PO statuses are displayed
        int nrow = ns / ncol; // the number of rows when PO statuses are displayed, assuming that ns % ncol = 0
        int count = 0;
        for (int i=0; i<nrow; i++) {
            for (int j=0; j<ncol; j++) {
                statuses.add(sorts[j*nrow+i]);
                count++;
            }
        }
        // in case  ns % ncol != 0, process the leftover, even though the order will be messed up
        for (int n=count; n<ns; n++)
            statuses.add(sorts[n]);
        */
        
        // generate output
        List labels = new ArrayList();
        for (Status status : sortStatuses) {
            if (status.isActive()) {
                labels.add(new KeyLabelPair(status.getStatusCode(), status.getStatusDescription()));
            }
        }
        return labels;
    }
    
    /**
     * @see org.kuali.module.purap.lookup.keyvalues.PurApStatusKeyValuesBase#getStatusClass()
     */
    @Override
    public Class getStatusClass() {
        return PurchaseOrderStatus.class;
    }
}