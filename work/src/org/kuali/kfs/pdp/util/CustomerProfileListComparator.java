/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.util;

import java.util.Comparator;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;

public class CustomerProfileListComparator implements Comparator<CustomerProfile> {

    public int compare(CustomerProfile o1, CustomerProfile o2) {
        String chartCode1 = o1.getChartCode();
        String chartCode2 = o2.getChartCode();
        
        String orgCode1 = o1.getOrgCode();
        String orgCode2 = o2.getOrgCode();
        
        String subUnitCode1 = o1.getSubUnitCode();
        String subUnitCode2 = o2.getSubUnitCode();
        
        if (chartCode1.compareToIgnoreCase(chartCode2) < 0) return -1;
        else if (chartCode1.compareToIgnoreCase(chartCode2) > 0) return 1;
        else {
            if (orgCode1.compareTo(orgCode2) < 0) return -1;
            else if ( orgCode1.compareTo(orgCode2) > 0 ) return 1;
            else {
                if (subUnitCode1.compareTo(subUnitCode2) < 0) return -1;
                else if ( subUnitCode1.compareTo(subUnitCode2) > 0 ) return 1;
            }
        }
        
        return 0;
    }

}
