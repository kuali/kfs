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
package org.kuali.kfs.module.bc.document.validation.impl;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class BudgetConstructionOrganizationReportsRule extends MaintenanceDocumentRuleBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionOrganizationReportsRule.class);


    protected OrganizationService orgService;
    protected ChartService chartService;
    protected BudgetConstructionOrganizationReportsService bcOrgReportsService;

    protected BudgetConstructionOrganizationReports oldBCOrgReports;
    protected BudgetConstructionOrganizationReports newBCOrgReports;


    public BudgetConstructionOrganizationReportsRule() {
        super();


        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
        this.setOrgService(SpringContext.getBean(OrganizationService.class));
        this.setChartService(SpringContext.getBean(ChartService.class));
        this.setBCOrgReportsService(SpringContext.getBean(BudgetConstructionOrganizationReportsService.class));
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");

        // check reporting hierarchy is valid
        success &= checkSimpleRules(document);

        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        // check reporting hierarchy is valid
        success &= checkSimpleRules(document);
        
        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");

        // check reporting hierarchy is valid
        checkSimpleRules(document);
        
        return true;
    }


    protected boolean checkSimpleRules(MaintenanceDocument document) {

        boolean success = true;
        String lastReportsToChartOfAccountsCode;
        String lastReportsToOrganizationCode;
        boolean continueSearch;
        BudgetConstructionOrganizationReports tempBCOrgReports;
        Organization tempOrg;
        Integer loopCount;
        Integer maxLoopCount = 40;

        boolean orgMustReportToSelf = false;
        tempOrg = null;

        // Get the Org business object so that we can check the org type
        if (ObjectUtils.isNotNull(newBCOrgReports.getChartOfAccountsCode()) && ObjectUtils.isNotNull(newBCOrgReports.getOrganizationCode())) {
            tempOrg = orgService.getByPrimaryId(newBCOrgReports.getChartOfAccountsCode(), newBCOrgReports.getOrganizationCode());

            // Check the Org Type of the Org business object to see if it is the root (reports to self)

            if (ObjectUtils.isNotNull(tempOrg)) {
                if (/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Organization.class, KFSConstants.ChartApcParms.ORG_MUST_REPORT_TO_SELF_ORG_TYPES, tempOrg.getOrganizationTypeCode()).evaluationSucceeds()) {
                    orgMustReportToSelf = true;
                }
            }
        }

        // Reports To Chart/Org should not be same as this Chart/Org
        // However, allow special case where organization type is listed in the business rules
        if (ObjectUtils.isNotNull(newBCOrgReports.getReportsToChartOfAccountsCode()) && ObjectUtils.isNotNull(newBCOrgReports.getReportsToOrganizationCode()) && ObjectUtils.isNotNull(newBCOrgReports.getChartOfAccountsCode()) && ObjectUtils.isNotNull(newBCOrgReports.getOrganizationCode())) {
            if (!orgMustReportToSelf) {

                if ((newBCOrgReports.getReportsToChartOfAccountsCode().equals(newBCOrgReports.getChartOfAccountsCode())) && (newBCOrgReports.getReportsToOrganizationCode().equals(newBCOrgReports.getOrganizationCode()))) {
                    putFieldError("reportsToOrganizationCode", KFSKeyConstants.ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_CANNOT_BE_SAME_ORG);
                    success = false;
                }
                else {
                    // Don't allow a circular reference on Reports to Chart/Org
                    // terminate the search when a top-level org is found
                    lastReportsToChartOfAccountsCode = newBCOrgReports.getReportsToChartOfAccountsCode();
                    lastReportsToOrganizationCode = newBCOrgReports.getReportsToOrganizationCode();
                    continueSearch = true;
                    loopCount = 0;
                    do {
                        tempBCOrgReports = bcOrgReportsService.getByPrimaryId(lastReportsToChartOfAccountsCode, lastReportsToOrganizationCode);
                        loopCount++;
                        ;
                        if (ObjectUtils.isNull(tempBCOrgReports)) {
                            continueSearch = false;
                            // if a null is returned on the first iteration, then the reports-to org does not exist
                            // fail the validation
                            if (loopCount == 1) {
                                putFieldError("reportsToOrganizationCode", KFSKeyConstants.ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_MUST_EXIST);
                                success = false;
                            }
                        }
                        else {
                            {
                                // LOG.info("Found Org = " + lastReportsToChartOfAccountsCode + "/" +
                                // lastReportsToOrganizationCode);
                                lastReportsToChartOfAccountsCode = tempBCOrgReports.getReportsToChartOfAccountsCode();
                                lastReportsToOrganizationCode = tempBCOrgReports.getReportsToOrganizationCode();

                                if ((tempBCOrgReports.getReportsToChartOfAccountsCode().equals(newBCOrgReports.getChartOfAccountsCode())) && (tempBCOrgReports.getReportsToOrganizationCode().equals(newBCOrgReports.getOrganizationCode()))) {
                                    putFieldError("reportsToOrganizationCode", KFSKeyConstants.ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_CANNOT_BE_CIRCULAR_REF_TO_SAME_ORG);
                                    success = false;
                                    continueSearch = false;
                                }
                            }
                        }
                        if (loopCount > maxLoopCount) {
                            continueSearch = false;
                        }
                        // stop the search if we reach an org that reports to itself
                        if (continueSearch && (tempBCOrgReports.getReportsToChartOfAccountsCode().equals(tempBCOrgReports.getReportsToChartOfAccountsCode())) && (tempBCOrgReports.getReportsToOrganizationCode().equals(tempBCOrgReports.getOrganizationCode())))
                            continueSearch = false;

                    } while (continueSearch == true);

                } // end else (checking for circular ref)

            }
            else { // org must report to self (university level organization)
                if (!(newBCOrgReports.getReportsToChartOfAccountsCode().equals(newBCOrgReports.getChartOfAccountsCode()) && newBCOrgReports.getReportsToOrganizationCode().equals(newBCOrgReports.getOrganizationCode()))) {
                    putFieldError("reportsToOrganizationCode", KFSKeyConstants.ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_MUST_BE_SAME_ORG);
                    success = false;
                }
            }
        }

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
        oldBCOrgReports = (BudgetConstructionOrganizationReports) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBCOrgReports = (BudgetConstructionOrganizationReports) super.getNewBo();
    }

    /**
     * Sets the orgService attribute value.
     * 
     * @param orgService The orgService to set.
     */
    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    /**
     * Sets the chartService attribute value.
     * 
     * @param chartService The orgService to set.
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * Sets the bcOrgReportsService attribute value.
     * 
     * @param bcOrgReportsService The bcOrgReportsService to set.
     */
    public void setBCOrgReportsService(BudgetConstructionOrganizationReportsService bcOrgReportsService) {
        this.bcOrgReportsService = bcOrgReportsService;
    }

}

