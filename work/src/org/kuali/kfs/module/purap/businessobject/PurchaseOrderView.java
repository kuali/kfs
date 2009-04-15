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
package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Purchase Order View Business Object.
 */
public class PurchaseOrderView extends AbstractRelatedView {

    private Boolean purchaseOrderCurrentIndicator;
    private String purchaseOrderStatusCode;
    private String recurringPaymentTypeCode;
    private String vendorChoiceCode;
    private Timestamp recurringPaymentEndDate;
    
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

    public String getPurchaseOrderStatusCode() {
        return purchaseOrderStatusCode;
    }

    public void setPurchaseOrderStatusCode(String purchaseOrderStatusCode) {
        this.purchaseOrderStatusCode = purchaseOrderStatusCode;
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

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        if (this.isPurchaseOrderCurrentIndicator()) {
            if (notes == null) {
                notes = new TypedArrayList(Note.class);
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
        
}
