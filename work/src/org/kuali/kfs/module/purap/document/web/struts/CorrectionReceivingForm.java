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
package org.kuali.kfs.module.purap.document.web.struts;

import org.kuali.kfs.module.purap.businessobject.CorrectionReceivingItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;

public class CorrectionReceivingForm extends ReceivingFormBase {

    protected String receivingLineDocId;
    protected CorrectionReceivingItem newCorrectionReceivingItemLine;
    
    /**
     * Constructs a ReceivingCorrectionForm instance and sets up the appropriately casted document.
     */
    public CorrectionReceivingForm() {
        super();

        this.setNewCorrectionReceivingItemLine(setupNewCorrectionReceivingItemLine());
        newCorrectionReceivingItemLine.setItemTypeCode("ITEM");
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "RCVC";
    }
    
    public CorrectionReceivingDocument getCorrectionReceivingDocument() {
        return (CorrectionReceivingDocument) getDocument();
    }

    public void setCorrectionReceivingDocument(CorrectionReceivingDocument CorrectionReceivingDocument) {
        setDocument(CorrectionReceivingDocument);
    }

    public String getReceivingLineDocId() {
        return receivingLineDocId;
    }

    public void setReceivingLineDocId(String purchaseOrderDocId) {
        this.receivingLineDocId = purchaseOrderDocId;
    }

    public CorrectionReceivingItem setupNewCorrectionReceivingItemLine() {
        return new CorrectionReceivingItem();
    }

    public ReceivingItem getNewReceivingCorrectionItemLine() {
        return newCorrectionReceivingItemLine;
    }

    public void setNewCorrectionReceivingItemLine(CorrectionReceivingItem newCorrectionReceivingItemLine) {
        this.newCorrectionReceivingItemLine = newCorrectionReceivingItemLine;
    }
}
