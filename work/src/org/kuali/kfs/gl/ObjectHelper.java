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
package org.kuali.kfs.gl;

/**
 * This class is used to compare objects with one another
 */
public class ObjectHelper {
    protected ObjectHelper() {
    }

    /**
     * Returns true if object on left side is equal to object on right side
     * 
     * @param lhs object on left side of equation
     * @param rhs object on right side of equation
     * @return true if both lhs and rhs are null or if lhs.equals(rhs)
     */
    static public boolean isEqual(Object lhs, Object rhs) {
        return (null == lhs && null == rhs) || (null != lhs && lhs.equals(rhs));
    }

    /**
     * Return true if object on left side is one of the items in array of objects
     * 
     * @param lhs object on left side of equation
     * @param rhs object on right side of equation
     * @return false if rhs is null. true if isEqual(lhs, rhs[i]) for any ith element of rhs.
     */
    static public boolean isOneOf(Object lhs, Object[] rhs) {
        if (rhs == null)
            return false;

        // simple linear search. Arrays.binarySearch isn't appropriate
        // because the elements of rhs aren't in natural order.
        for (int i = 0; i < rhs.length; i++) {
            if (isEqual(lhs, rhs[i])) {
                return true;
            }
        }

        return false;
    }
}
