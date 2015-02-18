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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.coa.businessobject.OrganizationType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link OrgType}s)
 */
public class OrgTypeValuesFinder extends KeyValuesBase {

    /**
     * Constructs a OrgTypeValuesFinder.java.
     */
    public OrgTypeValuesFinder() {
    }

    /**
     * Creates a list of {@link OrgType}s using their code as their key, and their code "-" name as the display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all OrgTypes
        List<OrganizationType> codes = (List<OrganizationType>) SpringContext.getBean(KeyValuesService.class).findAll(OrganizationType.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( codes == null ) {
            codes = new ArrayList<OrganizationType>(0);
        } else {
            codes = new ArrayList<OrganizationType>( codes );
        }

        // sort using comparator.
        Collections.sort(codes, new OrgTypeComparator());

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", "")); // blank first entry

        for (OrganizationType orgType : codes) {
            if(orgType.isActive()) {
                labels.add(new ConcreteKeyValue(orgType.getOrganizationTypeCode(), orgType.getOrganizationTypeCode() + " - " + orgType.getOrganizationTypeName()));
            }
        }

        return labels;
    }

}
