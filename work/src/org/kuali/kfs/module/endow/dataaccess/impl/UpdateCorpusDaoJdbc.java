/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.dataaccess.UpdateCorpusDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UpdateCorpusDaoJdbc extends PlatformAwareDaoBaseJdbc implements UpdateCorpusDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateCorpusDaoJdbc.class);

    /**
     *
     * @see org.kuali.kfs.module.endow.dataaccess.UpdateCorpusDao#updateKemIdCorpusPriorYearValues()
     */
    @Override
    public void updateKemIdCorpusPriorYearValues(){

        LOG.debug("updateKemIdCorpusPriorYearValues() started");

        try {
            String updateCurrentCorpusSql = "UPDATE END_KEMID_CORPUS_VAL_T SET PRIOR_FY_CORPUS_VAL = CRNT_CORPUS_VAL, PRIOR_FY_PRIN_MVAL = CRNT_PRIN_MVAL";

            //Update the current corpus table's prior year values
            int count = getSimpleJdbcTemplate().update(updateCurrentCorpusSql);

            LOG.info ("updateKemIdCorpusPriorYearValues has updated prior year values for " + count + " records in the END_KEMID_CORPUS_VAL_T table.");
        }
        catch (Exception e) {
            LOG.error("updateKemIdCorpusPriorYearValues() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }

        LOG.debug("updateKemIdCorpusPriorYearValues() completed");
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.dataaccess.UpdateCorpusDao#updateCorpusAmountsFromTransactionArchive()
     */
    @Override
    public void updateCorpusAmountsFromTransactionArchive(Date currentDate){

        LOG.debug("updateCorpusAmountsFromTransactionArchive() started");

        Map<String, Object> currentCorpus = null;
        BigDecimal currentCorpusAmount = null;
        Date tomrrowsDate = getNextDay(currentDate);

        try {
            String selectTransactionArchiveSql = "SELECT TRAN_KEMID, SUM(TRAN_CORPUS_AMT) TRAN_CORPUS_AMT FROM END_TRAN_ARCHV_T WHERE TRAN_CORPUS_IND = ? AND (TRAN_PSTD_DT >= ? AND TRAN_PSTD_DT < ?) GROUP BY TRAN_KEMID";

            String selectCurrentCorpusSql = "SELECT CRNT_CORPUS_VAL FROM END_CRNT_ENDOW_CORPUS_T WHERE KEMID = ?";
            String updateCurrentCorpusSql = "UPDATE END_CRNT_ENDOW_CORPUS_T SET CRNT_CORPUS_VAL = ? WHERE KEMID = ?";
            String insertCurrentCorpusSql = "INSERT INTO END_CRNT_ENDOW_CORPUS_T (KEMID, CRNT_CORPUS_VAL, CRNT_PRIN_MVAL, PRIOR_FY_CORPUS_VAL, PRIOR_FY_PRIN_MVAL, VER_NBR, OBJ_ID) VALUES (?, ?, 0, 0, 0, 1, ?)";

            String updateKemIdCorpusSql = "UPDATE END_KEMID_CORPUS_VAL_T SET CRNT_CORPUS_VAL = ? WHERE KEMID = ?";

            //Summarize transaction archive records by KemID on current date for CorpusAmount.
            SqlRowSet pendingEntryRowSet = getJdbcTemplate().queryForRowSet(selectTransactionArchiveSql, new Object[] { EndowConstants.YES, currentDate, tomrrowsDate});

            int updateCount = 0;
            int insertCount = 0;

            //Loop over transaction archive
            while (pendingEntryRowSet.next()) {

                //store kem id and transaction corpus amount
                String kemId = pendingEntryRowSet.getString(EndowPropertyConstants.ColumnNames.UpdateCorpus.TRANSACTION_ARCHIVE_KEMID);
                BigDecimal corpusAmount = pendingEntryRowSet.getBigDecimal(EndowPropertyConstants.ColumnNames.UpdateCorpus.TRANSACTION_CORPUS_AMT);

                try {
                    //Try and get an Endow Current Transaction
                    currentCorpus = getSimpleJdbcTemplate().queryForMap(selectCurrentCorpusSql, kemId);
                }
                catch (IncorrectResultSizeDataAccessException ex) {
                    if (ex.getActualSize() != 0) {
                        LOG.error("current corpus sql returned more than one row, aborting", ex);
                        throw ex;
                    }
                    // no rows returned - that's ok
                }

                if (currentCorpus != null) {
                    //if exists update Current Corpus (trans amt + current amt)
                    updateCount++;

                    currentCorpusAmount = (BigDecimal) currentCorpus.get(EndowPropertyConstants.ColumnNames.UpdateCorpus.CRNT_ENDOW_CORPUS_VAL);
                    currentCorpusAmount = currentCorpusAmount.add(corpusAmount);

                    // A current corpus exists, so we need to update it
                    getSimpleJdbcTemplate().update(updateCurrentCorpusSql, currentCorpusAmount, kemId);

                } else {
                     //if doesn't exist insert Current Corpus(trans amt)
                    insertCount++;

                    currentCorpusAmount = new BigDecimal("0");
                    currentCorpusAmount = currentCorpusAmount.add(corpusAmount);

                    // No current corpus exists, so we need to insert one
                    getSimpleJdbcTemplate().update(insertCurrentCorpusSql, kemId, currentCorpusAmount, java.util.UUID.randomUUID().toString());
                }

                // Now update KEM ID Corpus table
                getSimpleJdbcTemplate().update(updateKemIdCorpusSql, currentCorpusAmount, kemId);

            }

            LOG.info ("updateCorpusAmountsFromTransactionArchive has inserted Corpus Amount values for " + insertCount + " records in the END_CRNT_ENDOW_CORPUS_T table.");
            LOG.info ("updateCorpusAmountsFromTransactionArchive has updated Corpus Amount values for " + updateCount + " records in the END_CRNT_ENDOW_CORPUS_T table.");
            LOG.info ("updateCorpusAmountsFromTransactionArchive has updated Corpus Amount values for " + (updateCount + insertCount) + " records in the END_KEMID_CORPUS_VAL_T table.");

        }
        catch (Exception e) {
            LOG.error("updateCorpusAmountsFromTransactionArchive() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }

        LOG.debug("updateCorpusAmountsFromTransactionArchive() completed");
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.dataaccess.UpdateCorpusDao#updateKEMIDCorpusPrincipalMarketValue()
     */
    @Override
    public void updateKemIdCorpusPrincipalMarketValue(){

        LOG.debug("updateKemIdCorpusPrincipalMarketValue() started");

        try{
            String selectCurrentBalanceSql = "select t2.KEMID, t2.PRIN_AT_MARKET from END_KEMID_CORPUS_VAL_T t1, END_KEMID_CRNT_BAL_V t2 where t1.KEMID = t2.KEMID";
            String updateCurrentCorpusSql = "UPDATE END_KEMID_CORPUS_VAL_T SET CRNT_PRIN_MVAL = ? WHERE KEMID = ?";

            SqlRowSet pendingEntryRowSet = getJdbcTemplate().queryForRowSet(selectCurrentBalanceSql);

            int count = 0;
            String kemId = null;
            BigDecimal principalAtMarket = null;

            while (pendingEntryRowSet.next()) {

                //store kem id and prin market
                kemId = pendingEntryRowSet.getString(EndowPropertyConstants.ColumnNames.UpdateCorpus.CRNT_BALANCE_KEMID);
                principalAtMarket = pendingEntryRowSet.getBigDecimal(EndowPropertyConstants.ColumnNames.UpdateCorpus.CRNT_BALANCE_PRINCIPAL_AT_MARKET);

                if(ObjectUtils.isNull(principalAtMarket)){
                    principalAtMarket = new BigDecimal("0");
                }

                // Update current corpus with new prin market value
                getSimpleJdbcTemplate().update(updateCurrentCorpusSql, principalAtMarket, kemId);

                count++;
            }

            LOG.info ("updateKemIdCorpusPrincipalMarketValue has updated Principal Market Value for " + count + " records in the END_KEMID_CORPUS_VAL_T table.");
        }
        catch (Exception e) {
            LOG.error("updateKemIdCorpusPrincipalMarketValue() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }

        LOG.debug("updateKemIdCorpusPrincipalMarketValue() completed");
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.dataaccess.UpdateCorpusDao#updateEndowmentCorpusWithCurrentEndowmentCorpus()
     */
    @Override
    public void updateEndowmentCorpusWithCurrentEndowmentCorpus(Date currentDate){

        LOG.debug("updateEndowmentCorpusWithCurrentEndowmentCorpus() started");

        try {
            String insertEndowCorpusSql = "INSERT INTO END_ENDOW_CORPUS_T (KEMID, CORPUS_VAL_HIST_DT, CORPUS_VAL, VER_NBR, OBJ_ID)";
            String selectCurrentCorpusSql = "SELECT KEMID, ?, CRNT_CORPUS_VAL, 1, ? FROM END_CRNT_ENDOW_CORPUS_T";

            //Update the current corpus table's prior year values
            int count = getSimpleJdbcTemplate().update(insertEndowCorpusSql + " " + selectCurrentCorpusSql, currentDate, java.util.UUID.randomUUID().toString());

            LOG.info ("updateEndowmentCorpusWithCurrentEndowmentCorpus has updated prior year values for " + count + " records in the END_ENDOW_CORPUS_T table.");
        }
        catch (Exception e) {
            LOG.error("updateEndowmentCorpusWithCurrentEndowmentCorpus() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }

        LOG.debug("updateEndowmentCorpusWithCurrentEndowmentCorpus() completed");

    }

    /**
     * Returns the next day
     *
     * @param currentDate
     * @return
     */
    protected Date getNextDay(Date currentDate){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        return new java.sql.Date(calendar.getTime().getTime());
    }
}
