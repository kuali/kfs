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
package org.kuali.kfs.sys.dataaccess;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * This class is intended to be used for inverting all the boolean values stored in the database before loading them into the
 * business object and vice versa. This functionality is necessary for situations where a database table stores the opposite of the
 * intented boolean attribute. An example is where a given business object has a pre-defined attribute, such as "inactive", while
 * the user wishes to display the value as an 'active' indicator rather than an 'inactive indicator. Ideally, it would be better to
 * replace the field in the database with the appropriate representation of the data so we do not have to perform these confusing
 * conversions on data. Unfortunately, this is not always an option.
 */
public class OjbCharBooleanFieldInverseConversion implements FieldConversion {

    private static final String TRUE = "Y";
    private static final String FALSE = "N";

    /**
     * This method takes the value intended to be passed to the SQL statement and replaces that value with its inverse. Thus TRUE
     * becomes FALSE and vice versa.
     * 
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     */
    public Object javaToSql(Object source) {
        if (source instanceof Boolean) {
            if (source.equals(Boolean.TRUE)) {
                return FALSE;
            }
            else {
                return TRUE;
            }
        }
        else if (source instanceof String) {
            if ("Y".equalsIgnoreCase((String) source)) {
                return FALSE;
            }
            else if ("N".equalsIgnoreCase((String) source)) {
                return TRUE;
            }
        }
        return source;
    }

    /**
     * This method takes the value returned from the database and replaces it with its inverse, thus FALSE becomes TRUE and vice
     * versa.
     * 
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object source) {
        if (source instanceof String) {
            if (TRUE.equals(source)) {
                return Boolean.FALSE;
            }
            else {
                return Boolean.TRUE;
            }
        }
        else {
            return source;
        }
    }

}
