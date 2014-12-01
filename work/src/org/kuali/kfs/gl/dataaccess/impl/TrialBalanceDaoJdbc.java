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
package org.kuali.kfs.gl.dataaccess.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.TrialBalanceReport;
import org.kuali.kfs.gl.dataaccess.TrialBalanceDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * A class to do the database queries needed to calculate Balance By Consolidation Balance Inquiry Screen
 */
public class TrialBalanceDaoJdbc extends PlatformAwareDaoBaseJdbc implements TrialBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceDaoJdbc.class);



    /*
     * Helper method used to build the YTD sum depending on the selected fiscal period
     * If the the period code specified is either empty or invalid, return the current balance amount + begining balance
     * Actuals to be totaled by BB + period1 Total + period2 Total + etc...
     *
     * @param periodCode
     * @return
     */
    private static String buildYTDQueryString( String periodCode ){

        if ( StringUtils.isBlank(periodCode)) {
            return " SUM(A0.FIN_BEG_BAL_LN_AMT + A0.ACLN_ANNL_BAL_AMT)";
        }

        int number = 0;
        try {
            number = Integer.parseInt( periodCode );
        } catch (NumberFormatException e){
            //if periodCode is not a number, then consider it blank
            return " SUM(A0.FIN_BEG_BAL_LN_AMT + A0.ACLN_ANNL_BAL_AMT)";
        }

        StringBuilder ytdQuery = new StringBuilder(" SUM(A0.FIN_BEG_BAL_LN_AMT + ");
        for ( int i = 1; i<=number; i++){
            ytdQuery.append("MO" + i);
            ytdQuery.append( i<number?"_ACCT_LN_AMT + ":"_ACCT_LN_AMT ");
        }

        return ytdQuery.append( ")" ).toString();
    }


    @Override
    public List<TrialBalanceReport> findBalanceByFields(String selectedFiscalYear, String chartCode, String periodCode) {
        final List<TrialBalanceReport> report = new ArrayList<TrialBalanceReport>();
        List<Object> queryArguments = new ArrayList<Object>(2);

        String YTDQuery = buildYTDQueryString(periodCode);
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT A0.FIN_OBJECT_CD, A0.FIN_COA_CD, A1.FIN_OBJ_CD_NM, A2.FIN_OBJTYP_DBCR_CD,");
        queryBuilder.append(YTDQuery + " AS YTD ");
        queryBuilder.append("FROM GL_BALANCE_T A0 JOIN CA_OBJECT_CODE_T A1 on A1.FIN_COA_CD = A0.FIN_COA_CD AND A1.UNIV_FISCAL_YR = A0.UNIV_FISCAL_YR and A1.FIN_OBJECT_CD = A0.FIN_OBJECT_CD ");
        queryBuilder.append("JOIN CA_OBJ_TYPE_T A2 on A2.FIN_OBJ_TYP_CD = A1.FIN_OBJ_TYP_CD ");
        queryBuilder.append("JOIN CA_ACCTG_CTGRY_T A3 on A3.ACCTG_CTGRY_CD = A2.ACCTG_CTGRY_CD ");
        queryBuilder.append("WHERE A0.FIN_BALANCE_TYP_CD = 'AC' ");
        queryBuilder.append("AND A0.UNIV_FISCAL_YR = ? ");
        queryArguments.add(selectedFiscalYear);

        if (StringUtils.isNotBlank(chartCode)) {
            queryBuilder.append("AND A0.FIN_COA_CD=? ");
            queryArguments.add(chartCode);
        }
        queryBuilder.append("GROUP BY A0.FIN_OBJECT_CD, A0.FIN_COA_CD, A1.FIN_OBJ_CD_NM, A2.FIN_OBJTYP_DBCR_CD, A3.FIN_REPORT_SORT_CD ");
        queryBuilder.append("HAVING "+YTDQuery+" <> 0 ");
        queryBuilder.append("ORDER BY A0.FIN_COA_CD, A3.FIN_REPORT_SORT_CD, A0.FIN_OBJECT_CD");

        getJdbcTemplate().query(queryBuilder.toString(), queryArguments.toArray(), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

                TrialBalanceReport reportLine = null;
                KualiDecimal ytdAmount = null;
                KualiDecimal totalDebit = KualiDecimal.ZERO;
                KualiDecimal totalCredit = KualiDecimal.ZERO;
                String objectTypeDebitCreditCd = null;
                int index = 1;

                // Iterator the search result and build the lookup object for trial balance report
                while (rs != null && rs.next()) {
                    reportLine = new TrialBalanceReport();
                    reportLine.setIndex(index++);
                    reportLine.setChartOfAccountsCode(rs.getString("FIN_COA_CD"));
                    reportLine.setObjectCode(rs.getString("FIN_OBJECT_CD"));
                    reportLine.setFinancialObjectCodeName(rs.getString("FIN_OBJ_CD_NM"));
                    objectTypeDebitCreditCd = rs.getString("FIN_OBJTYP_DBCR_CD");
                    ytdAmount = new KualiDecimal(rs.getBigDecimal("YTD"));

                    if ((ytdAmount.isPositive() && KFSConstants.GL_CREDIT_CODE.equals(objectTypeDebitCreditCd)) || (ytdAmount.isNegative() && KFSConstants.GL_DEBIT_CODE.equals(objectTypeDebitCreditCd))) {
                        reportLine.setCreditAmount(ytdAmount.abs());
                        // sum the total credit
                        totalCredit = totalCredit.add(reportLine.getCreditAmount());
                    }
                    else if ((ytdAmount.isPositive() && KFSConstants.GL_DEBIT_CODE.equals(objectTypeDebitCreditCd)) || (ytdAmount.isNegative() && KFSConstants.GL_CREDIT_CODE.equals(objectTypeDebitCreditCd))) {
                        reportLine.setDebitAmount(ytdAmount.abs());
                        // sum the total debit
                        totalDebit = totalDebit.add(reportLine.getDebitAmount());
                    }
                    report.add(reportLine);
                }

                // add a final line for total credit and debit
                if (!report.isEmpty()) {
                    reportLine = new TrialBalanceReport();
                    reportLine.setIndex(index++);
                    reportLine.setChartOfAccountsCode("Total");
                    reportLine.setDebitAmount(totalDebit);
                    reportLine.setCreditAmount(totalCredit);
                    report.add(reportLine);
                }
                return null;
            }
        });
        return report;
    }

}
