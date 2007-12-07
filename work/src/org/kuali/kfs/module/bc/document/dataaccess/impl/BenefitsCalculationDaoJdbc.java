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

import java.lang.String;
import java.lang.StringBuilder;

import org.kuali.core.util.Guid;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;

import org.kuali.module.budget.dao.BenefitsCalculationDao;
import org.kuali.module.budget.dao.jdbc.BudgetConstructionDaoJdbcBase;

public class BenefitsCalculationDaoJdbc extends BudgetConstructionDaoJdbcBase implements BenefitsCalculationDao {

    private static String[] sqlAnnualTemplates;
    private static String[] sqlMonthlyTemplates;
    @RawSQL
    public BenefitsCalculationDaoJdbc ()
    {
        StringBuilder sqlBuilder = new StringBuilder(500);
        sqlBuilder.append("DELETE FROM LD_PND_BCSNTR_GL_T\nWHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n  AND (LD_PND_BL_BCSNTR_GL_T.UNIV_FISCAL_YR =?)\n ");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n  AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        sqlAnnualTemplates[0] = sqlBuilder.toString();
        sqlBuilder.delete(0,sqlBuilder.length()-1);
        sqlBuilder.append("UPDATE LD_PND_BCNSTR_GL_T\nSET LD_PND_BCNSTR_GL_T.ACLN_ANNL_BAL_AMT =0\n");
        sqlBuilder.append("WHERE (LD_PND_BCNSTR_GL_T.FDOC_NBR = ?)\n  AND (LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.FIN_COA_CD = ?)\n  AND (LD_PND_BCNSTR_GL_T.ACCOUNT_NBR = ?)\n");
        sqlBuilder.append("  AND (LD_PND_BCNSTR_GL_T.SUB_ACCT_NBR = ?)\n AND (EXISTS (SELECT 1 FROM LD_BENEFITS_CALC_T\n");
        sqlBuilder.append("WHERE (LD_BENEFITS_CALC_T.UNIV_FISCAL_YR = LD_PND_BCNSTR_GL_T.UNIV_FISCAL_YR)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.FIN_COA_CD = LD_PND_BCNSTR_GL_T.FIN_COA_CD)\n");
        sqlBuilder.append("  AND (LD_BENEFITS_CALC_T.POS_FRNGBEN_OBJ_CD = LD_PND_BCNSTR_GL_T.FIN_OBJECT_CD)))");
        sqlAnnualTemplates[1] = sqlBuilder.toString();
    }
    @RawSQL
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
      String idForSession = (new Guid()).toString();
      getSimpleJdbcTemplate().update(sqlAnnualTemplates[0],documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
      getSimpleJdbcTemplate().update(sqlAnnualTemplates[1],documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
      clearTempTableBySesId("LD_BENEFITS_RECALC01_MT","SESID",idForSession);
    }

    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
      String idForSession = (new Guid()).toString();  

    }
}
