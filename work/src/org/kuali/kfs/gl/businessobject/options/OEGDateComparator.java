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
package org.kuali.kfs.gl.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;

/**
 * A comparator for two origin entry groups, based on the dates of their creations
 */
public class OEGDateComparator implements Comparator {

    /**
     * Constructs a OEGDateComparator
     */
    public OEGDateComparator() {
    }

    /**
     * Compares two origin entry groups, based on the dates of their creation
     * 
     * @param c1 the first origin entry group to compare
     * @param c2 you can't really compare without two origin groups
     * @return 0 if the creation dates are equal, a negative number if c1's creation date is less
     * than c2's; a positive number otherwise
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object c1, Object c2) {

        OriginEntryGroup oeg1 = (OriginEntryGroup) c1;
        OriginEntryGroup oeg2 = (OriginEntryGroup) c2;

        return oeg2.getDate().compareTo(oeg1.getDate());
    }

}
