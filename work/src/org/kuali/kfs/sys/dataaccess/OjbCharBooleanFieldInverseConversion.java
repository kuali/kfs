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
