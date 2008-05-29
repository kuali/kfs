/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.form;

import org.kuali.module.purap.bo.ReceivingCorrectionItem;
import org.kuali.module.purap.bo.ReceivingItem;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public class ReceivingCorrectionForm extends ReceivingFormBase {

    private String receivingLineDocId;
    private ReceivingCorrectionItem newReceivingCorrectionItemLine;
    
    /**
     * Constructs a ReceivingCorrectionForm instance and sets up the appropriately casted document.
     */
    public ReceivingCorrectionForm() {
        super();
        setDocument(new ReceivingCorrectionDocument());

        this.setNewReceivingCorrectionItemLine(setupNewReceivingCorrectionItemLine());
        newReceivingCorrectionItemLine.setItemTypeCode("ITEM");
    }

    public ReceivingCorrectionDocument getReceivingCorrectionDocument() {
        return (ReceivingCorrectionDocument) getDocument();
    }

    public void setReceivingCorrectionDocument(ReceivingCorrectionDocument ReceivingCorrectionDocument) {
        setDocument(ReceivingCorrectionDocument);
    }

    public String getReceivingLineDocId() {
        return receivingLineDocId;
    }

    public void setReceivingLineDocId(String purchaseOrderDocId) {
        this.receivingLineDocId = purchaseOrderDocId;
    }

    public ReceivingCorrectionItem setupNewReceivingCorrectionItemLine() {
        return new ReceivingCorrectionItem();
    }

    public ReceivingItem getNewReceivingCorrectionItemLine() {
        return newReceivingCorrectionItemLine;
    }

    public void setNewReceivingCorrectionItemLine(ReceivingCorrectionItem newReceivingCorrectionItemLine) {
        this.newReceivingCorrectionItemLine = newReceivingCorrectionItemLine;
    }
}
