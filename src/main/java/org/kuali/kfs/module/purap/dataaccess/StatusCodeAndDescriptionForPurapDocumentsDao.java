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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.Map;

/**
 * An interface to methods needed to join transaction related tables to create records
 */
public interface StatusCodeAndDescriptionForPurapDocumentsDao {
    
    /**
     * Retrieves the status code and status description for requistions.
     * 
     * @return Map<String, String>
     */
    public Map<String, String> getRequisitionDocumentStatuses();

    /**
     * Retrieves the status code and status description for purchase orders.
     * 
     * @return Map<String, String>
     */
    public Map<String, String> getPurchaseOrderDocumentStatuses();
    
    /**
     * Retrieves the status code and status description for vendor credit memos.
     * 
     * @return Map<String, String>
     */
    public Map<String, String> getVendorCreditMemoDocumentStatuses();
    
    /**
     * Retrieves the status code and status description for payment request.
     * 
     * @return Map<String, String>
     */
    public Map<String, String> getPaymentRequestDocumentStatuses();
    
    /**
     * Retrieves the status code and status description for Line Item Receiving.
     * 
     * @return Map<String, String>
     */
    public Map<String, String> getLineItemReceivingDocumentStatuses();
    
}
