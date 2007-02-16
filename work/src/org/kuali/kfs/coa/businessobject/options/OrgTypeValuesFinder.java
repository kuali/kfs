/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.chart.bo.OrgType;

public class OrgTypeValuesFinder extends KeyValuesBase {

    /**
     * 
     * Constructs a OrgTypeValuesFinder.java.
     */
    public OrgTypeValuesFinder() {
    }

    /**
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all OrgTypes
        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        List orgTypes = (List) boService.findAll(OrgType.class);

        // calling comparator.
        OrgTypeComparator orgTypeComparator = new OrgTypeComparator();

        // sort using comparator.
        Collections.sort(orgTypes, orgTypeComparator);

        // create a new list (code, descriptive-name)
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", "")); // blank first entry

        for (Iterator iter = orgTypes.iterator(); iter.hasNext();) {
            OrgType orgType = (OrgType) iter.next();
            labels.add(new KeyLabelPair(orgType.getOrganizationTypeCode(), orgType.getOrganizationTypeCode() + " - " + orgType.getOrganizationTypeName()));
        }

        return labels;
    }

}
