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
package org.kuali.kfs.module.tem.dataaccess;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;

public interface AccountingDocumentRelationshipDao {
    public List<AccountingDocumentRelationship> findAccountingDocumentRelationshipByDocumentNumber(String value);
    
    public List<AccountingDocumentRelationship> findAccountingDocumentRelationshipByDocumentNumber(String attribute, String value);
    
    public List<AccountingDocumentRelationship> findAccountingDocumentRelationship(AccountingDocumentRelationship adr);
       
    public void save(AccountingDocumentRelationship accountingDocumentRelationship);
    
    public void delete(AccountingDocumentRelationship accountingDocumentRelationship);    
}
