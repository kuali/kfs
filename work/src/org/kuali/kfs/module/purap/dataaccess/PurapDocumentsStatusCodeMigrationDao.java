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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;

/**
 * An interface to methods needed to join transaction related tables to create records
 */
public interface PurapDocumentsStatusCodeMigrationDao {
    
    /**
     * Retrieves the requisition documents to migrate status code to workflow side
     * 
     * @return requisition documents where status code exists.
     */
    public List<RequisitionDocument> getRequisitionDocumentsForStatusCodeMigration();

    /**
     * Retrieves the purchase order documents to migrate status code to workflow side
     * 
     * @return purchase order documents where status code exists.
     */
    public List<PurchaseOrderDocument> getPurchaseOrderDocumentsForStatusCodeMigration();
    
    /**
     * Retrieves the purchase order vendor quote documents to migrate status code to workflow side
     * 
     * @return purchase order vendor quote documents where status code exists.
     */
    public List<PurchaseOrderVendorQuote> getPurchaseOrderVendorQuoteDocumentsForStatusCodeMigration();
    
    /**
     * Retrieves the payment request documents to migrate status code to workflow side
     * 
     * @return payment request documents where status code exists.
     */
    public List<PaymentRequestDocument> getPaymentRequestDocumentsForStatusCodeMigration();
    
    /**
     * Retrieves the vendor credit memo documents to migrate status code to workflow side
     * 
     * @return vendor credit memo documents where status code exists.
     */
    public List<VendorCreditMemoDocument> getVendorCreditMemoDocumentsForStatusCodeMigration();
    
    
}
