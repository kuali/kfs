/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;

import edu.iu.uis.eden.clientapp.IDocHandler;

public abstract class ResearchDocumentActionBase extends KualiDocumentActionBase {

    public ResearchDocumentActionBase() {
        // TODO Auto-generated constructor stub
    }

    /**
     * This method will load the document, which was previously saved
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        researchForm.setDocId(researchForm.getDocument().getDocumentNumber());
        super.loadDocument(researchForm);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;

        if (IDocHandler.INITIATE_COMMAND.equals(researchForm.getCommand())) {
            // do something?
            researchForm.getResearchDocument().initialize();
        }
        return forward;
    }
}
