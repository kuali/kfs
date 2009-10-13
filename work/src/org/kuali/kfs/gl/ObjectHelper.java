/*
 * Copyright 2006 The Kuali Foundation
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
