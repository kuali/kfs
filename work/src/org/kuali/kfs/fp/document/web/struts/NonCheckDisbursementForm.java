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
import org.kuali.module.financial.document.NonCheckDisbursementDocument;

/**
 * This class is the action form for Non-Check Disbursement.
 * 
 * 
 */
public class NonCheckDisbursementForm extends KualiTransactionalDocumentFormBase {

    /**
     * NonCheckDisbursementForm default constructor
     */
    public NonCheckDisbursementForm() {
        super();
        setDocument(new NonCheckDisbursementDocument());
    }

    /**
     * @return Returns the NonCheckDisbursementDocument
     */
    public NonCheckDisbursementDocument getNonCheckDisbursementDocument() {
        return (NonCheckDisbursementDocument) getDocument();
    }
}
