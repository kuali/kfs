/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.document;

import java.util.List;

import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.bo.SourceDocumentReference;
import org.kuali.module.purap.bo.Status;
import org.kuali.module.purap.bo.StatusHistory;


/**
 * Purchasing-Accounts Payable Document Interface
 */
public interface PurchasingAccountsPayableDocument extends AccountingDocument {
    
    public Integer getVendorHeaderGeneratedIdentifier();

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier);


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     * 
     */
    public Integer getVendorDetailAssignedIdentifier();

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     * 
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier);

    public String getVendorCustomerNumber();

    public void setVendorCustomerNumber(String vendorCustomerNumber);

    public Integer getPurapDocumentIdentifier();

    public void setPurapDocumentIdentifier(Integer identifier);

    public Status getStatus();

    public void setStatus(Status status);

    public String getStatusCode();

    public void setStatusCode(String statusCode);

    public List<StatusHistory> getStatusHistories();

    public void setStatusHistories(List<StatusHistory> statusHistories);
    
    public List<SourceDocumentReference> getSourceDocumentReferences();
    
    public void setSourceDocumentReferences(List<SourceDocumentReference> sourceDocumentReferences);

}
