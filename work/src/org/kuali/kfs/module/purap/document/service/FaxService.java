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
