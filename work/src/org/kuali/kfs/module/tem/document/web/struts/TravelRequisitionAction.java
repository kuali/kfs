/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.web.struts.RequisitionAction;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class...
 */
public class TravelRequisitionAction extends RequisitionAction {
    
    
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward =super.docHandler(mapping, form, request, response);
        TravelRequisitionForm travelREQSForm = (TravelRequisitionForm) form;
        if(travelREQSForm.getTemDocID()!=null){
            TravelDocumentBase document=(TravelDocumentBase)getDocumentService().getByDocumentHeaderId(travelREQSForm.getTemDocID().toString());
            RequisitionDocument reqsDoc= (RequisitionDocument)travelREQSForm.getDocument();
            document.populateRequisitionFields(reqsDoc, document);
            travelREQSForm.getDocumentActions().put(KNSConstants.KUALI_ACTION_CAN_EDIT__DOCUMENT_OVERVIEW, "false");
            String relationDescription = document.getDocumentHeader().getWorkflowDocument().getDocumentType() + " - REQS";
            SpringContext.getBean(AccountingDocumentRelationshipService.class).save(new AccountingDocumentRelationship(document.getDocumentNumber(), reqsDoc.getDocumentNumber(), relationDescription));
        }
       
        return forward;
    }
   
    
}
