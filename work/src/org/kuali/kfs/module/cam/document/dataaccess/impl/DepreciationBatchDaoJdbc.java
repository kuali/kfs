/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.Guid;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * JDBC implementation of {@link DepreciationBatchDao}
 */
public class DepreciationBatchDaoJdbc extends PlatformAwareDaoBaseJdbc implements DepreciationBatchDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepreciationBatchDaoJdbc.class);
    private KualiConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private OptionsService optionsService;
    private BusinessObjectService businessObjectService;
    private UniversityDateDao universityDateDao;

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#resetPeriodValuesWhenFirstFiscalPeriod(java.lang.Integer)
     */
    public void resetPeriodValuesWhenFirstFiscalPeriod(Integer fiscalMonth) throws Exception {
        if (fiscalMonth == 1) {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Starting resetPeriodValuesWhenFirstFiscalPeriod");
            // update previous year depreciation amount with sum of all periodic values for all asset payments
            getJdbcTemplate().update("UPDATE CM_AST_PAYMENT_T SET AST_PRVYRDEPR1_AMT = (AST_PRD1_DEPR1_AMT + AST_PRD2_DEPR1_AMT + AST_PRD3_DEPR1_AMT + AST_PRD4_DEPR1_AMT + AST_PRD5_DEPR1_AMT + AST_PRD6_DEPR1_AMT + AST_PRD7_DEPR1_AMT + AST_PRD8_DEPR1_AMT + AST_PRD9_DEPR1_AMT + AST_PRD10DEPR1_AMT + AST_PRD11DEPR1_AMT + AST_PRD12DEPR1_AMT)");
            // reset periodic columns with zero dollar
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal month = 1. Therefore, initializing each month with zeros.");
            getJdbcTemplate().update("UPDATE CM_AST_PAYMENT_T SET AST_PRD1_DEPR1_AMT =0.0,  AST_PRD2_DEPR1_AMT =0.0,  AST_PRD3_DEPR1_AMT =0.0,  AST_PRD4_DEPR1_AMT =0.0,  AST_PRD5_DEPR1_AMT =0.0,  AST_PRD6_DEPR1_AMT =0.0,  AST_PRD7_DEPR1_AMT =0.0,  AST_PRD8_DEPR1_AMT =0.0,  AST_PRD9_DEPR1_AMT =0.0,  AST_PRD10DEPR1_AMT =0.0,  AST_PRD11DEPR1_AMT =0.0,  AST_PRD12DEPR1_AMT=0.0");
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished resetPeriodValuesWhenFirstFiscalPeriod");
        }
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#updateAssetPayments(java.util.List, java.lang.Integer)
     */
    public void updateAssetPayments(final List<AssetPaymentInfo> assetPayments, final Integer fiscalMonth) {
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Batch updating [" + assetPayments.size() + "] payments");
        getJdbcTemplate().batchUpdate("UPDATE CM_AST_PAYMENT_T SET AST_ACUM_DEPR1_AMT=? , AST_PRD" + fiscalMonth + (fiscalMonth < 10 ? "_" : "") + "DEPR1_AMT = ? WHERE CPTLAST_NBR = ? AND AST_PMT_SEQ_NBR = ? ", new BatchPreparedStatementSetter() {

            public int getBatchSize() {
                return assetPayments.size();
            }

            public void setValues(PreparedStatement pstmt, int index) throws SQLException {
                pstmt.setBigDecimal(1, assetPayments.get(index).getAccumulatedPrimaryDepreciationAmount().bigDecimalValue());
                pstmt.setBigDecimal(2, assetPayments.get(index).getTransactionAmount().bigDecimalValue());
                pstmt.setLong(3, assetPayments.get(index).getCapitalAssetNumber());
                pstmt.setInt(4, assetPayments.get(index).getPaymentSequenceNumber());
            }


        });
    }


    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#updateAssetsCreatedInLastFiscalPeriod(java.lang.Integer,
     *      java.lang.Integer)
     */
    public void updateAssetsCreatedInLastFiscalPeriod(final Integer fiscalMonth, final Integer fiscalYear) {
        // If we are in the last month of the fiscal year
        if (fiscalMonth == 12) {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Starting updateAssetsCreatedInLastFiscalPeriod()");
            // Getting last date of fiscal year
            final UniversityDate lastFiscalYearDate = universityDateDao.getLastFiscalYearDate(fiscalYear);
            if (lastFiscalYearDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }

            List<String> movableEquipmentObjectSubTypes = new ArrayList<String>();
            if (parameterService.parameterExists(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES)) {
                movableEquipmentObjectSubTypes = parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES);
            }

            // Only update assets with a object sub type code equals to any MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES.
            if (!movableEquipmentObjectSubTypes.isEmpty()) {
                getJdbcTemplate().update("UPDATE CM_CPTLAST_T SET CPTL_AST_IN_SRVC_DT=?, CPTL_AST_DEPR_DT=?, FDOC_POST_PRD_CD=? , FDOC_POST_YR=? WHERE CPTLAST_CRT_DT > ? AND FIN_OBJ_SUB_TYP_CD IN (" + buildINValues(movableEquipmentObjectSubTypes) + ")", new PreparedStatementSetter() {
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setDate(1, lastFiscalYearDate.getUniversityDate());
                        ps.setDate(2, lastFiscalYearDate.getUniversityDate());
                        ps.setString(3, fiscalMonth.toString());
                        ps.setInt(4, fiscalYear);
                        ps.setDate(5, lastFiscalYearDate.getUniversityDate());
                    }
                });
            }
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished updateAssetsCreatedInLastFiscalPeriod()");
        }
    }


    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#savePendingGLEntries(java.util.List)
     */
    public void savePendingGLEntries(final List<GeneralLedgerPendingEntry> glPendingEntries) {
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Batch update of [" + glPendingEntries.size() + "] glpes");
        // we need batch insert for gl pending entry
        getJdbcTemplate().batchUpdate("INSERT INTO GL_PENDING_ENTRY_T " + " (FS_ORIGIN_CD,FDOC_NBR,TRN_ENTR_SEQ_NBR,OBJ_ID,VER_NBR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,UNIV_FISCAL_YR,UNIV_FISCAL_PRD_CD,TRN_LDGR_ENTR_DESC,TRN_LDGR_ENTR_AMT,TRN_DEBIT_CRDT_CD,TRANSACTION_DT,FDOC_TYP_CD,ORG_DOC_NBR,PROJECT_CD,ORG_REFERENCE_ID,FDOC_REF_TYP_CD,FS_REF_ORIGIN_CD,FDOC_REF_NBR,FDOC_REVERSAL_DT,TRN_ENCUM_UPDT_CD,FDOC_APPROVED_CD,ACCT_SF_FINOBJ_CD,TRNENTR_PROCESS_TM) VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {

            public int getBatchSize() {
                return glPendingEntries.size();
            }

            public void setValues(PreparedStatement pstmt, int index) throws SQLException {
                GeneralLedgerPendingEntry generalLedgerPendingEntry = glPendingEntries.get(index);
                pstmt.setObject(1, generalLedgerPendingEntry.getFinancialSystemOriginationCode());
                pstmt.setObject(2, generalLedgerPendingEntry.getDocumentNumber());
                pstmt.setObject(3, generalLedgerPendingEntry.getTransactionLedgerEntrySequenceNumber());
                pstmt.setObject(4, new Guid().toString());
                pstmt.setObject(5, generalLedgerPendingEntry.getVersionNumber());
                pstmt.setObject(6, generalLedgerPendingEntry.getChartOfAccountsCode());
                pstmt.setObject(7, generalLedgerPendingEntry.getAccountNumber());
                pstmt.setObject(8, generalLedgerPendingEntry.getSubAccountNumber());
                pstmt.setObject(9, generalLedgerPendingEntry.getFinancialObjectCode());
                pstmt.setObject(10, generalLedgerPendingEntry.getFinancialSubObjectCode());
                pstmt.setObject(11, generalLedgerPendingEntry.getFinancialBalanceTypeCode());
                pstmt.setObject(12, generalLedgerPendingEntry.getFinancialObjectTypeCode());
                pstmt.setObject(13, generalLedgerPendingEntry.getUniversityFiscalYear());
                pstmt.setObject(14, generalLedgerPendingEntry.getUniversityFiscalPeriodCode());
                pstmt.setObject(15, generalLedgerPendingEntry.getTransactionLedgerEntryDescription());
                pstmt.setObject(16, generalLedgerPendingEntry.getTransactionLedgerEntryAmount().bigDecimalValue());
                pstmt.setObject(17, generalLedgerPendingEntry.getTransactionDebitCreditCode());
                pstmt.setObject(18, generalLedgerPendingEntry.getTransactionDate());
                pstmt.setObject(19, generalLedgerPendingEntry.getFinancialDocumentTypeCode());
                pstmt.setObject(20, generalLedgerPendingEntry.getOrganizationDocumentNumber());
                pstmt.setObject(21, generalLedgerPendingEntry.getProjectCode());
                pstmt.setObject(22, generalLedgerPendingEntry.getOrganizationReferenceId());
                pstmt.setObject(23, generalLedgerPendingEntry.getReferenceFinancialDocumentTypeCode());
                pstmt.setObject(24, generalLedgerPendingEntry.getReferenceFinancialSystemOriginationCode());
                pstmt.setObject(25, generalLedgerPendingEntry.getReferenceFinancialDocumentNumber());
                pstmt.setObject(26, generalLedgerPendingEntry.getFinancialDocumentReversalDate());
                pstmt.setObject(27, generalLedgerPendingEntry.getTransactionEncumbranceUpdateCode());
                pstmt.setObject(28, generalLedgerPendingEntry.getFinancialDocumentApprovedCode());
                pstmt.setObject(29, generalLedgerPendingEntry.getAcctSufficientFundsFinObjCd());
                pstmt.setObject(30, generalLedgerPendingEntry.getTransactionEntryProcessedTs());
            }

        });
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getListOfDepreciableAssetPaymentInfo(java.lang.Integer,
     *      java.lang.Integer, java.util.Calendar)
     */
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfo(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate) {
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Starting to get the list of depreciable asset payment list.");
        final List<AssetPaymentInfo> assetPaymentDetails = new ArrayList<AssetPaymentInfo>();
        List<String> depreciationMethodList = new ArrayList<String>();
        List<String> notAcceptedAssetStatus = new ArrayList<String>();
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        List<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
        if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES)) {
            notAcceptedAssetStatus = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES);
        }
        String sql = "SELECT A0.CPTLAST_NBR,A0.AST_PMT_SEQ_NBR,A1.CPTL_AST_DEPR_DT,A1.AST_DEPR_MTHD1_CD,A1.CPTLAST_SALVAG_AMT,";
        sql = sql + "A2.CPTLAST_DEPRLF_LMT,A5.ORG_PLNT_COA_CD,A5.ORG_PLNT_ACCT_NBR,A5.CMP_PLNT_COA_CD,A5.CMP_PLNT_ACCT_NBR,A3.FIN_OBJ_TYP_CD, ";
        sql = sql + "A3.FIN_OBJ_SUB_TYP_CD, A0.AST_DEPR1_BASE_AMT,A0.FIN_OBJECT_CD, A0.AST_ACUM_DEPR1_AMT,A0.SUB_ACCT_NBR,A0.FIN_SUB_OBJ_CD,A0.PROJECT_CD, A0.FIN_COA_CD  FROM CM_AST_PAYMENT_T A0 INNER JOIN CM_CPTLAST_T A1 ON A0.CPTLAST_NBR=A1.CPTLAST_NBR INNER JOIN ";
        sql = sql + "CM_ASSET_TYPE_T A2 ON A1.CPTLAST_TYP_CD=A2.CPTLAST_TYP_CD INNER JOIN CA_OBJECT_CODE_T A3 ON A0.FDOC_POST_YR=A3.UNIV_FISCAL_YR ";
        sql = sql + "AND A0.FIN_COA_CD=A3.FIN_COA_CD AND A0.FIN_OBJECT_CD=A3.FIN_OBJECT_CD INNER JOIN CA_ACCOUNT_T A4 ON A0.FIN_COA_CD=A4.FIN_COA_CD ";
        sql = sql + "AND A0.ACCOUNT_NBR=A4.ACCOUNT_NBR INNER JOIN CA_ORG_T A5 ON A4.FIN_COA_CD=A5.FIN_COA_CD AND A4.ORG_CD=A5.ORG_CD ";
        sql = sql + "WHERE (((( (((( ( A0.AST_DEPR1_BASE_AMT IS NOT NULL  AND  (A0.AST_DEPR1_BASE_AMT <> 0) AND (A0.AST_DEPR1_BASE_AMT <> A0.AST_ACUM_DEPR1_AMT)) AND  (A0.AST_TRNFR_PMT_CD ";
        sql = sql + "IN ('N','') OR  (A0.AST_TRNFR_PMT_CD IS NULL ))) AND  (A1.AST_DEPR_MTHD1_CD IN (" + buildINValues(depreciationMethodList) + "))) ";
        sql = sql + "AND A1.CPTL_AST_DEPR_DT IS NOT NULL ) AND A1.CPTL_AST_DEPR_DT <= ?) AND A1.CPTL_AST_DEPR_DT > ? ) AND  ";
        sql = sql + "(A1.AST_RETIR_FSCL_YR > '" + fiscalYear + "' OR  (A1.AST_RETIR_FSCL_YR IS NULL ) OR  (A1.AST_RETIR_PRD_CD IS NOT NULL ) ";
        sql = sql + "OR  ((A1.AST_RETIR_FSCL_YR = '" + fiscalYear + "') AND A1.AST_RETIR_PRD_CD > '" + fiscalMonth + "'))) ";
        sql = sql + "AND A1.AST_INVN_STAT_CD NOT IN (" + buildINValues(notAcceptedAssetStatus) + ")) AND A2.CPTLAST_DEPRLF_LMT > 0) ";
        sql = sql + "AND A3.FIN_OBJ_SUB_TYP_CD NOT IN (" + buildINValues(federallyOwnedObjectSubTypes) + ") AND NOT EXISTS ";
        sql = sql + "(SELECT 1 FROM CM_AST_TRNFR_DOC_T TRFR, FS_DOC_HEADER_T HDR WHERE HDR.FDOC_NBR = TRFR.FDOC_NBR AND ";
        sql = sql + "HDR.FDOC_STATUS_CD = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "' AND TRFR.CPTLAST_NBR = A0.CPTLAST_NBR) ";
        sql = sql + "AND NOT EXISTS (SELECT 1 FROM CM_AST_RETIRE_DTL_T DTL, FS_DOC_HEADER_T HDR WHERE HDR.FDOC_NBR = DTL.FDOC_NBR ";
        sql = sql + "AND HDR.FDOC_STATUS_CD = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "' AND DTL.CPTLAST_NBR = A0.CPTLAST_NBR) ";
        sql = sql + "ORDER BY A0.CPTLAST_NBR, A0.FS_ORIGIN_CD, A0.ACCOUNT_NBR, A0.SUB_ACCT_NBR, A0.FIN_OBJECT_CD, A0.FIN_SUB_OBJ_CD, A3.FIN_OBJ_TYP_CD, A0.PROJECT_CD";
        getJdbcTemplate().query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt) throws SQLException {
                // pstmt.setMaxRows(100000);
                Calendar DateOf1900 = Calendar.getInstance();
                DateOf1900.set(1900, 0, 1);
                pstmt.setDate(1, new Date(depreciationDate.getTimeInMillis()));
                pstmt.setDate(2, new Date(DateOf1900.getTimeInMillis()));
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                int counter = 0;
                while (rs != null && rs.next()) {
                    counter++;
                    if (counter % 10000 == 0) {
                        System.out.println("Result row at " + new java.util.Date() + " -  " + counter);
                    }
                    AssetPaymentInfo assetPaymentInfo = new AssetPaymentInfo();
                    assetPaymentInfo.setCapitalAssetNumber(rs.getLong(1));
                    assetPaymentInfo.setPaymentSequenceNumber(rs.getInt(2));
                    assetPaymentInfo.setDepreciationDate(rs.getDate(3));
                    assetPaymentInfo.setPrimaryDepreciationMethodCode(rs.getString(4));
                    BigDecimal salvage = rs.getBigDecimal(5);
                    assetPaymentInfo.setSalvageAmount(salvage == null ? KualiDecimal.ZERO : new KualiDecimal(salvage));
                    assetPaymentInfo.setDepreciableLifeLimit(rs.getInt(6));
                    assetPaymentInfo.setOrganizationPlantChartCode(rs.getString(7));
                    assetPaymentInfo.setOrganizationPlantAccountNumber(rs.getString(8));
                    assetPaymentInfo.setCampusPlantChartCode(rs.getString(9));
                    assetPaymentInfo.setCampusPlantAccountNumber(rs.getString(10));
                    assetPaymentInfo.setFinancialObjectTypeCode(rs.getString(11));
                    assetPaymentInfo.setFinancialObjectSubTypeCode(rs.getString(12));

                    BigDecimal primaryDeprAmt = rs.getBigDecimal(13);
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(primaryDeprAmt == null ? KualiDecimal.ZERO : new KualiDecimal(primaryDeprAmt));
                    assetPaymentInfo.setFinancialObjectCode(rs.getString(14));
                    BigDecimal accumDeprAmt = rs.getBigDecimal(15);
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumDeprAmt == null ? KualiDecimal.ZERO : new KualiDecimal(accumDeprAmt));
                    assetPaymentInfo.setSubAccountNumber(rs.getString(16));
                    assetPaymentInfo.setFinancialSubObjectCode(rs.getString(17));
                    assetPaymentInfo.setProjectCode(rs.getString(18));
                    assetPaymentInfo.setChartOfAccountsCode(rs.getString(19));

                    assetPaymentDetails.add(assetPaymentInfo);
                }
                return assetPaymentDetails;
            }

        });
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting list of [" + assetPaymentDetails.size() + "] depreciable asset payment list.");
        return assetPaymentDetails;
    }

    public Integer getFullyDepreciatedAssetCount() {
        int count = getJdbcTemplate().queryForInt("SELECT COUNT(1) FROM CM_CPTLAST_T AST, (SELECT CPTLAST_NBR, (SUM(AST_DEPR1_BASE_AMT - AST_ACUM_DEPR1_AMT) - (SELECT CPTLAST_SALVAG_AMT FROM CM_CPTLAST_T X WHERE X.CPTLAST_NBR = Y.CPTLAST_NBR)) B FROM CM_AST_PAYMENT_T Y WHERE AST_DEPR1_BASE_AMT IS NOT NULL AND AST_DEPR1_BASE_AMT <> 0.0 GROUP BY CPTLAST_NBR) PMT WHERE PMT.B = 0.0 AND AST.CPTLAST_NBR = PMT.CPTLAST_NBR");
        return count;
    }

    /**
     * This method the value of the system parameter NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
     * 
     * @return
     */
    private List<String> getFederallyOwnedObjectSubTypes() {
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "getting the federally owned object subtype codes.");

        List<String> federallyOwnedObjectSubTypes = new ArrayList<String>();
        if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES)) {
            federallyOwnedObjectSubTypes = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES);
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting the federally owned object subtype codes which are:" + federallyOwnedObjectSubTypes.toString());
        return federallyOwnedObjectSubTypes;
    }

    /**
     * Utility method that will convert a list into IN string clause for SQL
     * 
     * @param list values
     * @return concatenated string
     */
    private String buildINValues(List<String> list) {
        if (list.isEmpty()) {
            return "''";
        }
        String returnValue = "";
        for (String string : list) {
            returnValue = returnValue + "'" + string + "',";
        }
        return returnValue.substring(0, returnValue.lastIndexOf(','));
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the optionsService attribute.
     * 
     * @return Returns the optionsService.
     */
    public OptionsService getOptionsService() {
        return optionsService;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the universityDateDao attribute.
     * 
     * @return Returns the universityDateDao.
     */
    public UniversityDateDao getUniversityDateDao() {
        return universityDateDao;
    }

    /**
     * Sets the universityDateDao attribute value.
     * 
     * @param universityDateDao The universityDateDao to set.
     */
    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }
}
