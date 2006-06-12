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

import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.Constants.DepositConstants;
import org.kuali.KeyConstants.CashManagement;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.authorization.DocumentAuthorizer;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashManagementDocumentAuthorizer;
import org.kuali.module.financial.web.struts.form.CashManagementForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Action class for CashManagementForm
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementAction extends KualiDocumentActionBase {
    private static Logger LOG = Logger.getLogger(CashManagementAction.class);


    /**
     * Default constructor
     */
    public CashManagementAction() {
    }


    /**
     * Overrides to call super, but also make sure the helpers are populated.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward dest = super.execute(mapping, form, request, response);

        CashManagementForm cmf = (CashManagementForm) form;
        cmf.populateDeposits();
        cmf.populateDepositHelpers();

        return dest;
    }


    /**
     * Overrides the default document-creation code to auto-save new documents upon creation: since creating a CMDoc changes the
     * CashDrawer's state as a side-effect, we need all CMDocs to be docsearchable so that someone can relocate and use or cancel
     * whatever the current CMDoc is.
     * 
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        KualiUser user = GlobalVariables.getUserSession().getKualiUser();
        String workgroupName = SpringServiceLocator.getCashReceiptService().getCashReceiptVerificationUnitForUser(user);

        String defaultDescription = SpringServiceLocator.getKualiConfigurationService().getPropertyString(
                CashManagement.DEFAULT_DOCUMENT_DESCRIPTION);
        defaultDescription = StringUtils.replace(defaultDescription, "{0}", workgroupName);
        defaultDescription = StringUtils.substring(defaultDescription, 0, 39);

        // create doc
        CashManagementDocument cmDoc = SpringServiceLocator.getCashManagementService().createCashManagementDocument(workgroupName,
                defaultDescription, null);

        // update form
        kualiDocumentFormBase.setDocument(cmDoc);
        kualiDocumentFormBase.setDocTypeName(cmDoc.getDocumentHeader().getWorkflowDocument().getDocumentType());
    }

    private CashManagementDocumentAuthorizer getDocumentAuthorizer() {
        String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(
                CashManagementDocument.class);
        DocumentAuthorizer documentAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(
                documentTypeName);

        return (CashManagementDocumentAuthorizer) documentAuthorizer;
    }


    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addInterimDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        checkDepositAuthorization(cmDoc, DepositConstants.DEPOSIT_TYPE_INTERIM);

        String wizardUrl = buildDepositWizardUrl(cmDoc, DepositConstants.DEPOSIT_TYPE_INTERIM);
        return new ActionForward(wizardUrl, true);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addFinalDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        checkDepositAuthorization(cmDoc, DepositConstants.DEPOSIT_TYPE_FINAL);

        String wizardUrl = buildDepositWizardUrl(cmDoc, DepositConstants.DEPOSIT_TYPE_FINAL);
        return new ActionForward(wizardUrl, true);
    }

    /**
     * Throws a DocumentAuthorizationException if the current user is not authorized to add a deposit of the given type to the given
     * document.
     * 
     * @param cmDoc
     * @param depositTypeCode
     */
    private void checkDepositAuthorization(CashManagementDocument cmDoc, String depositTypeCode) {
        // verify user's ability to add an interim deposit
        KualiUser user = GlobalVariables.getUserSession().getKualiUser();
        Map editModes = getDocumentAuthorizer().getEditMode(cmDoc, user);
        if (!editModes.containsKey(AuthorizationConstants.CashManagementEditMode.ALLOW_ADDITIONAL_DEPOSITS)) {
            throw buildAuthorizationException("add a deposit", cmDoc);
        }
    }

    /**
     * @param cmDoc
     * @param depositTypeCode
     * @return URL for passing control to the DepositWizard
     */
    private String buildDepositWizardUrl(CashManagementDocument cmDoc, String depositTypeCode) {
        Properties params = new Properties();
        params.setProperty("methodToCall", "startWizard");
        params.setProperty("cmDocId", cmDoc.getFinancialDocumentNumber());
        params.setProperty("depositTypeCode", depositTypeCode);

        String wizardActionUrl = UrlFactory.parameterizeUrl("depositWizard.do", params);
        return wizardActionUrl;
    }


    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancelDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        // TODO: check for authorization to cancel deposit

        // cancel the deposit
        int depositIndex = getSelectedLine(request);
        Deposit deposit = cmDoc.removeDeposit(depositIndex);
        SpringServiceLocator.getCashManagementService().cancelDeposit(deposit);

        // update the form
        cmForm.removeDepositHelper(depositIndex);

        // display status message
        GlobalVariables.getMessageList().add(CashManagement.STATUS_DEPOSIT_CANCELED);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}