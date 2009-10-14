/*
 * Copyright 2008 The Kuali Foundation
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
