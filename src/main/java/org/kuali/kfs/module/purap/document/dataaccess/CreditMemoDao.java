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
