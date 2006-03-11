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

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.authorization.DocumentAuthorizer;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.web.struts.form.DepositWizardForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles Actions for the deposit document.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DepositWizardAction extends KualiDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepositWizardAction.class);
    
    /**
     * Overrides the parent to ensure the initiation check is done.  This has to be done in the non-typical place b/c 
     * the wizard is associated with a Cash Management Document; however, it's not a document it self so it doesn't 
     * have the luxury of the embedded initiation check that is doen for TP eDocs.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(CashManagementDocument.class);
        DocumentAuthorizer documentAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(documentTypeName);
        KualiUser user = GlobalVariables.getUserSession().getKualiUser();
        if (!documentAuthorizer.canInitiate(documentTypeName, user)) {
            throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "initiate", documentTypeName);
        }
        
        return super.execute(mapping, form, request, response);
    }

    /**
     * This method is the starting point for the deposit document wizard.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward startWizard(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * This method is the action method for creating the new deposit document from 
     * the information chosen by the user in the UI.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward createDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        DepositWizardForm depositDocumentWizardForm = (DepositWizardForm) form;

        // make sure something was selected
        if(depositDocumentWizardForm.getSelectedCashReceipts().isEmpty()) {
            GlobalVariables.getErrorMap().put(KeyConstants.GLOBAL_ERRORS, 
                    KeyConstants.CashManagement.ERROR_DOCUMENT_CASH_MGMT_NO_CASH_RECEIPTS_SELECTED);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
        
        // retrieve information about all that were selected
        Collection c = null;
        try {
            c = SpringServiceLocator.getDocumentService().getDocumentsByListOfDocumentHeaderIds(CashReceiptDocument.class, 
                    depositDocumentWizardForm.getSelectedCashReceipts());
        } catch(WorkflowException we) {
            throw new RuntimeException(we);
        }
        ArrayList selectedCashReceipts = new ArrayList(c);
        
        CashManagementDocument cmd = null;
        try {
            cmd = SpringServiceLocator.getCashManagementService().createCashManagementDocument("Fill me in...", 
                   selectedCashReceipts, Constants.CashReceiptConstants.CASH_RECEIPT_VERIFICATION_UNIT);  // for now it's just on verification unit
        } catch(WorkflowException we) {
            throw new RuntimeException(we);
        }
        
        String cmDocUrl = Constants.CASH_MANAGEMENT_DOCUMENT_ACTION + 
            "?methodToCall=docHandler&docId=" + 
            cmd.getFinancialDocumentNumber() + 
            "&command=displayDocSearchView";
        
        return new ActionForward(cmDocUrl, true);
    }
    
    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a private instance variable
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_CANCEL_QUESTION, kualiConfiguration
                    .getPropertyString("document.question.cancel.text"), Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CANCEL, "");
        }
        else {
            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((Constants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the IB doc
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
        }

        return new ActionForward(Constants.EMPTY_STRING, true);  // back to the index page
    }
}