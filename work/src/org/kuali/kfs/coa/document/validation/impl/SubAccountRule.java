/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/document/validation/impl/SubAccountRule.java,v $
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
package org.kuali.module.chart.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.KualiGroup;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.SubAccount;

/**
 * This class...
 * 
 * 
 */
public class SubAccountRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRule.class);

    public static final String CG_WORKGROUP_PARM_NAME = "SubAccount.CGWorkgroup";
    public static final String CG_FUND_GROUP_CODE = "SubAccount.CG.FundGroupCode";
    public static final String CG_ALLOWED_SUBACCOUNT_TYPE_CODES = "SubAccount.ValidSubAccountTypeCodes";
    public static final String CG_A21_TYPE_COST_SHARING = "CS";
    public static final String CG_A21_TYPE_ICR = "EX";

    private SubAccount oldSubAccount;
    private SubAccount newSubAccount;
    private boolean cgAuthorized;

    /**
     * Constructs a SubAccountRule.java.
     * 
     */
    public SubAccountRule() {
        super();
        setCgAuthorized(false);
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        // set whether the user is authorized to modify the CG fields
        setCgAuthorized(isCgAuthorized(GlobalVariables.getUserSession().getUniversalUser()));

        // check that all sub-objects whose keys are specified have matching objects in the db
        checkForPartiallyEnteredReportingFields();

        // process CG rules if appropriate
        checkCgRules(document);

        return true;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        // set whether the user is authorized to modify the CG fields
        setCgAuthorized(isCgAuthorized(GlobalVariables.getUserSession().getUniversalUser()));

        // check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkForPartiallyEnteredReportingFields();

        // process CG rules if appropriate
        success &= checkCgRules(document);

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        // set whether the user is authorized to modify the CG fields
        setCgAuthorized(isCgAuthorized(GlobalVariables.getUserSession().getUniversalUser()));

        // check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkForPartiallyEnteredReportingFields();

        // process CG rules if appropriate
        success &= checkCgRules(document);

        return success;
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldSubAccount = (SubAccount) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubAccount = (SubAccount) super.getNewBo();
    }

    protected boolean checkForPartiallyEnteredReportingFields() {

        LOG.info("Entering checkExistenceAndActive()");

        boolean success = true;
        boolean allReportingFieldsEntered = false;
        boolean anyReportingFieldsEntered = false;

        // set a flag if all three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(newSubAccount.getFinancialReportChartCode()) && StringUtils.isNotEmpty(newSubAccount.getFinReportOrganizationCode()) && StringUtils.isNotEmpty(newSubAccount.getFinancialReportingCode())) {
            allReportingFieldsEntered = true;
        }

        // set a flag if any of the three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(newSubAccount.getFinancialReportChartCode()) || StringUtils.isNotEmpty(newSubAccount.getFinReportOrganizationCode()) || StringUtils.isNotEmpty(newSubAccount.getFinancialReportingCode())) {
            anyReportingFieldsEntered = true;
        }

        // if any of the three reporting code fields are filled out, all three must be, or none
        // if any of the three are entered
        if (anyReportingFieldsEntered && !allReportingFieldsEntered) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
            success &= false;
        }

        return success;
    }

    /**
     * 
     * This method checks to see if the user is authorized for the CG fields, and if not, whether any CG fields have been entered or
     * modified.
     * 
     * If unauthorized changes have been made, then fail and log errors.
     * 
     * @param document - document to test
     * @return false if any unauthorized changes are made, true otherwise
     * 
     */
    protected boolean checkCgFieldsNotAuthorized(MaintenanceDocument document) {

        // This method is no longer necessary, as authorization restricted
        // fields are enforced in the general case in MaintenanceDocumentRuleBase
        // in checkAuthorizationRestrictions().

        boolean success = true;

        // if the user is authorized, then dont bother checking any of this
        if (getCgAuthorized()) {
            return success;
        }

        A21SubAccount newA21SubAccount = newSubAccount.getA21SubAccount();

        // if this is an edit, then neither old nor new A21 sub account should
        // ever be null, so we'll throw a runtime if so
        if (ObjectUtils.isNull(newA21SubAccount)) {
            throw new RuntimeException("A21 SubAccount object is null on a SubAccount maintenance doc. " + "This should never happen.");
        }

        // if this is a new document, disallow any values in the CG fields
        if (document.isNew()) {
            // success &= disallowAnyValues(newA21SubAccount.getSubAccountTypeCode(),
            // "a21SubAccount.subAccountTypeCode");
            success &= disallowAnyValues(newA21SubAccount.getCostShareChartOfAccountCode(), "a21SubAccount.costShareChartOfAccountCode");
            success &= disallowAnyValues(newA21SubAccount.getCostShareSourceAccountNumber(), "a21SubAccount.costShareSourceAccountNumber");
            success &= disallowAnyValues(newA21SubAccount.getCostShareSourceSubAccountNumber(), "a21SubAccount.costShareSourceSubAccountNumber");
            success &= disallowAnyValues(newA21SubAccount.getFinancialIcrSeriesIdentifier(), "a21SubAccount.financialIcrSeriesIdentifier");
            success &= disallowAnyValues(newA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode(), "a21SubAccount.indirectCostRecoveryChartOfAccountsCode");
            success &= disallowAnyValues(newA21SubAccount.getIndirectCostRecoveryAccountNumber(), "a21SubAccount.indirectCostRecoveryAccountNumber");
            success &= disallowAnyValues(newA21SubAccount.getIndirectCostRecoveryTypeCode(), "a21SubAccount.indirectCostRecoveryTypeCode");
        }

        // if this is an edit document, disallow any changes in the CG fields
        if (document.isEdit()) {

            A21SubAccount oldA21SubAccount = oldSubAccount.getA21SubAccount();

            // if this is an edit, then neither old nor new A21 sub account should
            // ever be null, so we'll throw a runtime if so
            if (ObjectUtils.isNull(oldA21SubAccount)) {
                throw new RuntimeException("A21 SubAccount object is null on a SubAccount that has already been created. " + "This should never happen.");
            }

            // only try this set of tests if both old and new A21 subaccounts exist.
            // success &= disallowChangedValues(oldA21SubAccount.getSubAccountTypeCode(),
            // newA21SubAccount.getSubAccountTypeCode(),
            // "a21SubAccount.subAccountTypeCode");
            success &= disallowChangedValues(oldA21SubAccount.getCostShareChartOfAccountCode(), newA21SubAccount.getCostShareChartOfAccountCode(), "a21SubAccount.CostShareChartOfAccountsCode");
            success &= disallowChangedValues(oldA21SubAccount.getCostShareSourceAccountNumber(), newA21SubAccount.getCostShareSourceAccountNumber(), "a21SubAccount.CostShareSourceAccountNumber");
            success &= disallowChangedValues(oldA21SubAccount.getCostShareSourceSubAccountNumber(), newA21SubAccount.getCostShareSourceSubAccountNumber(), "a21SubAccount.CostShareSourceSubAccountNumber");
            success &= disallowChangedValues(oldA21SubAccount.getFinancialIcrSeriesIdentifier(), newA21SubAccount.getFinancialIcrSeriesIdentifier(), "a21SubAccount.financialIcrSeriesIdentifier");
            success &= disallowChangedValues(oldA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode(), newA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode(), "a21SubAccount.indirectCostRecoveryChartOfAccountsCode");
            success &= disallowChangedValues(oldA21SubAccount.getIndirectCostRecoveryAccountNumber(), newA21SubAccount.getIndirectCostRecoveryAccountNumber(), "a21SubAccount.indirectCostRecoveryAccountNumber");
            success &= disallowChangedValues(oldA21SubAccount.getIndirectCostRecoveryTypeCode(), newA21SubAccount.getIndirectCostRecoveryTypeCode(), "a21SubAccount.indirectCostRecoveryTypeCode");
        }

        return success;
    }

    protected boolean checkCgRules(MaintenanceDocument document) {

        boolean success = true;

        // short circuit if this person isnt authorized for any CG fields
        if (!getCgAuthorized()) {
            return success;
        }

        // short circuit if the parent account is NOT part of a CG fund group
        if (ObjectUtils.isNotNull(newSubAccount.getAccount())) {
            if (ObjectUtils.isNotNull(newSubAccount.getAccount().getSubFundGroup())) {

                // get the fundgroupcode for this SubAccount, and the CG FundGroupcode
                String thisFundGroupCode = newSubAccount.getAccount().getSubFundGroup().getFundGroupCode();
                String cgFundGroupCode = getConfigService().getApplicationParameterValue(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, CG_FUND_GROUP_CODE);

                // compare them, exit if this isnt a CG subaccount
                if (!thisFundGroupCode.trim().equalsIgnoreCase(cgFundGroupCode.trim())) {

                    // KULCOA-1116 - Check if CG CS and CG ICR are empty, if not throw an error
                    if (checkCgCostSharingIsEmpty() == false) {
                        putFieldError("a21SubAccount.costShareChartOfAccountCode", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_CS_INVALID);
                    }

                    if (checkCgIcrIsEmpty() == false) {
                        putFieldError("a21SubAccount.indirectCostRecoveryTypeCode", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_ICR_INVALID);
                    }

                    // KULCOA-1116 - Check if sub account type code is blank, if not throw an error
                    if (ObjectUtils.isNull(newSubAccount.getA21SubAccount()) == false) {

                        if (StringUtils.isEmpty(newSubAccount.getA21SubAccount().getSubAccountTypeCode()) == false) {
                            putFieldError("a21SubAccount.subAccountTypeCode", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_SUB_ACCT_TYPE_CODE_INVALID);
                        }
                    }

                    return success;
                }
            }
        }

        // short circuit if there is no A21SubAccount object at all (ie, null)
        if (ObjectUtils.isNull(newSubAccount.getA21SubAccount())) {
            return success;
        }

        // FROM HERE ON IN WE CAN ASSUME THERE IS A VALID A21 SUBACCOUNT OBJECT

        // manually refresh the a21SubAccount object, as it wont have been
        // refreshed by the parent, as its updateable
        newSubAccount.getA21SubAccount().refresh();

        // C&G A21 Type field must be in the allowed values
        KualiParameterRule parmRule = getConfigService().getApplicationParameterRule(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, CG_ALLOWED_SUBACCOUNT_TYPE_CODES);
        if (parmRule.failsRule(newSubAccount.getA21SubAccount().getSubAccountTypeCode())) {
            putFieldError("a21SubAccount.subAccountTypeCode", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES, parmRule.getParameterText());
            success &= false;
        }

        // get a convenience reference to this code
        String cgA21TypeCode = newSubAccount.getA21SubAccount().getSubAccountTypeCode();

        // if this is a Cost Sharing SubAccount, run the Cost Sharing rules
        if ( CG_A21_TYPE_COST_SHARING.trim().equalsIgnoreCase(StringUtils.trim(cgA21TypeCode)) ) {
            success &= checkCgCostSharingRules();
        }

        // if this is an ICR subaccount, run the ICR rules
        if ( CG_A21_TYPE_ICR.trim().equals(StringUtils.trim(cgA21TypeCode)) ) {
            success &= checkCgIcrRules();
        }

        return success;
    }

    protected boolean checkCgCostSharingRules() {

        boolean success = true;
        boolean allFieldsSet = false;

        A21SubAccount a21 = newSubAccount.getA21SubAccount();

        // check to see if all required fields are set
        if (StringUtils.isNotEmpty(a21.getCostShareChartOfAccountCode()) && StringUtils.isNotEmpty(a21.getCostShareSourceAccountNumber())) {
            allFieldsSet = true;
        }

        // Cost Sharing COA Code and Cost Sharing Account Number are required
        success &= checkEmptyBOField("a21SubAccount.costShareChartOfAccountCode", a21.getCostShareChartOfAccountCode(), "Cost Share Chart of Accounts Code");
        success &= checkEmptyBOField("a21SubAccount.costShareSourceAccountNumber", a21.getCostShareSourceAccountNumber(), "Cost Share AccountNumber");

        // existence test on Cost Share Account
        if (allFieldsSet) {
            if (ObjectUtils.isNull(a21.getCostShareAccount())) {
                putFieldError("a21SubAccount.costShareSourceAccountNumber", KeyConstants.ERROR_EXISTENCE, getDisplayName("a21SubAccount.costShareSourceAccountNumber"));
                success &= false;
            }
        }

        // existence test on Cost Share SubAccount
        if (allFieldsSet && StringUtils.isNotBlank(a21.getCostShareSourceSubAccountNumber())) {
            if (ObjectUtils.isNull(a21.getCostShareSourceSubAccount())) {
                putFieldError("a21SubAccount.costShareSourceSubAccountNumber", KeyConstants.ERROR_EXISTENCE, getDisplayName("a21SubAccount.costShareSourceSubAccountNumber"));
                success &= false;
            }
        }

        // Cost Sharing Account may not be a CG fund group
        if (ObjectUtils.isNotNull(a21.getCostShareAccount())) {
            if (ObjectUtils.isNotNull(a21.getCostShareAccount().getSubFundGroup())) {

                // get the cost sharing account's fund group code, and the forbidden fund group code
                String costSharingAccountFundGroupCode = a21.getCostShareAccount().getSubFundGroup().getFundGroupCode();
                String cgFundGroupCode = getConfigService().getApplicationParameterValue(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, CG_FUND_GROUP_CODE);

                // disallow them being the same
                if (costSharingAccountFundGroupCode.trim().equalsIgnoreCase(cgFundGroupCode.trim())) {
                    putFieldError("a21SubAccount.costShareSourceAccountNumber", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARE_ACCOUNT_MAY_NOT_BE_CG_FUNDGROUP);
                    success &= false;
                }
            }
        }

        // The ICR fields must be empty if the sub-account type code is for cost sharing
        if (checkCgIcrIsEmpty() == false) {
            putFieldError("a21SubAccount.indirectCostRecoveryTypeCode", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_ICR_SECTION_INVALID, a21.getSubAccountTypeCode());
            success &= false;
        }

        return success;
    }

    protected boolean checkCgIcrRules() {

        boolean success = true;

        A21SubAccount a21 = newSubAccount.getA21SubAccount();

        // check required fields
        success &= checkEmptyBOField("a21SubAccount.indirectCostRecoveryTypeCode", a21.getIndirectCostRecoveryTypeCode(), "ICR Type Code");
        success &= checkEmptyBOField("a21SubAccount.indirectCostRecoveryChartOfAccountsCode", a21.getIndirectCostRecoveryChartOfAccountsCode(), "ICR Chart of Accounts Code");
        success &= checkEmptyBOField("a21SubAccount.indirectCostRecoveryAccountNumber", a21.getIndirectCostRecoveryAccountNumber(), "ICR Account Number");
        success &= checkEmptyBOField("a21SubAccount.financialIcrSeriesIdentifier", a21.getFinancialIcrSeriesIdentifier(), "Financial ICR Series ID");

        // existence check for ICR Type Code
        if (StringUtils.isNotEmpty(a21.getIndirectCostRecoveryTypeCode())) {
            if (ObjectUtils.isNull(a21.getIcrTypeCode())) {
                putFieldError("a21SubAccount.indirectCostRecoveryTypeCode", KeyConstants.ERROR_EXISTENCE, "ICR Type Code: " + a21.getIndirectCostRecoveryTypeCode());
            }
        }

        // existence check for Financial Series ID
        if (StringUtils.isNotEmpty(a21.getFinancialIcrSeriesIdentifier())) {

            Integer fiscalYear = getDateTimeService().getCurrentFiscalYear();

            Map fieldValues = new HashMap();
            fieldValues.put("financialIcrSeriesIdentifier", a21.getFinancialIcrSeriesIdentifier());
            // Collection results = boService.findMatchingOrderBy(IcrAutomatedEntry.class, fieldValues, "universityFiscalYear",
            // true);
            Collection results = getBoService().findMatching(IcrAutomatedEntry.class, fieldValues);

            // if there are any results, we need to see if there is a match on fiscal year
            boolean anyFound = false;
            boolean anyFoundInThisFy = false;
            if (!results.isEmpty()) {
                anyFound = true;
                for (Iterator iter = results.iterator(); iter.hasNext();) {
                    IcrAutomatedEntry icrAutomatedEntry = (IcrAutomatedEntry) iter.next();
                    if (fiscalYear.equals(icrAutomatedEntry.getUniversityFiscalYear())) {
                        anyFoundInThisFy = true;
                        break;
                    }
                }
            }

            // if there is one found, but not for this fiscal year, complain
            // about this to the user
            if (anyFound && !anyFoundInThisFy) {
                putFieldError("a21SubAccount.financialIcrSeriesIdentifier", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_ICR_FIN_SERIES_ID_EXISTS_BUT_NOT_FOR_THIS_FY, new String[] { a21.getFinancialIcrSeriesIdentifier(), fiscalYear.toString() });
            }

            // if one isnt found at all, complain about that
            if (!anyFound) {
                putFieldError("a21SubAccount.financialIcrSeriesIdentifier", KeyConstants.ERROR_EXISTENCE, "ICR Financial Series ID: " + a21.getFinancialIcrSeriesIdentifier());
            }
        }

        // existence check for ICR Account
        if (StringUtils.isNotEmpty(a21.getIndirectCostRecoveryChartOfAccountsCode()) && StringUtils.isNotEmpty(a21.getIndirectCostRecoveryAccountNumber())) {
            if (ObjectUtils.isNull(a21.getIndirectCostRecoveryAccount())) {
                putFieldError("a21SubAccount.indirectCostRecoveryAccountNumber", KeyConstants.ERROR_EXISTENCE, "ICR Account: " + a21.getIndirectCostRecoveryChartOfAccountsCode() + "-" + a21.getIndirectCostRecoveryAccountNumber());
            }
        }

        // The cost sharing fields must be empty if the sub-account type code is for ICR
        if (checkCgCostSharingIsEmpty() == false) {
            putFieldError("a21SubAccount.costShareChartOfAccountCode", KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARE_SECTION_INVALID, a21.getSubAccountTypeCode());

            success &= false;
        }

        return success;
    }

    /**
     * 
     * This method tests if all fields in the Cost Sharing section are empty.
     * 
     * @return true if the cost sharing values passed in are empty, otherwise false.
     * 
     */
    protected boolean checkCgCostSharingIsEmpty() {

        boolean success = true;

        A21SubAccount newA21SubAccount = newSubAccount.getA21SubAccount();

        success &= StringUtils.isEmpty(newA21SubAccount.getCostShareChartOfAccountCode());
        success &= StringUtils.isEmpty(newA21SubAccount.getCostShareSourceAccountNumber());
        success &= StringUtils.isEmpty(newA21SubAccount.getCostShareSourceSubAccountNumber());

        return success;
    }

    /**
     * 
     * This method tests if all fields in the ICR section are empty.
     * 
     * @return true if the ICR values passed in are empty, otherwise false.
     * 
     */
    protected boolean checkCgIcrIsEmpty() {

        boolean success = true;

        A21SubAccount newA21SubAccount = newSubAccount.getA21SubAccount();

        success &= StringUtils.isEmpty(newA21SubAccount.getFinancialIcrSeriesIdentifier());
        success &= StringUtils.isEmpty(newA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode());
        success &= StringUtils.isEmpty(newA21SubAccount.getIndirectCostRecoveryAccountNumber());
        success &= StringUtils.isEmpty(newA21SubAccount.getIndirectCostRecoveryTypeCode());
        // this is a boolean, so create any value if set to true, meaning a user checked the box, otherwise assume it's empty
        success &= StringUtils.isEmpty(newA21SubAccount.getOffCampusCode() ? "1" : "");

        return success;
    }

    /**
     * 
     * This method tests whether the specified user is part of the group that grants authorization to the CG fields.
     * 
     * @param user - the user to test
     * @return true if user is part of the group, false otherwise
     * 
     */
    protected boolean isCgAuthorized(UniversalUser user) {

        // attempt to get the group name that grants access to the CG fields
        String allowedCgWorkgroup = getConfigService().getApplicationParameterValue(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, CG_WORKGROUP_PARM_NAME);

        if (user.isMember( allowedCgWorkgroup )) {
            LOG.info("User '" + user.getPersonUserIdentifier() + "' is a member of the group '" + allowedCgWorkgroup + "', which gives them access to the CG fields.");
            return true;
        }
        else {
            LOG.info("User '" + user.getPersonUserIdentifier() + "' is not a member of the group '" + allowedCgWorkgroup + "', so they have no access to the CG fields.");
            return false;
        }
    }

    /**
     * 
     * This method tests the value entered, and if there is anything there it logs a new error, and returns false.
     * 
     * @param value - String value to be tested
     * @param fieldName - name of the field being tested
     * @return false if there is any value in value, otherwise true
     * 
     */
    protected boolean disallowAnyValues(String value, String fieldName) {
        if (StringUtils.isNotEmpty(value)) {
            putFieldError(fieldName, KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_ENTER_CG_FIELDS, getDisplayName(fieldName));
            return false;
        }
        return true;
    }

    /**
     * 
     * This method tests the two values entered, and if there is any change between the two, it logs an error, and returns false.
     * 
     * Note that the comparison is done after trimming both leading and trailing whitespace from both strings, and then doing a
     * case-insensitive comparison.
     * 
     * @param oldValue - the original String value of the field
     * @param newValue - the new String value of the field
     * @param fieldName - name of the field being tested
     * @return false if there is any difference between the old and new, true otherwise
     * 
     */
    protected boolean disallowChangedValues(String oldValue, String newValue, String fieldName) {

        if (isFieldValueChanged(oldValue, newValue)) {
            putFieldError(fieldName, KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_CHANGE_CG_FIELDS, getDisplayName(fieldName));
            return false;
        }
        return true;
    }

    protected boolean isFieldValueChanged(String oldValue, String newValue) {

        if (StringUtils.isBlank(oldValue) && StringUtils.isBlank(newValue)) {
            return false;
        }

        if (StringUtils.isBlank(oldValue) && StringUtils.isNotBlank(newValue)) {
            return true;
        }

        if (StringUtils.isNotBlank(oldValue) && StringUtils.isBlank(newValue)) {
            return true;
        }

        if (!oldValue.trim().equalsIgnoreCase(newValue.trim())) {
            return true;
        }

        return false;
    }


    private String getDisplayName(String propertyName) {
        return getDdService().getAttributeLabel(SubAccount.class, propertyName);
    }

    protected void setCgAuthorized(boolean cgAuthorized) {
        this.cgAuthorized = cgAuthorized;
    }

    protected boolean getCgAuthorized() {
        return cgAuthorized;
    }

}
