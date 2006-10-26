/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/web/struts/AssignContractManagerAction.java,v $
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

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.kra.budget.bo.AppointmentType;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.web.struts.form.AssignContractManagerForm;
import org.kuali.core.util.ObjectUtils;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * This class handles Actions for AssignContractManager.
 * 
 * @author PURAP (kualidev@oncourse.iu.edu)
 */

public class AssignContractManagerAction extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerAction.class);

    /**
     * Do initialization for a new AssignContractManagerDocument
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        
        super.createDocument(kualiDocumentFormBase);
        
        ((AssignContractManagerDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
        
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument)((AssignContractManagerForm)form).getDocument();

        List assignedRequisitions = acmDocument.getAssignContractManagerDetails();
        
        for (Iterator iter = acmDocument.getUnassignedRequisitions().iterator(); iter.hasNext();) {
            RequisitionDocument req = (RequisitionDocument) iter.next();
            
            if (ObjectUtils.isNotNull(req.getContractManagerCode())) {
                // TODO: check that we have a valid contractManagerCode.
                AssignContractManagerDetail detail = new AssignContractManagerDetail();
                detail.setContractManagerCode(req.getContractManagerCode());
                detail.setFinancialDocumentNumber(req.getFinancialDocumentNumber());
                detail.setRequisitionIdentifier(req.getIdentifier());
                
                assignedRequisitions.add(detail);
            }
        }
        acmDocument.setAssignContractManagerDetails(assignedRequisitions);
        
        return super.save(mapping, form, request, response);
    }

    
}
