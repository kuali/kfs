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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.sql.Date;

import org.apache.log4j.Logger;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;


/**
 * create methods for building SQL useful to all extenders
 */
public class BudgetConstructionDaoJdbcBase extends PlatformAwareDaoBaseJdbc {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDaoJdbcBase.class);

    private String ojbPlatform;
    private String ojbOraclePlatform;

    private StringBuilder[] oracleSubString = { new StringBuilder("SUBSTR("), new StringBuilder(","), new StringBuilder(","), new StringBuilder(")") };
    private StringBuilder[] ansi92SubString = { new StringBuilder("SUBSTRING("), new StringBuilder(" FROM "), new StringBuilder(" FOR "), new StringBuilder(")") };
    private String dateFetcher = new String("SELECT MIN(UNIV_DT) FROM SH_UNIV_DATE_T WHERE (UNIV_FISCAL_YR = ?)");

    protected void clearTempTableByUnvlId(String tableName, String personUnvlIdColumn, String principalName) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + personUnvlIdColumn + " = ?", principalName);
    }

    protected void clearTempTableBySesId(String tableName, String SesIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + SesIdColumn + " = ?", sessionId);
    }


    /**
     * given a fiscal year, get the first day of that fiscal year
     * 
     * @param universityFiscalYear = fiscal year (must be in the table)
     * @return the date on which the fiscal year passed as a parameter starts
     */
    protected Date getFiscalYearStartDate(Integer universityFiscalYear) {
        return getSimpleJdbcTemplate().queryForObject(dateFetcher, Date.class, universityFiscalYear);
    }


    /**
     * return a substring function that is Oracle-specific if the DB Platform is Oracle, and an ANSI-92 compliant function otherwise
     * Oracle's syntax is not ANSI-92 compliant
     * 
     * @param fieldName = string representing the name of the DB field (possibly qualified)
     * @param startLocation = starting location of the substring
     * @param substringLength = length of the substring
     * @return the substring function
     */
    protected StringBuilder getSqlSubStringFunction(String fieldName, Integer startLocation, Integer substringLength) {
        boolean oracleDB = ojbPlatform.equals(ojbOraclePlatform);
        StringBuilder subStringer = new StringBuilder(40);
        String start = startLocation.toString();
        String span = substringLength.toString();
        if (oracleDB) {
            subStringer.append(oracleSubString[0]);
            subStringer.append(fieldName);
            subStringer.append(oracleSubString[1]);
            subStringer.append(start);
            subStringer.append(oracleSubString[2]);
            subStringer.append(span);
            subStringer.append(oracleSubString[3]);
        }
        else {
            subStringer.append(ansi92SubString[0]);
            subStringer.append(fieldName);
            subStringer.append(ansi92SubString[1]);
            subStringer.append(start);
            subStringer.append(ansi92SubString[2]);
            subStringer.append(span);
            subStringer.append(ansi92SubString[3]);
        }
        return subStringer;
    }


    public void setOjbPlatform(String ojbPlatform) {
        this.ojbPlatform = ojbPlatform;
    }

    public void setOjbOraclePlatform(String ojbOraclePlatform) {
        this.ojbOraclePlatform = ojbOraclePlatform;
    }
}
