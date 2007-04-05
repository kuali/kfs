
package org.kuali.module.purap.service;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;

public interface PrintService {
    /**
     * Create the Purchase Order Quote Requests List Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po, 
        ByteArrayOutputStream byteArrayOutputStream);
    /**
     * Create the Purchase Order Quote Requests List Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @return Collection of ServiceError objects
     */
    public Collection savePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po);
    /**
     * Create the Purchase Order Quote Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @param povq      PurchaseOrderVendorQuote that is being transmitted to
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, 
        ByteArrayOutputStream byteArrayOutputStream, String environment);
    /**
     * Create the Purchase Order Quote Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @param poqv      PurchaseOrderVendorQuote that is being transmitted to
     * @return Collection of ServiceError objects
     */
    public Collection savePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, String environment);
    /**
     * Create the Purchase Order Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrderDocument that holds the Quote
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, 
        boolean isRetransmit, String environment);
    /**
     * Create the Purchase Order Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @return Collection of ServiceError objects
     */
    public Collection savePurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit, String environment);
} 
