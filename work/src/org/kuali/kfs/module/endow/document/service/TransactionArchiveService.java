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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.TransactionArchive;

public interface TransactionArchiveService {

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table
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
     * Gets a collection of records from END_TRAN_ARCHV_T table for a given DOC_TYP_NM
     * @param typeCode The document type name (DOC_TYP_NM in the table)
     * @return transactionArchives
     */
    public Collection<TransactionArchive> getTransactionArchivesByDocumentTypeName(String typeCode);

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table for a given ETranCode
     * @param etranCode The etranCode (TRAN_ETRAN_CD in the table)
     * @return transactionArchives
     */
    public Collection<TransactionArchive> getTransactionArchivesByETranCode(String etranCode);

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table for a given DOC_TYP_NM and TRAN_ETRAN_CD
     * @param typeCode The document type name (DOC_TYP_NM in the table)
     * @param etranCode The etranCode (TRAN_ETRAN_CD in the table)
     * @return transactionArchives
     */
    public Collection<TransactionArchive> getTransactionArchivesByDocumentTypeNameAndETranCode(String typeCode, String etranCode);

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table for a given TRAN_IP_IND_CD
     * @param incomePrincipalIndicator The income or principal indicator (TRAN_IP_IND_CD in the table)
     * @return transactionArchives where the indicator is I (income) or P (principal)
     */
    public Collection<TransactionArchive> getTransactionArchivesByIncomeOrPrincipalIndicator(String incomeOrPrincipalIndicator);

    /**
     * Gets a collection of records from END_TRAN_ARCHV_T table for a given TRAN_IP_IND_CD, DOC_TYP_NM, TRAN_ETRAN_CD
     * @param typeCode, etranCode
     * @return transactionArchives where the incomePrincipalIndicator = B
     */
    public Collection<TransactionArchive> getAllTransactionArchives(String typeCode, String etranCode);
    
}
