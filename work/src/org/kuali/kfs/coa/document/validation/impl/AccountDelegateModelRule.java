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
package org.kuali.kfs.coa.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegateModel;
import org.kuali.kfs.coa.businessobject.AccountDelegateModelDetail;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements the business rules specific to the {@link OrganizationRoutingModelName} Maintenance Document.
 */
public class AccountDelegateModelRule extends KfsMaintenanceDocumentRuleBase {

    protected AccountDelegateModel model;

    /**
     * This method sets the convenience objects like model, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        model = (AccountDelegateModel) super.getNewBo();
        for (AccountDelegateModelDetail delegateModel : model.getAccountDelegateModelDetails()) {
            delegateModel.refreshNonUpdateableReferences();
        }
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link AccountDelegateModelRule#checkSimpleRules(OrganizationRoutingModelName)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        return checkSimpleRules(document, this.model);
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link AccountDelegateModelRule#checkSimpleRules(OrganizationRoutingModelName)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        return checkSimpleRules(document, this.model);
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link AccountDelegateModelRule#checkSimpleRules(OrganizationRoutingModelName)}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        checkSimpleRules(document, this.model);
        return true;
    }

    /**
     * This method calls 
     * <ul>
     * <li>{@link AccountDelegateModelRule#checkSimpleRulesForOrganizationRoutingModel(OrganizationRoutingModelName, OrganizationRoutingModel)}</li>
     * </ul>
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        setupConvenienceObjects();
        final FinancialSystemDocumentTypeService documentService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
        return checkSimpleRulesForOrganizationRoutingModel(document, this.model, (AccountDelegateModelDetail) line, documentService);
    }

    /**
     * Checks the given rules against the entire Organization Routing Model parent.
     * 
     * @param globalDelegateTemplate the Organization Routing Model parent to check
     * @return true if document passes all rules, false if otherwise
     */
    protected boolean checkSimpleRules(MaintenanceDocument document, AccountDelegateModel globalDelegateTemplate) {
        boolean success = true;

        success &= checkModelNameHasAtLeastOneModel(globalDelegateTemplate);

        int line = 0;
        final FinancialSystemDocumentTypeService documentService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
        for (AccountDelegateModelDetail delegateModel : globalDelegateTemplate.getAccountDelegateModelDetails()) {
            GlobalVariables.getMessageMap().addToErrorPath(MAINTAINABLE_ERROR_PATH + ".accountDelegateModelDetails[" + line + "].");
            success &= checkSimpleRulesForOrganizationRoutingModel(document, globalDelegateTemplate, delegateModel, documentService);
            GlobalVariables.getMessageMap().addToErrorPath(MAINTAINABLE_ERROR_PATH + ".accountDelegateModelDetails[" + line + "].");
            line++;
        }
        
        return success;
    }

     /**
     * This method checks a series of basic rules for a single org routing model.
     * 
     * @return true if model passes all the checks, false if otherwise
     */
    protected boolean checkSimpleRulesForOrganizationRoutingModel(MaintenanceDocument document, AccountDelegateModel globalDelegateTemplate, AccountDelegateModelDetail delegateModel, FinancialSystemDocumentTypeService documentService) {
        boolean success = true;

        if (delegateModel.isActive()) {
            success &= checkStartDate(delegateModel);
            success &= checkDelegateFromAmountPositive(delegateModel);
            success &= checkDelegateToAmountGreaterThanFromAmount(delegateModel);
            success &= checkDelegateUserRules(document, delegateModel);
            success &= checkPrimaryRoutePerDocType(globalDelegateTemplate, delegateModel);
            success &= checkDelegateDocumentTypeCode(delegateModel.getFinancialDocumentTypeCode(), documentService);
        }

        return success;
    }

