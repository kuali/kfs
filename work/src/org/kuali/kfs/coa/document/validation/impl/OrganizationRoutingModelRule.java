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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.businessobject.AccountDelegateModelDetail;
import org.kuali.kfs.coa.businessobject.AccountDelegateModel;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class implements the business rules specific to the {@link OrganizationRoutingModelName} Maintenance Document.
 */
public class OrganizationRoutingModelRule extends MaintenanceDocumentRuleBase {

    private AccountDelegateModel model;

    private static final String ORG_ROUTING_MODEL_PREFIX = "organizationRoutingModel";

    /**
     * Constructs a OrganizationRoutingModelRule
     */
    public OrganizationRoutingModelRule() {
    }

    /**
     * This method sets the convenience objects like model, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        model = (AccountDelegateModel) super.getNewBo();
        for (AccountDelegateModelDetail delegateModel : model.getOrganizationRoutingModel()) {
            delegateModel.refreshNonUpdateableReferences();
        }
    }

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link OrganizationRoutingModelRule#checkSimpleRules(OrganizationRoutingModelName)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        return checkSimpleRules(this.model);
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link OrganizationRoutingModelRule#checkSimpleRules(OrganizationRoutingModelName)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        return checkSimpleRules(this.model);
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link OrganizationRoutingModelRule#checkSimpleRules(OrganizationRoutingModelName)}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        checkSimpleRules(this.model);
        return true;
    }

    /**
     * This method calls 
     * <ul>
     * <li>{@link OrganizationRoutingModelRule#checkSimpleRulesForOrganizationRoutingModel(OrganizationRoutingModelName, OrganizationRoutingModel)}</li>
     * </ul>
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        setupConvenienceObjects();
        return checkSimpleRulesForOrganizationRoutingModel(this.model, (AccountDelegateModelDetail) line);
    }

    /**
     * Checks the given rules against the entire Organization Routing Model parent.
     * 
     * @param globalDelegateTemplate the Organization Routing Model parent to check
     * @return true if document passes all rules, false if otherwise
     */
    protected boolean checkSimpleRules(AccountDelegateModel globalDelegateTemplate) {
        boolean success = true;

        success &= checkModelNameHasAtLeastOneModel(globalDelegateTemplate);

        int line = 0;
        for (AccountDelegateModelDetail delegateModel : globalDelegateTemplate.getOrganizationRoutingModel()) {
            GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_ERROR_PATH + ".organizationRoutingModel[" + line + "].");
            success &= checkSimpleRulesForOrganizationRoutingModel(globalDelegateTemplate, delegateModel);
            GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_ERROR_PATH + ".organizationRoutingModel[" + line + "].");
            line++;
        }
        return success;
    }

    /**
     * This method checks a series of basic rules for a single org routing model.
     * 
     * @return true if model passes all the checks, false if otherwise
     */
    protected boolean checkSimpleRulesForOrganizationRoutingModel(AccountDelegateModel globalDelegateTemplate, AccountDelegateModelDetail delegateModel) {
        boolean success = true;

        if (delegateModel.isActive()) {
            success &= checkDelegateFromAmountPositive(delegateModel);
            success &= checkDelegateToAmountNotNull(delegateModel);
            success &= checkDelegateToAmountGreaterThanFromAmount(delegateModel);
            success &= checkDelegateUserRules(delegateModel);
            success &= checkPrimaryRoutePerDocType(globalDelegateTemplate, delegateModel);
        }

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
        if (globalDelegateTemplate.getOrganizationRoutingModel().size() == 0) {
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "add.organizationRoutingModel.financialDocumentTypeCode", KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_DELEGATE, new String[0]);
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

        for (AccountDelegateModelDetail mdl : globalDelegateTemplate.getOrganizationRoutingModel()) {
            if (mdl.isActive()) {
                activeModelCount++;
            }
        }

        if (activeModelCount == 0) {
            success = false;
            if (globalDelegateTemplate.getOrganizationRoutingModel().size() == 0) {
                GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "add.organizationRoutingModel.active", KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_ACTIVE_DELEGATE, new String[0]);
            }
            else {
                GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "organizationRoutingModel[0].active", KFSKeyConstants.ERROR_DOCUMENT_DELEGATE_CHANGE_NO_ACTIVE_DELEGATE, new String[0]);
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
                GlobalVariables.getErrorMap().putError("approvalFromThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE, new String[0]);
                result = false;
            }
        }
        return result;
    }

    /**
     * Checks that if approval from amount is not null then approval to amount is also not null
     * 
     * @param delegateModel Organization Routing Model to check
     * @return true if Organization Routing Model passes all checks, false if otherwise
     */
    protected boolean checkDelegateToAmountNotNull(AccountDelegateModelDetail delegateModel) {
        boolean result = true;
        if (!ObjectUtils.isNull(delegateModel.getApprovalFromThisAmount()) && ObjectUtils.isNull(delegateModel.getApprovalToThisAmount())) {
            GlobalVariables.getErrorMap().putError("approvalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO, new String[0]);
            result = false;
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
        if (ObjectUtils.isNull(delegateModel.getApprovalFromThisAmount())) {
            if (!ObjectUtils.isNull(delegateModel.getApprovalToThisAmount()) && !delegateModel.getApprovalToThisAmount().equals(KualiDecimal.ZERO)) {
                GlobalVariables.getErrorMap().putError("approvalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO, new String[0]);
                result = false;
            }
        }
        else if (!ObjectUtils.isNull(delegateModel.getApprovalToThisAmount())) {
            if (delegateModel.getApprovalToThisAmount().isLessThan(delegateModel.getApprovalFromThisAmount())) {
                GlobalVariables.getErrorMap().putError("approvalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO, new String[0]);
                result = false;
            }
        }
        return result;
    }

    /**
     * Checks that the account delegate listed exists in the system, and that user has an active status and is a professional type
     * 
     * @param delegateModel the Organization Routing Model to check
     * @return true if delegate user passes the rules described above; false if they fail
     */
    protected boolean checkDelegateUserRules(AccountDelegateModelDetail delegateModel) {
        boolean success = true;

        // refresh account delegate
        try {
            delegateModel.setAccountDelegate(SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPerson(delegateModel.getAccountDelegateUniversalId()));
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("User Not Found Exception: " + e);
            }
        }

        // user must exist
        if (delegateModel.getAccountDelegate() == null) {
            GlobalVariables.getErrorMap().putError("accountDelegate.principalName", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST, new String[0]);
            success = false;
        }

        if (success) {
            Person user = delegateModel.getAccountDelegate();

            if (!SpringContext.getBean(FinancialSystemUserService.class).isActiveFinancialSystemUser(user)) {
                GlobalVariables.getErrorMap().putError("accountDelegate.principalName", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE, new String[0]);
                success = false;
            }

            String principalId = user.getPrincipalId();
            String namespaceCode = KFSConstants.ParameterNamespaces.CHART;
            String permissionName = KFSConstants.PermissionName.SERVE_AS_ACCOUNT_MANAGER.name;
            
            IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
            Boolean isAuthorized = identityManagementService.hasPermission(principalId, namespaceCode, permissionName, null);
            if (!isAuthorized) {
                GlobalVariables.getErrorMap().putError("accountDelegate.principalName", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_PROFESSIONAL, new String[0]);
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
        if (delegateModel == null || globalDelegateTemplate == null || globalDelegateTemplate.getOrganizationRoutingModel().isEmpty()) {
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
        for (AccountDelegateModelDetail currDelegateModel : globalDelegateTemplate.getOrganizationRoutingModel()) {
            if (currDelegateModel.isActive() && !delegateModel.equals(currDelegateModel) && currDelegateModel.getAccountDelegatePrimaryRoutingIndicator() && delegateModel.getFinancialDocumentTypeCode().equals(currDelegateModel.getFinancialDocumentTypeCode())) {
                success = false;
                GlobalVariables.getErrorMap().putError("accountDelegatePrimaryRoutingIndicator", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_DELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE, new String[0]);
            }
        }

        return success;
    }

}

