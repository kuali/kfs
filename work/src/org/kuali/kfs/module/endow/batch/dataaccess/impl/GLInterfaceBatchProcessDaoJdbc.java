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
package org.kuali.kfs.module.endow.batch.dataaccess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao;
import org.kuali.kfs.module.endow.businessobject.GLCombinedTransactionArchive;
import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * A class to do the database queries needed to calculate Balance By Consolidation Balance Inquiry Screen
 */
public class GLInterfaceBatchProcessDaoJdbc extends PlatformAwareDaoBaseJdbc implements GLInterfaceBatchProcessDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GLInterfaceBatchProcessDaoJdbc.class);

    private static final String NON_COMBINE_ENTRIES_SORT_ORDER = "ORDER BY a.DOC_TYP_NM, c.CHRT_CD, c.ACCT_NBR, b.OBJECT, a.TRAN_IP_IND_CD, a.TRAN_KEMID";
    private static final String COMBINE_ENTRIES_SORT_ORDER = "ORDER BY c.CHRT_CD, c.ACCT_NBR, b.OBJECT";

    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#findDocumentTypes()
     */
    public Collection<String> findDocumentTypes() {
        LOG.info("findDocumentTypes() started");
        
        Collection<String> documentTypes = new ArrayList();
        
        SqlRowSet documentTypesRowSet = getJdbcTemplate().queryForRowSet("SELECT DISTINCT(DOC_TYP_NM) DOC_TYP_NM FROM END_TRAN_ARCHV_T ORDER BY DOC_TYP_NM"); 

        while (documentTypesRowSet.next()) {
            documentTypes.add(documentTypesRowSet.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_DOC_TYP_NM));
        }
        
        LOG.info("findDocumentTypes() exited");
        
        return documentTypes;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemTransactions(java.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactions(java.util.Date postedDate) {
        LOG.info("getAllKemTransactions() started");

        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = findDocumentTypes();
        
        for (String documentType : documentTypes) {
            //get the cash activity records...
            SqlRowSet cashTransactionActivities = getAllKemTransactionsForCashActivity(documentType, postedDate, NON_COMBINE_ENTRIES_SORT_ORDER);
            buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
            
            //get non-cash activity records....
            SqlRowSet nonCashTransactionActivities = getAllKemTransactionsForNonCashActivity(documentType, postedDate, NON_COMBINE_ENTRIES_SORT_ORDER);
            buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
        }
        
        LOG.info("getAllKemTransactions() exited");
        
        return kemArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllCombinedKemTransactions(java.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllCombinedKemTransactions(java.util.Date postedDate) {
        LOG.info("getAllCombinedKemTransactions() started");

        Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions = new ArrayList();
        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = findDocumentTypes();
        
        for (String documentType : documentTypes) {
            //get the cash activity records...
            SqlRowSet cashTransactionActivities = getAllKemTransactionsForCashActivity(documentType, postedDate, COMBINE_ENTRIES_SORT_ORDER);
            buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemArchiveTransactions, true);
            
            //get non-cash activity records....
            SqlRowSet nonCashTransactionActivities = getAllKemTransactionsForNonCashActivity(documentType, postedDate, COMBINE_ENTRIES_SORT_ORDER);
            buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemArchiveTransactions, false);
        }
        
        LOG.info("getAllCombinedKemTransactions() exited.");
        
        return kemCombinedArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemTransactionsByDocumentType(Stringjava.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactionsByDocumentType(String documentType, java.util.Date postedDate) {
        LOG.info("getAllKemTransactionsByDocumentType() started");

        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList();
        
        //get the cash activity records...
        SqlRowSet cashTransactionActivities = getAllKemTransactionsForCashActivity(documentType, postedDate, NON_COMBINE_ENTRIES_SORT_ORDER);
        buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
        
        //get non-cash activity records....
        SqlRowSet nonCashTransactionActivities = getAllKemTransactionsForNonCashActivity(documentType, postedDate, NON_COMBINE_ENTRIES_SORT_ORDER);
        buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
        
        LOG.info("getAllKemTransactionsByDocumentType() exited.");
        
        return kemArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemCombinedTransactionsByDocumentType(Stringjava.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemCombinedTransactionsByDocumentType(String documentType, java.util.Date postedDate) {
        LOG.info("getAllKemCombinedTransactionsByDocumentType() started");

        Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions = new ArrayList();
        Collection<GlInterfaceBatchProcessKemLine> kemCashArchiveTransactions = new ArrayList();;
        Collection<GlInterfaceBatchProcessKemLine> kemNonCashArchiveTransactions = new ArrayList();;
        
        //get the cash activity records...
        SqlRowSet cashTransactionActivities = getAllKemTransactionsForCashActivity(documentType, postedDate, COMBINE_ENTRIES_SORT_ORDER);
        buildTransactionActivities(kemCashArchiveTransactions, cashTransactionActivities, true);
        if (kemCashArchiveTransactions.size() > 0) {
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemCashArchiveTransactions, true);
        }
        
        //get non-cash activity records....
        SqlRowSet nonCashTransactionActivities = getAllKemTransactionsForNonCashActivity(documentType, postedDate, COMBINE_ENTRIES_SORT_ORDER);
        buildTransactionActivities(kemNonCashArchiveTransactions, nonCashTransactionActivities, false);
        if (kemNonCashArchiveTransactions.size() > 0) {
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemNonCashArchiveTransactions, false);
        }
        
        LOG.info("getAllKemCombinedTransactionsByDocumentType() exited.");
        
        return kemCombinedArchiveTransactions;
    }
    
    /**
     * Method to get the cash activity transactions for a given document type.
     * @param documenType, postedDate, sortOrder
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, and END_ETRAN_GL_LNK_T tables in the given sort order
     */
    protected SqlRowSet getAllKemTransactionsForCashActivity(String documentType, java.util.Date postedDate, String sortOrder) {
        String cashTransactionArchiveSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                            + "a.TRAN_KEMID, a.TRAN_ETRAN_CD, a.TRAN_IP_IND_CD, a.TRAN_INC_CSH_AMT, a.TRAN_PRIN_CSH_AMT, "
                                            + "b.OBJECT, c.CHRT_CD, c.ACCT_NBR "
                                            + "FROM END_TRAN_ARCHV_T a, END_ETRAN_GL_LNK_T b, END_KEMID_GL_LNK_T c " 
                                            + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = 'C' AND "
                                            + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' AND "
                                            + "c.CHRT_CD = b.CHART_CD AND a.TRAN_ETRAN_CD = b.ETRAN_CD AND b.ROW_ACTV_IND = 'Y' " 
                                            + sortOrder);
        
        return (getJdbcTemplate().queryForRowSet(cashTransactionArchiveSql, new Object[] { postedDate, documentType }));
    }
    
    /**
     * Method to get the cash activity transactions for a given document type.
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, 
     * and END_ETRAN_GL_LNK_T, END_TRAN_ARCHV_SEC_T tables....
     */
    protected SqlRowSet getAllKemTransactionsForNonCashActivity(String documentType, java.util.Date postedDate, String sortOrder) {
        String nonCashTransactionsSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                         + "a.TRAN_KEMID, a.TRAN_IP_IND_CD, b.OBJECT, c.CHRT_CD, c.ACCT_NBR, "
                                         + "d.TRAN_SEC_COST, d.TRAN_SEC_LT_GAIN_LOSS, d.TRAN_SEC_ST_GAIN_LOSS "
                                         + "FROM END_TRAN_ARCHV_T a, END_ETRAN_GL_LNK_T b, END_KEMID_GL_LNK_T c, END_TRAN_ARCHV_SEC_T d " 
                                         + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = 'N' AND "
                                         + "a.FDOC_NBR = d.FDOC_NBR AND a.FDOC_LN_NBR = d.FDOC_LN_NBR AND a.FDOC_LN_TYP_CD = d.FDOC_LN_TYP_CD AND "
                                         + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' AND "
                                         + "c.CHRT_CD = b.CHART_CD AND d.TRAN_SEC_ETRAN_CD = b.ETRAN_CD AND b.ROW_ACTV_IND = 'Y' "
                                         + sortOrder);
        
        return (getJdbcTemplate().queryForRowSet(nonCashTransactionsSql, new Object[] { postedDate,  documentType}));
    }

    /**
     * method to go through the rowset and put into transient bo and add to the collection.
     */
    protected void buildTransactionActivities(Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions, SqlRowSet archiveTransactions, boolean cashType) {
        LOG.info("buildTransactionActivities() started");

        while (archiveTransactions.next()) {
            GlInterfaceBatchProcessKemLine glKemLine = new GlInterfaceBatchProcessKemLine();

            glKemLine.setDocumentNumber(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_NBR));
            glKemLine.setLineNumber(archiveTransactions.getInt(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_LN_NBR));
            glKemLine.setLineTypeCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_LN_TYP_CD));
            glKemLine.setSubTypeCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_SUB_TYP_CD));
            glKemLine.setTypeCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_DOC_TYP_NM));
            glKemLine.setKemid(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_KEM_ID));
            glKemLine.setIncomePrincipalIndicatorCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_IP_IND_CD));
            glKemLine.setObjectCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_OBJECT));

            //get transaction amount....
            if (cashType) {
                glKemLine.setTransactionArchiveIncomeAmount(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_INC_CSH_AMT));
                glKemLine.setTransactionArchivePrincipalAmount(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_PRIN_CSH_AMT));
                glKemLine.setHoldingCost(BigDecimal.ZERO);
                glKemLine.setLongTermGainLoss(BigDecimal.ZERO);
                glKemLine.setShortTermGainLoss(BigDecimal.ZERO);
            }
            else {
                glKemLine.setTransactionArchiveIncomeAmount(BigDecimal.ZERO);
                glKemLine.setTransactionArchivePrincipalAmount(BigDecimal.ZERO);
                glKemLine.setHoldingCost(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_SEC_COST));
                glKemLine.setShortTermGainLoss(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_SEC_ST_GAIN_LOSS));
                glKemLine.setLongTermGainLoss(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_SEC_LT_GAIN_LOSS));
            }
            
            glKemLine.setChartCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_CHRT_CD));
            glKemLine.setAccountNumber(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_ACCT_NBR));
            
            kemArchiveTransactions.add(glKemLine);
        }

        LOG.info("buildTransactionActivities() exited.");
    }
    
    /**
     * method to combine the kem transactions based on chart, account and object code
     * into single data records
     */
    protected void buildCombinedTransactionActivities(Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions, Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions, boolean cashType) {
        LOG.info("buildCombinedTransactionActivities() started");

        GLCombinedTransactionArchive gLCombinedTransactionArchive = new GLCombinedTransactionArchive();
        
        for (GlInterfaceBatchProcessKemLine kemArchiveTransaction : kemArchiveTransactions) {
            
            if (gLCombinedTransactionArchive.getChartCode() == null && gLCombinedTransactionArchive.getAccountNumber() == null && gLCombinedTransactionArchive.getObjectCode() == null) {
                gLCombinedTransactionArchive.copyChartAndAccountNumberAndObjectCodeValues(kemArchiveTransaction);
            } 
            if (gLCombinedTransactionArchive.getChartCode().compareToIgnoreCase(kemArchiveTransaction.getChartCode()) == 0
                    && gLCombinedTransactionArchive.getAccountNumber().compareToIgnoreCase(kemArchiveTransaction.getAccountNumber()) == 0
                    && gLCombinedTransactionArchive.getObjectCode().compareToIgnoreCase(kemArchiveTransaction.getObjectCode()) == 0) {
                gLCombinedTransactionArchive.incrementCombinedEntryCount();
                gLCombinedTransactionArchive.copyKemArchiveTransactionValues(kemArchiveTransaction, cashType);
            }
            else {
                GlInterfaceBatchProcessKemLine glKemLine = gLCombinedTransactionArchive.copyValuesToCombinedTransactionArchive(cashType);
                
                kemCombinedArchiveTransactions.add(glKemLine);
                gLCombinedTransactionArchive.initializeAmounts();
                gLCombinedTransactionArchive.copyChartAndAccountNumberAndObjectCodeValues(kemArchiveTransaction);
                gLCombinedTransactionArchive.copyKemArchiveTransactionValues(kemArchiveTransaction, cashType);
            } 
        }
        
        //write the last record for the document type....
        
        GlInterfaceBatchProcessKemLine glKemLine = gLCombinedTransactionArchive.copyValuesToCombinedTransactionArchive(cashType);
        kemCombinedArchiveTransactions.add(glKemLine);
        
        LOG.info("buildCombinedTransactionActivities() exited.");
    }
}   
