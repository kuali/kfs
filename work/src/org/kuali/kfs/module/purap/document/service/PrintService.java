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
package org.kuali.module.purap.service;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * Defines methods that must be implemented by classes providing a PrintService.
 */
public interface PrintService {
    /**
     * Create the Purchase Order Quote Requests List Pdf document and send it back to the Action so that it can be dealt with.
     * 
     * @param po                     The PurchaseOrderDocument.
     * @param byteArrayOutputStream  ByteArrayOutputStream that the action is using, where the pdf will be printed to.
     * @return                       Collection of error strings
     */
    public Collection generatePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream);

    /**
     * Create the Purchase Order Quote Requests List Pdf document and save it so that it can be faxed in a later process.
     * 
     * @param po The PurchaseOrderDocument.
     * @return   Collection of error strings.
     */
    public Collection savePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po);

    /**
     * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
     * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
     * 
     * Create the Purchase Order Quote Pdf document and send it back to the Action so that it can be dealt with.
     * 
     * @param po                     PurchaseOrderDocument that holds the Quote.
     * @param povq                   PurchaseOrderVendorQuote that is being transmitted to.
     * @param byteArrayOutputStream  ByteArrayOutputStream that the action is using, where the pdf will be printed to.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @return                       Collection of error strings.
     */
    public Collection generatePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream byteArrayOutputStream, String environment);

    /**
     * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
     * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
     * Create the Purchase Order Quote Pdf document and save it so that it can be faxed in a later process.
     * 
     * @param po           PurchaseOrderDocument that holds the Quote.
     * @param povq         PurchaseOrderVendorQuote that is being transmitted to.
     * @param environment  The current environment used (e.g. DEV if it is a development environment).
     * @return             Collection of error strings.
     */
    public Collection savePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, String environment);

    /**
     * Create the Purchase Order Pdf document for non-retransmission and send it back to the Action so that it can be dealt with.
     * 
     * @param po                     The PurchaseOrderDocument.
     * @param byteArrayOutputStream  ByteArrayOutputStream that the action is using, where the pdf will be printed to.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @param retransmitItems        The items selected by the user to be retransmitted.
     * @return                       Collection of error strings.
     */
    public Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String environment, List<PurchaseOrderItem> retransmitItems);

    /**
     * Create the Purchase Order Pdf document for retransmission and send it back to the Action so that it can be dealt with.
     * 
     * @param po                     The PurchaseOrderDocument.
     * @param byteArrayOutputStream  ByteArrayOutputStream that the action is using, where the pdf will be printed to.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @param retransmitItems        The items selected by the user to be retransmitted.
     * @return                       Collection of error strings.
     */
    public Collection generatePurchaseOrderPdfForRetransmission(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String environment, List<PurchaseOrderItem> retransmitItems);

    /**
     * Create the Purchase Order Pdf document for non-retransmission and save it so that it can be faxed in a later process.
     * 
     * @param po                     The PurchaseOrderDocument.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @return                       Collection of error strings.
     */
    public Collection savePurchaseOrderPdf(PurchaseOrderDocument po, String environment);

    /**
     * Create the Purchase Order Pdf document for retransmission and save it so that it can be faxed in a later process.
     * 
     * @param po           The PurchaseOrderDocument.
     * @param environment  The current environment used (e.g. DEV if it is a development environment).
     * @return             Collection of error strings.
     */
    public Collection savePurchaseOrderPdfForRetransmission(PurchaseOrderDocument po, String environment);
}
