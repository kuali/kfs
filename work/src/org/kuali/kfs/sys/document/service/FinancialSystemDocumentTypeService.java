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
package org.kuali.kfs.sys.document.service;

/**
 * Interface for methods which perform Financial System document type operations
 */
public interface FinancialSystemDocumentTypeService {
    /**
     * Determines if the doc type code is within the financial system and thus can be used on account
     * delegates. 
     * @param documentTypeCode the document type code to check
     * @return true if the document type code is within the KFS application space, false otherwise
     */
    public abstract boolean isFinancialSystemDocumentType(String documentTypeCode);
    
    /**
     * Determines if the document type code represents an accounting document
     * @param documentTypeCode the document type code to check
     * @return true if the document represents an accounting document, false otherwise
     */
    public abstract boolean isCurrentActiveAccountingDocumentType(String documentTypeCode);
}
