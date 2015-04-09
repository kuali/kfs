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

import org.kuali.kfs.sys.KFSConstants.BudgetConstructionPositionConstants;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;

/**
 * Handles conversion of active indicator so as to piggy back onto the existing position effective status. Converts database values
 * of A and I to java Y and N and back.
 */
public class OjbBCPositionActiveIndicatorConversion extends OjbCharBooleanConversion {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OjbBCPositionActiveIndicatorConversion.class);

    public final static String INDICATOR_NO = "N";
    public final static String INDICATOR_YES = "Y";

    /**
     * @see org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion#javaToSql(java.lang.Object)
     */
    @Override
    public Object javaToSql(Object source) {
        Object sqlValue = super.javaToSql(source);

        if (INDICATOR_NO.equals(sqlValue)) {
            return BudgetConstructionPositionConstants.POSITION_EFFECTIVE_STATUS_INACTIVE;
        }
        else if (INDICATOR_YES.equals(sqlValue)) {
            return BudgetConstructionPositionConstants.POSITION_EFFECTIVE_STATUS_ACTIVE;
        }

        return sqlValue;
    }

    /**
     * @see org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion#sqlToJava(java.lang.Object)
     */
    @Override
    public Object sqlToJava(Object source) {
        try {
            if (source instanceof String) {
                if (source != null) {
                    String s = (String) source;
                    return Boolean.valueOf(BudgetConstructionPositionConstants.POSITION_EFFECTIVE_STATUS_ACTIVE.indexOf(s) >= 0);
                }
                else {
                    return null;
                }
            }
            return source;
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("I have exploded converting types", t);
        }
    }

}
