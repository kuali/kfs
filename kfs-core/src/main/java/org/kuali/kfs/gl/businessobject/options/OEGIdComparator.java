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
package org.kuali.kfs.gl.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;

/**
 * A comparator for origin entry groups, based on their group IDs
 */
public class OEGIdComparator implements Comparator {

    /**
     * Constructs a OEGIdComparator
     */
    public OEGIdComparator() {
    }

    /**
     * Compares two origin entry groups based on their group ids
     * 
     * @param c1 the first origin entry group to compare
     * @param c2 the second origin entry group to comare
     * @return a negative if c1's group ID is less than c2's; a zero if the group IDs are equal; a positive number otherwise
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object c1, Object c2) {

        OriginEntryGroup oeg1 = (OriginEntryGroup) c1;
        OriginEntryGroup oeg2 = (OriginEntryGroup) c2;

        return oeg2.getId().compareTo(oeg1.getId());
    }

}
