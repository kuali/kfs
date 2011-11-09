/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.document.validation.event.AddCheckEvent;
import org.kuali.kfs.fp.document.validation.event.CashieringTransactionApplicationEventBase;
import org.kuali.kfs.fp.document.validation.event.DeleteCheckEvent;
import org.kuali.kfs.fp.document.web.struts.CashManagementForm.CashDrawerSummary;
import org.kuali.kfs.fp.exception.CashDrawerStateException;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.sys.KFSConstants.DepositConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSKeyConstants.CashManagement;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for CashManagementForm
 */
public class CashManagementAction extends KualiTransactionalDocumentActionBase {
    protected static Logger LOG = Logger.getLogger(CashManagementAction.class);
    protected static final String CASH_MANAGEMENT_STATUS_PAGE = "/cashManagementStatus.do";

    /**
     * Default constructor
     */
    public CashManagementAction() {
    }


    /**
     * Overrides to call super, but also make sure the helpers are populated.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward dest = null;
        
        try {
            dest = super.execute(mapping, form, request, response);

            CashManagementForm cmf = (CashManagementForm) form;
            cmf.populateDepositHelpers();
            WorkflowDocument kwd = cmf.getDocument().getDocumentHeader().getWorkflowDocument();
            if (kwd.isEnroute() || kwd.isFinal()) {
                cmf.setCashDrawerSummary(null);
            }
            else {
                if (cmf.getCashDrawerSummary() == null) {
                    cmf.populateCashDrawerSummary();
                }
            }
            // put any recently closed items in process in the form
            cmf.setRecentlyClosedItemsInProcess(SpringContext.getBean(CashManagementService.class).getRecentlyClosedItemsInProcess(cmf.getCashManagementDocument()));
        } catch (CashDrawerStateException cdse) {
            dest = new ActionForward(UrlFactory.parameterizeUrl(CASH_MANAGEMENT_STATUS_PAGE, cdse.toProperties()), true);
        }
        
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
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        Person user = GlobalVariables.getUserSession().getPerson();
        String campusCode = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(user);

        String defaultDescription = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CashManagement.DEFAULT_DOCUMENT_DESCRIPTION);
        defaultDescription = StringUtils.replace(defaultDescription, "{0}", campusCode);
        defaultDescription = StringUtils.substring(defaultDescription, 0, 39);

        // create doc
        CashManagementDocument cmDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(campusCode, defaultDescription, null);

        // update form
        kualiDocumentFormBase.setDocument(cmDoc);
        kualiDocumentFormBase.setDocTypeName(cmDoc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addInterimDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        checkDepositAuthorization(cmForm, cmDoc);

        String wizardUrl = buildDepositWizardUrl(cmDoc, DepositConstants.DEPOSIT_TYPE_INTERIM);
        return new ActionForward(wizardUrl, true);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addFinalDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        checkDepositAuthorization(cmForm, cmDoc);

        String wizardUrl = buildDepositWizardUrl(cmDoc, DepositConstants.DEPOSIT_TYPE_FINAL);
        return new ActionForward(wizardUrl, true);
    }

    /**
     * Throws a DocumentAuthorizationException if the current user is not authorized to add a deposit of the given type to the given
     * document.
     * 
     * @param cmDoc
     * @param cmForm
     */
    protected void checkDepositAuthorization(CashManagementForm cmForm, CashManagementDocument cmDoc) {
        //deposits can only be added if the CashDrawer is open
        if (!cmDoc.getCashDrawerStatus().equals(CashDrawerConstants.STATUS_OPEN)) {
            throw new IllegalStateException("CashDrawer '" + cmDoc.getCampusCode() + "' must be open for deposits to be made");
        }
        
        //verify user's ability to add a deposit
        Map<String, String> documentActions = cmForm.getEditingMode();
        if (!documentActions.containsKey(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_ADDITIONAL_DEPOSITS)) {
            throw buildAuthorizationException("add a deposit", cmDoc);
        }
    }

