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

import java.util.Comparator;

import org.kuali.module.chart.bo.OrgType;

public class OrgTypeComparator implements Comparator {

    public OrgTypeComparator() {
    }

    public int compare(Object o1, Object o2) {

        OrgType orgType1 = (OrgType) o1;
        OrgType orgType2 = (OrgType) o2;

        return orgType1.getOrganizationTypeCode().compareTo(orgType2.getOrganizationTypeCode());
    }

}
