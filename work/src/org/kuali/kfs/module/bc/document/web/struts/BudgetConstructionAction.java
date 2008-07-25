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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.document.validation.event.AddPendingBudgetGeneralLedgerLineEvent;
import org.kuali.kfs.module.bc.document.validation.event.DeletePendingBudgetGeneralLedgerLineEvent;
import org.kuali.kfs.module.bc.exception.BudgetConstructionDocumentAuthorizationException;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.kfs.sys.KfsAuthorizationConstants.BudgetConstructionEditMode;
import org.kuali.kfs.sys.context.SpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * need to figure out if this should extend KualiAction, KualiDocumentActionBase or KualiTransactionDocumentActionBase
 */
public class BudgetConstructionAction extends KualiTransactionalDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAction.class);

    /**
     * Entry point to all actions
     * Checks for cases where methodToCall is loadDocument, performAccountPullup or performAccountPushdown
     * and creates global messages to describe the new editingMode state.  Also handles document locking
     * if the editingMode is BudgetConstructionEditMode.FULL_ENTRY. (Re)Populates the pullup and pushdown
     * selection controls based on the current level of the document and the user's approval access for the levels
     * above and below the current level.
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.execute(mapping, form, request, response);

        // apprise user of granted access
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        if (budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_METHOD) || budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_PULLUP_METHOD) || budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_PUSHDOWN_METHOD)) {

            if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
                GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SYSTEM_VIEW_ONLY);
            }

            if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.VIEW_ONLY)) {
                GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_VIEW_ONLY);
            }

            if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.FULL_ENTRY)) {
                if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)){
                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_VIEW_ONLY);
                }
                else {
                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_EDIT_ACCESS);
                }

                // TODO: maybe move this to BC document service
                if (!budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
                    LockService lockService = SpringContext.getBean(LockService.class);
                    HashMap primaryKey = new HashMap();
                    primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionForm.getDocument().getDocumentNumber());

                    BudgetConstructionHeader budgetConstructionHeader = (BudgetConstructionHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
                    if (budgetConstructionHeader != null) {
                        BudgetConstructionLockStatus bcLockStatus = lockService.lockAccount(budgetConstructionHeader, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());

                        // TODO: make this a switch
                        if (bcLockStatus.getLockStatus() == LockStatus.SUCCESS) {

                            // update the document version number
                            // so the saved lock of header doesn't produce an optimistic lock error
                            // and has the info we just put in the header so doc save doesn't wipe out the lock
                            budgetConstructionForm.getBudgetConstructionDocument().setVersionNumber(bcLockStatus.getBudgetConstructionHeader().getVersionNumber());
                            budgetConstructionForm.getBudgetConstructionDocument().setBudgetLockUserIdentifier(bcLockStatus.getBudgetConstructionHeader().getBudgetLockUserIdentifier());
                        }
                        else {
                            if (bcLockStatus.getLockStatus() == LockStatus.BY_OTHER) {
                                String lockerName = SpringContext.getBean(UniversalUserService.class).getUniversalUser(bcLockStatus.getAccountLockOwner()).getPersonName();
                                throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonName(), "open", budgetConstructionForm.getDocument().getDocumentHeader().getDocumentNumber(), "(document is locked by " + lockerName + ")", budgetConstructionForm.isPickListMode());
                            }
                            else {
                                if (bcLockStatus.getLockStatus() == LockStatus.FLOCK_FOUND) {
                                    throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonName(), "open", budgetConstructionForm.getDocument().getDocumentHeader().getDocumentNumber(), "(funding for document is locked)", budgetConstructionForm.isPickListMode());
                                }
                                else {
                                    throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonName(), "open", budgetConstructionForm.getDocument().getDocumentHeader().getDocumentNumber(), "(optimistic lock or other failure during lock attempt)", budgetConstructionForm.isPickListMode());
                                }
                            }
                        }
                    }
                    else {
                        // unlikely
                        throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonName(), "open", budgetConstructionForm.getDocument().getDocumentHeader().getDocumentNumber(), "(can't find document for locking)", budgetConstructionForm.isPickListMode());
                    }
                }

                // getting here implies a lock or system view mode, try to build a pullup list
                // pushdown is only allowed if user has edit access (regardless of system view only mode)
                if (budgetConstructionForm.getBudgetConstructionDocument().getOrganizationLevelCode() != 0) {
                    if (!budgetConstructionForm.getAccountOrgHierLevels().isEmpty()) {
                        budgetConstructionForm.populatePushPullLevelKeyLabels(budgetConstructionForm.getBudgetConstructionDocument(), budgetConstructionForm.getAccountOrgHierLevels(), false);
                    }
                }
            }

            // pullup is allowed regardless of editMode
            if (!budgetConstructionForm.getAccountOrgHierLevels().isEmpty()) {
                budgetConstructionForm.populatePushPullLevelKeyLabels(budgetConstructionForm.getBudgetConstructionDocument(), budgetConstructionForm.getAccountOrgHierLevels(), true);
            }
        }

        return forward;
    }

    /**
     * gwp - no call to super, need to work through command we will use randall - This method might be unnecessary, but putting it
     * here allows URL to be consistent with Document URLs gwp - i think we still want this method, just need to figure out if we
     * use command initiate or displayDocSearchView or something else. i expect we will get the account/subaccount or docnumber from
     * the previous form and assume the document will already exist regardless of creation by genesis or
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        String command = budgetConstructionForm.getCommand();

        loadDocument(budgetConstructionForm);
        this.initAuthorizationEditMode(budgetConstructionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

        /**
         * from KualiDocumentActionBase,docHandler() KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
         * String command = kualiDocumentFormBase.getCommand(); // in all of the following cases we want to load the document // if
         * (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && kualiDocumentFormBase.getDocId() != null) {
         * loadDocument(kualiDocumentFormBase); } else if (IDocHandler.INITIATE_COMMAND.equals(command)) {
         * createDocument(kualiDocumentFormBase); } else { LOG.error("docHandler called with invalid parameters"); throw new
         * IllegalStateException("docHandler called with invalid parameters"); } // attach any extra JS from the data dictionary if
         * (LOG.isDebugEnabled()) { LOG.debug("kualiDocumentFormBase.getAdditionalScriptFile(): " +
         * kualiDocumentFormBase.getAdditionalScriptFile()); } if (kualiDocumentFormBase.getAdditionalScriptFile() == null ||
         * kualiDocumentFormBase.getAdditionalScriptFile().equals("")) { DocumentEntry docEntry =
         * SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(kualiDocumentFormBase.getDocument().getClass());
         * kualiDocumentFormBase.setAdditionalScriptFile(docEntry.getWebScriptFile()); if (LOG.isDebugEnabled()) { LOG.debug("set
         * kualiDocumentFormBase.getAdditionalScriptFile() to: " + kualiDocumentFormBase.getAdditionalScriptFile()); } } if
         * (IDocHandler.SUPERUSER_COMMAND.equalsIgnoreCase(command)) { kualiDocumentFormBase.setSuppressAllButtons(true); } return
         * mapping.findForward(KFSConstants.MAPPING_BASIC);
         */
    }

    /**
     * Coded this to look like KualiDocumentActionBase.loadDocument()
     * 
     * @param budgetConstructionForm
     * @throws WorkflowException
     */
    private void loadDocument(BudgetConstructionForm budgetConstructionForm) throws WorkflowException {

        BudgetConstructionHeader budgetConstructionHeader;

        if (budgetConstructionForm.getDocId() != null) {
            HashMap primaryKey = new HashMap();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionForm.getDocId());

            budgetConstructionHeader = (BudgetConstructionHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);

        }
        else {
            // use the passed url autoloaded parms to get the record from DB
            // BudgetConstructionDaoOjb bcHeaderDao;
            String chartOfAccountsCode = budgetConstructionForm.getChartOfAccountsCode();
            String accountNumber = budgetConstructionForm.getAccountNumber();
            String subAccountNumber = budgetConstructionForm.getSubAccountNumber();
            Integer universityFiscalYear = budgetConstructionForm.getUniversityFiscalYear();

            budgetConstructionHeader = (BudgetConstructionHeader) SpringContext.getBean(BudgetDocumentService.class).getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        }

        BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(budgetConstructionHeader.getDocumentNumber());
        budgetConstructionForm.setDocument(budgetConstructionDocument);

        // init the benefits calc flags
        budgetConstructionDocument.setBenefitsCalcNeeded(false);
        budgetConstructionDocument.setMonthlyBenefitsCalcNeeded(false);

        // init the new line objects
        budgetConstructionForm.initNewLine(budgetConstructionForm.getNewRevenueLine(), true);
        budgetConstructionForm.initNewLine(budgetConstructionForm.getNewExpenditureLine(), false);

        // need this here to do totaling on initial load
        budgetConstructionForm.populatePBGLLines();
        budgetConstructionForm.initializePersistedRequestAmounts();

        KualiWorkflowDocument workflowDoc = budgetConstructionDocument.getDocumentHeader().getWorkflowDocument();
        budgetConstructionForm.setDocTypeName(workflowDoc.getDocumentType());

        // KualiDocumentFormBase.populate() needs this updated in the session
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
        
    }

    /**
     * Calculates the edit mode based on the BC security model and store it in session for later retrieval by
     * budget by account expansion screens using BudgetConstructionDocumentAuthorizer.getEditModeFromSession()
     * 
     * @param bcDoc
     */
    private void initAuthorizationEditMode(BudgetConstructionForm bcForm){
        BudgetConstructionDocument bcDoc = bcForm.getBudgetConstructionDocument(); 
        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);

        String chartOfAccountsCode = bcDoc.getChartOfAccountsCode();
        String accountNumber = bcDoc.getAccountNumber();
        String subAccountNumber = bcDoc.getSubAccountNumber();
        Integer universityFiscalYear = bcDoc.getUniversityFiscalYear();
        
        Map editModeMap = new HashMap();
        String editMode = budgetDocumentService.getAccessMode(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, GlobalVariables.getUserSession().getUniversalUser());
        editModeMap.put(editMode, "TRUE");
        
        // adding the case where system is in view only mode in case we need this fact for functionality
        // getAccessMode() will not return FULL_ENTRY if the system is in view only mode,
        // so we may or may not need this extra map row
        if (!fiscalYearFunctionControlService.isBudgetUpdateAllowed(universityFiscalYear)){
            editModeMap.put(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY, "TRUE");
        }
        
        GlobalVariables.getUserSession().removeObject(BCConstants.BC_DOC_EDIT_MODE_SESSIONKEY);
        GlobalVariables.getUserSession().addObject(BCConstants.BC_DOC_EDIT_MODE_SESSIONKEY, editModeMap);
        
        // need to set this immediately so pull/push action methods can use it
        bcForm.setEditingMode(editModeMap);
       
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // return super.close(mapping, form, request, response);
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;

        // only want to prompt them to save if they already can save
        if (docForm.getDocumentActionFlags().getCanSave()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    // if yes button clicked - save the doc

                    // SpringContext.getBean(DocumentService.class).saveDocument(docForm.getDocument());
                    // TODO for now just do trivial save eventually need to add validation, routelog stuff, etc

                    // SpringContext.getBean(DocumentService.class).updateDocument(docForm.getDocument());
                    BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
                    budgetDocumentService.saveDocument((BudgetConstructionDocument) docForm.getDocument());
                    budgetDocumentService.calculateBenefitsIfNeeded((BudgetConstructionDocument) docForm.getDocument());
                    docForm.initializePersistedRequestAmounts();

                    // TODO confirm save and close functionality with SME group
                    // if (docForm.isPickListMode()){
                    // GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
                    // }
                    GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                // else go to close logic below
            }
        }

        // TODO: maybe move this to BC document service
        // do the unlock if they have full access and not system view mode
        if (docForm.getEditingMode().containsKey(BudgetConstructionEditMode.FULL_ENTRY)) {

            if (!docForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
                LockService lockService = SpringContext.getBean(LockService.class);
                HashMap primaryKey = new HashMap();
                primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, docForm.getDocument().getDocumentNumber());

                BudgetConstructionHeader budgetConstructionHeader = (BudgetConstructionHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
                if (budgetConstructionHeader != null) {
                    LockStatus lockStatus = lockService.unlockAccount(budgetConstructionHeader);

                    // TODO: figure out if we need error message and redisplay for not success?
                    if (lockStatus != LockStatus.SUCCESS) {

                    }
                }
                else {
                    // unlikely, but benign problem here
                }
            }
        }


        if (docForm.isPickListMode()) {
            // TODO for now just redisplay with a message
            GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
            docForm.setPickListClose(true);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);

        }
        else {

            if (docForm.getReturnFormKey() == null) {

                // assume called from doc search or lost the session - go back to main
                return returnToSender(mapping, docForm);
            }
            else {
                // setup the return parms for the document and anchor and go back to doc select
                Properties parameters = new Properties();
                parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_SELECTION_REFRESH_METHOD);
                parameters.put(KFSConstants.DOC_FORM_KEY, docForm.getReturnFormKey());
                parameters.put(KFSConstants.ANCHOR, docForm.getReturnAnchor());
                parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.BC_DOCUMENT_REFRESH_CALLER);

                String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_SELECTION_ACTION, parameters);
                return new ActionForward(lookupUrl, true);
            }
        }

        // TODO this needs to return to bc doc selection
        // return returnToSender(mapping, docForm);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        // return super.save(mapping, form, request, response);
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();
        // DocumentService documentService = SpringContext.getBean(DocumentService.class);
        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // TODO for now just do trivial save eventually need to add validation, routelog stuff, etc
        // and calc benefits flag checking and calc benefits if needed
        // documentService.updateDocument(bcDocument);

        // TODO use this instead? research the differences - may need to inject DocumentService and roll our own bcdocservice
        // documentService.saveDocument(document);
        // rolling own - next line
        budgetDocumentService.saveDocument(bcDocument);
        budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);

        // TODO may need to move this to generic save method to handle all actions requiring save
        budgetConstructionForm.initializePersistedRequestAmounts();

        // TODO not sure this is needed in BC
        budgetConstructionForm.setAnnotation("");

        // redisplay the document along with document saved message
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performBalanceInquiryForRevenueLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performBalanceInquiry(true, mapping, form, request, response);
    }

    public ActionForward performBalanceInquiryForExpenditureLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performBalanceInquiry(false, mapping, form, request, response);
    }

    /**
     * This method is similar to org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase.performBalanceInquiry()
     * 
     * @param isRevenue
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiry(boolean isRevenue, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;

        // get the selected line, setup parms and redirect to balance inquiry
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        PendingBudgetConstructionGeneralLedger pbglLine;
        if (isRevenue) {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines().get(this.getSelectedLine(request));
        }
        else {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));
        }

        // build out base path for return location, use config service instead
        // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
        // request.getContextPath();
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObject(form, BCConstants.FORMKEY_PREFIX);

        // now add required parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        // need this next param b/c the lookup's return back will overwrite
        // the original doc form key
        parameters.put(KFSConstants.BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        // TODO need to set an anchor for which to return?? pass this value in?

        if (StringUtils.isNotBlank(pbglLine.getChartOfAccountsCode())) {
            parameters.put("chartOfAccountsCode", pbglLine.getChartOfAccountsCode());
        }
        if (StringUtils.isNotBlank(pbglLine.getAccountNumber())) {
            parameters.put("accountNumber", pbglLine.getAccountNumber());
        }
        if (StringUtils.isNotBlank(pbglLine.getFinancialObjectCode())) {
            parameters.put("financialObjectCode", pbglLine.getFinancialObjectCode());
        }
        if (StringUtils.isNotBlank(pbglLine.getSubAccountNumber())) {
            parameters.put("subAccountNumber", pbglLine.getSubAccountNumber());
        }
        if (StringUtils.isNotBlank(pbglLine.getFinancialSubObjectCode())) {
            parameters.put("financialSubObjectCode", pbglLine.getFinancialSubObjectCode());
        }

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.BALANCE_INQUIRY_REPORT_MENU_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performMonthlyRevenueBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performMonthlyBudget(true, mapping, form, request, response);
    }

    public ActionForward performMonthlyExpenditureBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performMonthlyBudget(false, mapping, form, request, response);
    }

    public ActionForward performMonthlyBudget(boolean isRevenue, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;

        // TODO adjust this to check for 2PLG and turn off SS/monthly RI check if found
        // the final save after removing 2PLG will catch any monthly discrepencies

        // validate, save, etc first then goto the monthly screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        if (budgetConstructionForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
            budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
            budgetConstructionForm.initializePersistedRequestAmounts();

            // repop and refresh refs - esp monthly so jsp can properly display state
            budgetConstructionForm.populatePBGLLines();

        }

        PendingBudgetConstructionGeneralLedger pbglLine;
        if (isRevenue) {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines().get(this.getSelectedLine(request));
        }
        else {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));
        }


        // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
        // request.getContextPath();
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.MONTHLY_BUDGET_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put("documentNumber", pbglLine.getDocumentNumber());
        parameters.put("universityFiscalYear", pbglLine.getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", pbglLine.getChartOfAccountsCode());
        parameters.put("accountNumber", pbglLine.getAccountNumber());
        parameters.put("subAccountNumber", pbglLine.getSubAccountNumber());
        parameters.put("financialObjectCode", pbglLine.getFinancialObjectCode());
        parameters.put("financialSubObjectCode", pbglLine.getFinancialSubObjectCode());
        parameters.put("financialBalanceTypeCode", pbglLine.getFinancialBalanceTypeCode());
        parameters.put("financialObjectTypeCode", pbglLine.getFinancialObjectTypeCode());

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.MONTHLY_BUDGET_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;

        // TODO adjust this to check for 2PLG and turn off SS/monthly RI check if found
        // the final save after removing 2PLG will catch any monthly discrepencies

        // validate, save, etc first then goto the SalarySetting screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        if (budgetConstructionForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
            budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
            budgetConstructionForm.initializePersistedRequestAmounts();

            // repop and refresh refs - esp monthly so jsp can properly display state
            budgetConstructionForm.populatePBGLLines();

        }
        PendingBudgetConstructionGeneralLedger pbglLine;
        pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));

        // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
        // request.getContextPath();
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.QUICK_SALARY_SETTING_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        parameters.put("documentNumber", pbglLine.getDocumentNumber());
        parameters.put("universityFiscalYear", pbglLine.getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", pbglLine.getChartOfAccountsCode());
        parameters.put("accountNumber", pbglLine.getAccountNumber());
        parameters.put("subAccountNumber", pbglLine.getSubAccountNumber());
        parameters.put("financialObjectCode", pbglLine.getFinancialObjectCode());
        parameters.put("financialSubObjectCode", pbglLine.getFinancialSubObjectCode());
        parameters.put("financialBalanceTypeCode", pbglLine.getFinancialBalanceTypeCode());
        parameters.put("financialObjectTypeCode", pbglLine.getFinancialObjectTypeCode());

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.QUICK_SALARY_SETTING_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * This adds a revenue line to the BC document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertRevenueLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;

        PendingBudgetConstructionGeneralLedger line = budgetConstructionForm.getNewRevenueLine();

        boolean rulePassed = true;

        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddPendingBudgetGeneralLedgerLineEvent(BCConstants.NEW_REVENUE_LINE_PROPERTY_NAME, budgetConstructionForm.getDocument(), line, true));

        if (rulePassed) {
            // TODO this should not be needed since ref objects are retrieved in populate
            // this is here to be consistent with how KualiAccountingDocumentActionBase insert new lines
            // but it looks like this would circumvent business rules checks
            // SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);

            // add PBGLLine
            insertPBGLLine(true, budgetConstructionForm, line);

            // clear the used newRevenueLine
            budgetConstructionForm.setNewRevenueLine(new PendingBudgetConstructionGeneralLedger());
            budgetConstructionForm.initNewLine(budgetConstructionForm.getNewRevenueLine(), true);


        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This adds an expenditure line to the BC document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertExpenditureLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;

        PendingBudgetConstructionGeneralLedger line = budgetConstructionForm.getNewExpenditureLine();

        boolean rulePassed = true;

        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddPendingBudgetGeneralLedgerLineEvent(BCConstants.NEW_EXPENDITURE_LINE_PROPERTY_NAME, budgetConstructionForm.getDocument(), line, false));

        if (rulePassed) {
            // TODO this should not be needed since ref objects are retrieved in populate
            // this is here to be consistent with how KualiAccountingDocumentActionBase insert new lines
            // but it looks like this would circumvent business rules checks
            // SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);

            // add PBGLLine
            insertPBGLLine(false, budgetConstructionForm, line);

            // clear the used newExpenditureLine
            budgetConstructionForm.setNewExpenditureLine(new PendingBudgetConstructionGeneralLedger());
            budgetConstructionForm.initNewLine(budgetConstructionForm.getNewExpenditureLine(), false);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This inserts a PBGL revenue or expenditure line
     * 
     * @param isRevenue
     * @param budgetConstructionForm
     * @param line
     */
    protected void insertPBGLLine(boolean isRevenue, BudgetConstructionForm budgetConstructionForm, PendingBudgetConstructionGeneralLedger line) {

        // TODO create and init a decorator if determined to be needed
        // is this needed??
        BudgetConstructionDocument bcDoc = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        // null subobj must be set to dashes
        if (StringUtils.isBlank(line.getFinancialSubObjectCode())) {
            line.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }

        // add the line in the proper order - assumes already exists check is done in rules
        bcDoc.addPBGLLine(line, isRevenue);

        // adjust totals
        if (line.getAccountLineAnnualBalanceAmount() != null && line.getAccountLineAnnualBalanceAmount() != KualiInteger.ZERO) {
            if (isRevenue) {
                bcDoc.setRevenueAccountLineAnnualBalanceAmountTotal(bcDoc.getRevenueAccountLineAnnualBalanceAmountTotal().add(line.getAccountLineAnnualBalanceAmount()));
            }
            else {
                bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(line.getAccountLineAnnualBalanceAmount()));
            }
        }
        if (line.getFinancialBeginningBalanceLineAmount() != null && line.getFinancialBeginningBalanceLineAmount() != KualiInteger.ZERO) {
            if (isRevenue) {
                bcDoc.setRevenueFinancialBeginningBalanceLineAmountTotal(bcDoc.getRevenueFinancialBeginningBalanceLineAmountTotal().add(line.getFinancialBeginningBalanceLineAmount()));
            }
            else {
                bcDoc.setExpenditureFinancialBeginningBalanceLineAmountTotal(bcDoc.getExpenditureFinancialBeginningBalanceLineAmountTotal().add(line.getFinancialBeginningBalanceLineAmount()));
            }
        }


        // TODO add the decorator, if determined to be needed

    }

    /**
     * Deletes an existing PendingBudgetConstructionGeneralLedger revenue line if rules passed. Any associated monthly budget
     * (BudgetConstructionMonthly) is also deleted.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteRevenueLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument tDoc = tForm.getBudgetConstructionDocument();

        boolean rulePassed = true;
        int deleteIndex = this.getLineToDelete(request);

        // check business rule if there is a persisted request amount, otherwise the line can just be removed
        PendingBudgetConstructionGeneralLedger revLine = tDoc.getPendingBudgetConstructionGeneralLedgerRevenueLines().get(deleteIndex);
        if (revLine.getPersistedAccountLineAnnualBalanceAmount() == null) {
            rulePassed = true;
        }
        else {
            // check deletion rules and delete if passed
            String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_REVENUE_LINES + "[" + deleteIndex + "]";
            rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new DeletePendingBudgetGeneralLedgerLineEvent(errorPath, tDoc, revLine, true));
        }

        if (rulePassed) {
            deletePBGLLine(true, tForm, deleteIndex, revLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes an existing PendingBudgetConstructionGeneralLedger expenditure line if rules passed Any associated monthly budget
     * (BudgetConstructionMonthly) is also deleted.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteExpenditureLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument tDoc = tForm.getBudgetConstructionDocument();

        boolean rulePassed = true;
        int deleteIndex = this.getLineToDelete(request);

        // check business rule if there is a persisted request amount, otherwise the line can just be removed
        PendingBudgetConstructionGeneralLedger expLine = tDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(deleteIndex);
        if (expLine.getPersistedAccountLineAnnualBalanceAmount() == null) {
            rulePassed = true;
        }
        else {
            // check deletion rules and delete if passed
            String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_EXPENDITURE_LINES + "[" + deleteIndex + "]";
            rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new DeletePendingBudgetGeneralLedgerLineEvent(errorPath, tDoc, expLine, false));
        }

        if (rulePassed) {
            deletePBGLLine(false, tForm, deleteIndex, expLine);
        }

        // GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Delete Expenditure Line");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes an existing PendingBudgetConstructionGeneralLedger revenue or expenditure line along with any associated monthly
     * budget (BudgetConstructionMonthly)
     * 
     * @param isRevenue
     * @param budgetConstructionForm
     * @param deleteIndex
     */
    protected void deletePBGLLine(boolean isRevenue, BudgetConstructionForm budgetConstructionForm, int deleteIndex, PendingBudgetConstructionGeneralLedger line) {

        BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();

        // adjust totals
        if (line.getAccountLineAnnualBalanceAmount() != null && line.getAccountLineAnnualBalanceAmount() != KualiInteger.ZERO) {
            if (isRevenue) {
                bcDoc.setRevenueAccountLineAnnualBalanceAmountTotal(bcDoc.getRevenueAccountLineAnnualBalanceAmountTotal().subtract(line.getAccountLineAnnualBalanceAmount()));
            }
            else {
                bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().subtract(line.getAccountLineAnnualBalanceAmount()));
            }
        }
        if (line.getFinancialBeginningBalanceLineAmount() != null && line.getFinancialBeginningBalanceLineAmount() != KualiInteger.ZERO) {
            if (isRevenue) {
                bcDoc.setRevenueFinancialBeginningBalanceLineAmountTotal(bcDoc.getRevenueFinancialBeginningBalanceLineAmountTotal().subtract(line.getFinancialBeginningBalanceLineAmount()));
            }
            else {
                bcDoc.setExpenditureFinancialBeginningBalanceLineAmountTotal(bcDoc.getExpenditureFinancialBeginningBalanceLineAmountTotal().subtract(line.getFinancialBeginningBalanceLineAmount()));
            }
        }

        // remove the line
        if (isRevenue) {
            bcDoc.getPendingBudgetConstructionGeneralLedgerRevenueLines().remove(deleteIndex);
        }
        else {
            bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().remove(deleteIndex);
        }

    }

    /*
     * public ActionForward returnFromMonthly(ActionMapping mapping, ActionForm form, HttpServletRequest request,
     * HttpServletResponse response) throws Exception { BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm)
     * form; String documentNumber = request.getParameter("documentNumber"); BudgetConstructionDocument budgetConstructionDocument =
     * (BudgetConstructionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
     * budgetConstructionForm.setDocument(budgetConstructionDocument); KualiWorkflowDocument workflowDoc =
     * budgetConstructionDocument.getDocumentHeader().getWorkflowDocument();
     * budgetConstructionForm.setDocTypeName(workflowDoc.getDocumentType()); // KualiDocumentFormBase.populate() needs this updated
     * in the session GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc); return
     * mapping.findForward(KFSConstants.MAPPING_BASIC); }
     */

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;

        // Do specific refresh stuff here based on refreshCaller parameter
        // typical refresh callers would be monthlyBudget or salarySetting or lookupable
        // need to look at optmistic locking problems since we will be storing the values in the form before hand
        // this locking problem may workout if we store first then put the form in session
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(BCConstants.MONTHLY_BUDGET_REFRESH_CALLER)) {

            // TODO do things specific to returning from MonthlyBudget
            // like refreshing the line itself if the monthly budget process overrides the annual request
            // this would need to know what line to operate on
            // might not need since monthly process should just update the value directly in DB and form session object
            // need to check if editing mode

        }
        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(BCConstants.QUICK_SALARY_SETTING_REFRESH_CALLER)) {

            // TODO do things specific to returning from Salary Setting
            // like refreshing the line itself if the salary setting process overrides the annual request
            // this would need to know what line to operate on
            // might not need since ss process should just update the value directly in DB and form session object
            // need to check if editing mode

        }
        

        
        // TODO populate should already handle all this
        // take this out when populate is fixed and confirmed to handle
        /*
         * // need to get current state of monthly budgets regardless of who calls refresh for
         * (PendingBudgetConstructionGeneralLedger line :
         * budgetConstructionForm.getBudgetConstructionDocument().getPendingBudgetConstructionGeneralLedgerExpenditureLines()){
         * line.refreshReferenceObject("budgetConstructionMonthly"); } for (PendingBudgetConstructionGeneralLedger line :
         * budgetConstructionForm.getBudgetConstructionDocument().getPendingBudgetConstructionGeneralLedgerRevenueLines()){
         * line.refreshReferenceObject("budgetConstructionMonthly"); }
         */
        // TODO this should figure out if user is returning to a rev or exp line and refresh just that
        // TODO this should also keep original values of obj, sobj to compare and null out dependencies when needed
        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL)) {
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "financialObject", "financialSubObject" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionForm.getNewRevenueLine(), REFRESH_FIELDS);
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionForm.getNewExpenditureLine(), REFRESH_FIELDS);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show
     * all of the descriptions and labels for each of the pbgl line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        tForm.setHideDetails(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action toggles the value of the hide field in the user interface to "hide" so that when the page is rendered, the UI
     * displays values without all of the descriptions and labels for each of the pbgl lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        tForm.setHideDetails(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward toggleAdjustmentMeasurement(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;

        boolean currentStatus = docForm.isHideAdjustmentMeasurement();
        docForm.setHideAdjustmentMeasurement(!currentStatus);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward adjustRevenueLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDoc = docForm.getBudgetConstructionDocument();
        PendingBudgetConstructionGeneralLedger revLine = bcDoc.getPendingBudgetConstructionGeneralLedgerRevenueLines().get(this.getSelectedLine(request));

        if (revLine.getAdjustmentAmount() != null) {
            this.adjustRequest(revLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward adjustExpenditureLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDoc = docForm.getBudgetConstructionDocument();
        PendingBudgetConstructionGeneralLedger expLine = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));

        if (expLine.getAdjustmentAmount() != null) {
            this.adjustRequest(expLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward adjustAllRevenueLinesPercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDoc = docForm.getBudgetConstructionDocument();
        List<PendingBudgetConstructionGeneralLedger> revenueLines = bcDoc.getPendingBudgetConstructionGeneralLedgerRevenueLines();

        KualiDecimal adjustmentAmount = docForm.getRevenueAdjustmentAmount();
        if (adjustmentAmount != null) {

            // not sure we need this check since the tool isn't displayed in view mode
            boolean isEditable = (docForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !docForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY));
            for (PendingBudgetConstructionGeneralLedger revenueLine : revenueLines) {
                if (isEditable) {
                    revenueLine.setAdjustmentAmount(adjustmentAmount);
                    this.adjustRequest(revenueLine);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward adjustAllExpenditureLinesPercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDoc = docForm.getBudgetConstructionDocument();
        List<PendingBudgetConstructionGeneralLedger> expenditureLines = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();

        KualiDecimal adjustmentAmount = docForm.getExpenditureAdjustmentAmount();
        if (adjustmentAmount != null) {

            // not sure we need this check since the tool isn't displayed in view mode
            boolean isEditable = (docForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !docForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY));
            // (!benecalcDisabled && !empty item.laborObject && item.laborObject.financialObjectFringeOrSalaryCode == 'F')
            for (PendingBudgetConstructionGeneralLedger expenditureLine : expenditureLines) {
                boolean isLineEditable = (isEditable && (docForm.isBenefitsCalculationDisabled() || (expenditureLine.getLaborObject() == null) || !expenditureLine.getLaborObject().getFinancialObjectFringeOrSalaryCode().equalsIgnoreCase(BCConstants.LABOR_OBJECT_FRINGE_CODE)));
                if (isLineEditable) {
                    expenditureLine.setAdjustmentAmount(adjustmentAmount);
                    this.adjustRequest(expenditureLine);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles the document (account) pullup action, resetting the cached editingMode as appropriate for the new level.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performAccountPullup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean doAllowPullup = false;
        boolean lockNeeded = false;
        boolean prePullReadOnlyAccess;

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // if system view only or view only - reload to get the latest status
        // and check that the document is still below the selected POV
        if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY) || tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.VIEW_ONLY)){

            prePullReadOnlyAccess = true;
            // if not system view only mode, we are in document view mode - we'll need a lock before the pullup
            // since by definition pullup puts the account at a full_entry level
            // and we need exclusive access to perform the pullup
            if (!tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)){
                lockNeeded = true;
            }

            // now reload the document and get latest status info
            loadDocument(tForm);
            this.initAuthorizationEditMode(tForm);
            if (tForm.getBudgetConstructionDocument().getOrganizationLevelCode() < Integer.parseInt(tForm.getPullupKeyCode())){
                doAllowPullup = true;
            }
            else {
                // document has been moved and is either at the desired level or above
                doAllowPullup = false;
                lockNeeded = false;
                
                // document has been moved above the desired level - let populate through an authorization exception
                if (tForm.getBudgetConstructionDocument().getOrganizationLevelCode() > Integer.parseInt(tForm.getPullupKeyCode())){
                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Document has already been moved above the selected level.");
                }
            }
            
            // if the reload now shows system view only mode and we flagged that a lock was needed - turn it back off
            if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)){
                lockNeeded = false;
            }

        }
        else {
            // we are in document full_entry and not system view only
            // assume we already have a lock, allow the pullup
            // and we need to ensure the user can finish editing work if after pullup system goes to system view only
            prePullReadOnlyAccess = false;
            doAllowPullup = true;
        }
        
        if (lockNeeded || doAllowPullup){

            // get a fresh header to use for lock and/or pullup
            HashMap primaryKey = new HashMap();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, tForm.getDocument().getDocumentNumber());
            BudgetConstructionHeader budgetConstructionHeader = (BudgetConstructionHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
            if (budgetConstructionHeader == null){
                GlobalVariables.getErrorMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Fatal, Document not found.");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            if (lockNeeded){
                // only successful lock allows pullup here
                doAllowPullup = false;

                LockService lockService = SpringContext.getBean(LockService.class);
                BudgetConstructionLockStatus bcLockStatus = lockService.lockAccount(budgetConstructionHeader, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
                LockStatus lockStatus = bcLockStatus.getLockStatus();
                switch (lockStatus){
                    case SUCCESS:
                        doAllowPullup = true;
                        break;
                    case BY_OTHER:
                        String lockerName = SpringContext.getBean(UniversalUserService.class).getUniversalUser(bcLockStatus.getAccountLockOwner()).getPersonName();
                        GlobalVariables.getErrorMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Locked by "+lockerName);
                        break;
                    case FLOCK_FOUND:
                        GlobalVariables.getErrorMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Funding lock found.");
                        break;
                    default:
                        GlobalVariables.getErrorMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Optimistic lock or other failure during lock attempt.");
                        break;
                }
              }

              // attempt pullup
              if (doAllowPullup){
                  budgetConstructionHeader.setOrganizationLevelCode(Integer.parseInt(tForm.getPullupKeyCode()));
                  budgetConstructionHeader.setOrganizationLevelChartOfAccountsCode(tForm.getAccountOrgHierLevels().get(Integer.parseInt(tForm.getPullupKeyCode())).getOrganizationChartOfAccountsCode());
                  budgetConstructionHeader.setOrganizationLevelOrganizationCode(tForm.getAccountOrgHierLevels().get(Integer.parseInt(tForm.getPullupKeyCode())).getOrganizationCode());
                  SpringContext.getBean(BusinessObjectService.class).save(budgetConstructionHeader); 
                 
                  // finally refresh the doc with the changed header info
                  tForm.getBudgetConstructionDocument().setVersionNumber(budgetConstructionHeader.getVersionNumber());
                  tForm.getBudgetConstructionDocument().setOrganizationLevelCode(budgetConstructionHeader.getOrganizationLevelCode());
                  tForm.getBudgetConstructionDocument().setOrganizationLevelChartOfAccountsCode(budgetConstructionHeader.getOrganizationLevelChartOfAccountsCode());
                  tForm.getBudgetConstructionDocument().setOrganizationLevelOrganizationCode(budgetConstructionHeader.getOrganizationLevelOrganizationCode());
                  
                  // refresh the lock info even though the user may be pulling while in edit mode
                  tForm.getBudgetConstructionDocument().setBudgetLockUserIdentifier(budgetConstructionHeader.getBudgetLockUserIdentifier());
                  
                  // refresh organization - so UI shows new level description
                  tForm.getBudgetConstructionDocument().refreshReferenceObject("organizationLevelOrganization");

                  this.initAuthorizationEditMode(tForm);

                  // if before pullup, system was 'not system view only' goes to 'system view only' after pull
                  // need to manually remove the system view only editingMode here to allow the user to save work since still full_entry
                  if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !prePullReadOnlyAccess){
                      if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)){
                          tForm.getEditingMode().remove(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY);
                      }
                  }
              }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Handles the document (account) pushdown action, resetting the cached editingMode as appropriate for the new level.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performAccountPushdown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean doAllowPushdown = false;
        boolean unlockNeeded = false;
        boolean prePushSystemViewOnly;
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();
        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // This method is called only if user has edit access and there is somewhere to push to.
        // If not system view only and the intended push level is view, we need to validate and save
        // Otherwise new level is still allowing editing, just push and keep current lock
        if (!tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)){
            prePushSystemViewOnly = false;

            // check editing mode at the intended level
            BudgetConstructionHeader bcHdr = this.getTestHeaderFromDocument(bcDocument, Integer.parseInt(tForm.getPushdownKeyCode()));
            String targetEditMode = budgetDocumentService.getAccessMode(bcHdr,GlobalVariables.getUserSession().getUniversalUser());
            if (targetEditMode.equals(BudgetConstructionEditMode.VIEW_ONLY)){

                budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
                budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
                tForm.initializePersistedRequestAmounts();

                // repop and refresh refs - esp monthly so jsp can properly display state
                tForm.populatePBGLLines();
                
                unlockNeeded = true;
            }
            doAllowPushdown = true;
        }
        else {
            prePushSystemViewOnly = true;
            
            // reload document to get most up-to-date status and recheck that we still have FULL_ENTRY access
            // anything else means the document was moved by someone else and we may no longer even have read access
            loadDocument(tForm);
            this.initAuthorizationEditMode(tForm);
            if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)){
                doAllowPushdown = true;
            }
            else {
                // document has moved
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_BUDGET_PUSHDOWN_DOCUMENT, "Full Access Control Lost.");
            }
        }

        // gets here if editing and pushing to view and doc is valid and persisted
        // or we are pushing from edit to edit
        // or we are in system view only
        if (doAllowPushdown){

            HashMap primaryKey = new HashMap();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, tForm.getDocument().getDocumentNumber());

            BudgetConstructionHeader budgetConstructionHeader = (BudgetConstructionHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
            if (budgetConstructionHeader != null){
                budgetConstructionHeader.setOrganizationLevelCode(Integer.parseInt(tForm.getPushdownKeyCode()));
                if (Integer.parseInt(tForm.getPushdownKeyCode()) == 0){
                    budgetConstructionHeader.setOrganizationLevelChartOfAccountsCode(null);
                    budgetConstructionHeader.setOrganizationLevelOrganizationCode(null);
                }
                else {
                    budgetConstructionHeader.setOrganizationLevelChartOfAccountsCode(tForm.getAccountOrgHierLevels().get(Integer.parseInt(tForm.getPushdownKeyCode())).getOrganizationChartOfAccountsCode());
                    budgetConstructionHeader.setOrganizationLevelOrganizationCode(tForm.getAccountOrgHierLevels().get(Integer.parseInt(tForm.getPushdownKeyCode())).getOrganizationCode());
                }
            }
            
            // unlock if needed (which stores) - otherwise store the new level
            if (unlockNeeded){

                LockService lockService = SpringContext.getBean(LockService.class);
                lockService.unlockAccount(budgetConstructionHeader);
            }
            else {
                SpringContext.getBean(BusinessObjectService.class).save(budgetConstructionHeader); 
                
            }
            
            // finally refresh the doc with the changed header info
            tForm.getBudgetConstructionDocument().setVersionNumber(budgetConstructionHeader.getVersionNumber());
            tForm.getBudgetConstructionDocument().setOrganizationLevelCode(budgetConstructionHeader.getOrganizationLevelCode());
            tForm.getBudgetConstructionDocument().setOrganizationLevelChartOfAccountsCode(budgetConstructionHeader.getOrganizationLevelChartOfAccountsCode());
            tForm.getBudgetConstructionDocument().setOrganizationLevelOrganizationCode(budgetConstructionHeader.getOrganizationLevelOrganizationCode());
            tForm.getBudgetConstructionDocument().setBudgetLockUserIdentifier(budgetConstructionHeader.getBudgetLockUserIdentifier());
            
            // refresh organization - so UI shows new level description
            tForm.getBudgetConstructionDocument().refreshReferenceObject("organizationLevelOrganization");
            
            this.initAuthorizationEditMode(tForm);
            
            // if before push, system is 'not system view only' goes to 'system view only' after push
            // need to manually remove the system view only editingMode here to allow the user to save work if still full_entry
            if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)){
                if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY) && !prePushSystemViewOnly){
                    tForm.getEditingMode().remove(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY);
                }
            }
            
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Derives a BudgetConstructionHeader from a BudgetConstructionDocument and sets a testLevel
     * to be used in finding out the access mode for the level.
     *  
     * @param bcDoc
     * @param testLevel
     * @return
     */
    private BudgetConstructionHeader getTestHeaderFromDocument(BudgetConstructionDocument bcDoc, Integer testLevel){

        BudgetConstructionHeader bcHdr = new BudgetConstructionHeader();
        bcHdr.setDocumentNumber(bcDoc.getDocumentNumber());
        bcHdr.setUniversityFiscalYear(bcDoc.getUniversityFiscalYear());
        bcHdr.setChartOfAccountsCode(bcDoc.getChartOfAccountsCode());
        bcHdr.setAccountNumber(bcDoc.getAccountNumber());
        bcHdr.setSubAccountNumber(bcDoc.getSubAccountNumber());
        bcHdr.setOrganizationLevelCode(testLevel);

        return bcHdr;
    }

    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !tForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
            budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
            tForm.initializePersistedRequestAmounts();

            // repop and refresh refs - esp monthly so jsp can properly display state
            tForm.populatePBGLLines();
        }

        // gets here if rules passed and doc detail gets persisted and refreshed
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.MONTHLY_BUDGET_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put("documentNumber", tForm.getDocument().getDocumentNumber());
        parameters.put("universityFiscalYear", tForm.getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", tForm.getChartOfAccountsCode());
        parameters.put("accountNumber", tForm.getAccountNumber());
        parameters.put("subAccountNumber", tForm.getSubAccountNumber());

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.REPORT_RUNNER_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performPercentChange(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Percent Change");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performRevMonthSpread(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.performMonthSpread(mapping, form, request, response, true);
    }

    public ActionForward performExpMonthSpread(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.performMonthSpread(mapping, form, request, response, false);
    }

    public ActionForward performMonthSpread(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, boolean isRevenue) throws Exception {

        // no check for full_entry and system edit mode since this control is not displayed for this case

        // need to validate, save and calc benefits first
        // this is different than client/server model - need to always keep DB consistent
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // validate and save without checking monthly RI since the spread will keep things consistent
        if (isRevenue) {
            budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.REVENUE, false);
        }
        else {
            budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.EXPENDITURE, false);
        }
        budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
        tForm.initializePersistedRequestAmounts();

        BudgetConstructionMonthlyBudgetsCreateDeleteService monthlyBudgetService = SpringContext.getBean(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);

        if (isRevenue) {
            monthlyBudgetService.spreadBudgetConstructionMonthlyBudgetsRevenue(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber());
        }
        else {
            // service returns true if benefit eligible monthly lines exist
            if (monthlyBudgetService.spreadBudgetConstructionMonthlyBudgetsExpenditure(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber())) {
                bcDocument.setMonthlyBenefitsCalcNeeded(true);
                budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
            }
        }

        // repop and refresh refs - esp monthly so jsp can properly display state
        tForm.populatePBGLLines();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performRevMonthDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.performMonthDelete(mapping, form, request, response, true);
    }

    public ActionForward performExpMonthDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.performMonthDelete(mapping, form, request, response, false);
    }

    public ActionForward performMonthDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, boolean isRevenue) throws Exception {

        // no check for full_entry and system edit mode since this control is not displayed for this case

        // need to validate, save and calc benefits first
        // this is different than client/server model - need to always keep DB consistent
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // validate and save without checking monthly RI since the delete will make RI check moot
        if (isRevenue) {
            budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.REVENUE, false);
        }
        else {
            budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.EXPENDITURE, false);
        }
        budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
        tForm.initializePersistedRequestAmounts();

        BudgetConstructionMonthlyBudgetsCreateDeleteService monthlyBudgetService = SpringContext.getBean(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);

        if (isRevenue) {
            monthlyBudgetService.deleteBudgetConstructionMonthlyBudgetsRevenue(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber());
        }
        else {
            monthlyBudgetService.deleteBudgetConstructionMonthlyBudgetsExpenditure(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber());
        }

        // repop and refresh refs - esp monthly so jsp can properly display state
        tForm.populatePBGLLines();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performCalculateBenefits(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // no check for full_entry and system edit mode since this control is not displayed for this case
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) tForm.getDocument();

        // allow benecalc if account is not salary only and benefits calc not disabled
        if (!tForm.isBenefitsCalculationDisabled() && !bcDocument.isSalarySettingOnly()) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
            budgetDocumentService.calculateBenefits(bcDocument);
            tForm.initializePersistedRequestAmounts();

            // repop and refresh refs - esp monthly so jsp can properly display state
            tForm.populatePBGLLines();

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private void adjustRequest(PendingBudgetConstructionGeneralLedger pbglLine) {

        KualiInteger baseAmount = pbglLine.getFinancialBeginningBalanceLineAmount();
        if (baseAmount.isNonZero()) {
            KualiDecimal percent = pbglLine.getAdjustmentAmount();
            BigDecimal adjustedAmount = baseAmount.multiply(percent).divide(KFSConstants.ONE_HUNDRED);

            KualiInteger requestAmount = new KualiInteger(adjustedAmount).add(baseAmount);
            pbglLine.setAccountLineAnnualBalanceAmount(requestAmount);
        }

    }
}
