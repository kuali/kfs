/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Bank;

/**
 * This service interface defines methods that a BankService implementation must provide.
 */
public interface BankService {
    public static final String[] PERMANENT_BANK_SPECIFICATION_ENABLED_DOCUMENT_TYPES = {KFSConstants.FinancialDocumentTypeCodes.ADVANCE_DEPOSIT, KFSConstants.FinancialDocumentTypeCodes.CASH_MANAGEMENT, KFSConstants.FinancialDocumentTypeCodes.NON_CHECK_DISBURSEMENT};

    /**
     * Retrieves a bank object who's primary id matches the values provided.
     *
     * @param bankCode The bank code to be looked up by.
     * @return A Bank object with a matching primary id.
     */
    public Bank getByPrimaryId(String bankCode);

    /**
     * Retrieves the default bank code for the given document type from system parameter.
     * .
     * @param documentClass <code>Class</code> for the document type
     * @return <code>Bank</code> object retrieved by default bank code
     */
    public Bank getDefaultBankByDocType(Class<?> documentClass);

    /**
     * Retrieves the default bank code for the given document type from system parameter.
     * .
     * @param documentTypeCode the document type code
     * @return <code>Bank</code> object retrieved by default bank code
     */
    public Bank getDefaultBankByDocType(String documentTypeCode);

    /**
     * Determines if the bank specification is enabled in the system by system parameter.
     *
     * @return true if specification is enabled
     */
    public boolean isBankSpecificationEnabled();

    /**
     * Determines if the bank specification is enabled for the given document by a) the document being one of the permanent
     * bank code documents (AD's, CMD's, and ND's) or by the document being listed in the KFS-SYS / Bank / BANK_CODE_DOCUMENT_TYPES parameter
     * @param documentClass the class of the document to determine if it has bank specifications enabled
     * @return true if specification is enabled, false if specification is disabled
     */
    public boolean isBankSpecificationEnabledForDocument(Class<?> documentClass);
}
