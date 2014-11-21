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
