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
package org.kuali.module.purap.web.struts.action;

import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.service.ReceivingService;
import org.kuali.module.purap.web.struts.form.ReceivingCorrectionForm;
import org.kuali.module.purap.web.struts.form.ReceivingLineForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class ReceivingCorrectionAction extends ReceivingBaseAction {

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {       
        super.createDocument(kualiDocumentFormBase);

        ReceivingCorrectionForm rcf = (ReceivingCorrectionForm)kualiDocumentFormBase;
        ReceivingCorrectionDocument rcDoc = (ReceivingCorrectionDocument)rcf.getDocument();
        
        //set identifier from form value
        rcDoc.setReceivingLineDocumentNumber( rcf.getReceivingLineDocId() );
        
        rcDoc.initiateDocument();
        
        //populate and save Receiving Line Document from Purchase Order        
        SpringContext.getBean(ReceivingService.class).populateReceivingCorrectionDocument(rcDoc);

    }

}