    /**
     * @param cmDoc
     * @param depositTypeCode
     * @return URL for passing control to the DepositWizard
     */
    protected String buildDepositWizardUrl(CashManagementDocument cmDoc, String depositTypeCode) {
        Properties params = new Properties();
        params.setProperty("methodToCall", "startWizard");
        params.setProperty("cmDocId", cmDoc.getDocumentNumber());
        params.setProperty("depositTypeCode", depositTypeCode);

        String wizardActionUrl = UrlFactory.parameterizeUrl("depositWizard.do", params);
        return wizardActionUrl;
    }


    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancelDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        // validate cancelability
        int depositIndex = getSelectedLine(request);
        Deposit deposit = cmDoc.getDeposit(depositIndex);
        if (StringUtils.equals(deposit.getDepositTypeCode(), DepositConstants.DEPOSIT_TYPE_INTERIM) && cmDoc.hasFinalDeposit()) {
            throw new IllegalStateException("interim deposits cannot be canceled if the document already has a final deposit");
        }

        // cancel the deposit
        deposit = cmDoc.removeDeposit(depositIndex);
        SpringContext.getBean(CashManagementService.class).cancelDeposit(deposit);

        // update the form
        cmForm.removeDepositHelper(depositIndex);

        // open the CashDrawer so that user can add new deposits
        cmDoc.getCashDrawer().setStatusCode(KFSConstants.CashDrawerConstants.STATUS_OPEN);

        // display status message
        KNSGlobalVariables.getMessageList().add(CashManagement.STATUS_DEPOSIT_CANCELED);
        
        ((CashManagementForm) form).getCashDrawerSummary().resummarize(cmDoc);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#reload(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward dest = super.reload(mapping, form, request, response);

        // refresh the CashDrawerSummary, just in case
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        CashDrawerSummary cms = cmForm.getCashDrawerSummary();
        if (cms != null) {
            cms.resummarize(cmDoc);
        }

