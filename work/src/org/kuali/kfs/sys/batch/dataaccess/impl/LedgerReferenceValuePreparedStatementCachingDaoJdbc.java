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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.dataaccess.LedgerReferenceValuePreparedStatementCachingDao;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;

public class LedgerReferenceValuePreparedStatementCachingDaoJdbc extends AbstractPreparedStatementCachingDaoJdbc implements LedgerReferenceValuePreparedStatementCachingDao {
    static final Map<String,String> sql = new HashMap<String,String>();
    static {    
        sql.put(RETRIEVE_PREFIX + UniversityDate.class, "select univ_fiscal_yr, univ_fiscal_prd_cd from SH_UNIV_DATE_T where univ_dt = ?");
        sql.put(RETRIEVE_PREFIX + SystemOptions.class, "select act_fin_bal_typ_cd, fobj_typ_asset_cd, fobj_typ_fndbal_cd, fobj_typ_lblty_cd, ext_enc_fbaltyp_cd, int_enc_fbaltyp_cd, pre_enc_fbaltyp_cd, fobjtp_xpnd_exp_cd, fobjtp_xpndnexp_cd, fobjtp_expnxpnd_cd, bdgt_chk_baltyp_cd, CSTSHR_ENCUM_FIN_BAL_TYP_CD, FIN_OBJECT_TYP_TRNFR_EXP_CD from FS_OPTION_T where univ_fiscal_yr = ?");
        sql.put(RETRIEVE_PREFIX + OriginationCode.class, "select ROW_ACTV_IND from FS_ORIGIN_CODE_T where fs_origin_cd = ?");
    }

    @Override
    protected Map<String, String> getSql() {
        return sql;
    }

    public OriginationCode getOriginationCode(final String financialSystemOriginationCode) {
        return new RetrievingJdbcWrapper<OriginationCode>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, financialSystemOriginationCode);
            }
            @Override
            protected OriginationCode extractResult(ResultSet resultSet) throws SQLException {
                OriginationCode originationCode = new OriginationCode();
                originationCode.setFinancialSystemOriginationCode(financialSystemOriginationCode);
                originationCode.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                return originationCode;
            }
        }.get(OriginationCode.class);
    }

    public SystemOptions getSystemOptions(final Integer fiscalYear) {
        return new RetrievingJdbcWrapper<SystemOptions>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, fiscalYear);
            }
            @Override
            protected SystemOptions extractResult(ResultSet resultSet) throws SQLException {
                SystemOptions systemOptions = new SystemOptions();
                systemOptions.setUniversityFiscalYear(fiscalYear);
                systemOptions.setActualFinancialBalanceTypeCd(resultSet.getString(1));
                systemOptions.setFinancialObjectTypeAssetsCd(resultSet.getString(2));
                systemOptions.setFinObjectTypeFundBalanceCd(resultSet.getString(3));
                systemOptions.setFinObjectTypeLiabilitiesCode(resultSet.getString(4));
                systemOptions.setCostShareEncumbranceBalanceTypeCd(resultSet.getString(12));
                systemOptions.setFinancialObjectTypeTransferExpenseCd(resultSet.getString(13));
                systemOptions.setExtrnlEncumFinBalanceTypCd(resultSet.getString(5));
                systemOptions.setIntrnlEncumFinBalanceTypCd(resultSet.getString(6));
                systemOptions.setPreencumbranceFinBalTypeCd(resultSet.getString(7));
                systemOptions.setFinObjTypeExpenditureexpCd(resultSet.getString(8));
                systemOptions.setFinObjTypeExpendNotExpCode(resultSet.getString(9));
                systemOptions.setFinObjTypeExpNotExpendCode(resultSet.getString(10));
                systemOptions.setBudgetCheckingBalanceTypeCd(resultSet.getString(11));
                return systemOptions;
            }
        }.get(SystemOptions.class);
    }

    public UniversityDate getUniversityDate(final Date date) {
        return new RetrievingJdbcWrapper<UniversityDate>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setDate(1, date);
            }
            @Override
            protected UniversityDate extractResult(ResultSet resultSet) throws SQLException {
                UniversityDate universityDate = new UniversityDate();
                universityDate.setUniversityDate(date);
                universityDate.setUniversityFiscalYear(resultSet.getInt(1));
                universityDate.setUniversityFiscalAccountingPeriod(resultSet.getString(2));
                return universityDate;
            }
        }.get(UniversityDate.class);
    }
}
