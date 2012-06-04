/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.document.NonCheckDisbursementDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;


/**
 * Action class for <code>{@link org.kuali.kfs.fp.document.NonCheckDisbursementDocument}</code> It doesn't do anything
 * because it doesn't need to.
 */
public class NonCheckDisbursementAction extends KualiAccountingDocumentActionBase {

    /**
     * Override to set default bank code.
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        
        ((NonCheckDisbursementDocument) kualiDocumentFormBase.getDocument()).setDefaultBankCode();
    }
    
}
