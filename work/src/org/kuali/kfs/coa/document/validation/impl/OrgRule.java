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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.OrganizationService;

public class OrgRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrgRule.class);
    
    protected static final String APC_HRMS_ACTIVE_KEY = "Org.HrmsOrgActive";
    
    private OrganizationService orgService;
    
    private Org oldOrg;
    private Org newOrg;
    private boolean isChartManager;
    private boolean isHrmsOrgActivated;
    
    public OrgRule() {
        super();
        isChartManager = false;

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes 
        // to spring-managed with these services injected by Spring at some later date.  
        // When this happens, just remove these calls to the setters with 
        // SpringServiceLocator, and configure the bean defs for spring.
        this.setOrgService(SpringServiceLocator.getOrganizationService());
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        //  determine whether this person is the Chart manager for this org
        isChartManager = isChartManager(GlobalVariables.getUserSession().getKualiUser());

        //  determine whether HRMS ORG is activated in this app instance
        isHrmsOrgActivated = isHrmsOrgActivated();
        
        //  check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        success &= checkPlantAccountRules(document);
        
        success &= checkOrgClosureRules(document);
        
        return success;
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        //  determine whether this person is the Chart manager for this org
        isChartManager = isChartManager(GlobalVariables.getUserSession().getKualiUser());

        //  determine whether HRMS ORG is activated in this app instance
        isHrmsOrgActivated = isHrmsOrgActivated();
        
        //  check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        success &= checkPlantAccountRules(document);
        
        success &= checkOrgClosureRules(document);
        
        return success;
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        //  determine whether this person is the Chart manager for this org
        isChartManager = isChartManager(GlobalVariables.getUserSession().getKualiUser());

        //  determine whether HRMS ORG is activated in this app instance
        isHrmsOrgActivated = isHrmsOrgActivated();
        
        //  check that all sub-objects whose keys are specified have matching objects in the db
        checkExistenceAndActive();

        checkPlantAccountRules(document);
        
        checkOrgClosureRules(document);
        
        return true;
    }
    
    protected boolean checkExistenceAndActive() {
        
        LOG.info("Entering checkExistenceAndActive()");
        boolean success = true;
        
        //  shortcut out with no enforcement if this org is closed
        if (!newOrg.isOrganizationActiveIndicator()) {
            return success;
        }

        success &= checkChartManagerRequiredActiveExistence();
        
        return success;
    }

    protected boolean checkChartManagerRequiredActiveExistence() {
        
        boolean success = true;
        
        //  shortcut out with no enforcement if this org is closed
        if (!newOrg.isOrganizationActiveIndicator()) {
            return success;
        }

        //  exit without doing any tests if not a chart manager
        if (!isChartManager) {
            return true;
        }
            
        //  require Org Plant ChartCode
        success &= checkEmptyBOField("organizationPlantChartCode", 
                newOrg.getOrganizationPlantChartCode(), 
                "Organization Plant Chart of Accounts Code");
        
        //  require Org Plant AccountNumber
        success &= checkEmptyBOField("organizationPlantAccountNumber", 
                newOrg.getOrganizationPlantAccountNumber(), 
                "Organization Plant Account Number");
        
        //  require Campus Plant ChartCode
        success &= checkEmptyBOField("campusPlantChartCode", 
                newOrg.getCampusPlantChartCode(), 
                "Campus Plant Chart of Accounts Code");
        
        //  require Org Plant ChartCode
        success &= checkEmptyBOField("CampusPlantAccountNumber", 
                newOrg.getCampusPlantAccountNumber(), 
                "Campus Plant Chart of Accounts Code");
        
        //  validate Org Plant Account
        success &= dictionaryValidationService.validateReferenceExistsAndIsActive(newOrg, "organizationPlantAccount", 
                    "accountClosedIndicator", true, true, MAINTAINABLE_ERROR_PREFIX + "organizationPlantAccountNumber", 
                    "Organization Plant Account");
        
        //  validate Campus Plant Account
        success &= dictionaryValidationService.validateReferenceExistsAndIsActive(newOrg, "campusPlantAccount", 
                    "accountClosedIndicator", true, true, MAINTAINABLE_ERROR_PREFIX + "campusPlantAccountNumber", 
                    "Campus Plant Account");
        
        return success;
    }
    
    /**
     * 
     * This method enforces the business rules surrounding when an Org 
     * becomes closed/inactive.
     * 
     * @param document
     * @return
     * 
     */
    protected boolean checkOrgClosureRules(MaintenanceDocument document) {
        
        boolean success = true;
        boolean orgBeingClosed = false;
        
        //  if its an edit, and its being closed
        if (document.isEdit()) {
            if (oldOrg.isOrganizationActiveIndicator() && !newOrg.isOrganizationActiveIndicator()) {
                orgBeingClosed = true;
            }
        }

        //  if its new, and is being created as closed
        if (document.isNew()) {
            if (!newOrg.isOrganizationActiveIndicator()) {
                orgBeingClosed = true;
            }
        }
        
        //  if the org isnt being closed, stop processing here
        if (!orgBeingClosed) {
            return success;
        }
        
        // FROM HERE ON WE'RE ASSUMING THE ORG IS BEING CLOSED
        
        //  do not allow the org to be closed while there are active accounts tied 
        // to this org
        List childAccounts = orgService.getActiveAccountsByOrg(newOrg.getChartOfAccountsCode(), 
                                                                newOrg.getOrganizationCode());
        if (childAccounts.size() > 0) {
            
            //  get the first three accounts on the list for display
            StringBuffer childAccountList = new StringBuffer();
            int count = 0;
            String delim = "";
            for (Iterator iter = childAccounts.iterator(); iter.hasNext();) {
                Account account = (Account) iter.next();
                childAccountList.append(delim + account.getChartOfAccountsCode() + "-" + account.getAccountNumber());
                count++;
                if (count >= 1) {
                    delim = ", ";
                }
                if (count >= 3) {
                    break;
                }
            }
            if (childAccounts.size() > count) {
                childAccountList.append(", ... (" + (childAccounts.size() - count) + " more)");
            }
            
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ORGMAINT_OPEN_CHILD_ACCOUNTS_ON_ORG_CLOSURE, childAccountList.toString());
            success &= false;
        }
        
        //  do not allow this org to be closed while there are still active orgs 
        // that have this org as their reportsToOrg
        List childOrgs = orgService.getActiveChildOrgs(newOrg.getChartOfAccountsCode(), 
                                                                newOrg.getOrganizationCode());
        if (childOrgs.size() > 0) {
            
            //  get the first three orgs on the list for display
            StringBuffer childOrgsList = new StringBuffer();
            int count = 0;
            String delim = "";
            for (Iterator iter = childOrgs.iterator(); iter.hasNext();) {
                Org org = (Org) iter.next();
                childOrgsList.append(delim + org.getChartOfAccountsCode() + "-" + org.getOrganizationCode());
                count++;
                if (count >= 1) {
                    delim = ", ";
                }
                if (count >= 3) {
                    break;
                }
            }
            if (childOrgs.size() > count) {
                childOrgsList.append(", ... (" + (childOrgs.size() - count) + " more)");
            }
            
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ORGMAINT_OPEN_CHILD_ORGS_ON_ORG_CLOSURE, childOrgsList.toString());
            success &= false;
        }
        
        
        //  if org is being closed, end-date must be valid and present
        if (ObjectUtils.isNull(newOrg.getOrganizationEndDate())) {
            success &= false;
            putFieldError("organizationEndDate", KeyConstants.ERROR_DOCUMENT_ORGMAINT_END_DATE_REQUIRED_ON_ORG_CLOSURE);
        }
        return success;
        
    }
    
    /**
     * 
     * This method implements the HRMS Org rules.
     * 
     * @param document
     * @return
     */
    protected boolean checkHrmsOrgRules(MaintenanceDocument document) {
        
        boolean success = true;
        
        //  shortcut out with no enforcement if this org is closed
        if (!newOrg.isOrganizationActiveIndicator()) {
            return success;
        }

        //  short circuit and fail if HRMSOrg is turned off
        if (!isHrmsOrgActivated) {
            return success;
        }

        //  if the system has a HRMS Org record attached to this org record, then prompt the 
        // user to fill out the HRMS Org info
        
        //  HRMS Org Campus == Org Campus
        //  HRMS Org campus code must be the same as Org campus code
        
        
        //  if the 
        return success;
    }
    
    /**
     * 
     * This method disallows changes to any of the plant account fields if 
     * the user is not a member of the correct chartManager role
     * 
     * @param document - Maintenance document being tested
     * @return - true if no errors are found, false if any are found
     * 
     */
    protected boolean checkPlantAccountRules(MaintenanceDocument document) {
        
        boolean success = true;
        
        //  shortcut out with no enforcement if this org is closed
        if (!newOrg.isOrganizationActiveIndicator()) {
            return success;
        }

        //  if user is not the IU chart manager, dont let them edit or change OrgPlantAccount 
        // or CampusPlantAccount information
        if (!isChartManager) {
            
            String oldOrgPlantChartCode;
            String oldOrgPlantAccountNumber;
            String oldCampusPlantChartCode;
            String oldCampusPlantAccountNumber;
            String newOrgPlantChartCode = newOrg.getOrganizationPlantChartCode();
            String newOrgPlantAccountNumber = newOrg.getOrganizationPlantAccountNumber();
            String newCampusPlantChartCode = newOrg.getCampusPlantChartCode();
            String newCampusPlantAccountNumber = newOrg.getCampusPlantAccountNumber();
            
            if (document.isNew()) {
                oldOrgPlantChartCode = null;
                oldOrgPlantAccountNumber = null;
                oldCampusPlantChartCode = null;
                oldCampusPlantAccountNumber = null;
            }
            else {
                oldOrgPlantChartCode = oldOrg.getOrganizationPlantChartCode();
                oldOrgPlantAccountNumber = oldOrg.getOrganizationPlantAccountNumber();
                oldCampusPlantChartCode = oldOrg.getCampusPlantChartCode();
                oldCampusPlantAccountNumber = oldOrg.getCampusPlantAccountNumber();
            }

            //  test org plant chart code
            if (fieldsHaveChanged(oldOrgPlantChartCode, newOrgPlantChartCode)) {
                success &= false;
                putFieldError("organizationPlantChartCode", 
                        KeyConstants.ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_ORG_PLANT_ACCT_CHARTCODE);
            }
            
            //  test org plant account number
            if (fieldsHaveChanged(oldOrgPlantAccountNumber, newOrgPlantAccountNumber)) {
                success &= false;
                putFieldError("organizationPlantAccountNumber", 
                        KeyConstants.ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_ORG_PLANT_ACCT_NUMBER);
            }
            
            //  test campus plant chart code
            if (fieldsHaveChanged(oldCampusPlantChartCode, newCampusPlantChartCode)) {
                success &= false;
                putFieldError("campusPlantChartCode", 
                        KeyConstants.ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_CAMPUS_PLANT_ACCT_CHARTCODE);
            }
            
            //  test campus plant account number
            if (fieldsHaveChanged(oldCampusPlantAccountNumber, newCampusPlantAccountNumber)) {
                success &= false;
                putFieldError("campusPlantAccountNumber", 
                        KeyConstants.ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_CAMPUS_PLANT_ACCT_NUMBER);
            }
        }
        
        return success;
    }
    
    /**
     * 
     * This method compares an old and new value, and determines if they've changed.
     * 
     * If the old was null/blank, and the new is not, return true.
     * If the old had a value, and the new is null/blank, return true.
     * If both old and new had a value, and the values are different (excluding 
     * trailing or leading whitespaces, and excluding case changes), return true.
     * If none of the above, return false.
     * 
     * @param oldValue - Old value to test.
     * @param newValue - New value to test.
     * @return - true or false, based on the algorithm described above.
     * 
     */
    protected boolean fieldsHaveChanged(String oldValue, String newValue) {
        
        //  if old was null/blank and new is not
        if (StringUtils.isBlank(oldValue) && StringUtils.isNotBlank(newValue)) {
            return true;
        }
        
        //  if old had a value, but new is null/blank
        if (StringUtils.isNotBlank(oldValue) && StringUtils.isBlank(newValue)) {
            return true;
        }

        //  at this point, we know that we had a value before, and we have a 
        // value now, so we need to test whether this value has changed
        if (!oldValue.trim().equalsIgnoreCase(newValue.trim())) {
            return true;
        }
        
        //  if we've made it to here, then no changes have happened to the values
        return false;
    }
    
    /**
     * 
     * This method determines whether the given use is a ChartManager 
     * for the chart this Org belongs to.
     * 
     * @param user - user to test
     * @return - true if the user is the Chart Manager, false otherwise
     * 
     */
    protected boolean isChartManager(KualiUser user) {
        
        boolean success = true;
        
        if (success) {
            LOG.info("User: [" + user.getPersonUserIdentifier() + "] " + user.getPersonName() + 
                    " is a Chart Manager for this Org's Chart: " + newOrg.getChartOfAccountsCode());
        }
        else {
            LOG.info("User: [" + user.getPersonUserIdentifier() + "] " + user.getPersonName() + 
                    " is NOT a Chart Manager for this Org's Chart: " + newOrg.getChartOfAccountsCode());
        }
        
        return success;
    }
    
    /**
     * 
     * This method looks up in the APC system whether ther HRMS Org system is turned on.
     * 
     * @return - true or false depending on the app configuration
     * 
     */
    protected boolean isHrmsOrgActivated() {
        
        String flag = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, APC_HRMS_ACTIVE_KEY);
        if (StringUtils.isBlank(flag)) {
            throw new RuntimeException("Application Parameter Value did not return a value, and one is required. " + 
                    "[" + CHART_MAINTENANCE_EDOC + "] '" + APC_HRMS_ACTIVE_KEY + "'");
        }
        
        if (flag.trim().equalsIgnoreCase("Y")) {
            return true;
        }
        return false;
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
    public void setupConvenienceObjects() {
        
        //  setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldOrg = (Org) super.oldBo;

        //  setup newAccount convenience objects, make sure all possible sub-objects are populated
        newOrg = (Org) super.newBo;
    }

    /**
     * Sets the orgService attribute value.
     * @param orgService The orgService to set.
     */
    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }
    
}
