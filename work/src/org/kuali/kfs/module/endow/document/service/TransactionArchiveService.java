/*
 * Copyright 2009 The Kuali Foundation.
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
