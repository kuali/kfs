/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document.web.struts;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.UrlFactory;


/**
 * Action class for CashManagementStatusForm
 */
public class CashManagementStatusAction extends KualiAction {
    private static Logger LOG = Logger.getLogger(CashManagementStatusAction.class);

    /**
     * Default constructor
     */
    public CashManagementStatusAction() {
    }


    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // populate with exception values, if any
        CashManagementStatusForm cform = (CashManagementStatusForm) form;

        if (cform.getMethodToCall().equals("docHandler")) {
            cform.setMethodToCall("displayPage");
        }

        // generate the status message
        String[] msgParams = { cform.getVerificationUnit(), cform.getControllingDocumentId(), cform.getCurrentDrawerStatus(), cform.getDesiredDrawerStatus() };

        ActionMessage message = new ActionMessage(KFSKeyConstants.CashDrawer.MSG_CASH_DRAWER_ALREADY_OPEN, msgParams);

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
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
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

        String cmDocTypeName = SpringContext.getBean(DataDictionaryService.class).getValidDocumentTypeNameByClass(CashManagementDocument.class);

        Properties params = new Properties();
        params.setProperty("methodToCall", "docHandler");
        params.setProperty("command", "displayDocSearchView");
        params.setProperty("docId", cform.getControllingDocumentId());


        String cmActionUrl = UrlFactory.parameterizeUrl(KFSConstants.CASH_MANAGEMENT_DOCUMENT_ACTION, params);

        return new ActionForward(cmActionUrl, true);
    }
}
