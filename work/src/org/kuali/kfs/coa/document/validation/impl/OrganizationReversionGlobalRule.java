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

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalDetail;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalOrganization;
import org.kuali.kfs.coa.businessobject.options.OrganizationReversionCodeValuesFinder;
import org.kuali.kfs.coa.document.OrganizationReversionGlobalMaintainableImpl;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class implements the business rules for {@link OrganizationReversionGlobal}
 */
public class OrganizationReversionGlobalRule extends GlobalDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalRule.class);
    protected OrganizationReversionGlobal globalOrganizationReversion;
    protected OrganizationReversionService organizationReversionService;
    protected ObjectCodeService objectCodeService;

    /**
     * 
     * Constructs a OrganizationReversionGlobalRule
     * Pseudo-injects services 
     */
    public OrganizationReversionGlobalRule() {
        super();
        setOrganizationReversionService(SpringContext.getBean(OrganizationReversionService.class));
        setObjectCodeService(SpringContext.getBean(ObjectCodeService.class));
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        this.globalOrganizationReversion = (OrganizationReversionGlobal) super.getNewBo();
        for (OrganizationReversionGlobalDetail detail : this.globalOrganizationReversion.getOrganizationReversionGlobalDetails()) {
            detail.refreshNonUpdateableReferences();
        }
        for (OrganizationReversionGlobalOrganization org : this.globalOrganizationReversion.getOrganizationReversionGlobalOrganizations()) {
            org.refreshNonUpdateableReferences();
        }
    }

    /**
     * Calls the basic rules check on document save:
     * <ul>
     * <li>{@link OrganizationReversionGlobalRule#checkSimpleRules(OrganizationReversionGlobal)}</li>
     * </ul>
     * Does not fail on rules failure
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkSimpleRules(getGlobalOrganizationReversion());
        return true; // always return true on save
    }

    /**
     * Calls the basic rules check on document approval:
     * <ul>
     * <li>{@link OrganizationReversionGlobalRule#checkSimpleRules(OrganizationReversionGlobal)}</li>
     * </ul>
     * Fails on rules failure
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        return checkSimpleRules(getGlobalOrganizationReversion());
    }

    /**
     * Calls the basic rules check on document routing:
     * <ul>
     * <li>{@link OrganizationReversionGlobalRule#checkSimpleRules(OrganizationReversionGlobal)}</li>
     * </ul>
     * Fails on rules failure
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return checkSimpleRules(getGlobalOrganizationReversion());
    }

    /**
     * This performs rules checks whenever a new {@link OrganizationReversionGlobalDetail} or {@link OrganizationReversionGlobalOrganization} is added
     * <p>
     * This includes:
     * <ul>
     * <li>{@link OrganizationReversionGlobalRule#checkDetailObjectCodeValidity(OrganizationReversionGlobal, OrganizationReversionGlobalDetail)}</li>
     * <li>{@link OrganizationReversionGlobalRule#checkDetailObjectReversionCodeValidity(OrganizationReversionGlobalDetail)}</li>
     * <li>ensure that the chart of accounts code and organization codes for {@link OrganizationReversionGlobalOrganization} are not empty values</li>
     * <li>{@link OrganizationReversionGlobalRule#checkAllObjectCodesForValidity(OrganizationReversionGlobal, OrganizationReversionGlobalOrganization)}</li>
     * <li>{@link OrganizationReversionGlobalRule#checkOrganizationChartValidity(OrganizationReversionGlobalOrganization)</li>
     * <li>{@link OrganizationReversionGlobalRule#checkOrganizationValidity(OrganizationReversionGlobalOrganization)</li>
     * <li>{@link OrganizationReversionGlobalRule#checkOrganizationReversionForOrganizationExists(OrganizationReversionGlobal, OrganizationReversionGlobalOrganization)</li>
     * <li>{@link OrganizationReversionGlobalRule#checkOrganizationIsNotAmongOrgRevOrganizations(OrganizationReversionGlobal, OrganizationReversionGlobalOrganization)</li>
     * </ul>
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = true;
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal) ((OrganizationReversionGlobalMaintainableImpl) document.getNewMaintainableObject()).getBusinessObject();
        if (line instanceof OrganizationReversionGlobalDetail) {
            OrganizationReversionGlobalDetail detail = (OrganizationReversionGlobalDetail) line;
            success &= checkDetailObjectCodeValidity(globalOrgRev, detail);
            success &= checkDetailObjectReversionCodeValidity(detail);
        }
        else if (line instanceof OrganizationReversionGlobalOrganization) {
            OrganizationReversionGlobalOrganization org = (OrganizationReversionGlobalOrganization) line;
            if (!checkEmptyValue(org.getChartOfAccountsCode())) {
                GlobalVariables.getMessageMap().putError("chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, "Chart of Accounts Code");
            }
            if (!checkEmptyValue(org.getOrganizationCode())) {
                GlobalVariables.getMessageMap().putError("organizationCode", KFSKeyConstants.ERROR_REQUIRED, "Organization Code");
            }
            if (success) {
                success &= checkAllObjectCodesForValidity(globalOrgRev, org);
                success &= checkOrganizationChartValidity(org);
                success &= checkOrganizationValidity(org);
                success &= checkOrganizationReversionForOrganizationExists(globalOrgRev, org);
                success &= checkOrganizationIsNotAmongOrgRevOrganizations(globalOrgRev, org);
            }
        }
        return success;
    }

    /**
     * Convenient convenience method to test all the simple rules in one go. Including:
     * <ul>
     * <li>{@link OrganizationReversionGlobalRule#checkBudgetReversionAccountPair(OrganizationReversionGlobal)}</li>
     * <li>{@link OrganizationReversionGlobalRule#checkCashReversionAccountPair(OrganizationReversionGlobal)}</li>
     * <li>{@link OrganizationReversionGlobalRule#areAllDetailsValid(OrganizationReversionGlobal)}</li>
     * <li>{@link OrganizationReversionGlobalRule#areAllOrganizationsValid(OrganizationReversionGlobal)</li>
     * </ul>
     * @param globalOrgRev the global organization reversion to check
     * @return true if the new global organization reversion passes all tests, false if it deviates even a tiny little bit
     */
    public boolean checkSimpleRules(OrganizationReversionGlobal globalOrgRev) {
        boolean success = true;

        success &= checkBudgetReversionAccountPair(globalOrgRev);
        success &= checkCashReversionAccountPair(globalOrgRev);

        success &= areAllDetailsValid(globalOrgRev);
        success &= areAllOrganizationsValid(globalOrgRev);

        return success;
    }

    /**
     * This method makes sure that if one part of the Budget Reversion Chart/Account pair is specified, both are specified, or an
     * error is thrown.
     * 
     * @param globalOrgRev the Global Organization Reversion to check
     * @return true if budget reversion chart/account pair is specified correctly, false if otherwise
     */
    public boolean checkBudgetReversionAccountPair(OrganizationReversionGlobal globalOrgRev) {
        boolean success = true;
        if ((!StringUtils.isBlank(globalOrgRev.getBudgetReversionChartOfAccountsCode()) && StringUtils.isBlank(globalOrgRev.getBudgetReversionAccountNumber())) || (StringUtils.isBlank(globalOrgRev.getBudgetReversionChartOfAccountsCode()) && !StringUtils.isBlank(globalOrgRev.getBudgetReversionAccountNumber()))) {
            success = false;
            GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PREFIX + "budgetReversionChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_BUDGET_REVERSION_INCOMPLETE, new String[] {});
        }
        return success;
    }

    /**
     * This method makes sure that if one part of the Cash Reversion Chart/Account pair is specified, both are specified, or an
     * error is thrown.
     * 
     * @param globalOrgRev the Global Organization Reversion to check
     * @return true if cash reversion chart/account pair is specified correctly, false if otherwise
     */
    public boolean checkCashReversionAccountPair(OrganizationReversionGlobal globalOrgRev) {
        boolean success = true;
        if ((!StringUtils.isBlank(globalOrgRev.getCashReversionFinancialChartOfAccountsCode()) && StringUtils.isBlank(globalOrgRev.getCashReversionAccountNumber())) || (StringUtils.isBlank(globalOrgRev.getCashReversionFinancialChartOfAccountsCode()) && !StringUtils.isBlank(globalOrgRev.getCashReversionAccountNumber()))) {
            success = false;
            GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PREFIX + "cashReversionFinancialChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_CASH_REVERSION_INCOMPLETE, new String[] {});
        }
        return success;
    }

    /**
     * Tests if all of the {@link OrganizationReversionGlobalDetail} objects associated with the given global organization reversion are
     * valid.
     * 
     * @param globalOrgRev the global organization reversion to check
     * @return true if valid, false otherwise
     */
    public boolean areAllDetailsValid(OrganizationReversionGlobal globalOrgRev) {
        boolean success = true;
        for (int i = 0; i < globalOrgRev.getOrganizationReversionGlobalDetails().size(); i++) {
            OrganizationReversionGlobalDetail detail = globalOrgRev.getOrganizationReversionGlobalDetails().get(i);
            
            String errorPath = MAINTAINABLE_ERROR_PREFIX + "organizationReversionGlobalDetails[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);

            if (!StringUtils.isBlank(detail.getOrganizationReversionObjectCode()) && !StringUtils.isBlank(detail.getOrganizationReversionCode())) {
                success &= this.checkDetailOrgReversionCategoryValidity(detail);
                success &= this.checkDetailObjectCodeValidity(globalOrgRev, detail);
                success &= this.checkDetailObjectReversionCodeValidity(detail);
            }
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
        }
        return success;
    }

    /**
     * Tests if the Organization Reversion Category existed in the database and was active.
     * 
     * @param detail OrganizationReversionGlobalDetail to check
     * @return true if the category is valid, false if otherwise
     */
    public boolean checkDetailOrgReversionCategoryValidity(OrganizationReversionGlobalDetail detail) {
        boolean success = true;
        if (StringUtils.isBlank(detail.getOrganizationReversionCategoryCode())) {
            success = false;
            GlobalVariables.getMessageMap().putError("organizationReversionCategoryCode", KFSKeyConstants.ERROR_REQUIRED, new String[] {});
        }
        else {
            detail.refreshReferenceObject("organizationReversionCategory");
            if (detail.getOrganizationReversionCategory() == null || !detail.getOrganizationReversionCategory().isActive()) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationReversionCategoryCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_ORG_REVERSION_CATEGORY, new String[] { detail.getOrganizationReversionCategoryCode() });
            }
        }
        return success;
    }

    /**
     * For each organization, tests if the object code in the detail exists in the system and is active
     * 
     * @param detail the OrganizationReversionGlobalDetail to check
     * @return true if it is valid, false if otherwise
     */
    public boolean checkDetailObjectCodeValidity(OrganizationReversionGlobal globalOrgRev, OrganizationReversionGlobalDetail detail) {
        boolean success = true;
        for (OrganizationReversionGlobalOrganization org : globalOrgRev.getOrganizationReversionGlobalOrganizations()) {
            if (!validObjectCode(globalOrgRev.getUniversityFiscalYear(), org.getChartOfAccountsCode(), detail.getOrganizationReversionObjectCode())) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationReversionObjectCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_OBJECT_CODE_INVALID, new String[] { globalOrgRev.getUniversityFiscalYear().toString(), org.getChartOfAccountsCode(), detail.getOrganizationReversionObjectCode(), org.getChartOfAccountsCode(), org.getOrganizationCode() });
            }
        }
        return success;
    }

    /**
     * This method loops through each of the OrganizationReversionGlobalDetail objects, checking that the entered object codes for
     * each of them are compatible with the OrganizationReversionGlobalOrganization specified.
     * 
     * @param globalOrgRev the global organization reversion to check
     * @param org the OrganizationReversionGlobalOrganization with a new chart to check against all of the object codes
     * @return true if there are no conflicts, false if otherwise
     */
    public boolean checkAllObjectCodesForValidity(OrganizationReversionGlobal globalOrgRev, OrganizationReversionGlobalOrganization org) {
        boolean success = true;
        for (OrganizationReversionGlobalDetail detail : globalOrgRev.getOrganizationReversionGlobalDetails()) {
            if (!validObjectCode(globalOrgRev.getUniversityFiscalYear(), org.getChartOfAccountsCode(), detail.getOrganizationReversionObjectCode())) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_OBJECT_CODE_INVALID, new String[] { globalOrgRev.getUniversityFiscalYear().toString(), org.getChartOfAccountsCode(), detail.getOrganizationReversionObjectCode(), org.getChartOfAccountsCode(), org.getOrganizationCode() });
            }
        }
        return success;
    }

    /**
     * This method checks if an object code with the given primary key fields exists in the database.
     * 
     * @param universityFiscalYear the university fiscal year of the object code
     * @param chartOfAccountsCode the chart of accounts code of the object code
     * @param objectCode the object code itself
     * @return true if it exists (or was not filled in to begin with), false if otherwise
     */
    public boolean validObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String objectCode) {
        if (!StringUtils.isBlank(objectCode) && universityFiscalYear != null && !StringUtils.isBlank(chartOfAccountsCode)) {
            ObjectCode objCode = objectCodeService.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, objectCode);
            return (ObjectUtils.isNotNull(objCode));
        }
        else {
            return true; // blank object code? well, it's not required...and thus, it's a valid choice
        }
    }

    /**
     * Tests if the object reversion code is a valid code.
     * 
     * @param detail the OrganizationReversionGlobalDetail to check
     * @return true if it the detail is valid, false if otherwise
     */
    public boolean checkDetailObjectReversionCodeValidity(OrganizationReversionGlobalDetail detail) {
        boolean success = true;
        if (!StringUtils.isBlank(detail.getOrganizationReversionCode())) {
            boolean foundInList = false;
            // TODO Dude!! The *only* place that the org reversion code values are defined
            // is in the lookup class, so I've got to use a web-based class to actually
            // search through the values. Is that right good & healthy?
            for (Object kvPairObj : new OrganizationReversionCodeValuesFinder().getKeyValues()) {
                KeyValue kvPair = (KeyValue) kvPairObj;
                if (kvPair.getKey().toString().equals(detail.getOrganizationReversionCode())) {
                    foundInList = true;
                    break;
                }
            }
            if (!foundInList) {
                success = false; // we've failed to find the code in the list...FAILED!
                GlobalVariables.getMessageMap().putError("organizationReversionCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_ORG_REVERSION_CODE, new String[] { detail.getOrganizationReversionCode() });
            }
        }
        return success;
    }

    /**
     * This method tests if all the OrganizationReversionGlobalOrganization objects associated with the given global organization
     * reversion pass all of their tests.
     * 
     * @param globalOrgRev the global organization reversion to check
     * @return true if valid, false otherwise
     */
    public boolean areAllOrganizationsValid(OrganizationReversionGlobal globalOrgRev) {
        boolean success = true;
        if (globalOrgRev.getOrganizationReversionGlobalOrganizations().size() == 0) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + "organizationReversionGlobalOrganizations.organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_NO_ORGANIZATIONS);
        }
        else {
            for (int i = 0; i < globalOrgRev.getOrganizationReversionGlobalOrganizations().size(); i++) {
                OrganizationReversionGlobalOrganization org = globalOrgRev.getOrganizationReversionGlobalOrganizations().get(i);
                String errorPath = MAINTAINABLE_ERROR_PREFIX + "organizationReversionGlobalOrganizations[" + i + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                success &= checkAllObjectCodesForValidity(globalOrgRev, org);
                success &= checkOrganizationValidity(org);
                success &= checkOrganizationChartValidity(org);
                success &= checkOrganizationReversionForOrganizationExists(globalOrgRev, org);
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            }
        }
        return success;
    }

    /**
     * Tests if the the organization of the given OrganizationReversionGlobalOrganization is within the chart of the global
     * organization reversion as a whole.
     * 
     * @param globalOrgRev the global organization reversion that is currently being validated.
     * @param org the OrganizationReversionGlobalOrganization to check
     * @return true if valid, false otherwise
     */
    public boolean checkOrganizationChartValidity(OrganizationReversionGlobalOrganization org) {
        boolean success = true;
        if (StringUtils.isBlank(org.getChartOfAccountsCode())) {
            if (!StringUtils.isBlank(org.getOrganizationCode())) {
                success = false;
                GlobalVariables.getMessageMap().putError("chartOfAccountsCode", KFSKeyConstants.ERROR_REQUIRED, new String[] {});
            }
        }
        else {
            org.setChartOfAccountsCode(org.getChartOfAccountsCode().toUpperCase());
            org.refreshReferenceObject("chartOfAccounts");
            if (org.getChartOfAccounts() == null) {
                success = false;
                GlobalVariables.getMessageMap().putError("chartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_CHART, new String[] { org.getChartOfAccountsCode() });
            }
        }
        return success;
    }

    /**
     * Tests if the given OrganizationReversionGlobalOrganization's Organization is active and within the system.
     * 
     * @param org the OrganizationReversionGlobalOrganization to check
     * @return true if valid, false otherwise
     */
    public boolean checkOrganizationValidity(OrganizationReversionGlobalOrganization org) {
        boolean success = true;
        if (StringUtils.isBlank(org.getOrganizationCode())) {
            if (!StringUtils.isBlank(org.getChartOfAccountsCode())) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationCode", KFSKeyConstants.ERROR_REQUIRED, new String[] {});
            }
        }
        else if (!StringUtils.isBlank(org.getChartOfAccountsCode())) {
            org.refreshReferenceObject("organization");
            if (org.getOrganization() == null) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_ORGANIZATION, new String[] { org.getChartOfAccountsCode(), org.getOrganizationCode() });
            }
        }
        return success;
    }

    /**
     * Checks that an organization reversion for the given organization reversion change and organization reversion change
     * organization exist.
     * 
     * @param globalOrgRev global Organization Reversion to check
     * @param org organization within that Global Organization Reversion to check specifically
     * @return true if organization reversion for organization exists, false if otherwise
     */
    public boolean checkOrganizationReversionForOrganizationExists(OrganizationReversionGlobal globalOrgRev, OrganizationReversionGlobalOrganization org) {
        boolean success = true;
        if (globalOrgRev.getUniversityFiscalYear() != null) {
            if (organizationReversionService.getByPrimaryId(globalOrgRev.getUniversityFiscalYear(), org.getChartOfAccountsCode(), org.getOrganizationCode()) == null) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_NO_ORG_REVERSION, new String[] { globalOrgRev.getUniversityFiscalYear().toString(), org.getChartOfAccountsCode(), org.getOrganizationCode() });
            }
        }
        return success;
    }

    /**
     * This method checks if a newly added organization is already among the organizations already listed. WARNING: only use on add
     * line rules; there's no good way to use this method when testing the entire document.
     * 
     * @param globalOrgRev the global Organization Reversion to check
     * @param orgRevOrg the newly adding organization reversion change organization
     * @return true if organization should be added as it is not currently in the collection, false if otherwise
     */
    public boolean checkOrganizationIsNotAmongOrgRevOrganizations(OrganizationReversionGlobal globalOrgRev, OrganizationReversionGlobalOrganization orgRevOrg) {
        boolean success = true;
        Iterator<OrganizationReversionGlobalOrganization> iter = globalOrgRev.getOrganizationReversionGlobalOrganizations().iterator();
        while (iter.hasNext() && success) {
            OrganizationReversionGlobalOrganization currOrg = iter.next();
            if (areContainingSameOrganizations(currOrg, orgRevOrg)) {
                success = false;
                GlobalVariables.getMessageMap().putError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_DUPLICATE_ORGS, new String[] { orgRevOrg.getChartOfAccountsCode(), orgRevOrg.getOrganizationCode() });
            }
        }
        return success;
    }

    /**
     * This method tests if two OrganizationReversionGlobalOrganization objects are holding the same underlying Organization.
     * 
     * @param orgRevOrgA the first OrganizationReversionGlobalOrganization to check
     * @param orgRevOrgB the second OrganizationReversionGlobalOrganization to check
     * @return true if they share the organization, false if otherwise
     */
    public static boolean areContainingSameOrganizations(OrganizationReversionGlobalOrganization orgRevOrgA, OrganizationReversionGlobalOrganization orgRevOrgB) {
        boolean containingSame = false;
        if (orgRevOrgA.getChartOfAccountsCode() != null && orgRevOrgB.getChartOfAccountsCode() != null && orgRevOrgA.getOrganizationCode() != null && orgRevOrgB.getOrganizationCode() != null) {
            containingSame = (orgRevOrgA.getChartOfAccountsCode().equals(orgRevOrgB.getChartOfAccountsCode()) && orgRevOrgA.getOrganizationCode().equals(orgRevOrgB.getOrganizationCode()));
        }
        return containingSame;
    }

    public void setOrganizationReversionService(OrganizationReversionService organizationReversionService) {
        this.organizationReversionService = organizationReversionService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    protected OrganizationReversionGlobal getGlobalOrganizationReversion() {
        return this.globalOrganizationReversion;
    }
}
