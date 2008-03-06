/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.dao.jdbc;

import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.core.dbplatform.RawSQL;

/**
 * create methods for building SQL useful to all extenders
 */
public class BudgetConstructionDaoJdbcBase extends PlatformAwareDaoBaseJdbc {

    @RawSQL
    protected void clearTempTableByUnvlId(String tableName, String personUnvlIdColumn, String personUserIdentifier) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + personUnvlIdColumn + " = ?", personUserIdentifier);
    }

    @RawSQL
    protected void clearTempTableBySesId(String tableName, String SesIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + SesIdColumn + " = ?", sessionId);
    }
    
    /**
     * 
     * build a string of placeholders for a parameterized java.sql IN clause
     * @param parameterCount the number of parameters in the IN clause
     * @return the String (?,?,?) with the correct nubmer of parameters
     */       
    @RawSQL
    protected String inString(Integer parameterCount)
    {
        // there should be at least one parameter in the IN string
        // but allow people to screw up and have an IN condition which is never satisfied
        if (parameterCount == 0)
        {
            return new String("('')");
        }
        StringBuilder sb = new StringBuilder(20);
        sb = sb.append("(?");
        for (int i = 1; i < parameterCount; i++)
        {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();
    }

}
