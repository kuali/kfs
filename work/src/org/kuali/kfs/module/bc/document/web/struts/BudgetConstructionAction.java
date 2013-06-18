/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.BCKeyLabelPair;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAuthorizationStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.validation.event.AddPendingBudgetGeneralLedgerLineEvent;
import org.kuali.kfs.module.bc.document.validation.event.DeletePendingBudgetGeneralLedgerLineEvent;
import org.kuali.kfs.module.bc.exception.BudgetConstructionDocumentAuthorizationException;
import org.kuali.kfs.module.bc.identity.BudgetConstructionNoAccessMessageSetting;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * need to figure out if this should extend KualiAction, KualiDocumentActionBase or KualiTransactionDocumentActionBase
 */
public class BudgetConstructionAction extends KualiTransactionalDocumentActionBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAction.class);

    /**
     * Entry point to all actions Checks for cases where methodToCall is loadDocument, performAccountPullup or
     * performAccountPushdown and creates global messages to describe the new editingMode state. Also handles document locking if
     * the editingMode is BudgetConstructionEditMode.FULL_ENTRY. (Re)Populates the pullup and pushdown selection controls based on
     * the current level of the document and the user's approval access for the levels above and below the current level.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;

        // this is only used to indicate to the rules the user has clicked save or close save-yes
        // which forces tighter checks (nonZeroRequest amount) when access is cleanup mode
        if (budgetConstructionForm.getBudgetConstructionDocument().isCleanupModeActionForceCheck()) {
            budgetConstructionForm.getBudgetConstructionDocument().setCleanupModeActionForceCheck(Boolean.FALSE);
        }

        // TODO: the catch code comes from KualiDocumentActionBase.loadDocument()
        // this allows the top portion of the document to be populated and the close button to function
        // and also displays all the possible error messages for now
        // we need to update this once KIM is adjusted to handle BC no access cases
        // and maybe just show the authorization exception message
        ActionForward forward = null;
        try {
            forward = super.execute(mapping, form, request, response);
        }
        catch (AuthorizationException e) {
            forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            String docId = budgetConstructionForm.getDocId();
            Document doc = null;
            doc = getDocumentService().getByDocumentHeaderId(docId);
            if (doc == null) {
                throw new UnknownDocumentIdException("Document no longer exists.  It may have been cancelled before being saved.");
            }
            WorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
            // if (!getDocumentHelperService().getDocumentAuthorizer(doc).canOpen(doc,
            // GlobalVariables.getUserSession().getPerson())) {
            // throw buildAuthorizationException("open", doc);
            // }
            // re-retrieve the document using the current user's session - remove the system user from the WorkflowDcument object
            if (workflowDocument != doc.getDocumentHeader().getWorkflowDocument()) {
                LOG.warn("Workflow document changed via canOpen check");
                doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
            }
            budgetConstructionForm.setDocument(doc);
            WorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
            budgetConstructionForm.setDocTypeName(workflowDoc.getDocumentTypeName());
            // KualiDocumentFormBase.populate() needs this updated in the session

            SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), workflowDoc);

            budgetConstructionForm.setSecurityNoAccess(true);
            setBudgetDocumentNoAccessMessage(budgetConstructionForm);
            budgetConstructionForm.getDocumentActions().put(KRADConstants.KUALI_ACTION_CAN_CLOSE, Boolean.TRUE);

        }

        // apprise user of granted access
        if (budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_METHOD) || budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_PULLUP_METHOD) || budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_PUSHDOWN_METHOD)) {

            // init the account org hier state on initial load only - this is stored as hiddens
            if (budgetConstructionForm.getMethodToCall().equals(BCConstants.BC_DOCUMENT_METHOD)) {
                budgetConstructionForm.setAccountOrgHierLevels(SpringContext.getBean(BudgetDocumentService.class).getPushPullLevelList(budgetConstructionForm.getBudgetConstructionDocument(), GlobalVariables.getUserSession().getPerson()));
            }

            if (budgetConstructionForm.isSystemViewOnly()) {
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SYSTEM_VIEW_ONLY);
            }

            if (!budgetConstructionForm.isEditAllowed()) {
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_VIEW_ONLY);
            }

            if (budgetConstructionForm.isEditAllowed()) {
                if (budgetConstructionForm.isSystemViewOnly()) {
                    KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_VIEW_ONLY);
                }
                else {

                    // tell the user if the document is in not budgetable mode
                    budgetConstructionForm.getBudgetConstructionDocument().setBudgetableDocument(SpringContext.getBean(BudgetDocumentService.class).isBudgetableDocumentNoWagesCheck(budgetConstructionForm.getBudgetConstructionDocument()));
                    // budgetConstructionForm.setBudgetableDocument(SpringContext.getBean(BudgetDocumentService.class).isBudgetableDocumentNoWagesCheck(budgetConstructionForm.getBudgetConstructionDocument()));
                    if (!budgetConstructionForm.isBudgetableDocument()) {
                        KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_DOCUMENT_NOT_BUDGETABLE);
                    }

                    KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_EDIT_ACCESS);
                }

                if (!budgetConstructionForm.isSystemViewOnly()) {
                    LockService lockService = SpringContext.getBean(LockService.class);
                    HashMap primaryKey = new HashMap();
                    primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionForm.getDocument().getDocumentNumber());

                    BudgetConstructionHeader budgetConstructionHeader = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
                    if (budgetConstructionHeader != null) {
                        // BudgetConstructionLockStatus bcLockStatus = lockService.lockAccount(budgetConstructionHeader,
                        // GlobalVariables.getUserSession().getPerson().getPrincipalId());
                        BudgetConstructionLockStatus bcLockStatus = lockService.lockAccountAndCommit(budgetConstructionHeader, GlobalVariables.getUserSession().getPerson().getPrincipalId());

                        if (bcLockStatus.getLockStatus() == LockStatus.SUCCESS) {

                            // update the document version number
                            // so the saved lock of header doesn't produce an optimistic lock error
                            // and has the info we just put in the header so doc save doesn't wipe out the lock
                            budgetConstructionForm.getBudgetConstructionDocument().setVersionNumber(bcLockStatus.getBudgetConstructionHeader().getVersionNumber());
                            budgetConstructionForm.getBudgetConstructionDocument().setBudgetLockUserIdentifier(bcLockStatus.getBudgetConstructionHeader().getBudgetLockUserIdentifier());
                        }
                        else {
                            if (bcLockStatus.getLockStatus() == LockStatus.BY_OTHER) {
                                Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(bcLockStatus.getAccountLockOwner());
                                String lockerName = principal.getPrincipalName();
                                this.cleanupForLockError(budgetConstructionForm);
                                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_DOCUMENT_LOCKED, lockerName);
                                return forward;
                            }
                            else {
                                if (bcLockStatus.getLockStatus() == LockStatus.FLOCK_FOUND) {
                                    this.cleanupForLockError(budgetConstructionForm);
                                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_FUNDING_LOCKED);
                                    return forward;
                                }
                                else {
                                    this.cleanupForLockError(budgetConstructionForm);
                                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_DOCUMENT_OTHER);
                                    return forward;
                                }
                            }
                        }
                    }
                    else {
                        // unlikely
                        throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getPerson().getName(), "open", budgetConstructionForm.getDocument().getDocumentHeader().getDocumentNumber(), "(can't find document for locking)", budgetConstructionForm.isPickListMode());
                    }

                    // if editing, check if 2plg adjustment needed and calc benefits
                    // since checkTwoPlugAdjusmtent will only be set in docHandler during initial load
                    // if document is initially view only we want the 2plg adjustment to happen if the user does a pullup to edit
                    if (budgetConstructionForm.isCheckTwoPlugAdjustment() && budgetConstructionForm.getBudgetConstructionDocument().isContainsTwoPlug() && !budgetConstructionForm.getBudgetConstructionDocument().isSalarySettingOnly()) {
                        // do 2plg related benefits calc and adjust 2plg for any diff - reset checkTwoPlugAdjusment
                        budgetConstructionForm.setCheckTwoPlugAdjustment(false);
                        this.adjustForSalarySettingChanges(budgetConstructionForm);
                    }
                }

                // getting here implies a lock or system view mode, try to build a pullup list
                // pushdown is only allowed if user has edit access (regardless of system view only mode)
                if (budgetConstructionForm.getBudgetConstructionDocument().getOrganizationLevelCode() != 0) {
                    if (!budgetConstructionForm.getAccountOrgHierLevels().isEmpty()) {
                        budgetConstructionForm.populatePushPullLevelKeyLabels(budgetConstructionForm.getBudgetConstructionDocument(), budgetConstructionForm.getAccountOrgHierLevels(), false);
                    }
                }
                else {
                    // document(account) at level zero - clear out the pushdownKeyLabels widget for any previous performPush case
                    budgetConstructionForm.setPushdownLevelKeyLabels(new ArrayList<BCKeyLabelPair>());
                }
            } // FULL_ENTRY

            // pullup is allowed regardless of editMode
            if (!budgetConstructionForm.getAccountOrgHierLevels().isEmpty()) {
                budgetConstructionForm.populatePushPullLevelKeyLabels(budgetConstructionForm.getBudgetConstructionDocument(), budgetConstructionForm.getAccountOrgHierLevels(), true);
            }
        }

        // cleanup the session edit mode store so we don't side effect organization salary setting
        if (budgetConstructionForm.isClosingDocument()) {
            GlobalVariables.getUserSession().removeObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY);
        }

        return forward;
    }

    /**
     * Finds the role type service associated with the document viewer role, than calls method on role type service to set the no
     * access message
     *
     * @param budgetConstructionForm form containing budget document
     */
    protected void setBudgetDocumentNoAccessMessage(BudgetConstructionForm budgetConstructionForm) {
        Role roleInfo = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName(BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.DOCUMENT_VIEWER_ROLE_NAME);
        KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());

        if (StringUtils.isNotBlank(typeInfo.getServiceName())) {
            // fix to find service using new prefixed name if so configured
            // RoleTypeService roleTypeService = (RoleTypeService) SpringContext.getService(typeInfo.getServiceName());
            RoleTypeService roleTypeService;
            String svcName = typeInfo.getServiceName();
            String[] svcNameParts = svcName.split("\\{|\\}");
            if (svcNameParts.length == 3){
                String nameSpaceURI = svcNameParts[1];
                String remoteServiceName = svcNameParts[2];
                roleTypeService = (RoleTypeService)GlobalResourceLoader.getService(new QName(nameSpaceURI, remoteServiceName));
            } else {
              roleTypeService = (RoleTypeService) SpringContext.getService(typeInfo.getServiceName());
            }

            if (roleTypeService instanceof BudgetConstructionNoAccessMessageSetting) {
                ((BudgetConstructionNoAccessMessageSetting) roleTypeService).setNoAccessMessage(budgetConstructionForm.getBudgetConstructionDocument(), GlobalVariables.getUserSession().getPerson(), GlobalVariables.getMessageMap());
            } else {
                LOG.warn(String.format("typeInfo.getServiceName() = %s is not an instance of BudgetConstructionNoAccessMessageSetting for role %s/%s", typeInfo.getServiceName(), BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.DOCUMENT_VIEWER_ROLE_NAME));
            }
        } else {
            LOG.warn(String.format("typeInfo.getServiceName() returned a blank service name for role %s/%s", BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.DOCUMENT_VIEWER_ROLE_NAME));
        }
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

        loadDocument(budgetConstructionForm);

        // set flag to have execute perform 2plug adjusment if the doc goes into edit mode later
        budgetConstructionForm.setCheckTwoPlugAdjustment(true);

        this.initAuthorization(budgetConstructionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Initially load the document from the DB Coded this to look like KualiDocumentActionBase.loadDocument()
     *
     * @param budgetConstructionForm
     * @throws WorkflowException
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) kualiDocumentFormBase;

        BudgetConstructionHeader budgetConstructionHeader;
        if (budgetConstructionForm.getDocId() != null) {
            Map<String, Object> primaryKey = new HashMap<String, Object>();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionForm.getDocId());

            budgetConstructionHeader = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);

            // getting called from doc search?
            if (budgetConstructionForm.getMethodToCall().equalsIgnoreCase(KFSConstants.DOC_HANDLER_METHOD)){

                // now fill in the form parms normally passed from BC selection or Account selection screens
                budgetConstructionForm.setChartOfAccountsCode(budgetConstructionHeader.getChartOfAccountsCode());
                budgetConstructionForm.setAccountNumber(budgetConstructionHeader.getAccountNumber());
                budgetConstructionForm.setSubAccountNumber(budgetConstructionHeader.getSubAccountNumber());
                budgetConstructionForm.setUniversityFiscalYear(budgetConstructionHeader.getUniversityFiscalYear());
                budgetConstructionForm.setPickListMode(Boolean.TRUE);
            }

        }
        else {
            // use the passed url autoloaded parms to get the record from DB
            // BudgetConstructionDaoOjb bcHeaderDao;
            String chartOfAccountsCode = budgetConstructionForm.getChartOfAccountsCode();
            String accountNumber = budgetConstructionForm.getAccountNumber();
            String subAccountNumber = budgetConstructionForm.getSubAccountNumber();
            Integer universityFiscalYear = budgetConstructionForm.getUniversityFiscalYear();

            budgetConstructionHeader = SpringContext.getBean(BudgetDocumentService.class).getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        }

        kualiDocumentFormBase.setDocId(budgetConstructionHeader.getDocumentNumber());
        super.loadDocument(kualiDocumentFormBase);

        BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) kualiDocumentFormBase.getDocument();

        // init the benefits calc flags
        budgetConstructionDocument.setBenefitsCalcNeeded(false);
        budgetConstructionDocument.setMonthlyBenefitsCalcNeeded(false);

        // init the new line objects
        budgetConstructionForm.initNewLine(budgetConstructionForm.getNewRevenueLine(), true);
        budgetConstructionForm.initNewLine(budgetConstructionForm.getNewExpenditureLine(), false);

        // need this here to do totaling on initial load
        budgetConstructionForm.populatePBGLLines();
        budgetConstructionForm.initializePersistedRequestAmounts();
    }

    /**
     * Cleans up state info to handle no access lock errors
     *
     * @param budgetConstructionForm
     */
    protected void cleanupForLockError(BudgetConstructionForm budgetConstructionForm) {
        budgetConstructionForm.setSecurityNoAccess(true);
        budgetConstructionForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        budgetConstructionForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        KNSGlobalVariables.getMessageList().remove(BCKeyConstants.MESSAGE_BUDGET_EDIT_ACCESS);
    }

    /**
     * Override to set authorization Maps from session
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#populateAuthorizationFields(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void populateAuthorizationFields(KualiDocumentFormBase formBase) {
        BudgetConstructionAuthorizationStatus authorizationStatus = (BudgetConstructionAuthorizationStatus) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY);

        if (authorizationStatus == null) {
            // execute handles any session timeout before this is called
            return;
        }

        formBase.setDocumentActions(authorizationStatus.getDocumentActions());
        formBase.setEditingMode(authorizationStatus.getEditingMode());
    }

    /**
     * Calls authorizer to determine if the user has edit permission and checks if budget construction is active is fiscal year
     * function table. Then updates the <code>BudgetConstructionEditStatus</code> in session. Finally updates authorization in the
     * form
     *
     * @param budgetConstructionForm current bc action form that will be updated
     */
    protected void initAuthorization(BudgetConstructionForm budgetConstructionForm) {
        // GlobalVariables.setRequestCache(ROLE_QUALIFICATION_CACHE_NAME, budgetConstructionForm)
        super.populateAuthorizationFields(budgetConstructionForm);

        BudgetConstructionAuthorizationStatus editStatus = new BudgetConstructionAuthorizationStatus();
        editStatus.setDocumentActions(budgetConstructionForm.getDocumentActions());
        editStatus.setEditingMode(budgetConstructionForm.getEditingMode());

        GlobalVariables.getUserSession().addObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY, editStatus);
    }

    /**
     * Applies adjustments due to 2plg existence at initial load or detected salary setting changes upon returning from the Quick
     * Salary Setting screen. The adjustments consist of calculating benefits and adding any expenditure total before/after
     * difference back into a 2plg row, creating or updating as needed. Then, validation is performed. Sucessful validation removes
     * the 2plg row if the final, post benefits calc, adjusted amount is zero. This method assumes the set of expenditure rows in
     * memory currently matches the DB.
     *
     * @param bcForm
     */
    protected void adjustForSalarySettingChanges(BudgetConstructionForm bcForm) {

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
        BudgetConstructionDocument bcDoc = (BudgetConstructionDocument) bcForm.getDocument();
        KualiInteger oldRequestAmount = bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal();

        // calc benefits also handles setting persisted amounts and populating reloaded benefit rows
        budgetDocumentService.calculateBenefits(bcDoc);
        // bcForm.initializePersistedRequestAmounts();

        // repop and refresh refs - esp monthly so jsp can properly display state
        // bcForm.populatePBGLLines();

        // need 2plg adjustment and save, even if it is zero
        KualiInteger newRquestAmount = bcForm.getBudgetConstructionDocument().getExpenditureAccountLineAnnualBalanceAmountTotal();
        PendingBudgetConstructionGeneralLedger twoPlugRow = budgetDocumentService.updatePendingBudgetGeneralLedgerPlug(bcDoc, newRquestAmount.subtract(oldRequestAmount));
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // return super.close(mapping, form, request, response);
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;

        // only want to prompt them to save if they already can save
        if (docForm.getDocumentActions().keySet().contains(KRADConstants.KUALI_ACTION_CAN_SAVE)) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyValueAsString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    // if yes button clicked - save the doc
                    BudgetConstructionDocument bcDoc = (BudgetConstructionDocument) docForm.getDocument();

                    // force tighter checks when saving in cleanup mode
                    if (!bcDoc.isBudgetableDocument()) {
                        bcDoc.setCleanupModeActionForceCheck(Boolean.TRUE);
                    }

                    // SpringContext.getBean(DocumentService.class).updateDocument(docForm.getDocument());
                    BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
                    budgetDocumentService.saveDocument(bcDoc);
                    docForm.initializePersistedRequestAmounts();
                    if (bcDoc.isBenefitsCalcNeeded() || bcDoc.isMonthlyBenefitsCalcNeeded()) {
                        budgetDocumentService.calculateBenefitsIfNeeded(bcDoc);

                        KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
                        return mapping.findForward(KFSConstants.MAPPING_BASIC);
                    }
                    else {
                        // else drop to close logic below
                    }

                }
                // else drop to close logic below
            }
        }

        // do the unlock if they have full access and not system view mode
        if (docForm.isEditAllowed() && !docForm.isSystemViewOnly()) {
            LockService lockService = SpringContext.getBean(LockService.class);
            HashMap primaryKey = new HashMap();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, docForm.getDocument().getDocumentNumber());

            BudgetConstructionHeader budgetConstructionHeader = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
            if (budgetConstructionHeader != null) {
                LockStatus lockStatus = lockService.unlockAccount(budgetConstructionHeader);
            }
            else {
                // unlikely, but benign problem here
            }
        }

        // flag to cleanup the session edit mode store so we don't side effect organization salary setting
        // used in the bottom of execute()
        docForm.setClosingDocument(Boolean.TRUE);

        if (docForm.isPickListMode()) {
            // redisplay with a message
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
            docForm.setPickListClose(true);

            // this is a hack to do our own session document cleanup since refreshCaller=QuestionRefresh
            // prevents proper cleanup in KualiRequestProcessor.processActionPerform()
            UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
            String docFormKey = docForm.getFormKey();
            SpringContext.getBean(SessionDocumentService.class).purgeDocumentForm(docForm.getDocument().getDocumentNumber(), docFormKey, userSession, request.getRemoteAddr());

            return mapping.findForward(KFSConstants.MAPPING_BASIC);

        }
        else {
            if (docForm.getReturnFormKey() == null) {

                // assume called from doc search or lost the session - go back to main
                return returnToSender(request, mapping, docForm);
            }
            else {
                // setup the return parms for the document and anchor and go back to doc select
                Properties parameters = new Properties();
                parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_SELECTION_REFRESH_METHOD);
                parameters.put(KFSConstants.DOC_FORM_KEY, docForm.getReturnFormKey());
                parameters.put(KFSConstants.ANCHOR, docForm.getReturnAnchor());
                parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.BC_DOCUMENT_REFRESH_CALLER);

                String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_SELECTION_ACTION, parameters);

                // this is a hack to do our own session document cleanup since refreshCaller=QuestionRefresh
                // prevents proper cleanup in KualiRequestProcessor.processActionPerform()
                UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
                String docFormKey = docForm.getFormKey();
                SpringContext.getBean(SessionDocumentService.class).purgeDocumentForm(docForm.getDocument().getDocumentNumber(), docFormKey, userSession, request.getRemoteAddr());

                return new ActionForward(lookupUrl, true);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();
        // DocumentService documentService = SpringContext.getBean(DocumentService.class);
        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // force tighter checks when saving in cleanup mode
        if (!bcDocument.isBudgetableDocument()) {
            bcDocument.setCleanupModeActionForceCheck(Boolean.TRUE);
        }

        budgetDocumentService.saveDocument(bcDocument);
        budgetConstructionForm.initializePersistedRequestAmounts();
        budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
        KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);

        budgetConstructionForm.setAnnotation("");

        // redisplay the document along with document saved message
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Calls the single line benefits impact screen by setting up the required parameters and feeding them to the temporary list
     * lookup action for the expenditure line selected. This is called from the ShowBenefits button on the BC document screen when
     * an expenditure line is associated with benefits and benefits calculation is enabled.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performShowBenefits(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument tDoc = tForm.getBudgetConstructionDocument();
        int selectIndex = this.getSelectedLine(request);
        PendingBudgetConstructionGeneralLedger expLine = tDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(selectIndex);

        // when we return from the lookup, our next request's method to call is going to be refresh
        tForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        if (StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // this hack sets the return anchor we want to return too after the inquiry
        // do this here so it gets into the session stored form version
        // refresh checks for this after and resets the anchor
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            tForm.setBalanceInquiryReturnAnchor(((KualiForm) form).getAnchor());
        }

        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, BCConstants.REQUEST_BENEFITS_BO);
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "true");
        parameters.put(BCConstants.SHOW_INITIAL_RESULTS, "true");
        parameters.put(BCConstants.TempListLookupMode.TEMP_LIST_LOOKUP_MODE, Integer.toString(BCConstants.TempListLookupMode.SHOW_BENEFITS));

        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, expLine.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, expLine.getChartOfAccountsCode());
        parameters.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, expLine.getFinancialObjectCode());
        parameters.put(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT, expLine.getAccountLineAnnualBalanceAmount().toString());
        parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, expLine.getAccountNumber());
        parameters.put(KRADConstants.LOOKUP_READ_ONLY_FIELDS, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR + "," + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + "," + KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME + "," + KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT);

        String url = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_TEMP_LIST_LOOKUP, parameters);
        this.setupDocumentExit();
        return new ActionForward(url, true);
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

        // when we return from the lookup, our next request's method to call is going to be refresh
        budgetConstructionForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        PendingBudgetConstructionGeneralLedger pbglLine;
        if (isRevenue) {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines().get(this.getSelectedLine(request));
        }
        else {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));
        }

        // build out base path for return location, use config service
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

        // this hack sets the return anchor we want to return too after the inquiry
        // do this here so it gets into the session stored form version
        // refresh checks for this after and resets the anchor
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            budgetConstructionForm.setBalanceInquiryReturnAnchor(((KualiForm) form).getAnchor());
        }

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX);

        // now add required parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        // need this next param b/c the lookup's return back will overwrite
        // the original doc form key
        parameters.put(KFSConstants.BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        // anchor, if it exists
        // this doesn't seem to work with the balance inquiry infrastructure, so added hack above
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

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

        this.setupDocumentExit();
        return new ActionForward(lookupUrl, true);
    }

    /**
     * Calls performMonthlyBudget for the selected revenue line
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performMonthlyRevenueBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performMonthlyBudget(true, mapping, form, request, response);
    }

    /**
     * Calls performMonthlyBudget for the selected expenditure line
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performMonthlyExpenditureBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performMonthlyBudget(false, mapping, form, request, response);
    }

    /**
     * Forwards the user to the monthly budget screen. Doing this in edit mode causes the document to be validated, saved and
     * benefits calculated (if needed).
     *
     * @param isRevenue
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performMonthlyBudget(boolean isRevenue, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;

        // this to checks for 2PLG and turns off SS/monthly RI check if found
        // the final save after removing 2PLG will catch any monthly discrepencies
        // also need to save object,subobject key we are operating on so refresh can get the latest row version from DB

        // validate, save, etc first then goto the monthly screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        PendingBudgetConstructionGeneralLedger pbglLine;
        if (isRevenue) {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerRevenueLines().get(this.getSelectedLine(request));
        }
        else {
            pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));
        }

        // when we return from the lookup, our next request's method to call is going to be refresh
        budgetConstructionForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        if (budgetConstructionForm.isEditAllowed() && !budgetConstructionForm.isSystemViewOnly()) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            // if the doc contains a 2plg line, turn off RI checking to allow cleanup.
            // The act of attempting to remove a 2plg (delete) forces a complete RI check.
            // The document is assumed inconsistent as long as a 2plg exists.
            if (!isRevenue && bcDocument.isContainsTwoPlug()) {
                if (pbglLine.getLaborObject() != null && pbglLine.getLaborObject().isDetailPositionRequiredIndicator()) {
                    budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.EXPENDITURE, false);
                }
                else {
                    budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.EXPENDITURE, true);
                }
            }
            else {
                budgetDocumentService.saveDocumentNoWorkflow(bcDocument);

            }
            budgetConstructionForm.initializePersistedRequestAmounts();
            budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

            // repop and refresh refs - esp monthly so jsp can properly display state
            // budgetConstructionForm.populatePBGLLines();

        }

        // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
        // request.getContextPath();
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
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
        parameters.put("revenue", (isRevenue ? "true" : "false"));
        parameters.put(BCPropertyConstants.MAIN_WINDOW, (budgetConstructionForm.isMainWindow() ? "true" : "false"));

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.MONTHLY_BUDGET_ACTION, parameters);
        this.setupDocumentExit();
        return new ActionForward(lookupUrl, true);
    }

    /**
     * Forwards the user to the quick salary setting screen. Doing this in edit mode causes the document to be validated, saved and
     * benefits calculated (if needed).
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;

        // this to checks for 2PLG and turns off monthly RI check if found
        // the final save after removing 2PLG will catch any monthly discrepencies

        // validate, save, etc first then goto the SalarySetting screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        // when we return from the lookup, our next request's method to call is going to be refresh
        budgetConstructionForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        if (budgetConstructionForm.isEditAllowed() && !budgetConstructionForm.isSystemViewOnly()) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            // Regardless if the doc contains a 2plg line, turn off RI checking to allow cleanup.
            // The act of attempting to remove a 2plg (delete) forces a complete RI check.
            // The document is assumed inconsistent as long as a 2plg exists.
            budgetDocumentService.saveDocumentNoWorkFlow(bcDocument, MonthSpreadDeleteType.EXPENDITURE, false);

            // init persisted property and get the current list of salary setting related rows
            budgetConstructionForm.initializePersistedRequestAmounts(true);

            budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

            // repop and refresh refs - esp monthly so jsp can properly display state
            // budgetConstructionForm.populatePBGLLines();

        }
        PendingBudgetConstructionGeneralLedger pbglLine;
        pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));

        // String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
        // request.getContextPath();
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
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
        parameters.put(BCPropertyConstants.MAIN_WINDOW, (budgetConstructionForm.isMainWindow() ? "true" : "false"));

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.QUICK_SALARY_SETTING_ACTION, parameters);
        this.setupDocumentExit();
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

        BudgetConstructionDocument bcDoc = (BudgetConstructionDocument) budgetConstructionForm.getDocument();

        // KFSMI-7828 reset object type from object code table
        line.setFinancialObjectTypeCode(line.getFinancialObject().getFinancialObjectTypeCode());

        // null subobj must be set to dashes
        if (StringUtils.isBlank(line.getFinancialSubObjectCode())) {
            line.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }

        // check the DB for an existing persisted version of the line
        // and reinstate that with a message to the user indicating such
        boolean isReinstated = false;
        Map<String, Object> primaryKey = new HashMap<String, Object>();
        primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, line.getDocumentNumber());
        primaryKey.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, line.getUniversityFiscalYear());
        primaryKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
        primaryKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, line.getAccountNumber());
        primaryKey.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, line.getSubAccountNumber());
        primaryKey.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, line.getFinancialBalanceTypeCode());
        primaryKey.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, line.getFinancialObjectTypeCode());

        primaryKey.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, line.getFinancialObjectCode());
        primaryKey.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, line.getFinancialSubObjectCode());

        PendingBudgetConstructionGeneralLedger dbLine = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PendingBudgetConstructionGeneralLedger.class, primaryKey);
        if (dbLine != null){
            line = dbLine;
            SpringContext.getBean(BudgetDocumentService.class).populatePBGLLine(line);
            isReinstated = true;
        }

        // add the line in the proper order - assumes already exists check is done in rules
        int insertPoint = bcDoc.addPBGLLine(line, isRevenue);

        if (isReinstated){
            String errorKey;
            if (isRevenue){
                errorKey = KRADConstants.DOCUMENT_PROPERTY_NAME + "." + BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_REVENUE_LINES + "[" + insertPoint  + "]." + KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT;
            }
            else {
                errorKey = KRADConstants.DOCUMENT_PROPERTY_NAME + "." + BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_EXPENDITURE_LINES + "[" + insertPoint  + "]." + KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT;
            }
            GlobalVariables.getMessageMap().putError(errorKey, BCKeyConstants.ERROR_BUDGET_LINE_REINSTATED, dbLine.getFinancialObjectCode() + "," + dbLine.getFinancialSubObjectCode());
        }

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
     * (BudgetConstructionMonthly) is also deleted. Check for the special case where the line is a 2PLG line, in which case the
     * document is validated and RI checks are forced even if there are no current differences between persisted and request
     * amounts.
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
            // check regular deletion rules and delete if passed
            String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_GENERAL_LEDGER_EXPENDITURE_LINES + "[" + deleteIndex + "]";
            rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new DeletePendingBudgetGeneralLedgerLineEvent(errorPath, tDoc, expLine, false));
        }

        if (rulePassed) {

            // if the line is a 2PLG line do document validation, which forces RI checks in no current change situation
            if (expLine.getFinancialObjectCode().equalsIgnoreCase(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG)) {
                SpringContext.getBean(BudgetDocumentService.class).validateDocument(tDoc);
            }
            // gets here if line is not 2PLG or doc is valid from 2plg perspective
            deletePBGLLine(false, tForm, deleteIndex, expLine);
        }

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
            if (line.getFinancialObjectCode().equalsIgnoreCase(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG)) {
                bcDoc.setContainsTwoPlug(false);
            }
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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        budgetConstructionForm.setDerivedValuesOnForm(request);

        // Do specific refresh stuff here based on refreshCaller parameter
        // typical refresh callers would be monthlyBudget or salarySetting or lookupable
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(BCConstants.MONTHLY_BUDGET_REFRESH_CALLER)) {

            // monthly process applies any changes to the DB and the form session object
            // including any override to the request amount which also changes the request total
            // it also sets up calc monthly benefits if the line is involved in benefits
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
            budgetDocumentService.calculateBenefitsIfNeeded(budgetConstructionForm.getBudgetConstructionDocument());

        }
        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(BCConstants.QUICK_SALARY_SETTING_REFRESH_CALLER)) {


            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            // if editing - reload expenditure and check for changes to detail salary lines and 2plg request amount
            boolean diffFound = false;
            if (budgetConstructionForm.isEditAllowed() && !budgetConstructionForm.isSystemViewOnly()) {
                BudgetConstructionDocument currentBCDoc = budgetConstructionForm.getBudgetConstructionDocument();

                // get the current set of salary setting related rows from DB and compare against preSalarySettingRows

                List<PendingBudgetConstructionGeneralLedger> dbSalarySettingRows = budgetDocumentService.getPBGLSalarySettingRows(currentBCDoc);
                for (PendingBudgetConstructionGeneralLedger dbSalarySettingRow : dbSalarySettingRows) {
                    if (budgetConstructionForm.getPreSalarySettingRows().containsKey(dbSalarySettingRow.getFinancialObjectCode() + dbSalarySettingRow.getFinancialSubObjectCode())) {

                        // update the existing row if a difference is found
                        KualiInteger dbReqAmount = dbSalarySettingRow.getAccountLineAnnualBalanceAmount();
                        KualiInteger preReqAmount = budgetConstructionForm.getPreSalarySettingRows().get(dbSalarySettingRow.getFinancialObjectCode() + dbSalarySettingRow.getFinancialSubObjectCode()).getAccountLineAnnualBalanceAmount();
                        Long dbVersionNumber = dbSalarySettingRow.getVersionNumber();
                        Long preReqVersionNumber = budgetConstructionForm.getPreSalarySettingRows().get(dbSalarySettingRow.getFinancialObjectCode() + dbSalarySettingRow.getFinancialSubObjectCode()).getVersionNumber();
                        if ((dbVersionNumber.compareTo(preReqVersionNumber) != 0) || (dbReqAmount.compareTo(preReqAmount) != 0)) {
                            budgetDocumentService.addOrUpdatePBGLRow(currentBCDoc, dbSalarySettingRow, Boolean.FALSE);

                            // only flag for existing line diff when the request amount changes
                            // changes in versionNumber implies offsetting updates of some sort
                            if (dbReqAmount.compareTo(preReqAmount) != 0) {
                                diffFound = true;
                            }
                        }
                    }
                    else {

                        // update the req amount and version or add the new row to the current doc as needed
                        // insert the new DB row to the set in memory
                        budgetDocumentService.addOrUpdatePBGLRow(currentBCDoc, dbSalarySettingRow, Boolean.FALSE);
                        diffFound = true;
                    }
                }

                if (diffFound) {
                    this.adjustForSalarySettingChanges(budgetConstructionForm);

                }
            }

        }

        if (refreshCaller != null && refreshCaller.endsWith(KFSConstants.LOOKUPABLE_SUFFIX)) {

            this.checkAndFixReturnedLookupValues(budgetConstructionForm.getNewRevenueLine(), budgetConstructionForm.getBudgetConstructionDocument());
            this.checkAndFixReturnedLookupValues(budgetConstructionForm.getNewExpenditureLine(), budgetConstructionForm.getBudgetConstructionDocument());

            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "financialObject", "financialSubObject" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionForm.getNewRevenueLine(), REFRESH_FIELDS);
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionForm.getNewExpenditureLine(), REFRESH_FIELDS);
        }

        // balance inquiry anchor is set before doing a balance inquiry
        if (budgetConstructionForm.getBalanceInquiryReturnAnchor() != null) {
            budgetConstructionForm.setAnchor(budgetConstructionForm.getBalanceInquiryReturnAnchor());
            budgetConstructionForm.setBalanceInquiryReturnAnchor(null);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * checks the passed in newLine for any data consistency problems compared to the currently loaded document
     * this is typically called after return from a lookup since the user can potentially select from other fiscal years, etc.
     *
     * @param newLine
     * @param bcDoc
     */
    protected void checkAndFixReturnedLookupValues(PendingBudgetConstructionGeneralLedger newLine, BudgetConstructionDocument bcDoc){
        if (!newLine.getUniversityFiscalYear().equals(bcDoc.getUniversityFiscalYear())){
            newLine.setUniversityFiscalYear(bcDoc.getUniversityFiscalYear());
        }
        if (!newLine.getChartOfAccountsCode().equals(bcDoc.getChartOfAccountsCode())){
            newLine.setChartOfAccountsCode(bcDoc.getChartOfAccountsCode());
        }
        if (!newLine.getAccountNumber().equals(bcDoc.getAccountNumber())){
            newLine.setAccountNumber(bcDoc.getAccountNumber());
        }
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
            docForm.populatePBGLLines();
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward adjustExpenditureLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionForm docForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDoc = docForm.getBudgetConstructionDocument();
        PendingBudgetConstructionGeneralLedger expLine = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));

        if (expLine.getAdjustmentAmount() != null) {
            this.adjustRequest(expLine);
            docForm.populatePBGLLines();
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
            boolean isEditable = docForm.isEditAllowed() && !docForm.isSystemViewOnly();
            for (PendingBudgetConstructionGeneralLedger revenueLine : revenueLines) {
                if (isEditable) {
                    revenueLine.setAdjustmentAmount(adjustmentAmount);
                    this.adjustRequest(revenueLine);
                }
            }
            if (isEditable){
                docForm.populatePBGLLines();
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
            boolean isEditable = docForm.isEditAllowed() && !docForm.isSystemViewOnly();
            // (!benecalcDisabled && !empty item.laborObject && item.laborObject.financialObjectFringeOrSalaryCode == 'F')
            for (PendingBudgetConstructionGeneralLedger expenditureLine : expenditureLines) {
                boolean isLineEditable = (isEditable && (docForm.isBenefitsCalculationDisabled() || (expenditureLine.getLaborObject() == null) || !expenditureLine.getLaborObject().getFinancialObjectFringeOrSalaryCode().equalsIgnoreCase(BCConstants.LABOR_OBJECT_FRINGE_CODE)));
                if (isLineEditable) {
                    expenditureLine.setAdjustmentAmount(adjustmentAmount);
                    this.adjustRequest(expenditureLine);
                }
            }
            if (isEditable){
                docForm.populatePBGLLines();
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
        BudgetConstructionForm tForm = (BudgetConstructionForm) form;

        boolean doAllowPullup = false;
        boolean lockNeeded = false;
        boolean prePullReadOnlyAccess;

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

        // if system view only or view only - reload to get the latest status
        // and check that the document is still below the selected POV
        if (!tForm.isEditAllowed() || tForm.isSystemViewOnly()) {

            prePullReadOnlyAccess = true;

            // now reload the document and get latest status info
            loadDocument(tForm);
            this.initAuthorization(tForm);
            if (tForm.getBudgetConstructionDocument().getOrganizationLevelCode() < Integer.parseInt(tForm.getPullupKeyCode())) {
                doAllowPullup = true;

                // if not system view only mode, we are in document view mode - we'll need a lock before the pullup
                // since by definition pullup puts the account at a full_entry level
                // and we need exclusive access to perform the pullup
                if (!tForm.isSystemViewOnly()) {
                    lockNeeded = true;
                }
            }
            else {
                // document has been moved and is either at the desired level or above
                doAllowPullup = false;
                lockNeeded = false;

                // document has been moved above the desired level - let populate through an authorization exception
                if (tForm.getBudgetConstructionDocument().getOrganizationLevelCode() > Integer.parseInt(tForm.getPullupKeyCode())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Document has already been moved above the selected level.");
                }
            }
        }
        else {
            // we are in document full_entry
            // assume we already have a lock, allow the pullup
            // and we need to ensure the user can finish editing work if after pullup system goes to system view only
            prePullReadOnlyAccess = false;
            doAllowPullup = true;
        }

        if (lockNeeded || doAllowPullup) {

            // get a fresh header to use for lock and/or pullup
            HashMap primaryKey = new HashMap();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, tForm.getDocument().getDocumentNumber());
            BudgetConstructionHeader budgetConstructionHeader = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
            if (budgetConstructionHeader == null) {
                GlobalVariables.getMessageMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Fatal, Document not found.");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            if (lockNeeded) {
                // only successful lock allows pullup here
                doAllowPullup = false;

                LockService lockService = SpringContext.getBean(LockService.class);
                BudgetConstructionLockStatus bcLockStatus = lockService.lockAccount(budgetConstructionHeader, GlobalVariables.getUserSession().getPerson().getPrincipalId());
                LockStatus lockStatus = bcLockStatus.getLockStatus();
                switch (lockStatus) {
                    case SUCCESS:
                        doAllowPullup = true;
                        break;
                    case BY_OTHER:
                        Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(bcLockStatus.getAccountLockOwner());
                        String lockerName = principal.getPrincipalName();
                        GlobalVariables.getMessageMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Locked by " + lockerName);
                        break;
                    case FLOCK_FOUND:
                        GlobalVariables.getMessageMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Funding lock found.");
                        break;
                    default:
                        GlobalVariables.getMessageMap().putError(BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS, BCKeyConstants.ERROR_BUDGET_PULLUP_DOCUMENT, "Optimistic lock or other failure during lock attempt.");
                        break;
                }
            }

            // attempt pullup
            if (doAllowPullup) {
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

                this.initAuthorization(tForm);

                // if before pullup, system was 'not system view only' goes to 'system view only' after pull
                // need to manually remove the system view only editingMode here to allow the user to save work since still
                // full_entry
                if (tForm.isEditAllowed() && !prePullReadOnlyAccess) {
                    if (tForm.isSystemViewOnly()) {
                        tForm.getEditingMode().remove(BCConstants.EditModes.SYSTEM_VIEW_ONLY);
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
        if (!tForm.isSystemViewOnly()) {
            prePushSystemViewOnly = false;

            // check editing mode at the intended level
            if (!hasEditPermission(bcDocument, tForm.getPushdownKeyCode(), GlobalVariables.getUserSession().getPerson())) {
                budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
                tForm.initializePersistedRequestAmounts();
                budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

                // repop and refresh refs - esp monthly so jsp can properly display state
                // tForm.populatePBGLLines();

                unlockNeeded = true;
            }
            doAllowPushdown = true;
        }
        else {
            prePushSystemViewOnly = true;

            // reload document to get most up-to-date status and recheck that we still have FULL_ENTRY access
            // anything else means the document was moved by someone else and we may no longer even have read access
            loadDocument(tForm);
            this.initAuthorization(tForm);
            if (tForm.isEditAllowed()) {
                doAllowPushdown = true;
            }
            else {
                // document has moved
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_BUDGET_PUSHDOWN_DOCUMENT, "Full Access Control Lost.");
            }
        }

        // gets here if editing and pushing to view and doc is valid and persisted
        // or we are pushing from edit to edit
        // or we are in system view only
        if (doAllowPushdown) {

            HashMap primaryKey = new HashMap();
            primaryKey.put(KFSPropertyConstants.DOCUMENT_NUMBER, tForm.getDocument().getDocumentNumber());

            BudgetConstructionHeader budgetConstructionHeader = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionHeader.class, primaryKey);
            if (budgetConstructionHeader != null) {
                budgetConstructionHeader.setOrganizationLevelCode(Integer.parseInt(tForm.getPushdownKeyCode()));
                if (Integer.parseInt(tForm.getPushdownKeyCode()) == 0) {
                    budgetConstructionHeader.setOrganizationLevelChartOfAccountsCode(null);
                    budgetConstructionHeader.setOrganizationLevelOrganizationCode(null);
                }
                else {
                    budgetConstructionHeader.setOrganizationLevelChartOfAccountsCode(tForm.getAccountOrgHierLevels().get(Integer.parseInt(tForm.getPushdownKeyCode())).getOrganizationChartOfAccountsCode());
                    budgetConstructionHeader.setOrganizationLevelOrganizationCode(tForm.getAccountOrgHierLevels().get(Integer.parseInt(tForm.getPushdownKeyCode())).getOrganizationCode());
                }
            }

            // unlock if needed (which stores) - otherwise store the new level
            if (unlockNeeded) {

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

            this.initAuthorization(tForm);

            // if before push, system is 'not system view only' goes to 'system view only' after push
            // need to manually remove the system view only editingMode here to allow the user to save work if still full_entry
            if (tForm.isEditAllowed()) {
                if (tForm.isSystemViewOnly() && !prePushSystemViewOnly) {
                    tForm.getEditingMode().remove(BCConstants.EditModes.SYSTEM_VIEW_ONLY);
                }
            }

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Checks whether the current user would have access for the given budget document for the given organization level code
     *
     * @param document current bc document
     * @param orgLevelCode organization level code for access check
     * @param user user to check access for
     * @return true if user would have edit permission, false otherwise
     */
    protected boolean hasEditPermission(BudgetConstructionDocument document, String orgLevelCode, Person user) {
        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(document);

        Map<String,String> roleQualifiers = new HashMap<String,String>();
        roleQualifiers.put(BCPropertyConstants.ORGANIZATION_LEVEL_CODE, orgLevelCode);

        List<BudgetConstructionAccountOrganizationHierarchy> accountOrganizationHierarchy = SpringContext.getBean(BudgetDocumentService.class).retrieveOrBuildAccountOrganizationHierarchy(document.getUniversityFiscalYear(), document.getChartOfAccountsCode(), document.getAccountNumber());
        for (BudgetConstructionAccountOrganizationHierarchy accountOrganization : accountOrganizationHierarchy) {
            if (accountOrganization.getOrganizationLevelCode().intValue() == Integer.parseInt(orgLevelCode)) {
                roleQualifiers.put(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE, accountOrganization.getOrganizationChartOfAccountsCode());
                roleQualifiers.put(KfsKimAttributes.ORGANIZATION_CODE, accountOrganization.getOrganizationCode());
            }
        }

        return documentAuthorizer.isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId(), null, roleQualifiers);
    }

    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = tForm.getBudgetConstructionDocument();

        // when we return from the lookup, our next request's method to call is going to be refresh
        tForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        if (tForm.isEditAllowed() && !tForm.isSystemViewOnly()) {
            BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

            budgetDocumentService.saveDocumentNoWorkflow(bcDocument);
            budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
            tForm.initializePersistedRequestAmounts();

            // repop and refresh refs - esp monthly so jsp can properly display state
            // tForm.populatePBGLLines();
        }

        // gets here if rules passed and doc detail gets persisted and refreshed
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.MONTHLY_BUDGET_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put("documentNumber", tForm.getDocument().getDocumentNumber());
        parameters.put("universityFiscalYear", tForm.getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", tForm.getChartOfAccountsCode());
        parameters.put("accountNumber", tForm.getAccountNumber());
        parameters.put("subAccountNumber", tForm.getSubAccountNumber());
        parameters.put(BCPropertyConstants.MAIN_WINDOW, (tForm.isMainWindow() ? "true" : "false"));

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.REPORT_RUNNER_ACTION, parameters);
        this.setupDocumentExit();
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performPercentChange(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm tForm = (BudgetConstructionForm) form;
        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Percent Change");

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
        tForm.initializePersistedRequestAmounts();
        // budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

        BudgetConstructionMonthlyBudgetsCreateDeleteService monthlyBudgetService = SpringContext.getBean(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);

        if (isRevenue) {
            monthlyBudgetService.spreadBudgetConstructionMonthlyBudgetsRevenue(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber());
        }
        else {
            // service returns true if benefit eligible monthly lines exist
            if (monthlyBudgetService.spreadBudgetConstructionMonthlyBudgetsExpenditure(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber())) {
                bcDocument.setMonthlyBenefitsCalcNeeded(true);
                // budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);
            }
        }
        budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

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
        tForm.initializePersistedRequestAmounts();
        // budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

        BudgetConstructionMonthlyBudgetsCreateDeleteService monthlyBudgetService = SpringContext.getBean(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);

        if (isRevenue) {
            monthlyBudgetService.deleteBudgetConstructionMonthlyBudgetsRevenue(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber());
        }
        else {
            monthlyBudgetService.deleteBudgetConstructionMonthlyBudgetsExpenditure(bcDocument.getDocumentNumber(), bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode(), bcDocument.getAccountNumber(), bcDocument.getSubAccountNumber());
        }
        budgetDocumentService.calculateBenefitsIfNeeded(bcDocument);

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
            tForm.initializePersistedRequestAmounts();
            budgetDocumentService.calculateBenefits(bcDocument);

            // repop and refresh refs - esp monthly so jsp can properly display state
            // tForm.populatePBGLLines();

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void adjustRequest(PendingBudgetConstructionGeneralLedger pbglLine) {

        KualiInteger baseAmount = pbglLine.getFinancialBeginningBalanceLineAmount();
        if (baseAmount.isNonZero()) {
            KualiDecimal percent = pbglLine.getAdjustmentAmount();
            BigDecimal adjustedAmount = baseAmount.multiply(percent).divide(KFSConstants.ONE_HUNDRED);

            KualiInteger requestAmount = new KualiInteger(adjustedAmount).add(baseAmount);
            pbglLine.setAccountLineAnnualBalanceAmount(requestAmount);
        }

    }
}
