/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.form;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.InternalBillingItem;
import org.kuali.module.financial.document.InternalBillingDocument;

/**
 * This class is the action form for Internal Billing.
 * 
 * 
 */

public class InternalBillingForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;
    private InternalBillingItem newItem;

    public InternalBillingForm() {
        super();
        setDocument(new InternalBillingDocument());
        this.newItem = new InternalBillingItem();
    }

    /**
     * @return Returns the internalBillingDocument.
     */
    public InternalBillingDocument getInternalBillingDocument() {
        return (InternalBillingDocument) getDocument();
    }

    /**
     * @param internalBillingDocument The internalBillingDocument to set.
     */
    public void setInternalBillingDocument(InternalBillingDocument internalBillingDocument) {
        setDocument(internalBillingDocument);
    }

    /**
     * @return Returns the newItem.
     */
    public InternalBillingItem getNewItem() {
        return newItem;
    }

    /**
     * @param newItem The newItem to set.
     */
    public void setNewItem(InternalBillingItem newItem) {
        this.newItem = newItem;
    }
}
