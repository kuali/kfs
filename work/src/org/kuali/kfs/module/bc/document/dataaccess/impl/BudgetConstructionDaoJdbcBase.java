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
