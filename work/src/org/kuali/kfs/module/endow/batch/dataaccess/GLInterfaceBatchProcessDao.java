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
     * First all the document types are retrieved then for each document type,
     * all cash activity records are collected and then all non-cash activity records are gathered.
     * @param postedDate
     * @return Collection<GlInterfaceBatchProcessKemLine>
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactions(java.util.Date postedDate);

    /**
     * method to gather all the kem transactions as collection of records
     * First all the document types are retrieved then for each document type,
     * all cash activity records are collected.  The single records are then grouped 
     * based on chart, account number and object codes.
     * The same process is done for non-cash activity records.
     * @param postedDate
     * @return Collection<GlInterfaceBatchProcessKemLine>
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllCombinedKemTransactions(java.util.Date postedDate);

    /**
     * method to gather all the kem transactions as collection of records
     * First all cash activity records are collected for the given document type, 
     * The single records are then grouped based on chart, account number and object codes.
     * The same process is done for non-cash activity records.
     * @param documentType, postedDate
     * @return Collection<GlInterfaceBatchProcessKemLine>
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemTransactionsByDocumentType(String documentType, java.util.Date postedDate);

    /**
     * method to gather all the kem transactions as collection of records.
     * First all cash activity records are collected for the given document type and then these 
     * records are grouped based on chart, account number and object codes.
     * The same process is done for non-cash activity records.
     * @param documentType, postedDate
     * @return Collection<GlInterfaceBatchProcessKemLine>
     */
    public Collection<GlInterfaceBatchProcessKemLine> getAllKemCombinedTransactionsByDocumentType(String documentType, java.util.Date postedDate);
}
