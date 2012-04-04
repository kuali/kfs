/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ojb.broker.OptimisticLockException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springmodules.orm.ojb.OjbOperationException;

public class CabActionBase extends KualiAction {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward = mapping.findForward(RiceConstants.MAPPING_BASIC);

        // if found methodToCall, pass control to that method
        try {
            returnForward = super.execute(mapping, form, request, response);
        }
        catch (OjbOperationException e) {
            // special handling for OptimisticLockExceptions
            OjbOperationException ooe = (OjbOperationException) e;

            Throwable cause = ooe.getCause();
            if (cause instanceof OptimisticLockException) {
                OptimisticLockException ole = (OptimisticLockException) cause;
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, CabKeyConstants.DATA_EDIT_LOCK_ERROR);
            }
            else {
                // if exceptions are from 'save'
                throw e;
            }
        }
        return returnForward;
    }


    /**
     * This method will process the view document request by clicking on a specific document.
     * 
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward viewDoc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String documentId = request.getParameter("documentNumber");

        String docHandlerUrl = KewApiServiceLocator.getWorkflowDocumentService().getDocument(documentId).getDocumentHandlerUrl();
        if (docHandlerUrl.indexOf("?") == -1) {
            docHandlerUrl += "?";
        }
        else {
            docHandlerUrl += "&";
        }

        docHandlerUrl += "docId=" + documentId + "&" + "command=displayDocSearchView";
        return new ActionForward(docHandlerUrl, true);

    }
}
