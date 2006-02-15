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
package org.kuali.module.chart.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.financial.rules.KualiParameterRule;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SubAccountRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRule.class);
    
    private static final String CG_WORKGROUP_PARM_NAME = "SubAccount.CGWorkgroup";
    private static final String CG_FUND_GROUP_CODE = "SubAccount.CG.FundGroupCode";
    private static final String CG_ALLOWED_SUBACCOUNT_TYPE_CODES = "SubAccount.ValidSubAccountTypeCodes";
    private static final String CG_A21_TYPE_COST_SHARING = "CS";
    private static final String CG_A21_TYPE_ICR = "EX";
    
    private SubAccount oldSubAccount;
    private SubAccount newSubAccount;
    private boolean cgAuthorized;
    
    private KualiConfigurationService configService;
    
    /**
     * Constructs a SubAccountRule.java.
     * 
     */
    public SubAccountRule() {
        super();
        cgAuthorized = false;

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes 
        // to spring-managed with these services injected by Spring at some later date.  
        // When this happens, just remove these calls to the setters with 
        // SpringServiceLocator, and configure the bean defs for spring.
        this.setConfigService(SpringServiceLocator.getKualiConfigurationService());
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");
        setupConvenienceObjects(document);
    
        //  set whether the user is authorized to modify the CG fields
        cgAuthorized = isCgAuthorized(GlobalVariables.getUserSession().getKualiUser());
        
        //  disallow any values entered or changed in CG fields if not authorized
        checkCgFieldsNotAuthorized(document);
        
        //	check that all sub-objects whose keys are specified have matching objects in the db
        checkExistenceAndActive();

        //	check simple rules
        checkSimpleRules();
        
        return true;
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomRouteDocumentBusinessRules()");
        setupConvenienceObjects(document);

        //  set whether the user is authorized to modify the CG fields
        cgAuthorized = isCgAuthorized(GlobalVariables.getUserSession().getKualiUser());
        
        //  disallow any values entered or changed in CG fields if not authorized
        success &= checkCgFieldsNotAuthorized(document);

        //	check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        //	check simple rules
        success &= checkSimpleRules();
        
        return success;
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomSaveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        //  set whether the user is authorized to modify the CG fields
        cgAuthorized = isCgAuthorized(GlobalVariables.getUserSession().getKualiUser());
        
        //  disallow any values entered or changed in CG fields if not authorized
        success &= checkCgFieldsNotAuthorized(document);

        //	check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        //	check simple rules
        success &= checkSimpleRules();
        
        return success;
    }
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load 
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        
        //	setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldSubAccount = (SubAccount) super.oldBo;

        //	setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubAccount = (SubAccount) super.newBo;
    }
    
    private boolean checkExistenceAndActive() {
        
        LOG.info("Entering checkExistenceAndActive()");
        
        boolean success = true;
        boolean allReportingFieldsEntered = false;
        boolean anyReportingFieldsEntered = false;
        
        //	if both ChartCode and AccountNumber are filled in, validate that they map to real objects
        if (StringUtils.isNotEmpty(newSubAccount.getChartOfAccountsCode()) && StringUtils.isNotEmpty(newSubAccount.getAccountNumber())) {
            if (ObjectUtils.isNull(newSubAccount.getAccount())) {
                putFieldError("accountNumber", KeyConstants.ERROR_EXISTENCE, 
                        "Chart Code and Account Number (" + newSubAccount.getChartOfAccountsCode() + "-" + newSubAccount.getAccountNumber() + ")");
                success &= false;
            }
        }
        
        //	set a flag if all three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(newSubAccount.getFinancialReportChartCode()) &&
                	StringUtils.isNotEmpty(newSubAccount.getFinReportOrganizationCode()) &&
                	StringUtils.isNotEmpty(newSubAccount.getFinancialReportingCode())) {
            allReportingFieldsEntered = true;
        }

        //	set a flag if any of the three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(newSubAccount.getFinancialReportChartCode()) ||
                	StringUtils.isNotEmpty(newSubAccount.getFinReportOrganizationCode()) ||
                	StringUtils.isNotEmpty(newSubAccount.getFinancialReportingCode())) {
            anyReportingFieldsEntered = true;
        }

        //	if any of the three reporting code fields are filled out, all three must be, or none
        //	if any of the three are entered
        if (anyReportingFieldsEntered && !allReportingFieldsEntered) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
            success &= false;
        }
        
        //	if all three reporting code fields are filled out, check that the object exists in the db
        if (allReportingFieldsEntered) {
            if (ObjectUtils.isNull(newSubAccount.getReportingCode())) {
                putFieldError("financialReportingCode", 
                        		KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_DOES_NOT_EXIST_IN_SYSTEM, 
                        		newSubAccount.getFinancialReportChartCode() + "-" + 
                        		newSubAccount.getFinReportOrganizationCode() + "-" + 
                        		newSubAccount.getFinancialReportingCode());
                success &= false;
            }
        }
        
        return success;
    }
    
    private boolean checkSimpleRules() {
        
        boolean success = true;
        
        return success;
    }
    
    /**
     * 
     * This method checks to see if the user is authorized for the CG fields, 
     * and if not, whether any CG fields have been entered or modified.
     * 
     * If unauthorized changes have been made, then fail and log errors.
     * 
     * @param document - document to test
     * @return - false if any unauthorized changes are made, true otherwise
     * 
     */
    private boolean checkCgFieldsNotAuthorized(MaintenanceDocument document) {
        
        boolean success = true;
        
        //  if the user is authorized, then dont bother checking any of this
        if (cgAuthorized) {
            return success;
        }
        
        A21SubAccount newA21SubAccount = newSubAccount.getA21SubAccount();
        
        //  if this is a new document, disallow any values in the CG fields
        if (document.isNew()) {
            if (ObjectUtils.isNotNull(newA21SubAccount)) {
                success &= disallowAnyValues(newA21SubAccount.getSubAccountTypeCode(), 
                                "subAccountTypeCode");
                success &= disallowAnyValues(newA21SubAccount.getCostShareChartOfAccountCode(), 
                                "costSharingChartOfAccountsCode");
                success &= disallowAnyValues(newA21SubAccount.getCostShareSourceAccountNumber(), 
                                "costSharingAccountNumber");
                success &= disallowAnyValues(newA21SubAccount.getCostShareSourceSubAccountNumber(), 
                                "costSharingSubAccountNumber");
                success &= disallowAnyValues(newA21SubAccount.getFinancialIcrSeriesIdentifier(), 
                                "financialIcrSeriesIdentifier");
                success &= disallowAnyValues(newA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode(), 
                                "indirectCostRecoveryChartOfAccountsCode");
                success &= disallowAnyValues(newA21SubAccount.getIndirectCostRecoveryAccountNumber(), 
                                "indirectCostRecoveryAccountNumber");
                success &= disallowAnyValues(newA21SubAccount.getIndirectCostRecoveryTypeCode(), 
                                "indirectCostRecoveryTypeCode");
            }
        }
        
        //  if this is an edit document, disallow any changes in the CG fields
        if (document.isEdit()) {

            A21SubAccount oldA21SubAccount = oldSubAccount.getA21SubAccount();

            //  if this is an edit, then neither old nor new A21 sub account should 
            // ever be null, so we'll throw a runtime if so
            if (ObjectUtils.isNull(oldA21SubAccount) || ObjectUtils.isNull(newA21SubAccount)) {
                throw new RuntimeException("A21 SubAccount object is null on a SubAccount that has already been created. " + 
                                "This should never happen.");
            }
            
            //  only try this set of tests if both old and new A21 subaccounts exist.  
            success &= disallowChangedValues(oldA21SubAccount.getSubAccountTypeCode(), 
                                            newA21SubAccount.getSubAccountTypeCode(), 
                                            "subAccountTypeCode");
            success &= disallowChangedValues(oldA21SubAccount.getCostShareChartOfAccountCode(), 
                                            newA21SubAccount.getCostShareChartOfAccountCode(), 
                                            "CostShareChartOfAccountsCode");
            success &= disallowChangedValues(oldA21SubAccount.getCostShareSourceAccountNumber(), 
                                            newA21SubAccount.getCostShareSourceAccountNumber(), 
                                            "CostShareAccountNumber");
            success &= disallowChangedValues(oldA21SubAccount.getCostShareSourceSubAccountNumber(), 
                                            newA21SubAccount.getCostShareSourceSubAccountNumber(), 
                                            "CostShareSubAccountNumber");
            success &= disallowChangedValues(oldA21SubAccount.getFinancialIcrSeriesIdentifier(), 
                                            newA21SubAccount.getFinancialIcrSeriesIdentifier(), 
                                            "financialIcrSeriesIdentifier");
            success &= disallowChangedValues(oldA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode(), 
                                            newA21SubAccount.getIndirectCostRecoveryChartOfAccountsCode(), 
                                            "indirectCostRecoveryChartOfAccountsCode");
            success &= disallowChangedValues(oldA21SubAccount.getIndirectCostRecoveryAccountNumber(), 
                                            newA21SubAccount.getIndirectCostRecoveryAccountNumber(), 
                                            "indirectCostRecoveryAccountNumber");
            success &= disallowChangedValues(oldA21SubAccount.getIndirectCostRecoveryTypeCode(), 
                                            newA21SubAccount.getIndirectCostRecoveryTypeCode(), 
                                            "indirectCostRecoveryTypeCode");
        }
        
        return success;
    }
    
    private boolean checkCgRules(MaintenanceDocument document) {
    
        boolean success = true;
        
        //  short circuit if this person isnt authorized for any CG fields
        if (!cgAuthorized) {
            return success;
        }
        
        //  short circuit if the parent account is NOT part of a CG fund group
        if (ObjectUtils.isNotNull(newSubAccount.getAccount())) {
            if (ObjectUtils.isNotNull(newSubAccount.getAccount().getSubFundGroup())) {
                
                //  get the fundgroupcode for this SubAccount, and the CG FundGroupcode
                String thisFundGroupCode = newSubAccount.getAccount().getSubFundGroup().getFundGroupCode();
                String cgFundGroupCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, CG_FUND_GROUP_CODE);
                
                //  compare them, exit if this isnt a CG subaccount
                if (!thisFundGroupCode.trim().equalsIgnoreCase(cgFundGroupCode.trim())) {
                    return success;
                }
            }
        }
        
        //  short circuit if there is no A21SubAccount object at all (ie, null)
        if (ObjectUtils.isNull(newSubAccount.getA21SubAccount())) {
            return success;
        }
        
        //  FROM HERE ON IN WE CAN ASSUME THERE IS A VALID A21 SUBACCOUNT OBJECT
        
        //  C&G A21 Type field must be in the allowed values
        KualiParameterRule parmRule = configService.getApplicationParameterRule(CHART_MAINTENANCE_EDOC, CG_ALLOWED_SUBACCOUNT_TYPE_CODES);
        if (parmRule.failsRule(newSubAccount.getA21SubAccount().getSubAccountTypeCode())) {
            putFieldError("a21SubAccount.subAccountTypeCode", 
                            KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES, 
                            parmRule.getParameterText());
            success &= false;
        }
        
        //  get a convenience reference to this code
        String cgA21TypeCode = newSubAccount.getA21SubAccount().getSubAccountTypeCode();
        
        //  if this is a Cost Sharing SubAccount, run the Cost Sharing rules
        if (cgA21TypeCode.trim().equalsIgnoreCase(CG_A21_TYPE_COST_SHARING.trim())) {
            success &= checkCgCostSharingRules();
        }
        
        //  if this is an ICR subaccount, run the ICR rules
        if (cgA21TypeCode.trim().equalsIgnoreCase(CG_A21_TYPE_ICR.trim())) {
            success &= checkCgIcrRules();
        }
        
        return success;
    }
    
    private boolean checkCgCostSharingRules() {
        
        boolean success = true;
        boolean anyFieldSet = false;
        boolean allFieldsSet = false;
        
        //  check to see if any fields are set
        if (StringUtils.isNotEmpty(newSubAccount.getA21SubAccount().getCostShareChartOfAccountCode()) || 
                StringUtils.isNotEmpty(newSubAccount.getA21SubAccount().getCostShareSourceAccountNumber())) {
            anyFieldSet = true;
        }
        
        //  check to see if all required fields are set
        if (StringUtils.isNotEmpty(newSubAccount.getA21SubAccount().getCostShareChartOfAccountCode()) && 
                StringUtils.isNotEmpty(newSubAccount.getA21SubAccount().getCostShareSourceAccountNumber())) {
            allFieldsSet = true;
        }
        
        //  if any required fields are filled out, then all required fields must be filled out
        if (anyFieldSet && !allFieldsSet) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARING_ACCT_FIELDS_INCOMPLETE);
            success &= false;
        }
        
        //  existence test on Cost Share Account
        if (allFieldsSet) {
            if (ObjectUtils.isNull(newSubAccount.getA21SubAccount().getCostShareAccount())) {
                putFieldError("a21SubAccount.costShareAccountNumber", KeyConstants.ERROR_EXISTENCE, "Cost Sharing Account");
                success &= false;
            }
        }
        
        //  Cost Sharing Account may not be a CG fund group
        if (ObjectUtils.isNotNull(newSubAccount.getA21SubAccount().getCostShareAccount())) {
            if (ObjectUtils.isNotNull(newSubAccount.getA21SubAccount().getCostShareAccount().getSubFundGroup())) {
                
                //   get the cost sharing account's fund group code, and the forbidden fund group code
                String costSharingAccountFundGroupCode = newSubAccount.getA21SubAccount().getCostShareAccount().getSubFundGroup().getFundGroupCode();
                String cgFundGroupCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, CG_FUND_GROUP_CODE);
                
                //  disallow them being the same
                if (costSharingAccountFundGroupCode.trim().equalsIgnoreCase(cgFundGroupCode.trim())) {
                    putFieldError("a21SubAccount.costShareAccountNumber", KeyConstants.ERROR_EXISTENCE);
                    success &= false;
                }
            }
        }
        return success;
    }
    
    private boolean checkCgIcrRules() {
        
        boolean success = true;
        
        return success;
    }
    
    /**
     * 
     * This method tests whether the specified user is part of the 
     * group that grants authorization to the CG fields.
     * 
     * @param user - the user to test
     * @return - true if user is part of the group, false otherwise
     * 
     */
    private boolean isCgAuthorized(KualiUser user) {
        
        String allowedCgWorkgroup = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, CG_WORKGROUP_PARM_NAME);
        if (user.isMember(new KualiGroup(allowedCgWorkgroup))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 
     * This method tests the value entered, and if there is anything there 
     * it logs a new error, and returns false.
     * 
     * @param value - String value to be tested
     * @param fieldName - name of the field being tested
     * @return - false if there is any value in value, otherwise true
     * 
     */
    private boolean disallowAnyValues(String value, String fieldName) {
        if (StringUtils.isNotEmpty(value)) {
            putFieldError(fieldName, KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_ENTER_CG_FIELDS);
            return false;
        }
        return true;
    }
    
    /**
     * 
     * This method tests the two values entered, and if there is any change 
     * between the two, it logs an error, and returns false.
     * 
     * Note that the comparison is done after trimming both leading and trailing 
     * whitespace from both strings, and then doing a case-insensitive comparison.
     * 
     * @param oldValue - the original String value of the field
     * @param newValue - the new String value of the field
     * @param fieldName - name of the field being tested
     * @return - false if there is any difference between the old and new, true otherwise
     * 
     */
    private boolean disallowChangedValues(String oldValue, String newValue, String fieldName) {
        if (!oldValue.trim().equalsIgnoreCase(newValue.trim())) {
            putFieldError(fieldName, KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_CHANGE_CG_FIELDS);
            return false;
        }
        return true;
    }
    
    /**
     * Sets the configService attribute value.
     * @param configService The configService to set.
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
}
