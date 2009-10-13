/*
 * Copyright 2009 The Kuali Foundation
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
