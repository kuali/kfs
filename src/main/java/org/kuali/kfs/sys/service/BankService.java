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
