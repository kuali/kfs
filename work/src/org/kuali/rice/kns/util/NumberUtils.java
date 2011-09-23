/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.util;

/**
 * 
 */
public class NumberUtils extends org.apache.commons.lang.math.NumberUtils {
    /**
     * @param i
     * @param defaultValue
     * @return intValue of i, or defaultValue if i is null
     */
    public static int intValue(Integer i, int defaultValue) {
        int value = defaultValue;

        if (i != null) {
            value = i.intValue();
        }

        return value;
    }


    /**
     * @param i
     * @param j
     * @return true if both of the integers are null, or point to instances with the same mathematical value
     */
    public static boolean equals(Integer i, Integer j) {
        boolean equal = false;

        if ((i == null) && (j == null)) {
            equal = true;
        }
        else if (i != null) {
            equal = i.equals(j);
        }

        return equal;
    }


    /**
     * @param i
     * @param j
     * @return true if both of the given KualiDecimals are null, or point to instances with the same mathematical value
     */
    public static boolean equals(KualiDecimal j, KualiDecimal k) {
        boolean equal = false;

        if ((j == null) && (k == null)) {
            equal = true;
        }
        else if (j != null) {
            equal = j.equals(k);
        }

        return equal;
    }
}
