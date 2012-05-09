/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch.dataaccess.impl;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.batch.dataaccess.impl.LedgerEntryBalanceCachingDaoJdbc;

/**
 * This class...
 */
public class LaborLedgerEntryBalanceCachingDaoJdbc extends LedgerEntryBalanceCachingDaoJdbc {

    public List compareBalanceHistory(String balanceTable, String historyTable,int fiscalYear) {
        List<Map<String, Object>> data = null;

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("select bh.* ");
        queryBuilder.append("from " + historyTable + " bh  ");
        queryBuilder.append("left join ( select ");
        queryBuilder.append("UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD,  FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, POSITION_NBR, EMPLID, ");
        queryBuilder.append("ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT ");
        queryBuilder.append("from " + balanceTable + " ) e on ");
        queryBuilder.append("bh.UNIV_FISCAL_YR = e.UNIV_FISCAL_YR and bh.FIN_COA_CD = e.FIN_COA_CD and bh.FIN_OBJECT_CD = e.FIN_OBJECT_CD and bh.FIN_BALANCE_TYP_CD = e.FIN_BALANCE_TYP_CD and bh.SUB_ACCT_NBR = e.SUB_ACCT_NBR and bh.ACCOUNT_NBR = e.ACCOUNT_NBR and bh.FIN_SUB_OBJ_CD = e.FIN_SUB_OBJ_CD and bh.FIN_OBJ_TYP_CD = e.FIN_OBJ_TYP_CD and bh.POSITION_NBR = e.POSITION_NBR and bh.EMPLID = e.EMPLID ");
        queryBuilder.append(" where e.UNIV_FISCAL_YR >= " + fiscalYear + " ");
        queryBuilder.append("and (bh.ACLN_ANNL_BAL_AMT <> e.ACLN_ANNL_BAL_AMT or bh.FIN_BEG_BAL_LN_AMT <> e.FIN_BEG_BAL_LN_AMT or bh.CONTR_GR_BB_AC_AMT <> e.CONTR_GR_BB_AC_AMT or  ");
        queryBuilder.append("bh.MO1_ACCT_LN_AMT <> e.MO1_ACCT_LN_AMT or bh.MO2_ACCT_LN_AMT <> e.MO2_ACCT_LN_AMT or bh.MO3_ACCT_LN_AMT <> e.MO3_ACCT_LN_AMT or bh.MO4_ACCT_LN_AMT <> e.MO4_ACCT_LN_AMT or bh.MO5_ACCT_LN_AMT <> e.MO5_ACCT_LN_AMT or bh.MO6_ACCT_LN_AMT <> e.MO6_ACCT_LN_AMT or  ");
        queryBuilder.append("bh.MO7_ACCT_LN_AMT <> e.MO7_ACCT_LN_AMT or bh.MO8_ACCT_LN_AMT <> e.MO8_ACCT_LN_AMT or bh.MO9_ACCT_LN_AMT <> e.MO9_ACCT_LN_AMT or bh.MO10_ACCT_LN_AMT <> e.MO10_ACCT_LN_AMT or bh.MO11_ACCT_LN_AMT <> e.MO11_ACCT_LN_AMT or bh.MO12_ACCT_LN_AMT <> e.MO12_ACCT_LN_AMT or  ");
        queryBuilder.append("bh.MO13_ACCT_LN_AMT <> e.MO13_ACCT_LN_AMT) ");

        data = getSimpleJdbcTemplate().queryForList(queryBuilder.toString());

        return data;

    }


}
