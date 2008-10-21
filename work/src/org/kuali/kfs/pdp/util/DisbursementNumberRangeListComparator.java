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

import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;

public class DisbursementNumberRangeListComparator implements Comparator<DisbursementNumberRange> {

    public int compare(DisbursementNumberRange o1, DisbursementNumberRange o2) {
        String physCampusProcCode1 = o1.getPhysCampusProcCode();
        String physCampusProcCode2 = o2.getPhysCampusProcCode();
        
        String disbursementTypeCode1 = o1.getDisbursementTypeCode();
        String disbursementTypeCode2 = o2.getDisbursementTypeCode();
        
        if (physCampusProcCode1.compareToIgnoreCase(physCampusProcCode2) < 0) return -1;
        else if (physCampusProcCode1.compareToIgnoreCase(physCampusProcCode2) > 0) return 1;
        else {
            if (disbursementTypeCode1.compareTo(disbursementTypeCode2) < 0) return -1;
            else if ( disbursementTypeCode1.compareTo(disbursementTypeCode2) > 0 ) return 1;
        }
        
        return 0;
    }

}
