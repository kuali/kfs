/*
 * Copyright 2008 The Kuali Foundation
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
