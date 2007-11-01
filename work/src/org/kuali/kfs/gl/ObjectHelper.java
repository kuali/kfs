/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.util;

public class ObjectHelper {
    protected ObjectHelper() {
    }

    /**
     * @param lhs
     * @param rhs
     * @return true if both lhs and rhs are null or if lhs.equals(rhs)
     */
    static public boolean isEqual(Object lhs, Object rhs) {
        return (null == lhs && null == rhs) || (null != lhs && lhs.equals(rhs));
    }

    /**
     * @param lhs
     * @param rhs
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
