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
package org.kuali.kfs.module.purap.document.dataaccess;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Credit Memo DAO Interface. Defines DB access methods that a CreditMemoDaoImpl must implement.
 */
public interface CreditMemoDao {

    /**
     * Get all the credit memos that need to be extracted
     * 
     * @param chartCode - if not null, limit results to a single chart
     * @return - Iterator of credit memos
     */
    public List<VendorCreditMemoDocument> getCreditMemosToExtract(String chartCode);

    /**
     * Get all the credit memos that need to be extracted for a particular vendor record.
     * 
     * @param chartCode - if not null, limit results to a single chart
     * @param vendorHeaderGeneratedIdentifier
     * @param vendorDetailAssignedIdentifier
     * @return - Iterator of credit memos
     */
    public Collection<VendorCreditMemoDocument> getCreditMemosToExtractByVendor(String chartCode, VendorGroupingHelper vendor );
    
    /**
     * This method tests for a duplicate entry of a credit memo by the combination of vendorNumber HeaderId, vendorNumber and
     * creditMemoNumber. This method accepts the three values as arguments, and returns a boolean, describing whether a duplicate
     * exists in the system or not.
     * 
     * @param vendorNumberHeaderId - vendor number header id
     * @param vendorNumber - the composite two-part vendorNumber (headerId-detailId)
     * @param creditMemoNumber - the vendor-supplied creditMemoNumber
     * @return boolean - true if a match exists in the db, false if not
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, String creditMemoNumber);

    /**
     * This method tests for a duplicate entry of a credit memo by the combination of vendor number header id, vendor detail id,
     * date and amount. This method accepts the values as arguments, and returns a boolean, describing whether a duplicate exists in
     * the system or not.
     * 
     * @param vendorNumberHeaderId
     * @param vendorNumberDetailId
     * @param date - date of transaction
     * @param amount - amount of transaction
     * @return boolean - true if a match exists in the db, false if not
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, Date date, KualiDecimal amount);

    /**
     * This method returns a credit memo document number by id.
     * 
     * @param id - credit memo id
     * @return - document number
     */
    public String getDocumentNumberByCreditMemoId(Integer id);
    
    /**
     * Retrieves a list of potentially active credit memos for a purchase order by
     * status code. Active being defined as being enroute and before final. The issue
     * is that a status of vendor_tax_review may not mean that it's in review, but could be
     * in final (as there isn't a final status code for payment request). Workflow status
     * must be checked further after retrieval.
     * 
     * @param purchaseOrderId
     * @return
     */
    public List<String> getActiveCreditMemoDocumentNumbersForPurchaseOrder(Integer purchaseOrderId);
}
