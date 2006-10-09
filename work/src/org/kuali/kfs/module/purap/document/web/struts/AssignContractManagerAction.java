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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.authorization.DocumentAuthorizer;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.purap.web.struts.form.AssignContractManagerForm;


/**
 * This class handles Actions for Research Administration.
 * 
 * @author KRA (era_team@indiana.edu)
 */

public class AssignContractManagerAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssignContractManagerForm assignContractManagerForm = (AssignContractManagerForm) form;

        // check authorization manually, since the auth-check isn't inherited by this class
        // TODO complete logic (example from DepositWizardAction)
//        String cmDocTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(CashManagementDocument.class);
//        DocumentAuthorizer cmDocAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(cmDocTypeName);
//        KualiUser luser = GlobalVariables.getUserSession().getKualiUser();
//        if (!cmDocAuthorizer.canInitiate(cmDocTypeName, luser)) {
//            throw new DocumentTypeAuthorizationException(luser.getUniversalUser().getPersonUserIdentifier(), "add deposits to", cmDocTypeName);
//        }
        
        // populate the outgoing form used by the JSP if it seems empty (retrieve list of requisitions waiting for contract manager assignment)
        // TODO complete logic (example from DepositWizardAction)
//        String cmDocId = dwForm.getCashManagementDocId();
//        if (StringUtils.isBlank(cmDocId)) {
//            cmDocId = request.getParameter("cmDocId");
//            String depositTypeCode = request.getParameter("depositTypeCode");
//
//            CashManagementDocument cmDoc = (CashManagementDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(cmDocId);
//
//            initializeForm(dwForm, cmDoc, depositTypeCode);
//        }

        return super.execute(mapping, form, request, response);
    }

    /**
     * This method is assigns the contract managers entered and creates the PO documents.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // TODO complete logic to save requisitions with contract manager and create POs
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
}
