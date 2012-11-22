/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;

import org.kuali.kfs.module.tem.rule.event.BlanketApproveDocumentWithoutRuleEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.service.impl.DocumentServiceImpl;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Document Service that allows for blanket approve document without the validation - its only used for Travel
 * Disbursement Service when it needs to generate the DV to be approved without the rule validation.
 *
 * This customization could be removed if there is a way to specify DV's payee can be paid to the initiator base
 * on parameter.  UA mod handles for ALL DV.  We want to make this a rule for TEM only if possible
 */
public class BlanketApproveWithoutRuleDocumentServiceImpl extends DocumentServiceImpl{

    /**
     * @see org.kuali.rice.kns.service.DocumentService#blanketApproveDocument(org.kuali.rice.kns.document.Document, java.lang.String,
     *      java.util.List)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Document blanketApproveDocument(Document document, String annotation, List adHocRecipients) throws ValidationException, WorkflowException {
        checkForNulls(document);
        document.prepareForSave();

        // using the new Event which does not invoke approve rule nor generate the route event
        validateAndPersistDocument(document, new BlanketApproveDocumentWithoutRuleEvent(document));
        prepareWorkflowDocument(document);
        getWorkflowDocumentService().blanketApprove(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), workflowDocument);
        return document;
    }
}
