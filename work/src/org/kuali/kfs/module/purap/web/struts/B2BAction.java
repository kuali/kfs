/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BShoppingService;
import org.kuali.kfs.module.purap.document.web.struts.RequisitionForm;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.exception.MissingContractIdError;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class B2BAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BAction.class);

    public ActionForward shopCatalogs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        B2BForm b2bForm = (B2BForm) form;
        String url = SpringContext.getBean(B2BShoppingService.class).getPunchOutUrl(GlobalVariables.getUserSession().getFinancialSystemUser());

        if (ObjectUtils.isNull(url)) {
            // FIXME blow up
        }

        b2bForm.setShopUrl(url);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnFromShopping(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String cXml = request.getParameter("cxml-urlencoded");
        LOG.info("executeLogic() cXML returned in PunchoutOrderMessage:\n" + cXml);

        B2BShoppingCartParser cart = new B2BShoppingCartParser(cXml);

        if (cart.isSuccess()) {
            List requisitions = SpringContext.getBean(B2BShoppingService.class).createRequisitionsFromCxml(cart, GlobalVariables.getUserSession().getFinancialSystemUser());
            LOG.debug("executeLogic() REQS RETURNED TO ACTION");
            if (requisitions.size() > 1) {
                request.getSession().setAttribute("multipleB2BRequisitions", "true");
            }
            request.setAttribute("forward", "/portal.do?channelTitle=Requisition&channelUrl=purapRequisition.do?methodToCall=displayB2BRequisition");
            request.getSession().setAttribute("docId", ((RequisitionDocument) requisitions.get(0)).getDocumentNumber());
        }
        else {
            LOG.debug("executeLogic() Retrieving shopping cart from cxml was unsuccessful.");
            GlobalVariables.getErrorMap().putError("errorkey", "errors.b2b.nocart");
            //FIXME goto error page
        }

        return (mapping.findForward("removeframe"));
    }

}
