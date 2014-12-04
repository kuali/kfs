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

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

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
    
    public Collection generateBulkReceivingPDF(BulkReceivingDocument blkRecDoc,ByteArrayOutputStream stream);
}
