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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class PaymentRequestInvoiceImageAttachmentValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getErrorMap().clearErrorPath();
        
        if(isDocumentStoppedInRouteNode(document, "ImageAttachment")){
            //assume false if we're in the correct node
            valid = false;
            
            //loop through notes looking for a invoice image
            List boNotes = document.getBoNotes();
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
                GlobalVariables.getErrorMap().putError(KNSConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME, PurapKeyConstants.ERROR_PAYMENT_REQUEST_INVOICE_REQUIRED);
            }
        }
        
        GlobalVariables.getErrorMap().clearErrorPath();
                
        return valid;
    }

    private boolean isDocumentStoppedInRouteNode(PaymentRequestDocument document, String nodeName) {
        List<String> currentRouteLevels = new ArrayList<String>();
        try {
            KualiWorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            currentRouteLevels = Arrays.asList(document.getDocumentHeader().getWorkflowDocument().getNodeNames());
            if (currentRouteLevels.contains(nodeName) && workflowDoc.isApprovalRequested()) {
                return true;
            }
            return false;
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }
}
