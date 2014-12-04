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
package org.kuali.kfs.module.endow.dataaccess;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;

public interface TransactionArchiveDao {

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table. The data is sorted by DOC_TYP_NM, TRAN_SUB_TYP_CD, TRAN_IP_IND_CD,
     * TRAN_KEMID, TRAN_ETRAN_CD
     * 
     * @param postedDate
     * @return transactionArchives
     */
    public Collection<TransactionArchive> getAllTransactionArchives(java.util.Date postedDate);

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table
     * 
     * @return transactionArchives
     */
    public Collection<TransactionArchive> getAllTransactionArchives();

    /**
     * Gets a transactionArchive by primary keys.
     * 
     * @param documentNumber, lineNumber, lineTypeCode
     * @return a transactionArchive
     */
    public TransactionArchive getByPrimaryKey(String documentNumber, int lineNumber, String lineTypeCode);

    /**
     * Gets a count of total number of records from END_TRAN_ARCHV_T table for a given DOC_TYP_NM
     * 
     * @param feeMethod feeMethod object
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return count of transactionArhives matching the criteria
     */
    public long getTransactionArchivesCountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes);

    /**
     * Gets a count of total number of records from END_TRAN_ARCHV_T table for a given DOC_TYP_NM
     * 
     * @param feeMethodCode, transactionPostedDate
     * @return count of transactionArhives matching the passed in parameters
     */
    public long getTransactionArchivesCountByDocumentTypeName(String feeMethodCode, Date transactionPostedDate);

    /**
     * Gets a count of total number of records from END_TRAN_ARCHV_T table for a given DOC_TYP_NM
     * 
     * @param feeMethodCode, transactionPostedDate
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return count of transactionArhives matching the passed in parameters
     */
    public long getTransactionArchivesCountByETranCode(String feeMethodCode, Date transactionPostedDate, String feeMethodCodeForEtranCodes);

    /**
     * Gets a count of total number of records from END_TRAN_ARCHV_T table for a given DOC_TYP_NM and TRAN_ETRAN_CD
     * 
     * @param feeMethodCode, transactionPostedDate
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return count of transactionArhives matching the passed in parameters
     */
    public long getTransactionArchivesCountByDocumentTypeNameAndETranCode(String feeMethodCode, Date transactionPostedDate, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes);

    /**
     * Gets a count of total number of records from END_TRAN_ARCHV_T table for a given TRAN_IP_IND_CD
     * 
     * @param IncomeOrPrincipalIndicator
     * @return count of transactionArhives matching the passed in parameter
     */
    public long getTransactionArchivesCountByIncomeOrPrincipal(String IncomeOrPrincipalIndicator);

    /**
     * Gets a count of total number of records from END_TRAN_ARCHV_T table
     * 
     * @param feeMethod
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return count of transactionArhives based on the conditions in feeMethod object
     */
    public long getTransactionArchivesCountByBothIncomeAndPrincipal(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes);

    /**
     * Gets principal income amount from the selected records from END_TRAN_ARCHV_T table
     * 
     * @param feeMethod feeMethod object
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return incomeCashAmount of transactionArhives matching the criteria
     */
    public BigDecimal getTransactionArchivesIncomeCashAmountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes);

    /**
     * Gets principal cash amount from the selected records from END_TRAN_ARCHV_T table
     * 
     * @param feeMethod feeMethod object
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return incomeCashAmount of transactionArhives matching the criteria
     */
    public BigDecimal getTransactionArchivesPrincipalCashAmountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes);

    /**
     * Gets Income and principal cash amount from the selected records from END_TRAN_ARCHV_T table
     * 
     * @param feeMethod feeMethod object
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return Map with both income and principal cash amount of records matching the criteria
     */
    public HashMap<String, BigDecimal> getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes);

    /**
     * Gets total cash activity by adding income cash and principal cash amount from the selected records from END_TRAN_ARCHV_T
     * table
     * 
     * @param kemid kemid should be passed in as upper case if TransactionArchive kemid property is specified as forceUpperCase true
     *        in DD file
     * @param securityId
     * @return totalCashActivity
     */
    public BigDecimal getTransactionArchivesTotalCashActivity(String kemid, String securityId);

    /**
     * Gets a collection of TransactionArchive by kemids and posted dates
     * 
     * @param kemids
     * @param endowmentOption
     * @param beginningDate
     * @param endingDate
     * @return
     */
    public List<TransactionArchive> getTransactionArchiveByKemidsAndPostedDate(String kemid, String endowmentOption, java.util.Date beginningDate, java.util.Date endingDate, String closedIndicator, String transactionSubType);

    /**
     * Gets a collection of TransactionArchive by kemid and beginning and ending dates
     * 
     * @param kemids
     * @param beginningDate
     * @param endingDate
     * @return List<TransactionArchive>
     */
    public List<TransactionArchive> getTransactionArchivesByKemid(String kemid, java.util.Date beginningDate, java.util.Date endingDate);

}
