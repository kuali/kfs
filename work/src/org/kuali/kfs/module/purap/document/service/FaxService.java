/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

/**
 * Defines methods that must be implemented by classes providing a PrintService.
 */
public interface FaxService {
    /**
     * Create the Purchase Order Quote Pdf document and send it via
     * fax to the recipient in the PO Quote
     * 
     * @param po        PurchaseOrder that holds the Quote
     * @param poqv      PurchaseOrderQuoteVendor that is being transmitted to
     */
    public void faxPurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq);
    /**
     * Create the Purchase Order Pdf document and send it via
     * fax to the recipient in the PO
     * 
     * @param po                         PurchaseOrder that holds the Quote
     * @param isRetransmit               sends true if PO is being retransmitted
     */
    public void faxPurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit);
    /**
     * Create the Purchase Order Pdf document and send it via
     * fax to the recipient in the PO
     * 
     * @param po                         PurchaseOrder that holds the Quote
     * @param isRetransmit               if passed true then PO is being retransmitted
     */
    public void faxPurchaseOrderPdf(PurchaseOrderDocument po, String pdfFileLocation, String imageTempLocation, boolean isRetransmit);
    /**
     * Fax the PO and return true if it succeeds. Add error message and return false if problems
     * 
     * @param po                     The PurchaseOrderDocument.
     * @return                       boolean result
     */
    
}
