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
import org.kuali.module.financial.document.TransferOfFundsDocument;

/**
 * This class is the form class for the Transfer of Funds document. This method extends the parent
 * KualiTransactionalDocumentFormBase class which contains all of the common form methods and form attributes needed by the Transfer
 * of Funds document. It adds a new method which is a convenience method for getting at the Transfer of Funds document easier.
 * 
 * 
 */
public class TransferOfFundsForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TransferOfFundsForm instance and sets up the appropriately casted document.
     */
    public TransferOfFundsForm() {
        super();
        setDocument(new TransferOfFundsDocument());
    }

    /**
     * @return Returns the serviceBillingDocument.
     */
    public TransferOfFundsDocument getTransferOfFundsDocument() {
        return (TransferOfFundsDocument) getDocument();
    }
}
