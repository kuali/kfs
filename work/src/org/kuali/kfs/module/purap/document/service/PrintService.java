
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

/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */