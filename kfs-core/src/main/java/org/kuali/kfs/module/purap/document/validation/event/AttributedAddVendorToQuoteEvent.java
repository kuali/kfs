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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;



/**
 * Add a vendor to the quote tab event. 
 * This is triggered when a user presses the add vendor button for a given vendor.
 */
public final class AttributedAddVendorToQuoteEvent extends AttributedDocumentEventBase {
    
    private PurchaseOrderVendorQuote vendorQuote;
    
    /**
     * Constructs an AddVendorToQuoteEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     */
    public AttributedAddVendorToQuoteEvent(String errorPathPrefix, Document document, PurchaseOrderVendorQuote vendorQuote) {
        super("adding vendor to document " + getDocumentId(document), errorPathPrefix, document);
        this.vendorQuote = vendorQuote;
    }

    public PurchaseOrderVendorQuote getVendorQuote() {
        return vendorQuote;
    }

    public void setVendorQuote(PurchaseOrderVendorQuote vendorQuote) {
        this.vendorQuote = vendorQuote;
    }
    
    
}
