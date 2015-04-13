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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purchase Order View Business Object.
 */
public class PurchaseOrderView extends AbstractRelatedView {

    private Boolean purchaseOrderCurrentIndicator;
    private String recurringPaymentTypeCode;
    private String vendorChoiceCode;
    private Timestamp recurringPaymentEndDate;
    private Timestamp purchaseOrderInitialOpenTimestamp;

    private List<Note> notes;

    public boolean isPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    public boolean getPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    public void setPurchaseOrderCurrentIndicator(boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    public String getVendorChoiceCode() {
        return vendorChoiceCode;
    }

    public void setVendorChoiceCode(String vendorChoiceCode) {
        this.vendorChoiceCode = vendorChoiceCode;
    }

    public Timestamp getRecurringPaymentEndDate() {
        return recurringPaymentEndDate;
    }

    public void setRecurringPaymentEndDate(Timestamp recurringPaymentEndDate) {
        this.recurringPaymentEndDate = recurringPaymentEndDate;
    }

    public Timestamp getPurchaseOrderInitialOpenTimestamp() {
        return purchaseOrderInitialOpenTimestamp;
    }

    public void setPurchaseOrderInitialOpenTimestamp(Timestamp purchaseOrderInitialOpenTimestamp) {
        this.purchaseOrderInitialOpenTimestamp = purchaseOrderInitialOpenTimestamp;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        if (this.isPurchaseOrderCurrentIndicator()) {
            if (notes == null) {
                notes = new ArrayList<Note>();
                List<Note> tmpNotes = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderNotes(this.getPurapDocumentIdentifier());
                //FIXME if NoteService returns notes in descending order (newer ones first) then remove the following
                // reverse the order of notes retrieved so that newest note is in the front
                for (int i = tmpNotes.size()-1; i>=0; i--) {
                    Note note = tmpNotes.get(i);
                    notes.add(note);
                }
            }
        }
        else {
            notes = null;
        }

        return notes;
    }

    /**
     * The next four methods are overridden but shouldn't be! If they aren't overridden, they don't show up in the tag, not sure why at
     * this point! (AAP)
     *
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getPurapDocumentIdentifier()
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    @Override
    public String getDocumentIdentifierString() {
        return super.getDocumentIdentifierString();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getDocumentNumber()
     */
    @Override
    public String getDocumentNumber() {
        return super.getDocumentNumber();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getUrl()
     */
    @Override
    public String getUrl() {
        return super.getUrl();
    }

    /**
     * Checks whether the purchase order view needs a warning to be displayed, i.e. it never has been opened.
     * @return true if the purchase order needs a warning; false otherwise.
     */
    public boolean getNeedWarning() {
        return getPurchaseOrderInitialOpenTimestamp() == null;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getDocumentTypeName()
     */
    @Override
    public String getDocumentTypeName() {
        org.kuali.rice.kew.api.document.Document document = findWorkflowDocument(this.getDocumentNumber());
        if (ObjectUtils.isNotNull(document)) {
            return document.getDocumentTypeName();
        }

        return KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER;
    }
}
