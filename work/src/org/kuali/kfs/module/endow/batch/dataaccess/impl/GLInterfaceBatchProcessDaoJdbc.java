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
import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * A class to do the database queries needed to calculate Balance By Consolidation Balance Inquiry Screen
 */
public class GLInterfaceBatchProcessDaoJdbc extends PlatformAwareDaoBaseJdbc implements GLInterfaceBatchProcessDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GLInterfaceBatchProcessDaoJdbc.class);

    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#findDocumentTypes()
     */
    public Collection<String> findDocumentTypes() {
        Collection<String> documentTypes = new ArrayList();
        
        SqlRowSet documentTypesRowSet = getJdbcTemplate().queryForRowSet("SELECT DISTINCT(DOC_TYP_NM) DOC_TYP_NM FROM END_TRAN_ARCHV_T ORDER BY DOC_TYP_NM"); 

        while (documentTypesRowSet.next()) {
            documentTypes.add(documentTypesRowSet.getString(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_DOC_TYP_NM));
        }
        
        return documentTypes;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemTransactions(java.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactions(java.util.Date postedDate) {
        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = findDocumentTypes();
        
        for (String documentType : documentTypes) {
            //get the cash activity records...
            SqlRowSet cashTransactionActivities = getAllKemTransactionsForCashActivity(documentType, postedDate);
            buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
            
            //get noncash activitiy records....
            SqlRowSet nonCashTransactionActivities = getAllKemTransactionsForNonCashActivity(documentType, postedDate);
            buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
        }
        
        return kemArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllCombinedKemTransactions(java.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllCombinedKemTransactions(java.util.Date postedDate) {
        Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions = new ArrayList();
        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList();
        
        //get all the available document types names sorted
        Collection<String> documentTypes = findDocumentTypes();
        
        for (String documentType : documentTypes) {
            //get the cash activity records...
            SqlRowSet cashTransactionActivities = getAllCombinedKemTransactionsForCashActivity(documentType, postedDate);
            buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemArchiveTransactions, true);
            
            //get noncash activitiy records....
            SqlRowSet nonCashTransactionActivities = getAllCombinedKemTransactionsForNonCashActivity(documentType, postedDate);
            buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemArchiveTransactions, false);
        }
        
        return kemCombinedArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemTransactionsByDocumentType(Stringjava.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactionsByDocumentType(String documentType, java.util.Date postedDate) {
        Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions = new ArrayList();
        
        //get the cash activity records...
        SqlRowSet cashTransactionActivities = getAllKemTransactionsForCashActivity(documentType, postedDate);
        buildTransactionActivities(kemArchiveTransactions, cashTransactionActivities, true);
        
        //get noncash activitiy records....
        SqlRowSet nonCashTransactionActivities = getAllKemTransactionsForNonCashActivity(documentType, postedDate);
        buildTransactionActivities(kemArchiveTransactions, nonCashTransactionActivities, false);
        
        return kemArchiveTransactions;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#getAllKemCombinedTransactionsByDocumentType(Stringjava.util.Date)
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemCombinedTransactionsByDocumentType(String documentType, java.util.Date postedDate) {
        Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions = new ArrayList();
        Collection<GlInterfaceBatchProcessKemLine> kemCashArchiveTransactions = new ArrayList();;
        Collection<GlInterfaceBatchProcessKemLine> kemNonCashArchiveTransactions = new ArrayList();;
        
        //get the cash activity records...
        SqlRowSet cashTransactionActivities = getAllCombinedKemTransactionsForCashActivity(documentType, postedDate);
        buildTransactionActivities(kemCashArchiveTransactions, cashTransactionActivities, true);
        if (kemCashArchiveTransactions.size() > 0) {
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemCashArchiveTransactions, true);
        }
        
        //get noncash activitiy records....
        SqlRowSet nonCashTransactionActivities = getAllCombinedKemTransactionsForNonCashActivity(documentType, postedDate);
        buildTransactionActivities(kemNonCashArchiveTransactions, nonCashTransactionActivities, false);
        if (kemNonCashArchiveTransactions.size() > 0) {
            buildCombinedTransactionActivities(kemCombinedArchiveTransactions, kemNonCashArchiveTransactions, false);
        }
        
        return kemCombinedArchiveTransactions;
    }
    
    /**
     * Method to get the cash activity transactions for a given document type.
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, and END_ETRAN_GL_LNK_T tables....
     */
    private SqlRowSet getAllKemTransactionsForCashActivity(String documentType, java.util.Date postedDate) {
        String cashTransactionArchiveSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                         + "a.TRAN_KEMID, a.TRAN_ETRAN_CD, a.TRAN_IP_IND_CD, a.TRAN_INC_CSH_AMT, a.TRAN_PRIN_CSH_AMT, "
                                         + "b.OBJECT, c.CHRT_CD, c.ACCT_NBR "
                                         + "FROM END_TRAN_ARCHV_T a, END_ETRAN_GL_LNK_T b, END_KEMID_GL_LNK_T c " 
                                         + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = 'C' AND "
                                         + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' AND "
                                         + "c.CHRT_CD = b.CHART_CD AND a.TRAN_ETRAN_CD = b.ETRAN_CD AND b.ROW_ACTV_IND = 'Y' " 
                                         + "ORDER BY a.DOC_TYP_NM, c.CHRT_CD, c.ACCT_NBR, b.OBJECT, a.TRAN_IP_IND_CD, a.TRAN_KEMID");
        
        return (getJdbcTemplate().queryForRowSet(cashTransactionArchiveSql, new Object[] { postedDate, documentType }));
    }
    
    /**
     * Method to get the cash activity transactions for a given document type.
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, 
     * and END_ETRAN_GL_LNK_T, END_TRAN_ARCHV_SEC_T tables....
     */
    private SqlRowSet getAllKemTransactionsForNonCashActivity(String documentType, java.util.Date postedDate) {
        String nonCashTransactionsSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                        + "a.TRAN_KEMID, a.TRAN_IP_IND_CD, b.OBJECT, c.CHRT_CD, c.ACCT_NBR, "
                                        + "d.TRAN_SEC_COST, d.TRAN_SEC_LT_GAIN_LOSS, d.TRAN_SEC_ST_GAIN_LOSS "
                                        + "FROM END_TRAN_ARCHV_T a, END_ETRAN_GL_LNK_T b, END_KEMID_GL_LNK_T c, END_TRAN_ARCHV_SEC_T d " 
                                        + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = 'N' AND "
                                        + "a.FDOC_NBR = d.FDOC_NBR AND a.FDOC_LN_NBR = d.FDOC_LN_NBR AND a.FDOC_LN_TYP_CD = d.FDOC_LN_TYP_CD AND "
                                        + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' AND "
                                        + "c.CHRT_CD = b.CHART_CD AND d.TRAN_SEC_ETRAN_CD = b.ETRAN_CD AND b.ROW_ACTV_IND = 'Y' "
                                        + "ORDER BY a.DOC_TYP_NM, c.CHRT_CD, c.ACCT_NBR, b.OBJECT, a.TRAN_IP_IND_CD, a.TRAN_KEMID");
        
        return (getJdbcTemplate().queryForRowSet(nonCashTransactionsSql, new Object[] { postedDate,  documentType}));
    }

    /**
     * method to go through the rowset and put into transient bo and add to the collection.
     */
    private void buildTransactionActivities(Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions, SqlRowSet archiveTransactions, boolean cashType) {

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
    }
    
    
    /**
     * Method to get the cash activity transactions for a given document type combined.
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, and END_ETRAN_GL_LNK_T tables....
     */
    private SqlRowSet getAllCombinedKemTransactionsForCashActivity(String documentType, java.util.Date postedDate) {
        String cashTransactionArchiveSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                         + "a.TRAN_KEMID, a.TRAN_ETRAN_CD, a.TRAN_IP_IND_CD, a.TRAN_INC_CSH_AMT, a.TRAN_PRIN_CSH_AMT, "
                                         + "b.OBJECT, c.CHRT_CD, c.ACCT_NBR "
                                         + "FROM END_TRAN_ARCHV_T a, END_ETRAN_GL_LNK_T b, END_KEMID_GL_LNK_T c " 
                                         + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = 'C' AND "
                                         + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' AND "
                                         + "c.CHRT_CD = b.CHART_CD AND a.TRAN_ETRAN_CD = b.ETRAN_CD AND b.ROW_ACTV_IND = 'Y' " 
                                         + "ORDER BY c.CHRT_CD, c.ACCT_NBR, b.OBJECT");
        
        return (getJdbcTemplate().queryForRowSet(cashTransactionArchiveSql, new Object[] { postedDate, documentType }));
    }
    
    /**
     * Method to get the cash activity transactions for a given document type.
     * joins records from END_TRAN_ARCHV_T, END_KEMID_GL_LNK_T, 
     * and END_ETRAN_GL_LNK_T, END_TRAN_ARCHV_SEC_T tables....
     */
    private SqlRowSet getAllCombinedKemTransactionsForNonCashActivity(String documentType, java.util.Date postedDate) {
        String nonCashTransactionsSql = ("SELECT a.FDOC_NBR, a.FDOC_LN_NBR, a.FDOC_LN_TYP_CD, a.DOC_TYP_NM, a.TRAN_SUB_TYP_CD, "
                                        + "a.TRAN_KEMID, a.TRAN_IP_IND_CD, b.OBJECT, c.CHRT_CD, c.ACCT_NBR, "
                                        + "d.TRAN_SEC_COST, d.TRAN_SEC_LT_GAIN_LOSS, d.TRAN_SEC_ST_GAIN_LOSS "
                                        + "FROM END_TRAN_ARCHV_T a, END_ETRAN_GL_LNK_T b, END_KEMID_GL_LNK_T c, END_TRAN_ARCHV_SEC_T d " 
                                        + "WHERE a.TRAN_PSTD_DT = ? AND a.DOC_TYP_NM = ? AND a.TRAN_SUB_TYP_CD = 'N' AND "
                                        + "a.FDOC_NBR = d.FDOC_NBR AND a.FDOC_LN_NBR = d.FDOC_LN_NBR AND a.FDOC_LN_TYP_CD = d.FDOC_LN_TYP_CD AND "
                                        + "a.TRAN_KEMID = c.KEMID AND a.TRAN_IP_IND_CD = c.IP_IND_CD AND c.ROW_ACTV_IND = 'Y' AND "
                                        + "c.CHRT_CD = b.CHART_CD AND d.TRAN_SEC_ETRAN_CD = b.ETRAN_CD AND b.ROW_ACTV_IND = 'Y' "
                                        + "ORDER BY c.CHRT_CD, c.ACCT_NBR, b.OBJECT");
        
        return (getJdbcTemplate().queryForRowSet(nonCashTransactionsSql, new Object[] { postedDate,  documentType}));
    }
    
    /**
     * method to combine the kem transactions based on chart, account and object code
     * into single data records
     */
    protected void buildCombinedTransactionActivities(Collection<GlInterfaceBatchProcessKemLine> kemCombinedArchiveTransactions, Collection<GlInterfaceBatchProcessKemLine> kemArchiveTransactions, boolean cashType) {
        String chartCode = null;
        String accountNumber = null;
        String objectCode = null;
        long combinedEntryCount = 0;
        boolean recordWritten = true;
        
        int lineNumber = 0;
        String lineTypeCode = null;
        String subTypeCode = null;
        String typeCode = null;
        String ipIndicator = null;
        String kemid = null;
        String documentNumber = null;
        
        BigDecimal incomeAmount = BigDecimal.ZERO;
        BigDecimal principalAmount = BigDecimal.ZERO;
        BigDecimal holdingAmount = BigDecimal.ZERO;
        BigDecimal longTermAmount = BigDecimal.ZERO;
        BigDecimal shortTermAmount = BigDecimal.ZERO;

        for (GlInterfaceBatchProcessKemLine kemArchiveTransaction : kemArchiveTransactions) {
            GlInterfaceBatchProcessKemLine glKemLine = new GlInterfaceBatchProcessKemLine();
            
            if (chartCode == null && accountNumber == null && objectCode == null) {
                chartCode = kemArchiveTransaction.getChartCode();
                accountNumber = kemArchiveTransaction.getAccountNumber();
                objectCode = kemArchiveTransaction.getObjectCode();
            } 
            if (chartCode.compareToIgnoreCase(kemArchiveTransaction.getChartCode()) == 0
                    && accountNumber.compareToIgnoreCase(kemArchiveTransaction.getAccountNumber()) == 0
                    && objectCode.compareToIgnoreCase(kemArchiveTransaction.getObjectCode()) == 0) {
                combinedEntryCount++;
                recordWritten = false;
                lineNumber = kemArchiveTransaction.getLineNumber();
                lineTypeCode = kemArchiveTransaction.getLineTypeCode();
                subTypeCode = kemArchiveTransaction.getSubTypeCode();
                typeCode = kemArchiveTransaction.getTypeCode();
                ipIndicator = kemArchiveTransaction.getIncomePrincipalIndicatorCode();
                kemid = kemArchiveTransaction.getKemid();
                documentNumber = kemArchiveTransaction.getDocumentNumber();
                
                if (cashType) {
                    incomeAmount = incomeAmount.add(kemArchiveTransaction.getTransactionArchiveIncomeAmount());
                    principalAmount = principalAmount.add(kemArchiveTransaction.getTransactionArchivePrincipalAmount());
                }
                else {
                    holdingAmount = holdingAmount.add(kemArchiveTransaction.getHoldingCost());
                    shortTermAmount = shortTermAmount.add(kemArchiveTransaction.getShortTermGainLoss());
                    longTermAmount = longTermAmount.add(kemArchiveTransaction.getLongTermGainLoss());
                }
            }
            else {
                if (combinedEntryCount > 1) {
                    glKemLine.setDocumentNumber("Summary");
                    glKemLine.setKemid("Summary");
                }
                else {
                    glKemLine.setDocumentNumber(kemArchiveTransaction.getDocumentNumber());
                    glKemLine.setKemid(kemArchiveTransaction.getKemid());
                }
                glKemLine.setLineNumber(lineNumber);
                glKemLine.setLineTypeCode(lineTypeCode);
                glKemLine.setSubTypeCode(subTypeCode);
                glKemLine.setTypeCode(typeCode);
                glKemLine.setIncomePrincipalIndicatorCode(ipIndicator);
                glKemLine.setObjectCode(objectCode);
                
            //    glKemLine.setLineNumber(kemArchiveTransaction.getLineNumber());
           //     glKemLine.setLineTypeCode(kemArchiveTransaction.getLineTypeCode());
           //     glKemLine.setSubTypeCode(kemArchiveTransaction.getSubTypeCode());
           //     glKemLine.setTypeCode(kemArchiveTransaction.getTypeCode());
           //     glKemLine.setIncomePrincipalIndicatorCode(kemArchiveTransaction.getIncomePrincipalIndicatorCode());
           //     glKemLine.setObjectCode(objectCode);

                //get transaction amount....
                if (cashType) {
                    glKemLine.setTransactionArchiveIncomeAmount(incomeAmount);
                    glKemLine.setTransactionArchivePrincipalAmount(principalAmount);
                    glKemLine.setHoldingCost(BigDecimal.ZERO);
                    glKemLine.setLongTermGainLoss(BigDecimal.ZERO);
                    glKemLine.setShortTermGainLoss(BigDecimal.ZERO);
                }
                else {
                    glKemLine.setTransactionArchiveIncomeAmount(BigDecimal.ZERO);
                    glKemLine.setTransactionArchivePrincipalAmount(BigDecimal.ZERO);
                    glKemLine.setHoldingCost(holdingAmount);
                    glKemLine.setShortTermGainLoss(shortTermAmount);
                    glKemLine.setLongTermGainLoss(longTermAmount);
                }
                
                glKemLine.setChartCode(chartCode);
                glKemLine.setAccountNumber(accountNumber);
                
                kemCombinedArchiveTransactions.add(glKemLine);
                
                recordWritten = true;
                combinedEntryCount = 0;
                
                chartCode = kemArchiveTransaction.getChartCode();
                accountNumber = kemArchiveTransaction.getAccountNumber();
                objectCode = kemArchiveTransaction.getObjectCode();

                incomeAmount = BigDecimal.ZERO;
                principalAmount = BigDecimal.ZERO;
                holdingAmount = BigDecimal.ZERO;
                longTermAmount = BigDecimal.ZERO;
                shortTermAmount = BigDecimal.ZERO;
                recordWritten = false;
                
                lineNumber = kemArchiveTransaction.getLineNumber();
                lineTypeCode = kemArchiveTransaction.getLineTypeCode();
                subTypeCode = kemArchiveTransaction.getSubTypeCode();
                typeCode = kemArchiveTransaction.getTypeCode();
                ipIndicator = kemArchiveTransaction.getIncomePrincipalIndicatorCode();
                kemid = kemArchiveTransaction.getKemid();
                documentNumber = kemArchiveTransaction.getDocumentNumber();
                
                if (cashType) {
                    incomeAmount = incomeAmount.add(kemArchiveTransaction.getTransactionArchiveIncomeAmount());
                    principalAmount = principalAmount.add(kemArchiveTransaction.getTransactionArchivePrincipalAmount());
                }
                else {
                    holdingAmount = holdingAmount.add(kemArchiveTransaction.getHoldingCost());
                    shortTermAmount = shortTermAmount.add(kemArchiveTransaction.getShortTermGainLoss());
                    longTermAmount = longTermAmount.add(kemArchiveTransaction.getLongTermGainLoss());
                }
            } // adding record to the collection
        } //for loop
        
        //write the last record for the document type....
        GlInterfaceBatchProcessKemLine glKemLine = new GlInterfaceBatchProcessKemLine();
            
        if (combinedEntryCount > 1) {
            glKemLine.setDocumentNumber("Summary");
            glKemLine.setKemid("Summary");
        }
        else {
            glKemLine.setDocumentNumber(documentNumber);
            glKemLine.setKemid(kemid);
        }
        glKemLine.setLineNumber(lineNumber);
        glKemLine.setLineTypeCode(lineTypeCode);
        glKemLine.setSubTypeCode(subTypeCode);
        glKemLine.setTypeCode(typeCode);
        glKemLine.setIncomePrincipalIndicatorCode(ipIndicator);
        glKemLine.setObjectCode(objectCode);

        //get transaction amount....
        if (cashType) {
            glKemLine.setTransactionArchiveIncomeAmount(incomeAmount);
            glKemLine.setTransactionArchivePrincipalAmount(principalAmount);
            glKemLine.setHoldingCost(BigDecimal.ZERO);
            glKemLine.setLongTermGainLoss(BigDecimal.ZERO);
            glKemLine.setShortTermGainLoss(BigDecimal.ZERO);
        }
        else {
            glKemLine.setTransactionArchiveIncomeAmount(BigDecimal.ZERO);
            glKemLine.setTransactionArchivePrincipalAmount(BigDecimal.ZERO);
            glKemLine.setHoldingCost(holdingAmount);
            glKemLine.setShortTermGainLoss(shortTermAmount);
            glKemLine.setLongTermGainLoss(longTermAmount);
        }
            
        glKemLine.setChartCode(chartCode);
        glKemLine.setAccountNumber(accountNumber);
            
        kemCombinedArchiveTransactions.add(glKemLine);
    }
}   
