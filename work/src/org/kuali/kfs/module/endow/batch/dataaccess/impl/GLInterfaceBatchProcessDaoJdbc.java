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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao;
import org.kuali.kfs.module.endow.businessobject.GLCombinedTransactionArchive;
import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.GLLinkDao;
import org.kuali.kfs.module.endow.dataaccess.KemidGeneralLedgerAccountDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveSecurityDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * A class to do the database queries needed to calculate Balance By Consolidation Balance Inquiry Screen
 */
public class GLInterfaceBatchProcessDaoJdbc extends PlatformAwareDaoBaseJdbc implements GLInterfaceBatchProcessDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GLInterfaceBatchProcessDaoJdbc.class);
    
    protected KemidGeneralLedgerAccountDao kemidGeneralLedgerAccountDao;
    protected GLLinkDao gLLinkDao;
    protected TransactionArchiveSecurityDao transactionArchiveSecurityDao;
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#findDocumentTypes()
     */
    public Collection<String> findDocumentTypes() {
        LOG.debug("findDocumentTypes() started");
        
        Collection<String> documentTypes = new ArrayList<String>();
        
        SqlRowSet documentTypesRowSet = getJdbcTemplate().queryForRowSet("SELECT DISTINCT(DOC_TYP_NM) DOC_TYP_NM FROM END_TRAN_ARCHV_T ORDER BY DOC_TYP_NM"); 

        while (documentTypesRowSet.next()) {
            documentTypes.add(documentTypesRowSet.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_DOC_TYP_NM));
        }
        
        LOG.debug("findDocumentTypes() exited");
        
        return documentTypes;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemTransactions(java.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactions(java.util.Date postedDate) {
        LOG.debug("getAllKemTransactions() started");

        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = findDocumentTypes();
        
        for (String documentType : documentTypes) {
            //get the cash activity records...
            SqlRowSet cashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.CASH);
            buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
            
            //get non-cash activity records....
            SqlRowSet nonCashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.NON_CASH);
            buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
        }
        
        LOG.debug("getAllKemTransactions() exited");

        return kemArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllCombinedKemTransactions(java.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllCombinedKemTransactions(java.util.Date postedDate) {
        LOG.debug("getAllCombinedKemTransactions() started");

        Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = findDocumentTypes();
        
        for (String documentType : documentTypes) {
            //get the cash activity records...
            SqlRowSet cashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.CASH);
            buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemArchiveTransactions, true);
            
            //get non-cash activity records....
            SqlRowSet nonCashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.NON_CASH);
            buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemArchiveTransactions, false);
        }
        
        LOG.debug("getAllCombinedKemTransactions() exited.");
        
        return kemCombinedArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemTransactionsByDocumentType(Stringjava.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactionsByDocumentType(String documentType, java.util.Date postedDate) {
        LOG.debug("getAllKemTransactionsByDocumentType() started");

        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        
        //get the cash activity records...
        SqlRowSet cashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.CASH);
        buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
        
        //get non-cash activity records....
        SqlRowSet nonCashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.NON_CASH);
        buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
        
        LOG.debug("getAllKemTransactionsByDocumentType() exited.");
        
        return kemArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemCombinedTransactionsByDocumentType(Stringjava.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemCombinedTransactionsByDocumentType(String documentType, java.util.Date postedDate) {
        LOG.debug("getAllKemCombinedTransactionsByDocumentType() started");

        Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        Collection<GlInterfaceBatchProcessKemLine> kemCashArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        Collection<GlInterfaceBatchProcessKemLine> kemNonCashArchiveTransactions = new ArrayList<GlInterfaceBatchProcessKemLine>();
        
        //get the cash activity records...
        SqlRowSet cashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.CASH);
        buildTransactionActivities(kemCashArchiveTransactions, cashTransactionActivities, true);
        if (kemCashArchiveTransactions.size() > 0) {
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemCashArchiveTransactions, true);
        }
        
        //get non-cash activity records....
        SqlRowSet nonCashTransactionActivities = getAllKemTransactions(documentType, postedDate, EndowConstants.TransactionSubTypeCode.NON_CASH);
        buildTransactionActivities(kemNonCashArchiveTransactions, nonCashTransactionActivities, false);
        if (kemNonCashArchiveTransactions.size() > 0) {
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemNonCashArchiveTransactions, false);
        }
        
        LOG.debug("getAllKemCombinedTransactionsByDocumentType() exited.");
        
        return kemCombinedArchiveTransactions;
    }

    /**
     * Method to get the cash activity transactions for a given document type.
     * @param documenType, postedDate, sortOrder
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, and END_ETRAN_GL_LNK_T tables in the given sort order
     */
    protected SqlRowSet getAllKemTransactions(String documentType, java.util.Date postedDate, String TransactionSubTypeCode) {
        String transactionArchiveSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                        + "a.TRAN_KEMID, a.TRAN_ETRAN_CD, a.TRAN_IP_IND_CD, a.TRAN_INC_CSH_AMT, a.TRAN_PRIN_CSH_AMT, "
                                        + "c.CHRT_CD, c.ACCT_NBR "
                                        + "FROM END_TRAN_ARCHV_T a, END_KEMID_GL_LNK_T c " 
                                        + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = ? AND "
                                        + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' "
                                        + "ORDER BY a.DOC_TYP_NM, c.CHRT_CD, c.ACCT_NBR, a.TRAN_ETRAN_CD, a.TRAN_KEMID");
        
        return (getJdbcTemplate().queryForRowSet(transactionArchiveSql, new Object[] { postedDate, documentType, TransactionSubTypeCode }));
    }
    
    /**
     * method to go through the rowset and put into transient bo and add to the collection.
     */
    protected void buildTransactionActivities(Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions, SqlRowSet archiveTransactions, boolean cashType) {
        LOG.debug("buildTransactionActivities() started");

        while (archiveTransactions.next()) {
            GlInterfaceBatchProcessKemLine glKemLine = new GlInterfaceBatchProcessKemLine();

            glKemLine.setDocumentNumber(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_NBR));
            glKemLine.setLineNumber(archiveTransactions.getInt(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_LN_NBR));
            glKemLine.setLineTypeCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_LN_TYP_CD));
            glKemLine.setSubTypeCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_SUB_TYP_CD));
            glKemLine.setTypeCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_DOC_TYP_NM));
            glKemLine.setKemid(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_KEM_ID));
            glKemLine.setIncomePrincipalIndicatorCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_IP_IND_CD));

            glKemLine.setChartCode(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_CHRT_CD));
            glKemLine.setAccountNumber(archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_ACCT_NBR));
            
            //get the security record now....
            BigDecimal holdingCost = BigDecimal.ZERO;
            BigDecimal shortTermGainLoss = BigDecimal.ZERO;
            BigDecimal longTermGainLoss = BigDecimal.ZERO;
            String eTransactionCode = "";
            
            TransactionArchiveSecurity transactionArchiveSecurity = transactionArchiveSecurityDao.getByPrimaryKey(glKemLine.getDocumentNumber(), glKemLine.getLineNumber(), glKemLine.getLineTypeCode());
            if (ObjectUtils.isNotNull(transactionArchiveSecurity)) {
                eTransactionCode = transactionArchiveSecurity.getEtranCode();
                holdingCost = transactionArchiveSecurity.getHoldingCost();
                shortTermGainLoss = transactionArchiveSecurity.getShortTermGainLoss();
                longTermGainLoss = transactionArchiveSecurity.getLongTermGainLoss();
            }
            
            //get transaction amount....
            if (cashType) {
                glKemLine.setTransactionArchiveIncomeAmount(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_INC_CSH_AMT));
                glKemLine.setTransactionArchivePrincipalAmount(archiveTransactions.getBigDecimal(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_PRIN_CSH_AMT));
                glKemLine.setHoldingCost(BigDecimal.ZERO);
                glKemLine.setLongTermGainLoss(BigDecimal.ZERO);
                glKemLine.setShortTermGainLoss(BigDecimal.ZERO);
                if (glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE)) {
                    glKemLine.setHoldingCost(holdingCost);
                    glKemLine.setShortTermGainLoss(shortTermGainLoss);
                    glKemLine.setLongTermGainLoss(longTermGainLoss);
                }                
            }
            else {
                glKemLine.setTransactionArchiveIncomeAmount(BigDecimal.ZERO);
                glKemLine.setTransactionArchivePrincipalAmount(BigDecimal.ZERO);
                glKemLine.setHoldingCost(holdingCost);
                glKemLine.setShortTermGainLoss(shortTermGainLoss);
                glKemLine.setLongTermGainLoss(longTermGainLoss);
            }
            
            //get the object code..
            if (EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(glKemLine.getSubTypeCode()) && 
                    (glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE) || 
                            glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE) ||
                            glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER) ||
                            glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.GENERAL_LEDGER_TO_ENDOWMENT_TRANSFER) ||
                            glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_TO_GENERAL_LEDGER_TRANSFER)) ) {
                glKemLine.setObjectCode(gLLinkDao.getObjectCode(glKemLine.getChartCode(), archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_ETRAN_CD)));
            }
            else {
                //user transaction archive security etran code..
                glKemLine.setObjectCode(gLLinkDao.getObjectCode(glKemLine.getChartCode(), eTransactionCode));
            }
            
            if (EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(glKemLine.getSubTypeCode())) {
                glKemLine.setNonCashOffsetObjectCode(gLLinkDao.getObjectCode(glKemLine.getChartCode(), archiveTransactions.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_TRAN_ETRAN_CD)));
            }
            else {
                glKemLine.setNonCashOffsetObjectCode(null);
            }
                    
            kemArchiveTransactions.add(glKemLine);
        }
        
        LOG.debug("buildTransactionActivities() exited.");
    }
    
    /**
     * method to combine the kem transactions based on chart, account and object code
     * into single data records
     */
    protected void buildCombinedTransactionActivities(Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions, Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions, boolean cashType) {
        LOG.debug("buildCombinedTransactionActivities() started");

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
        if (kemArchiveTransactions.size()> 0) {
            GlInterfaceBatchProcessKemLine glKemLine = gLCombinedTransactionArchive.copyValuesToCombinedTransactionArchive(cashType);
            kemCombinedArchiveTransactions.add(glKemLine);
        }
        
        LOG.debug("buildCombinedTransactionActivities() exited.");
    }
    
    /**
     * gets attribute kemidGeneralLedgerAccountDao
     * @return kemidGeneralLedgerAccountDao
     */
    protected KemidGeneralLedgerAccountDao getKemidGeneralLedgerAccountDao() {
        return kemidGeneralLedgerAccountDao;
    }

    /**
     * sets attribute kemidGeneralLedgerAccountDao
     */
    public void setKemidGeneralLedgerAccountDao(KemidGeneralLedgerAccountDao kemidGeneralLedgerAccountDao) {
        this.kemidGeneralLedgerAccountDao = kemidGeneralLedgerAccountDao;
    }
    
    /**
     * gets attribute gLLinkDao
     * @return gLLinkDao
     */
    protected GLLinkDao getgLLinkDao() {
        return gLLinkDao;
    }
    
    /**
     * sets attribute gLLinkDao
     */
    public void setgLLinkDao(GLLinkDao gLLinkDao) {
        this.gLLinkDao = gLLinkDao;
    }
    
    /**
     * gets attribute transactionArchiveSecurityDao
     * @return transactionArchiveSecurityDao
     */
    public TransactionArchiveSecurityDao getTransactionArchiveSecurityDao() {
        return transactionArchiveSecurityDao;
    }

    /**
     * sets attribute transactionArchiveSecurityDao
     */
    public void setTransactionArchiveSecurityDao(TransactionArchiveSecurityDao transactionArchiveSecurityDao) {
        this.transactionArchiveSecurityDao = transactionArchiveSecurityDao;
    }
}   
