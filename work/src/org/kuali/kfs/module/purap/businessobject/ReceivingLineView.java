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
package org.kuali.module.purap.bo;

import java.util.List;

import org.kuali.core.bo.Note;

/**
 * Requisition View Business Object.
 */
public class ReceivingLineView extends AbstractRelatedView {
    private Integer purchaseOrderId;

    public Integer getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Integer purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    /**
     * The next three methods are overridden but shouldnt be! If they arent overridden, they dont show up in the tag, not sure why
     * at this point! (AAP)
     * 
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getPurapDocumentIdentifier()
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
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }

    /**
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getUrl()
     */
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
