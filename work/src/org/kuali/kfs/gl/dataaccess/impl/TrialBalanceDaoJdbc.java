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

    @Override
    public List<TrialBalanceReport> findBalanceByFields(String selectedFiscalYear, String chartCode) {
        final List<TrialBalanceReport> report = new ArrayList<TrialBalanceReport>();

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT A0.FIN_OBJECT_CD, A0.FIN_COA_CD, A1.FIN_OBJ_CD_NM, A2.FIN_OBJTYP_DBCR_CD, SUM(A0.FIN_BEG_BAL_LN_AMT + A0.ACLN_ANNL_BAL_AMT) YTD ");
        queryBuilder.append("FROM GL_BALANCE_T A0 JOIN CA_OBJECT_CODE_T A1 on A1.FIN_COA_CD = A0.FIN_COA_CD AND A1.UNIV_FISCAL_YR = A0.UNIV_FISCAL_YR and ");
        queryBuilder.append("A1.FIN_OBJECT_CD = A0.FIN_OBJECT_CD JOIN CA_OBJ_TYPE_T A2 on A2.FIN_OBJ_TYP_CD = A1.FIN_OBJ_TYP_CD WHERE A0.FIN_BALANCE_TYP_CD = 'AC' ");
        queryBuilder.append("AND A0.UNIV_FISCAL_YR = '" + selectedFiscalYear + "' ");

        if (StringUtils.isNotBlank(chartCode)) {
            queryBuilder.append("AND A0.FIN_COA_CD='" + chartCode + "' ");
        }
        queryBuilder.append("GROUP BY A0.FIN_OBJECT_CD, A0.FIN_COA_CD, A1.FIN_OBJ_CD_NM, A2.FIN_OBJTYP_DBCR_CD HAVING SUM(A0.FIN_BEG_BAL_LN_AMT + A0.ACLN_ANNL_BAL_AMT) <> 0 ORDER BY A0.FIN_COA_CD, A0.FIN_OBJECT_CD");

        getJdbcTemplate().query(queryBuilder.toString(), new ResultSetExtractor() {
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
