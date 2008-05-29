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
package org.kuali.module.budget.web.struts.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.authorization.KfsAuthorizationConstants.BudgetConstructionEditMode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.module.budget.BCConstants.MonthSpreadDeleteType;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.rule.event.AddPendingBudgetGeneralLedgerLineEvent;
import org.kuali.module.budget.rule.event.DeletePendingBudgetGeneralLedgerLineEvent;
import org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.module.budget.web.struts.form.BudgetConstructionForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * need to figure out if this should extend KualiAction, KualiDocumentActionBase or KualiTransactionDocumentActionBase
 */
public class BudgetConstructionAction extends KualiTransactionalDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingAction.class);

    /**
     * added this to be similar to KRA - remove if not needed
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.execute(mapping, form, request, response);

        // apprise user of granted access
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        if (budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_METHOD)) {
            if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) {
                GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SYSTEM_VIEW_ONLY);
            }
            if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.VIEW_ONLY)) {
                GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_VIEW_ONLY);
            }
            if (budgetConstructionForm.getEditingMode().containsKey(BudgetConstructionEditMode.FULL_ENTRY)) {
                GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_EDIT_ACCESS);
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
        // public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse
        // response) throws IOException, ServletException {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        String command = budgetConstructionForm.getCommand();

        // if (IDocHandler.INITIATE_COMMAND.equals(command)){
        loadDocument(budgetConstructionForm);
        // }

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

        // TODO may need to add BC security model checks here or will form populateAuthorizationFields do this

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
     * This method is similar to org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase.performBalanceInquiry()
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

        // TODO do validate, save, etc first then goto the monthly screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        // TODO for now just save
        documentService.updateDocument(bcDocument);

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

        // TODO do validate, save, etc first then goto the SalarySetting screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        if (budgetConstructionForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);

            // TODO for now just save eventually should validate and save like reports
            documentService.updateDocument(bcDocument);


        }
        PendingBudgetConstructionGeneralLedger pbglLine;
        pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));

        // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
        // request.getContextPath();
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.SALARY_SETTING_METHOD);

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

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.SALARY_SETTING_ACTION, parameters);
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

        // TODO still need to flesh out business rules
        // this assumes populate retrieves needed ref objects used in applying business rules
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

        // TODO still need to flesh out business rules
        // this assumes populate retrieves needed ref objects used in applying business rules
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
        if (line.getAccountLineAnnualBalanceAmount() != null && line.getAccountLineAnnualBalanceAmount() != KualiInteger.ZERO){
            if (isRevenue) {
                bcDoc.setRevenueAccountLineAnnualBalanceAmountTotal(bcDoc.getRevenueAccountLineAnnualBalanceAmountTotal().add(line.getAccountLineAnnualBalanceAmount()));
            }
            else {
                bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(line.getAccountLineAnnualBalanceAmount()));
            }
        }
        if (line.getFinancialBeginningBalanceLineAmount() != null && line.getFinancialBeginningBalanceLineAmount() != KualiInteger.ZERO){
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

        // GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Delete
        // Expenditure Line");

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
        if (line.getAccountLineAnnualBalanceAmount() != null && line.getAccountLineAnnualBalanceAmount() != KualiInteger.ZERO){
            if (isRevenue) {
                bcDoc.setRevenueAccountLineAnnualBalanceAmountTotal(bcDoc.getRevenueAccountLineAnnualBalanceAmountTotal().subtract(line.getAccountLineAnnualBalanceAmount()));
            }
            else {
                bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().subtract(line.getAccountLineAnnualBalanceAmount()));
            }
        }
        if (line.getFinancialBeginningBalanceLineAmount() != null && line.getFinancialBeginningBalanceLineAmount() != KualiInteger.ZERO){
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

    public ActionForward performAccountPullup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Pullup");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performAccountPushdown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Pushdown");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        if (tForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)) {
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

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + "budgetReportRunner.do", parameters);
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

        // need to validate, save and calc benefits first
        // this is different than client/server model - need to always keep DB consistent
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
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

        // need to validate, save and calc benefits first
        // this is different than client/server model - need to always keep DB consistent
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
        
        if (isRevenue){
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

}
