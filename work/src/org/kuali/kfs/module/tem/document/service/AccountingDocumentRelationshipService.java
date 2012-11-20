/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
}
