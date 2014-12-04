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
package org.kuali.kfs.module.tem.document.service;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;

public interface AccountingDocumentRelationshipService {

    /**
     * This method gets directly related document numbers
     *
     * @param documentNumber
     * @return
     */
    public Set<String> getRelatedDocumentNumbers(String documentNumber);

    /**
     * This method gets all related document numbers (sibling document numbers included).
     *
     * @param documentNumber
     * @return
     */
    public Set<String> getAllRelatedDocumentNumbers(String documentNumber);

    /**
     * This method gets the main root of the document number. The lookup is done recursively.
     *
     * @param documentNumber
     * @return
     */
    public String getRootDocumentNumber(String documentNumber);

    /**
     * This method saves a list of accountingDocumentRelationships
     *
     * @param accountingDocumentRelationships
     */
    public void save(List<AccountingDocumentRelationship> accountingDocumentRelationships);

    /**
     * This method saves an accountingDocumentRelationship
     *
     * @param accountingDocumentRelationship
     */
    public void save(AccountingDocumentRelationship accountingDocumentRelationship);

    /**
     * This method deletes a list of accountingDocumentRelationships
     *
     * @param accountingDocumentRelationships
     */
    public void delete(List<AccountingDocumentRelationship> accountingDocumentRelationships);

    /**
     * This method deletes an accountingDocumentRelationship
     *
     * @param accountingDocumentRelationship
     */
    public void delete(AccountingDocumentRelationship accountingDocumentRelationship);

    /**
     *
     * This method finds an accountingDocumentRelationship
     * @param adr
     * @return
     */
    public List<AccountingDocumentRelationship> find(AccountingDocumentRelationship adr);

    /**
     * This method gets directly related document numbers
     *
     * @param documentNumber
     * @return
     */
    public Set<String> huntForRelatedDocumentNumbersWithDocumentType(String documentNumber, String documentType);

    /**
     * Determines if the given document number represents a document which is a "child" of another document
     * @param documentNumber the document number to check for childishness
     * @return true if the document number represents a document which is the "related" document in any accounting document relationship; false otherwise
     */
    public boolean isDocumentSomebodysChild(String documentNumber);
}
