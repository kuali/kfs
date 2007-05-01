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
package org.kuali.module.budget.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.bo.BudgetConstructionAccountReports;
import org.kuali.module.chart.service.ChartService;

public class BudgetConstructionAccountReportsRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAccountReportsRule.class);

    private ChartService chartService;
    private BudgetConstructionAccountReports oldBCAccountReports;
    private BudgetConstructionAccountReports newBCAccountReports;
    private String rootChartUserId;


    public BudgetConstructionAccountReportsRule() {
        super();


        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringServiceLocator, and configure the bean defs for spring.
        this.setChartService(SpringServiceLocator.getChartService()); 
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        // check user authorized for this transaction
        success &= checkUserAuthorized();

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        // check user authorized for this transaction
        success &= checkUserAuthorized();
        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
 
        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        // check user authorized for this transaction
        checkUserAuthorized();
        return true;
    }

 


    /**
     * 
     * Check that the user is authorized to process this document.
     * 
     * The user is authorized if either of the following are true:
     * 1. The transaction user is the manager of the Chart
     * 2. The transaction user is the manager of the Root Chart
     * 
     * 
     */

    protected boolean checkUserAuthorized() {
        
        boolean success = true;
        String chartUserId = "";
        String transactionUserId = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        if (!(newBCAccountReports.getChartOfAccounts()== null)){
        
               chartUserId = newBCAccountReports.getChartOfAccounts().getFinCoaManagerUniversalId();
               if (transactionUserId.equals(chartUserId)||transactionUserId.equals(rootChartUserId)){
                   success = true;
               }else{
                   putFieldError("chartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_REPORTING_USER_MUST_BE_CHART_MANAGER_OR_ROOT_MANAGER);
                   success = false;
               }
        } else{
            putFieldError("chartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_REPORTING_USER_MUST_BE_CHART_MANAGER_OR_ROOT_MANAGER);
            success = false;
        }
//        LOG.info("transactionUserId = " + transactionUserId );
//        LOG.info("chartUserId = " + chartUserId );
//        LOG.info("rootChartUserId = " + rootChartUserId );
                  
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
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldBCAccountReports = (BudgetConstructionAccountReports) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBCAccountReports = (BudgetConstructionAccountReports) super.getNewBo();
        rootChartUserId = chartService.getUniversityChart().getFinCoaManagerUniversalId();
    }

    /**
     * Sets the chartService attribute value.
     * 
     * @param chartService The orgService to set.
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

}
