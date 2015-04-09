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
