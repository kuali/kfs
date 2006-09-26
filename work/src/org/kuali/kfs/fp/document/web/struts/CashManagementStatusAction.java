/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.action;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.web.struts.form.CashManagementStatusForm;


/**
 * Action class for CashManagementStatusForm
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class CashManagementStatusAction extends KualiAction {
    private static Logger LOG = Logger.getLogger(CashManagementStatusAction.class);

    /**
     * Default constructor
     */
    public CashManagementStatusAction() {
    }


    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // populate with exception values, if any
        CashManagementStatusForm cform = (CashManagementStatusForm) form;
        CashDrawerStateException e = (CashDrawerStateException) request.getAttribute(Globals.EXCEPTION_KEY);
        if (e != null) {
            cform.setMethodToCall("displayPage");

            cform.setVerificationUnit(e.getVerificationUnit());
            cform.setControllingDocumentId(e.getControllingDocumentId());
            cform.setCurrentDrawerStatus(e.getCurrentDrawerStatus());
            cform.setDesiredDrawerStatus(e.getDesiredDrawerStatus());
        }

        // generate the status message
        String[] msgParams = { cform.getVerificationUnit(), cform.getControllingDocumentId(), cform.getCurrentDrawerStatus(), cform.getDesiredDrawerStatus() };

        ActionMessage message = new ActionMessage(KeyConstants.CashDrawer.MSG_CASH_DRAWER_ALREADY_OPEN, msgParams);

        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, message);
        saveMessages(request, messages);

        return super.execute(mapping, form, request, response);
    }

    /**
     * Displays the status page. When requests get redirected here, I need to reset the form's methodToCall to something nonblank or
     * the superclass will try to invoke a method which (probably) doesn't exist in this class.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward displayPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Returns the user to the index page.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_CLOSE);
    }


    /**
     * Sends the user to the existing CashManagementDocument.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward openExisting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementStatusForm cform = (CashManagementStatusForm) form;

        String cmDocTypeName = SpringServiceLocator.getDocumentTypeService().getDocumentTypeNameByClass(CashManagementDocument.class);

        Properties params = new Properties();
        params.setProperty("methodToCall", "docHandler");
        params.setProperty("command", "displayDocSearchView");
        params.setProperty("docId", cform.getControllingDocumentId());


        String cmActionUrl = UrlFactory.buildDocumentActionUrl(cmDocTypeName, params);

        return new ActionForward(cmActionUrl, true);
    }
}