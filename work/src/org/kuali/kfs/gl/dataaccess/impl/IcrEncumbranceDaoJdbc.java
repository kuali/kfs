/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.gl.dataaccess.impl;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.dataaccess.IcrEncumbranceDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class IcrEncumbranceDaoJdbc extends PlatformAwareDaoBaseJdbc implements IcrEncumbranceDao {

    /**
     * @see org.kuali.kfs.gl.dataaccess.IcrEncumbranceDao#buildIcrEncumbranceFeed()
     */
    @Override
    public void buildIcrEncumbranceFeed(Integer fiscalYear, final String fiscalPeriod, final String icrEncumbOriginCode, final Collection<String> icrEncumbBalanceTypes, final String[] expenseObjectTypes, final String costShareSubAccountType, final Writer fw) throws IOException {
    final String rateSql = "select distinct t1.univ_fiscal_yr, t1.fin_coa_cd, t1.account_nbr, t1.sub_acct_nbr, "
        +   getDbPlatform().getIsNullFunction("t3.fin_series_id", "t2.fin_series_id") + " fin_series_id, " + getDbPlatform().getIsNullFunction("t3.icr_typ_cd", "t2.acct_icr_typ_cd") + " acct_icr_typ_cd "
        +  "from gl_encumbrance_t t1 join ca_account_t t2 on (t1.fin_coa_cd = t2.fin_coa_cd and t1.account_nbr = t2.account_nbr) "
        +  "left join ca_a21_sub_acct_t t3 on (t1.fin_coa_cd = t3.fin_coa_cd and t1.account_nbr = t3.account_nbr and t1.sub_acct_nbr = t3.sub_acct_nbr) "
        +  "where t1.fin_balance_typ_cd in " + inString(icrEncumbBalanceTypes.toArray()) + " and t1.fs_origin_cd <> '" + icrEncumbOriginCode + "' "
        +  "and t1.univ_fiscal_yr >= " + fiscalYear + " "
        +  "and (t3.sub_acct_typ_cd is null or t3.sub_acct_typ_cd <> '" + costShareSubAccountType + "') ";


        getJdbcTemplate().query(rateSql, new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                try {
                    String newLine = System.getProperty("line.separator");
                    while (rs.next()) {
                        String fin_series_id = rs.getString("fin_series_id");
                        String acct_icr_typ_cd = rs.getString("acct_icr_typ_cd");
                        String fiscalYear = rs.getString("univ_fiscal_yr");
                        String chartCode = rs.getString("fin_coa_cd");
                        String accountNbr = rs.getString("account_nbr");
                        String subAccountNbr = rs.getString("sub_acct_nbr");

                        Object[] encArgs = new String[6];
                        encArgs[0] = fin_series_id;
                        encArgs[1] = acct_icr_typ_cd;
                        encArgs[2] = fiscalYear;
                        encArgs[3] = chartCode;
                        encArgs[4] = accountNbr;
                        encArgs[5] = subAccountNbr;

                        executeEncumbranceSql(fiscalPeriod, icrEncumbOriginCode, icrEncumbBalanceTypes, expenseObjectTypes, encArgs, fw);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
    }

    /**
     * Retrieves and formats ICR Encumbrance information and writes output records to the file writer
     *
     * @param fiscalPeriod the current fiscal period
     * @param icrEncumbOriginCode the ICR origin code - system parameter INDIRECT_COST_RECOVERY_ENCUMBRANCE_ORIGINATION
     * @param icrEncumbBalanceTypes a list of balance types - system parameter INDIRECT_COST_RECOVERY_ENCUMBRANCE_BALANCE_TYPES
     * @param expenseObjectTypes a list of expense object types
     * @param encArgs a list of query arguments
     * @param fw the file writer
     */
    protected void executeEncumbranceSql(final String fiscalPeriod, final String icrEncumbOriginCode, final Collection<String> icrEncumbBalanceTypes, final String[] expenseObjectTypes, Object[] encArgs, final Writer fw) {
        final String encumbSql = "select t1.univ_fiscal_yr, t1.fin_coa_cd, t1.account_nbr, t1.sub_acct_nbr, t5.fin_object_cd, t1.fin_balance_typ_cd, "
                + "t1.fdoc_typ_cd, t1.fdoc_nbr, " + "sum(" + getDbPlatform().getIsNullFunction("t1.acln_encum_amt - t1.acln_encum_cls_amt", "0") + " * "
                +  getDbPlatform().getIsNullFunction("t5.awrd_icr_rate_pct", "0") + " * .01) encumb_amt  " + "from gl_encumbrance_t t1 "
                + "join ca_icr_auto_entr_t t5 on t5.fin_series_id = ? and t5.univ_fiscal_yr = t1.univ_fiscal_yr "
                + "and t5.trn_debit_crdt_cd = 'D' "
                + "join ca_object_code_t t4 on t4.univ_fiscal_yr = t1.univ_fiscal_yr and t4.fin_coa_cd = t1.fin_coa_cd and t4.fin_object_cd = t1.fin_object_cd "
                + "where not exists (select 1 from ca_icr_excl_type_t where acct_icr_typ_cd = ? "
                + "and acct_icr_excl_typ_actv_ind = 'Y' and fin_object_cd = t1.fin_object_cd) "
                + "and t1.univ_fiscal_yr = ? and t1.fin_coa_cd = ? and t1.account_nbr = ? and t1.sub_acct_nbr = ? "
                + "and t1.fin_balance_typ_cd in " + inString(icrEncumbBalanceTypes.toArray()) + " and t1.fs_origin_cd <> '" + icrEncumbOriginCode + "' "
                + "and t4.fin_obj_typ_cd IN " + inString(expenseObjectTypes)
                + " group by t1.univ_fiscal_yr, t1.fin_coa_cd, t1.account_nbr, t1.sub_acct_nbr, t5.fin_object_cd, t1.fin_balance_typ_cd, "
                + "t1.fdoc_typ_cd, t1.fdoc_nbr";

        getJdbcTemplate().query(encumbSql, encArgs, new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                try {
                    String newLine = System.getProperty("line.separator");
                    while (rs.next()) {
                        String fiscalYear = rs.getString("univ_fiscal_yr");
                        String chartCode = rs.getString("fin_coa_cd");
                        String accountNbr = rs.getString("account_nbr");
                        String subAccountNbr = rs.getString("sub_acct_nbr");
                        String objectCode = rs.getString("fin_object_cd");
                        String balanceType = rs.getString("fin_balance_typ_cd");
                        String docType = rs.getString("fdoc_typ_cd");
                        String docNbr = rs.getString("fdoc_nbr");

                        KualiDecimal encumb_amt = new KualiDecimal(rs.getDouble("encumb_amt"));
                        KualiDecimal current_amt = KualiDecimal.ZERO;

                        Object[] icrArgs = new String[8];
                        icrArgs[0] = fiscalYear;
                        icrArgs[1] = chartCode;
                        icrArgs[2] = accountNbr;
                        icrArgs[3] = subAccountNbr;
                        icrArgs[4] = objectCode;
                        icrArgs[5] = balanceType;
                        icrArgs[6] = docType;
                        icrArgs[7] = docNbr;

                        Double icrAmount = getCurrentEncumbranceAmount(icrEncumbOriginCode, icrArgs);

                        if (icrAmount != null) {
                            current_amt = new KualiDecimal(icrAmount);
                        }

                        KualiDecimal new_encumb_amt = encumb_amt.subtract(current_amt);
                        if (new_encumb_amt.isZero()) {
                            // ignore zero dollar amounts
                            continue;
                        }

                        icrArgs = new String[3];
                        icrArgs[0] = fiscalYear;
                        icrArgs[1] = chartCode;
                        icrArgs[2] = objectCode;

                        String objectTypeCode = getICRObjectTypeCode(icrArgs);

                        String desc = "ICR Encumbrance " + docType + " " + docNbr;
                        String debitCreditInd = "D";
                        if (new_encumb_amt.isNegative()) {
                            debitCreditInd = "C";
                        }

                        fw.write("" + fiscalYear // Fiscal year 1-4
                                + chartCode // Chart code 5-6
                                + accountNbr // Account Number 7-13
                                + StringUtils.rightPad(subAccountNbr, 5)// Sub Account 14-18
                                + objectCode // Object Code 19-22
                                + "---" // Sub Object 23-25
                                + balanceType // balance type code
                                + objectTypeCode // Object Type 28-29
                                + fiscalPeriod // Fiscal Period 30-31
                                + StringUtils.rightPad(docType, 4) // Document Type 32-35
                                + icrEncumbOriginCode // Origin Code 36-37
                                + StringUtils.rightPad(docNbr, 14) // Doc Number 38-51
                                + StringUtils.rightPad("", 5, '0') // Entry Seq Nbr 52-56
                                + StringUtils.rightPad(StringUtils.substring(desc, 0, 40), 40) // Description 57-96
                                + StringUtils.leftPad(new_encumb_amt.abs().toString(), 21, '0') // Amount 97-116
                                + debitCreditInd // Debit/Credit 117-117
                                + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) // Trans Date 118-127
                                + "          " // Org Doc Nbr 128-137
                                + "          " // Project Code 138-147
                                + "        " // orig ref id 148-155
                                + "    " // ref doc type 156-159
                                + "  " // ref origin code 160-161
                                + "              " // ref doc number 162-175
                                + "          " // reversal date 176-185
                                + "D" // Enc update code 186-186
                        );

                        fw.write(newLine);
                        fw.flush();
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
    }

    /**
     * Returns the current encumbrance amount
     *
     * @param icrEncumbOriginCode the ICR origin code - system parameter INDIRECT_COST_RECOVERY_ENCUMBRANCE_ORIGINATION
     * @param icrArgs a list of query arguments
     * @return the current encumbrance amount if found, null otherwise
     */
    protected Double getCurrentEncumbranceAmount(final String icrEncumbOriginCode, Object[] icrArgs) {
        final String icrSql = "select sum(" + getDbPlatform().getIsNullFunction("acln_encum_amt - acln_encum_cls_amt", "0") + ") current_amt "
                + "from gl_encumbrance_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? "
                + "and fin_balance_typ_cd = ? and fdoc_typ_cd = ? and fdoc_nbr = ? and fs_origin_cd = '" + icrEncumbOriginCode + "' ";

        Double icrAmount = (Double) getJdbcTemplate().query(icrSql, icrArgs, new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                try {
                    if (rs.next()) {
                        return rs.getDouble("current_amt");
                    }

                    return null;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return icrAmount;
    }

    /**
     * Returns the object type code for the object code associated with the ICR Rate
     *
     * @param icrArgs a list of query arguments
     * @return the object type code if found, null otherwise
     */
    protected String getICRObjectTypeCode(Object[] icrArgs) {
        final String icrSql = "select fin_obj_typ_cd "
                + "from ca_object_code_t where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?";

        String objectTypeCode = (String) getJdbcTemplate().query(icrSql, icrArgs, new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                try {
                    if (rs.next()) {
                        return rs.getString("fin_obj_typ_cd");
                    }

                    return null;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return objectTypeCode;
    }

    /**
     * Creates a String bounded with parantheses for creating SQL queries
     *
     * @param cobj the array to include in an SQL query
     * @return the resulting String
     */
    protected String inString(Object[] cobj) {
        StringBuffer sb = new StringBuffer("(");
        for (int i = 0; i < cobj.length; i++) {
            sb.append("'");
            sb.append(cobj[i].toString());
            sb.append("'");
            if (i < cobj.length - 1) {
                sb.append(',');
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
