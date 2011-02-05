/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;

/**
 * An interface to methods needed to join transaction related tables to create records
 */
public interface GLInterfaceBatchProcessDao {

    /**
     * method to find distinct document types from table END_TRAN_ARCHV_T.
     * 
     * @return List<Map<String, String>>
     */
    public Collection<String> findDocumentTypes();
    
    /**
     * method to gather all the kem transactions as collection of records
     * @param postedDate
     * @return Collection<GlInterfaceBatchProcessKemLine>
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactions(java.util.Date postedDate);

    /**
     * method to gather all the kem transactions as collection of records by a document type
     * @param documentType, postedDate
     * @return Collection<GlInterfaceBatchProcessKemLine>
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactionsByDocumentType(String documentType, java.util.Date postedDate);
    
}
