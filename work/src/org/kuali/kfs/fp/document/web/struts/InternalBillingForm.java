/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.InternalBillingItem;
import org.kuali.module.financial.document.InternalBillingDocument;

/**
 * This class is the action form for Internal Billing.
 * 
 * @author Kuali Financial Transactions Team ()
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
