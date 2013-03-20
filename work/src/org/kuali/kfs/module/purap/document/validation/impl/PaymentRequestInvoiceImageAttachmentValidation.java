/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestInvoiceImageAttachmentValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();

        if(isDocumentStoppedInRouteNode(document, "ImageAttachment")){
            //assume false if we're in the correct node
            valid = false;

            //loop through notes looking for a invoice image
            List boNotes = document.getNotes();
            if (ObjectUtils.isNotNull(boNotes)) {
                for (Object obj : boNotes) {
                    Note note = (Note) obj;

                    note.refreshReferenceObject("attachment");
                    if (ObjectUtils.isNotNull(note.getAttachment()) && PurapConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_INVOICE_IMAGE.equals(note.getAttachment().getAttachmentTypeCode())) {
                        valid = true;
                        break;
                    }
                }
            }

            if(valid == false){
                GlobalVariables.getMessageMap().putError(KRADConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME, PurapKeyConstants.ERROR_PAYMENT_REQUEST_INVOICE_REQUIRED);
            }
        }

        GlobalVariables.getMessageMap().clearErrorPath();

        return valid;
    }

    /**
     *
     * @param document
     * @param nodeName
     * @return
     */
    protected boolean isDocumentStoppedInRouteNode(PaymentRequestDocument document, String nodeName) {
        WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
        Set<String> currentRouteLevels = workflowDoc.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentRouteLevels) && currentRouteLevels.contains(nodeName) && workflowDoc.isApprovalRequested()) {
            return true;
        }
        return false;
    }
}
