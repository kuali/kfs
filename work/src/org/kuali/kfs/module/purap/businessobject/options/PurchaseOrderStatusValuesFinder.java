/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Value Finder for Purchase Order Statuses.
 */
public class PurchaseOrderStatusValuesFinder extends KeyValuesBase {

    /**
     * Overide this method to sort the PO statuses for proper display. 
     * 
     * @see org.kuali.kfs.module.purap.businessobject.options.PurApStatusKeyValuesBase#getKeyValues()
     */
    public List getKeyValues() {
        // get all PO statuses        
        SortedSet<String> incompleteCodes = new TreeSet<String>(PurchaseOrderStatuses.INCOMPLETE_STATUSES);
        SortedSet<String> completeCodes = new TreeSet<String>(PurchaseOrderStatuses.COMPLETE_STATUSES);
                   
        // generate output
        List labels = new ArrayList();
        
        labels.add(new ConcreteKeyValue("INCOMPLETE", "INCOMPLETE STATUSES"));
        for (String status : incompleteCodes) {
            labels.add(new ConcreteKeyValue(status, "- " + status));
        }

        labels.add(new ConcreteKeyValue("COMPLETE", "COMPLETE STATUSES"));
        for (String status : completeCodes) {
            labels.add(new ConcreteKeyValue(status, "- " + status));
        }
                
        return labels;
    }       
    
}