        return dest;
    }


    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward refreshSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();
        
        if (cmForm.getCashDrawerSummary() != null) {
            cmForm.getCashDrawerSummary().resummarize(cmDoc);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Saves the document, then opens the cash drawer
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward openCashDrawer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        if (!cmDoc.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            throw new IllegalStateException("openCashDrawer should only be called on documents which haven't yet been saved");
        }

        // open the CashDrawer
        CashDrawerService cds = SpringContext.getBean(CashDrawerService.class);
        cds.openCashDrawer(cmDoc.getCashDrawer(), cmDoc.getDocumentNumber());
        // now that the cash drawer is open, let's create currency/coin detail records for this document
        // create and save the cumulative cash receipt, deposit, money in and money out curr/coin details
        SpringContext.getBean(CashManagementService.class).createNewCashDetails(cmDoc, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        SpringContext.getBean(CashManagementService.class).createNewCashDetails(cmDoc, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        SpringContext.getBean(CashManagementService.class).createNewCashDetails(cmDoc, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        try {
            SpringContext.getBean(DocumentService.class).saveDocument(cmDoc);
        }
        catch (WorkflowException e) {
            // force it closed if workflow proves recalcitrant
            cds.closeCashDrawer(cmDoc.getCashDrawer());
            throw e;
        }

        // update the CashDrawerSummary to reflect the change
        cmForm.populateCashDrawerSummary();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action makes the last interim deposit a final deposit
     * 
     * @param mapping the mapping of the actions
     * @param form the Struts form populated on the post
     * @param request the servlet request
     * @param response the servlet response
     * @return a forward to the same page we were on
     * @throws Exception because you never know when something just might go wrong
     */
    public ActionForward finalizeLastInterimDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementDocument cmDoc = ((CashManagementForm) form).getCashManagementDocument();
        CashManagementService cms = SpringContext.getBean(CashManagementService.class);

        if (cmDoc.hasFinalDeposit()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS, CashManagement.ERROR_DOCUMENT_ALREADY_HAS_FINAL_DEPOSIT, new String[] {});
        }
        else if (cmDoc.getDeposits().size() == 0) {
            GlobalVariables.getMessageMap().putError(KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS, CashManagement.ERROR_DOCUMENT_NO_DEPOSITS_TO_MAKE_FINAL, new String[] {});
        }
        else if (!cms.allVerifiedCashReceiptsAreDeposited(cmDoc)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS, CashManagement.ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPTS, new String[] {});
        }

        cms.finalizeLastInterimDeposit(cmDoc);

        ((CashManagementForm) form).getCashDrawerSummary().resummarize(cmDoc);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action applies the current cashiering transaction to the cash drawer
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward applyCashieringTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementDocument cmDoc = ((CashManagementForm) form).getCashManagementDocument();
        CashManagementService cmService = SpringContext.getBean(CashManagementService.class);
        
        final boolean valid = SpringContext.getBean(KualiRuleService.class).applyRules(new CashieringTransactionApplicationEventBase("Cashiering Transaction Application Event", "", cmDoc, SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmDoc.getCampusCode()), cmDoc.getCurrentTransaction()));

        if (valid) {
            cmService.applyCashieringTransaction(cmDoc);

            ((CashManagementForm) form).getCashDrawerSummary().resummarize(cmDoc);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action allows the user to go to the cash drawer correction screen
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward correctCashDrawer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ActionForward("CashDrawerCorrectionForm", buildCashDrawerCorrectionUrl(((CashManagementForm) form).getCashManagementDocument()), true);
    }

    /**
     * @param cmDoc
     * @param depositTypeCode
     * @return URL for passing control to the DepositWizard
     */
    protected String buildCashDrawerCorrectionUrl(CashManagementDocument cmDoc) {
        Properties params = new Properties();
        params.setProperty("methodToCall", "startCorrections");
        params.setProperty("campusCode", cmDoc.getCampusCode());

        return UrlFactory.parameterizeUrl("cashDrawerCorrection.do", params);
    }

    /**
     * Adds Check instance created from the current "new check" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementDocument cmDoc = ((CashManagementForm) form).getCashManagementDocument();

        Check newCheck = cmDoc.getCurrentTransaction().getNewCheck();
        newCheck.setDocumentNumber(cmDoc.getDocumentNumber());

        // check business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCheckEvent(KFSConstants.NEW_CHECK_PROPERTY_NAME, cmDoc, newCheck));
        if (rulePassed) {
            // add check
            cmDoc.getCurrentTransaction().addCheck(newCheck);

            // clear the used newCheck
            cmDoc.getCurrentTransaction().setNewCheck(cmDoc.getCurrentTransaction().createNewCheck());

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected check (line) from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementDocument cmDoc = ((CashManagementForm) form).getCashManagementDocument();

        int deleteIndex = getLineToDelete(request);
        Check oldCheck = cmDoc.getCurrentTransaction().getCheck(deleteIndex);


        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteCheckEvent(KFSConstants.EXISTING_CHECK_PROPERTY_NAME, cmDoc, oldCheck));

        if (rulePassed) {
            // delete check
            cmDoc.getCurrentTransaction().removeCheck(deleteIndex);

            // delete baseline check, if any
            if (cmDoc.getCurrentTransaction().hasBaselineCheck(deleteIndex)) {
                cmDoc.getCurrentTransaction().getBaselineChecks().remove(deleteIndex);
            }

        }
        else {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.check[" + deleteIndex + "]", KFSKeyConstants.Check.ERROR_CHECK_DELETERULE, Integer.toString(deleteIndex));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Overridden to clear the CashDrawerSummary info
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashManagementForm cmForm = (CashManagementForm) form;
        CashManagementDocument cmDoc = cmForm.getCashManagementDocument();

        ActionForward dest = super.route(mapping, form, request, response);

        // clear the CashDrawerSummary
        cmForm.setCashDrawerSummary(null);

        return dest;
    }
}

