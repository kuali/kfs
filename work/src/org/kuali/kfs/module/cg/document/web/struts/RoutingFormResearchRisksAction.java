/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/web/struts/RoutingFormResearchRisksAction.java,v $
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
package org.kuali.module.kra.routingform.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.module.kra.routingform.bo.RoutingFormProtocol;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormResearchRisksAction extends RoutingFormAction {

    public ActionForward insertRoutingFormProtocol(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        super.save(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;
         
//document.routingFormProtocol[${status.index}].protocolApprovalPendingIndicator" attributeEntry="${routingFormProtocolAttributes.protocolApprovalPendingIndicator}" />

//        routingForm.getRoutingFormDocument().getRoutingFormProtocol().setRoutingFormProtocols(RoutingFormProtocol);
        
//        RoutingFormProtocol routingFormProtocol = (RoutingFormProtocol) routingFormProtocol.add().routingForm.getNewRoutingFormProtocol();
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}