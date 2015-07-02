package org.kuali.kfs.coa.dataaccess.impl;

import org.kuali.kfs.coa.dataaccess.GeneralLedgerDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralLedgerDaoJdbc extends PlatformAwareDaoBaseJdbc implements GeneralLedgerDao {
    protected static final String ACCOUNT_BALANCES_BY_CONSOLIDATION = "select gl_acct_balances_t.fin_coa_cd, gl_acct_balances_t.account_nbr, CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_NM, sum(gl_acct_balances_t.curr_bdln_bal_amt) as budget, sum(gl_acct_balances_t.acln_actls_bal_amt) as spent, sum(gl_acct_balances_t.acln_encum_bal_amt) as allocated, sum(gl_acct_balances_t.curr_bdln_bal_amt - (gl_acct_balances_t.acln_actls_bal_amt + gl_acct_balances_t.acln_encum_bal_amt)) as balance from CA_OBJ_CONSOLDTN_T join (CA_OBJ_LEVEL_T join (CA_OBJECT_CODE_T join gl_acct_balances_t on gl_acct_balances_t.univ_fiscal_yr = ca_object_code_t.univ_fiscal_yr and gl_acct_balances_t.fin_coa_cd = ca_object_code_t.fin_coa_cd and gl_acct_balances_t.fin_object_cd = ca_object_code_t.fin_object_cd) on CA_OBJECT_CODE_T.FIN_COA_CD = ca_obj_level_t.fin_coa_cd and ca_object_code_t.fin_obj_level_cd = ca_obj_level_t.fin_obj_level_cd) on CA_OBJ_LEVEL_T.FIN_COA_CD = CA_OBJ_CONSOLDTN_T.FIN_COA_CD and CA_OBJ_LEVEL_T.FIN_CONS_OBJ_CD = CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_CD where gl_acct_balances_t.univ_fiscal_yr = ? and gl_acct_balances_t.fin_coa_cd = ? and gl_acct_balances_t.account_nbr = ? group by gl_acct_balances_t.fin_coa_cd, gl_acct_balances_t.account_nbr, CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_CD, CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_NM order by CA_OBJ_CONSOLDTN_T.FIN_CONS_OBJ_CD";

    @Override
    public List<Map<String, Object>> lookupAccountBalancesByConsolidation(final Integer fiscalYear, final String chartOfAccountsCode, final String accountNumber) {
        final List<Map<String, Object>> results = new ArrayList<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(ACCOUNT_BALANCES_BY_CONSOLIDATION);
                ps.setInt(1, fiscalYear);
                ps.setString(2, chartOfAccountsCode);
                ps.setString(3, accountNumber);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                results.add(representAccountBalanceByConsolidationRow(rs));
            }
        });
        return results;
    }

    protected Map<String, Object> representAccountBalanceByConsolidationRow(ResultSet rs) throws SQLException {
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("chartOfAccountsCode", rs.getString(1));
        result.put("accountNumber", rs.getString(2));
        result.put("objectConsolidationName", rs.getString(3));
        result.put("budget", rs.getDouble(4));
        result.put("spent", rs.getDouble(5));
        result.put("allocated", rs.getDouble(6));
        result.put("balance", rs.getDouble(7));
        return result;
    }
}
