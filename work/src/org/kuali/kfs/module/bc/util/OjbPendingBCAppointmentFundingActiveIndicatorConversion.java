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
package org.kuali.kfs.module.bc.util;

import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;

/**
 * Handles conversion of active indicator so as to piggy back onto appointmentFundingDeleteIndicator by reversing the values.
 * Converts database delete values of Y and N to java active N and Y and back.
 */
public class OjbPendingBCAppointmentFundingActiveIndicatorConversion extends OjbCharBooleanConversion {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OjbPendingBCAppointmentFundingActiveIndicatorConversion.class);

    public final static String INDICATOR_NO  = "N";
    public final static String INDICATOR_YES = "Y";
    
    /**
     * This handles checking the boolean value coming in and converts it to 
     * the appropriate Y or N value.
     * @see FieldConversion#javaToSql(Object)
     */
    @Override
    public Object javaToSql(Object source) {
        Object sqlValue = super.javaToSql(source);
        
        if(INDICATOR_NO.equals(sqlValue)) {
            return INDICATOR_YES;
        }
        else if(INDICATOR_YES.equals(sqlValue)) {
            return INDICATOR_NO;
        }

        return sqlValue;
    }

    /**
     * This handles checking the sql coming back from the database and converting 
     * it to the appropriate boolean true or false value.
     * @see FieldConversion#sqlToJava(Object)
     */
    @Override
    public Object sqlToJava(Object source) {
        Object javaValue = super.sqlToJava(source);      
        
        if(javaValue == null) {
            return null;
        }
        else if(Boolean.TRUE.equals(javaValue)) {
            return Boolean.FALSE;
        }
        else if(Boolean.FALSE.equals(javaValue)) { 
            return Boolean.TRUE;
        }
         
        return javaValue;
    }
}
