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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.exception.MissingContractIdError;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser;
import org.kuali.kfs.pdp.web.struts.BaseAction;
import org.kuali.rice.kns.util.GlobalVariables;

public class B2BAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BAction.class);

  private B2BService b2bService;
  private RequisitionService requisitionService;

  public void setB2BService(B2BService b2b) {
    b2bService = b2b;
  }

  public void setRequisitionService(RequisitionService rs) {
    requisitionService = rs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.iu.uis.ps.action.BaseAction#executeLogic(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    // Start a B2B order, then get the Cxml from sciquest, create requisitions.
    String forward = null; //for test only

    String punchOut = request.getParameter("action");
    LOG.debug("executeLogic() ACTION = " + punchOut);
    request.getSession().removeAttribute(Globals.MESSAGE_KEY);

    if (! "create".equals(punchOut) ) {
      String url = b2bService.getPunchOutUrl(getUser(request));
      if ( url != null ) {
        request.setAttribute("shopURL",url);
        forward = "valid";
      } else {
        LOG.debug("perform() cxml response not successful");
        forward = "invalid";
      }
    } else {//need to check action again
      String cXml = request.getParameter("cxml-urlencoded");
      LOG.debug("executeLogic() cXML returned in PunchoutOrderMessage:\n" + cXml);      
      forward = "removeframe";

      try {
        B2BShoppingCartParser cart = new B2BShoppingCartParser(cXml);

        if (cart.isSuccess()) {
          List requisitions = b2bService.createRequisitionsFromCxml(cart, getUser(request));
          LOG.debug("executeLogic() REQS RETURNED TO ACTION");
          if (requisitions.size() > 1) {
            request.setAttribute("forward", "/requisition/listRequisitions.jsp");
            request.getSession().setAttribute("requisitions", requisitions);
          } else {
            request.setAttribute("forward", "/requisition.do");
            //need to create a new instance of reqForm to clear session
            RequisitionForm formBean = new RequisitionForm();
            formBean.setRequisitionDocument((RequisitionDocument) requisitions.get(0));
            request.getSession().setAttribute("RequisitionForm", formBean);
          }

        } else {
          LOG.debug("executeLogic() Retrieving shopping cart from cxml was unsuccessful.");
          GlobalVariables.getErrorMap().putError("errorkey", "errors.b2b.nocart");          
        }
      } catch (CxmlParseError e) {
        LOG.error("executeLogic() CxmlParseException occurred while retrieving shopping cart.", e);
        GlobalVariables.getErrorMap().putError("error.blank.msg", e.getMessage());        
      } catch (MissingContractIdError mcie) {
        LOG.error("executeLogic() ", mcie);
        GlobalVariables.getErrorMap().putError("error.blank.msg", mcie.getMessage());        
      } catch (IllegalArgumentException duns) {
        LOG.error("executeLogic() ", duns);
        GlobalVariables.getErrorMap().putError("error.blank.msg", duns.getMessage());        
      }
       
    }
    
    LOG.debug("executeLogic() Returning to " + forward);
    return (mapping.findForward(forward));
  }
}
