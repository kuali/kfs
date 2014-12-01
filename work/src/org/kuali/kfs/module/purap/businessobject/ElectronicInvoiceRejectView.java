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
package org.kuali.kfs.module.purap.businessobject;

import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.Note;

/**
 * Electronic invoice View Business Object.
 */
public class ElectronicInvoiceRejectView extends AbstractRelatedView {

    private Integer paymentRequestIdentifier;
    private Integer purchaseOrderIdentifier;

    @Override
    public String getDocumentIdentifierString() {
        return getDocumentNumber();
    }

    /**
     * Gets the paymentRequestIdentifier attribute.
     * @return Returns the paymentRequestIdentifier.
     */
    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    /**
     * Sets the paymentRequestIdentifier attribute value.
     * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
     */
    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    /**
     * The next three methods are overridden but shouldn't be! If they aren't
     * overridden, they don't show up in the tag, not sure why at this point! (AAP)
     *
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getPurapDocumentIdentifier()
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getDocumentTypeName()
     */
    @Override
    public String getDocumentTypeName() {
        return KFSConstants.FinancialDocumentTypeCodes.ELECTRONIC_INVOICE_REJECT;
    }
    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getUrl()
     */
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
