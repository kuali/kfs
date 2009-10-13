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
package org.kuali.kfs.coa.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.coa.businessobject.OrganizationType;

/**
 * This class allows us to compare two {@link OrgType}s by their codes
 */
public class OrgTypeComparator implements Comparator {

    public OrgTypeComparator() {
    }

    public int compare(Object o1, Object o2) {

        OrganizationType orgType1 = (OrganizationType) o1;
        OrganizationType orgType2 = (OrganizationType) o2;

        return orgType1.getOrganizationTypeCode().compareTo(orgType2.getOrganizationTypeCode());
    }

}
