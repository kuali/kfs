/*
 * Copyright 2006-2008 The Kuali Foundation
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

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * This class converts the "A" or "I" value from the database into a true or false in Java.
 * 
 * 
 */
public class OjbCharBooleanFieldAIConversion implements FieldConversion {
    private static final String TRUE = "A";
    private static final String FALSE = "I";

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     */
    public Object javaToSql(Object source) {
        if (source instanceof Boolean) {
            if (source.equals(Boolean.TRUE)) {
                return TRUE;
            }
            else {
                return FALSE;
            }
        }
        else if (source instanceof String) {
            if ("Y".equalsIgnoreCase((String)source)) {
                return TRUE;
            }
            else if ("N".equalsIgnoreCase((String)source)) {
                return FALSE;
            }
        }
        return source;
    }

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object source) {
        if (source instanceof String) {
            if (TRUE.equals(source)) {
                return Boolean.TRUE;
            }
            else {
                return Boolean.FALSE;
            }
        }
        else {
            return source;
        }
    }

}
