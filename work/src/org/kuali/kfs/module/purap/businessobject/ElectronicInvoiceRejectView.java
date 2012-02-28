/*
 * Copyright 2007-2009 The Kuali Foundation
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