    private boolean checkStartDate(AccountDelegateModelDetail delegateModel) {
        boolean success = true;
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        if (delegateModel.getAccountDelegateStartDate().before(ts)) success = false;
        GlobalVariables.getMessageMap().putError("accountDelegateStartDate", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_STARTDATE_IN_PAST, new String[0]);
        return success;
    }


    /**
     * This method makes certain that the collection of account delegates in the "mo itdel" has at least one account delegate
     * template in it.
     * 
     * @param globalDelegateTemplate the account delegate model to check
     * @return true if account delegate model has at least one account delegate template in it
     */
    protected boolean checkModelNameHasAtLeastOneModel(AccountDelegateModel globalDelegateTemplate) {
        boolean success = true;
        if (globalDelegateTemplate.getAccountDelegateModelDetails().size() == 0) {
            success = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "add.accountDelegateModelDetails.financialDocumentTypeCode", KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_DELEGATE, new String[0]);
        }
        return success;
    }

    /**
     * This method checks that the account delegate model has at least one active "model" within it.
     * 
     * @param globalDelegateTemplate the account delegate model to check
     * @return true if account delegate model has at least one active model in it.
     */
    // method not currently in use, as per Bill's comments in KULRNE-4805
    protected boolean checkModelNameHasAtLeastOneActiveModel(AccountDelegateModel globalDelegateTemplate) {
        boolean success = true;
        int activeModelCount = 0;

        for (AccountDelegateModelDetail mdl : globalDelegateTemplate.getAccountDelegateModelDetails()) {
            if (mdl.isActive()) {
                activeModelCount++;
            }
        }

        if (activeModelCount == 0) {
            success = false;
            if (globalDelegateTemplate.getAccountDelegateModelDetails().size() == 0) {
                GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "add.accountDelegateModelDetails.active", KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_ACTIVE_DELEGATE, new String[0]);
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "accountDelegateModelDetails[0].active", KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_ACTIVE_DELEGATE, new String[0]);
            }
        }
        return success;
    }

    /**
     * Checks that if approval from amount is not null, then it is positive
     * 
     * @param delegateModel Organization Routing Model to check
     * @return true if Organization Routing Model passes the checks, false if otherwise
     */
    protected boolean checkDelegateFromAmountPositive(AccountDelegateModelDetail delegateModel) {
        boolean result = true;
        if (!ObjectUtils.isNull(delegateModel.getApprovalFromThisAmount())) {
            if (delegateModel.getApprovalFromThisAmount().isLessThan(KualiDecimal.ZERO)) {
                GlobalVariables.getMessageMap().putError("approvalFromThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE, new String[0]);
                result = false;
            }
        }
        return result;
    }

    /**
     * Checks that if approval from amount is null, that approval to this amount is null or zero; and then checks that approval to
     * amount is greater than or equal to approval from amount.
     * 
     * @param delegateModel Organization Routing Model to check
     * @return true if the Organization Routing Model passes the checks, false if otherwise
     */
    protected boolean checkDelegateToAmountGreaterThanFromAmount(AccountDelegateModelDetail delegateModel) {
        boolean result = true;
        if (!ObjectUtils.isNull(delegateModel.getApprovalFromThisAmount())) {
            if (!ObjectUtils.isNull(delegateModel.getApprovalToThisAmount())) {
                if (delegateModel.getApprovalToThisAmount().isLessThan(delegateModel.getApprovalFromThisAmount())) {
                    GlobalVariables.getMessageMap().putError("approvalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO, new String[0]);
                    result = false;
                }
            }
        }
        if (!ObjectUtils.isNull(delegateModel.getApprovalToThisAmount()) && delegateModel.getApprovalToThisAmount().isLessThan(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError("approvalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO, new String[0]);
            result = false;
        }
        return result;
    }

    /**
     * Checks that the account delegate listed exists in the system, and that user has an active status and is a professional type
     * 
     * @param delegateModel the Organization Routing Model to check
     * @return true if delegate user passes the rules described above; false if they fail
     */
    protected boolean checkDelegateUserRules(MaintenanceDocument document, AccountDelegateModelDetail delegateModel) {
        boolean success = true;

        // refresh account delegate
        try {
            delegateModel.setAccountDelegate(SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPerson(delegateModel.getAccountDelegateUniversalId()));
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("User Not Found Exception: " + e);
            }
        }

        // user must exist
        if ((delegateModel.getAccountDelegate() == null) || (delegateModel.getAccountDelegate().getPrincipalId() == null)){
            GlobalVariables.getMessageMap().putError("accountDelegate.principalName", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST, new String[0]);
            success = false;
        }

        if (success) {
            if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorized(document, KFSConstants.CoreModuleNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE, delegateModel.getAccountDelegate().getPrincipalId())) {
                super.putFieldError("accountDelegate.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {delegateModel.getAccountDelegate().getName(), KFSConstants.CoreModuleNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER_DELEGATE});
                success = false;
            }
        }

        return success;
    }

    /**
     * This method validates the rule that says there can be only one PrimaryRoute delegate for each given docType. It checks the
     * delegateGlobalToTest against the list, to determine whether adding this new delegateGlobalToTest would violate any
     * PrimaryRoute business rule violations. If any of the incoming variables is null or empty, the method will do nothing, and
     * return Null. It will only process the business rules if there is sufficient data to do so.
     * 
     * @param delegateGlobalToTest A delegateGlobal line that you want to test against the list.
     * @param delegateGlobals A List of delegateGlobal items that is being tested against.
     * @return true if model, delegate template or org routing model is null, or if the primary routing indicator is set to false or the doc type code is empty
     * otherwise it checks to make sure that there is indeed one model marked as the primary route
     */
    protected boolean checkPrimaryRoutePerDocType(AccountDelegateModel globalDelegateTemplate, AccountDelegateModelDetail delegateModel) {
        boolean success = true;

        // exit immediately if the adding line isnt a Primary routing
        if (delegateModel == null || globalDelegateTemplate == null || globalDelegateTemplate.getAccountDelegateModelDetails().isEmpty()) {
            return success;
        }
        if (!delegateModel.getAccountDelegatePrimaryRoutingIndicator()) {
            return success;
        }
        if (StringUtils.isBlank(delegateModel.getFinancialDocumentTypeCode())) {
            return success;
        }

        // at this point, the delegateGlobal being added is a Primary for ALL docTypes, so we need to
        // test whether any in the existing list are also Primary, regardless of docType
        String docType = delegateModel.getFinancialDocumentTypeCode();
        for (AccountDelegateModelDetail currDelegateModel : globalDelegateTemplate.getAccountDelegateModelDetails()) {
            if (currDelegateModel.isActive() && !delegateModel.equals(currDelegateModel) && currDelegateModel.getAccountDelegatePrimaryRoutingIndicator() && delegateModel.getFinancialDocumentTypeCode().equals(currDelegateModel.getFinancialDocumentTypeCode())) {
                success = false;
                GlobalVariables.getMessageMap().putError("accountDelegatePrimaryRoutingIndicator", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_DELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE, new String[0]);
            }
        }

        return success;
    }
    
    /**
     * Validates the document type code for the delegate, to make sure it is a Financial System document type code
     * @param documentTypeCode the document type code to check
     * @param delegateService a helpful instance of the delegate service, so new ones don't have to be created all the time
     * @return true if the document type code is valid, false otherwise
     */
    protected boolean checkDelegateDocumentTypeCode(String documentTypeCode, FinancialSystemDocumentTypeService documentService) {
        if (!documentService.isFinancialSystemDocumentType(documentTypeCode)) {
            putFieldError("financialDocumentTypeCode", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_INVALID_DOC_TYPE, new String[] { documentTypeCode, KFSConstants.ROOT_DOCUMENT_TYPE });
            return false;
        }
        return true;
    }

}

