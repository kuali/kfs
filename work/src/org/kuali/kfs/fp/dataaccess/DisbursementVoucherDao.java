/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.dao;

import java.util.Collection;

import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.vendor.bo.VendorDetail;

public interface DisbursementVoucherDao {
    /**
     * Saves the Disbursement Voucher Document
     * 
     * @param document
     */
    public void save(DisbursementVoucherDocument document);

    /**
     * Returns a document by its document number
     * 
     * @param fdocNbr
     * @return document
     */
    public DisbursementVoucherDocument getDocument(String fdocNbr);

    /**
     * Returns a list of disbursement voucher documents with a specific doc header status
     * 
     * @param statusCode
     * @return list of doc headers
     */
    public Collection getDocumentsByHeaderStatus(String statusCode);

    /**
     * Get a vendor using a vendor Id
     * 
     * @param vendorHeaderId
     * @param vendorDetailId
     * @return
     */
    public VendorDetail getVendor(String vendorHeaderId, String vendorDetailId);
    
    /**
     * 
     * This method retrieves an instance of the employee object using the id provided.
     * @param uuid
     * @return
     */
    public FinancialSystemUser getEmployee(String uuid);
    
}
