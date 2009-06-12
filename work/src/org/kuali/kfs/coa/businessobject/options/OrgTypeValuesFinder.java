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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.coa.businessobject.OrganizationType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

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
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", "")); // blank first entry

        for (OrganizationType orgType : codes) {
            if(orgType.isActive()) {
                labels.add(new KeyLabelPair(orgType.getOrganizationTypeCode(), orgType.getOrganizationTypeCode() + " - " + orgType.getOrganizationTypeName()));
            }
        }

        return labels;
    }

}
