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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements the business rules specific to the {@link SubAccount} Maintenance Document.
 */
public class SubAccountRule extends IndirectCostRecoveryAccountsRule {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRule.class);

    protected SubAccount oldSubAccount;
    protected SubAccount newSubAccount;

    /**
     * This performs rules checks on document approve
     * <ul>
     * <li>{@link SubAccountRule#setCgAuthorized(boolean)}</li>
     * <li>{@link SubAccountRule#checkForPartiallyEnteredReportingFields()}</li>
     * <li>{@link SubAccountRule#checkCgRules(MaintenanceDocument)}</li>
     * </ul>
     * This rule fails on business rule failures
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");

        // check that all sub-objects whose keys are specified have matching objects in the db
        boolean success = checkForPartiallyEnteredReportingFields();

        // process CG rules if appropriate
        success &= checkCgRules(document);

        return success;
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link SubAccountRule#setCgAuthorized(boolean)}</li>
     * <li>{@link SubAccountRule#checkForPartiallyEnteredReportingFields()}</li>
     * <li>{@link SubAccountRule#checkCgRules(MaintenanceDocument)}</li>
     * </ul>
     * This rule fails on business rule failures
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        boolean success = true;

        // check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkForPartiallyEnteredReportingFields();

        // process CG rules if appropriate
        success &= checkCgRules(document);

        success &= super.processCustomRouteDocumentBusinessRules(document);
        return success;
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link SubAccountRule#setCgAuthorized(boolean)}</li>
     * <li>{@link SubAccountRule#checkForPartiallyEnteredReportingFields()}</li>
     * <li>{@link SubAccountRule#checkCgRules(MaintenanceDocument)}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");

        // check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkForPartiallyEnteredReportingFields();

        // process CG rules if appropriate
        success &= checkCgRules(document);

        return success;
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldSubAccount = (SubAccount) super.getOldBo();
        refreshSubObjects(oldSubAccount);

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubAccount = (SubAccount) super.getNewBo();
        refreshSubObjects(newSubAccount);
        
        //icr rule checking setup
        if (newSubAccount.getA21SubAccount() != null){
            List<IndirectCostRecoveryAccount> icrAccountList = new ArrayList<IndirectCostRecoveryAccount>(
                    newSubAccount.getA21SubAccount().getA21ActiveIndirectCostRecoveryAccounts());
            setActiveIndirectCostRecoveryAccountList(icrAccountList);
            setBoFieldPath(KFSPropertyConstants.A21INDIRECT_COST_RECOVERY_ACCOUNTS);
        }
    }
    
    /**
     * Refreshes the references of account
     * 
     * @param subaccount SubAccount
     */
    void refreshSubObjects(SubAccount subaccount) {
        if (subaccount != null) {
            if (subaccount.getA21SubAccount() != null) {
                subaccount.getA21SubAccount().refreshNonUpdateableReferences();
                // refresh contacts
//                if (subaccount.getA21SubAccount().getA21IndirectCostRecoveryAccounts() != null) {
//                    for (A21IndirectCostRecoveryAccount icra : subaccount.getA21SubAccount().getA21IndirectCostRecoveryAccounts()) {
//                        icra.refreshNonUpdateableReferences();
//                    }
//                }
            }
        }
    }

    /**
     * This checks that the reporting fields are entered altogether or none at all
     * 
     * @return false if only one reporting field filled out and not all of them, true otherwise
     */
    protected boolean checkForPartiallyEnteredReportingFields() {

        LOG.debug("Entering checkExistenceAndActive()");

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
            putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
            success &= false;
        }

        return success;
    }

    /**
     * This checks to make sure that if cgAuthorized is false it succeeds immediately, otherwise it checks that all the information
     * for CG is correctly entered and identified including:
     * <ul>
     * <li>If the {@link SubFundGroup} isn't for Contracts and Grants then check to make sure that the cost share and ICR fields are
     * not empty</li>
     * <li>If it isn't a child of CG, then the SubAccount must be of type ICR</li>
     * </ul>
     * 
     * @param document
     * @return true if the user is not authorized to change CG fields, otherwise it checks the above conditions
     */
    protected boolean checkCgRules(MaintenanceDocument document) {

        boolean success = true;

        // short circuit if the parent account is NOT part of a CG fund group
        boolean a21SubAccountRefreshed = false;
        if (ObjectUtils.isNotNull(newSubAccount.getAccount())) {
            if (ObjectUtils.isNotNull(newSubAccount.getAccount().getSubFundGroup())) {

                // compare them, exit if the account isn't for contracts and grants
                if (!SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(newSubAccount.getAccount().getSubFundGroup())) {

                    // KULCOA-1116 - Check if CG CS and CG ICR are empty, if not throw an error
                    if (checkCgCostSharingIsEmpty() == false) {
                        putFieldError("a21SubAccount.costShareChartOfAccountCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_CS_INVALID, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                        success = false;
                    }

                    if (checkCgIcrIsEmpty() == false) {
                        putFieldError("a21SubAccount.indirectCostRecoveryTypeCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_ICR_INVALID, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                        success = false;
                    }

                    // KULRNE-4660 - this isn't the child of a CG account; sub account must be ICR type
                    if (!ObjectUtils.isNull(newSubAccount.getA21SubAccount())) {
                        // KFSMI-798 - refresh() changed to refreshNonUpdateableReferences()
                        // All references for A21SubAccount are non-updatable
                        newSubAccount.getA21SubAccount().refreshNonUpdateableReferences();
                        a21SubAccountRefreshed = true;
                        if (StringUtils.isEmpty(newSubAccount.getA21SubAccount().getSubAccountTypeCode()) || !newSubAccount.getA21SubAccount().getSubAccountTypeCode().equals(KFSConstants.SubAccountType.EXPENSE)) {
                            putFieldError("a21SubAccount.subAccountTypeCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_SUB_ACCT_TYPE_CODE_INVALID, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                            success = false;
                        }
                    }

                    return success;
                }
            }
        }

        A21SubAccount a21 = newSubAccount.getA21SubAccount();
        
        // short circuit if there is no A21SubAccount object at all (ie, null)
        if (ObjectUtils.isNull(a21)) {
            return success;
        }

        // FROM HERE ON IN WE CAN ASSUME THERE IS A VALID A21 SUBACCOUNT OBJECT

        // KFSMI-6848 since there is a ICR Collection Account object, change refresh to perform 
        // manually refresh the a21SubAccount object, as it wont have been
        // refreshed by the parent, as its updateable
        // though only refresh if we didn't refresh in the checks above
        
        if (!a21SubAccountRefreshed) {
            //preserve the ICRAccounts before refresh to prevent the list from dropping
            List<A21IndirectCostRecoveryAccount>icrAccounts =a21.getA21IndirectCostRecoveryAccounts(); 
            a21.refresh();
            a21.setA21IndirectCostRecoveryAccounts(icrAccounts);
            
        }

        // C&G A21 Type field must be in the allowed values
        if (!KFSConstants.SubAccountType.ELIGIBLE_SUB_ACCOUNT_TYPE_CODES.contains(a21.getSubAccountTypeCode())) {
            putFieldError("a21SubAccount.subAccountTypeCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES, KFSConstants.SubAccountType.ELIGIBLE_SUB_ACCOUNT_TYPE_CODES.toString());
            success &= false;
        }

        // get a convenience reference to this code
        String cgA21TypeCode = a21.getSubAccountTypeCode();

        // if this is a Cost Sharing SubAccount, run the Cost Sharing rules
        if (KFSConstants.SubAccountType.COST_SHARE.trim().equalsIgnoreCase(StringUtils.trim(cgA21TypeCode))) {
            success &= checkCgCostSharingRules();
        }

        // if this is an ICR subaccount, run the ICR rules
        if (KFSConstants.SubAccountType.EXPENSE.trim().equals(StringUtils.trim(cgA21TypeCode))) {
            success &= checkCgIcrRules();
        }

        return success;
    }

    /**
     * This checks that if the cost share information is filled out that it is valid and exists, or if fields are missing (such as
     * the chart of accounts code and account number) an error is recorded
     * 
     * @return true if all cost share fields filled out correctly, false if the chart of accounts code and account number for cost
     *         share are missing
     */
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
                putFieldError("a21SubAccount.costShareSourceAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, getDisplayName("a21SubAccount.costShareSourceAccountNumber"));
                success &= false;
            }
        }

        // existence test on Cost Share SubAccount
        if (allFieldsSet && StringUtils.isNotBlank(a21.getCostShareSourceSubAccountNumber())) {
            if (ObjectUtils.isNull(a21.getCostShareSourceSubAccount())) {
                putFieldError("a21SubAccount.costShareSourceSubAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, getDisplayName("a21SubAccount.costShareSourceSubAccountNumber"));
                success &= false;
            }
        }

        // Cost Sharing Account may not be for contracts and grants
        if (ObjectUtils.isNotNull(a21.getCostShareAccount())) {
            if (ObjectUtils.isNotNull(a21.getCostShareAccount().getSubFundGroup())) {
                if (a21.getCostShareAccount().isForContractsAndGrants()) {
                    putFieldError("a21SubAccount.costShareSourceAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARE_ACCOUNT_MAY_NOT_BE_CG_FUNDGROUP, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                    success &= false;
                }
            }
        }

        // The ICR fields must be empty if the sub-account type code is for cost sharing
        if (checkCgIcrIsEmpty() == false) {
            putFieldError("a21SubAccount.indirectCostRecoveryTypeCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_ICR_SECTION_INVALID, a21.getSubAccountTypeCode());
            success &= false;
        }

        return success;
    }

    /**
     * This checks that if the ICR information is entered that it is valid for this fiscal year and that all of its fields are valid
     * as well (such as account)
     * 
     * @return true if the ICR information is filled in and it is valid
     */
    protected boolean checkCgIcrRules() {
        A21SubAccount a21 = newSubAccount.getA21SubAccount();
        if(ObjectUtils.isNull(a21)) {
            return true;
        }

        boolean success = true;
        
        // existence check for ICR Type Code
        if (StringUtils.isNotEmpty(a21.getIndirectCostRecoveryTypeCode())) {
            if (ObjectUtils.isNull(a21.getIndirectCostRecoveryType())) {
                putFieldError("a21SubAccount.indirectCostRecoveryTypeCode", KFSKeyConstants.ERROR_EXISTENCE, "ICR Type Code: " + a21.getIndirectCostRecoveryTypeCode());
                success = false;
            }
        }

        // existence check for Financial Series ID
        if (StringUtils.isNotEmpty(a21.getFinancialIcrSeriesIdentifier())) {            
            String fiscalYear = StringUtils.EMPTY + SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            String icrSeriesId = a21.getFinancialIcrSeriesIdentifier();
            
            Map<String, String> pkMap = new HashMap<String, String>();
            pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
            pkMap.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, icrSeriesId);
            Collection<IndirectCostRecoveryRateDetail> icrRateDetails = getBoService().findMatching(IndirectCostRecoveryRateDetail.class, pkMap);
            
            if (ObjectUtils.isNull(icrRateDetails) || icrRateDetails.isEmpty()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(A21SubAccount.class, KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER);
                putFieldError(KFSPropertyConstants.A21_SUB_ACCOUNT + "." + KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, KFSKeyConstants.ERROR_EXISTENCE, label + " (" + icrSeriesId + ")");
                success = false;
            }
            else {
                for(IndirectCostRecoveryRateDetail icrRateDetail : icrRateDetails) {
                    if(ObjectUtils.isNull(icrRateDetail.getIndirectCostRecoveryRate())){                                
                        putFieldError(KFSPropertyConstants.A21_SUB_ACCOUNT + "." + KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_NOT_FOUND, new String[]{fiscalYear, icrSeriesId});
                        success = false;
                        break;
                    }
                }
            }            
        }

        // existence check for ICR Account
        for (A21IndirectCostRecoveryAccount account : a21.getA21ActiveIndirectCostRecoveryAccounts()){
            if (StringUtils.isNotBlank(account.getIndirectCostRecoveryAccountNumber())
                && StringUtils.isNotBlank(account.getIndirectCostRecoveryFinCoaCode())){
                if(ObjectUtils.isNull(account.getIndirectCostRecoveryAccount())){                                
                    putFieldError(KFSPropertyConstants.A21INDIRECT_COST_RECOVERY_ACCOUNTS, KFSKeyConstants.ERROR_EXISTENCE, "ICR Account: " + account.getIndirectCostRecoveryFinCoaCode() + "-" + account.getIndirectCostRecoveryAccountNumber());
                    success = false;
                    break;
                }
            }
        }

        // The cost sharing fields must be empty if the sub-account type code is for ICR
        if (checkCgCostSharingIsEmpty() == false) {
            putFieldError("a21SubAccount.costShareChartOfAccountCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARE_SECTION_INVALID, a21.getSubAccountTypeCode());

            success &= false;
        }

        return success;
    }

    /**
     * This method tests if all fields in the Cost Sharing section are empty.
     * 
     * @return true if the cost sharing values passed in are empty, otherwise false.
     */
    protected boolean checkCgCostSharingIsEmpty() {
        boolean success = true;

        A21SubAccount newA21SubAccount = newSubAccount.getA21SubAccount();
        if (ObjectUtils.isNotNull(newA21SubAccount)) {
            success &= StringUtils.isEmpty(newA21SubAccount.getCostShareChartOfAccountCode());
            success &= StringUtils.isEmpty(newA21SubAccount.getCostShareSourceAccountNumber());
            success &= StringUtils.isEmpty(newA21SubAccount.getCostShareSourceSubAccountNumber());
        }

        return success;
    }

    /**
     * This method tests if all fields in the ICR section are empty.
     * 
     * @return true if the ICR values passed in are empty, otherwise false.
     */
    protected boolean checkCgIcrIsEmpty() {
        boolean success = true;
        
        A21SubAccount newA21SubAccount = newSubAccount.getA21SubAccount();
        if (ObjectUtils.isNotNull(newA21SubAccount)) {
            success &= StringUtils.isEmpty(newA21SubAccount.getFinancialIcrSeriesIdentifier());
            
            success &= checkICRCollectionExist(false);
            success &= StringUtils.isEmpty(newA21SubAccount.getIndirectCostRecoveryTypeCode());
            // this is a boolean, so create any value if set to true, meaning a user checked the box, otherwise assume it's empty
            success &= StringUtils.isEmpty(newA21SubAccount.getOffCampusCode() ? "1" : "");
        }

        return success;
    }

    /**
     * This method tests the value entered, and if there is anything there it logs a new error, and returns false.
     * 
     * @param value - String value to be tested
     * @param fieldName - name of the field being tested
     * @return false if there is any value in value, otherwise true
     */
    protected boolean disallowAnyValues(String value, String fieldName) {
        if (StringUtils.isNotEmpty(value)) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_ENTER_CG_FIELDS, getDisplayName(fieldName));
            return false;
        }
        return true;
    }

    /**
     * This method tests the two values entered, and if there is any change between the two, it logs an error, and returns false.
     * Note that the comparison is done after trimming both leading and trailing whitespace from both strings, and then doing a
     * case-insensitive comparison.
     * 
     * @param oldValue - the original String value of the field
     * @param newValue - the new String value of the field
     * @param fieldName - name of the field being tested
     * @return false if there is any difference between the old and new, true otherwise
     */
    protected boolean disallowChangedValues(String oldValue, String newValue, String fieldName) {

        if (isFieldValueChanged(oldValue, newValue)) {
            putFieldError(fieldName, KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_CHANGE_CG_FIELDS, getDisplayName(fieldName));
            return false;
        }
        return true;
    }

    /**
     * This compares two string values to see if the newValue has changed from the oldValue
     * 
     * @param oldValue - original value
     * @param newValue - new value
     * @return true if the two fields are different from each other
     */
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


    /**
     * This method retrieves the label name for a specific property
     * 
     * @param propertyName - property to retrieve label for (from the DD)
     * @return the label
     */
    protected String getDisplayName(String propertyName) {
        return getDdService().getAttributeLabel(SubAccount.class, propertyName);
    }

}
