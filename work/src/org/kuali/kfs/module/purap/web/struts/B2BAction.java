/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BShoppingService;
import org.kuali.kfs.module.purap.exception.B2BConnectionException;
import org.kuali.kfs.module.purap.exception.B2BShoppingException;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.util.cxml.B2BParserHelper;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCart;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class B2BAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BAction.class);

    public ActionForward shopCatalogs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        B2BForm b2bForm = (B2BForm) form;
        String url = SpringContext.getBean(B2BShoppingService.class).getPunchOutUrl(GlobalVariables.getUserSession().getPerson());

        if (ObjectUtils.isNull(url)) {
            throw new B2BConnectionException("Unable to connect to remote site for punchout.");
        }

        b2bForm.setShopUrl(url);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnFromShopping(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String cXml = request.getParameter("cxml-urlencoded");
        LOG.info("executeLogic() cXML returned in PunchoutOrderMessage:\n" + cXml);

        B2BShoppingCart cart = B2BParserHelper.getInstance().parseShoppingCartXML(cXml);

        if (cart.isSuccess()) {
            List requisitions = SpringContext.getBean(B2BShoppingService.class).createRequisitionsFromCxml(cart, GlobalVariables.getUserSession().getPerson());
            if (requisitions.size() > 1) {
                request.getSession().setAttribute("multipleB2BRequisitions", "true");
            }
            request.setAttribute("forward", SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapPropertyConstants.B2B_PUNCH_BACK_ACTION_FORWARDING_URL));
            request.getSession().setAttribute("docId", ((RequisitionDocument) requisitions.get(0)).getDocumentNumber());
        }
        else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("executeLogic() Retrieving shopping cart from cxml was unsuccessful. Error message:" + cart.getStatusText());
            }
            throw new B2BShoppingException("Retrieving shopping cart from cxml was unsuccessful. Error message:" + cart.getStatusText());
        }

        return (mapping.findForward("removeframe"));
    }

}

