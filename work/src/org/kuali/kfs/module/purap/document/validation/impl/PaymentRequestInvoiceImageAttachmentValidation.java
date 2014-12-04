/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
