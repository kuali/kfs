/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.rules;

import java.util.ArrayList;
import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.budget.rules.ResearchDocumentRuleBase;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;

/**
 * This class...
 * 
 * 
 */
public class RoutingFormDocumentRule extends ResearchDocumentRuleBase {
    /**
     * Checks business rules related to saving a ResearchDocument.
     * 
     * @param ResearchDocument researchDocument
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(ResearchDocument researchDocument) {
        if (!(researchDocument instanceof RoutingFormDocument)) {
            return false;
        }

        boolean valid = true;

        RoutingFormDocument routingFormDocument = (RoutingFormDocument) researchDocument;

        //changing this to '0' so it doesn't validate reference objects within a list (Subcontractors was causing a problem).
        SpringServiceLocator.getDictionaryValidationService().validateDocumentRecursively(routingFormDocument, 0);
        
        valid &= processInstitutionCostShare(routingFormDocument);
        
        valid &= processRoutingFormOrganizations(routingFormDocument);
        
        valid &= processRoutingFormSubcontractors(routingFormDocument);

        valid &= GlobalVariables.getErrorMap().isEmpty();

        return valid;
    }
    
    /**
     * This method validates Institution Cost Share Orgs.  It checks the following:
     * 1 - The Org must exist
     * 2 - If an Account Number is specified, the Account must exist
     * 3 - Orgs without Accounts can appear in the list only once
     * 4 - Accounts, when specified, can appear in the list only once 
     * 5 - Positive, non-zero amounts are required for all lines 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processInstitutionCostShare(RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        
        List accounts = new ArrayList();
        List orgNoAccounts = new ArrayList();

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        int i = 0;
        
        for (RoutingFormInstitutionCostShare costShare : routingFormDocument.getRoutingFormInstitutionCostShares()) {
            errorMap.addToErrorPath("routingFormInstitutionCostShare[" + i + "]");
            costShare.refresh();
            
            if (costShare.getRoutingFormCostShareAmount() == null || !costShare.getRoutingFormCostShareAmount().isPositive()) {
                //Amount is zero or less
                valid = false;
                errorMap.putError("routingFormCostShareAmount", KraKeyConstants.ERROR_INVALID_AMOUNT_POSITIVE_ONLY);
            }
            
            if (costShare.getOrganization() != null) {
                if (costShare.getAccountNumber() == null) {
                    if (!orgNoAccounts.contains(costShare.getOrganization())) {
                        orgNoAccounts.add(costShare.getOrganization());
                    } else {
                        //org already in list
                        valid = false;
                        errorMap.putError("organizationCode", KraKeyConstants.ERROR_ORG_ALREADY_EXISTS_ON_RF, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode());
                        
                    }
                } else {
                    //account number is not null
                    if (costShare.getAccount() == null) {
                        //account number is specified account doesn't exist
                        valid = false;
                        errorMap.putError("accountNumber", KeyConstants.ERROR_ACCOUNT_NOT_FOUND, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode(), costShare.getAccountNumber());
                    } else if (!accounts.contains(costShare.getAccount())){
                        accounts.add(costShare.getAccount());
                    } else {
                        //account already in list
                        valid = false;
                        errorMap.putError("accountNumber", KraKeyConstants.ERROR_ACCOUNT_ALREADY_EXISTS_ON_RF, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode(), costShare.getAccountNumber());
                    }
                }
            } else {
                //organization doesn't exist
                valid = false;
                errorMap.putError("organizationCode", KraKeyConstants.ERROR_ORG_NOT_FOUND, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode());
            }
            errorMap.removeFromErrorPath("routingFormInstitutionCostShare[" + i++ + "]");
        }
        return valid;
    }
    
    /**
     * This method validates 'Other Organizations'.  It checks the following:
     * 1 - The Org must exist
     * 2 - The Org must appear in the list only once
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processRoutingFormOrganizations(RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        
        List organizations = new ArrayList();
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        int i = 0;

        for (RoutingFormOrganization organization : routingFormDocument.getRoutingFormOrganizations()) {
            organization.refresh();
            
            errorMap.addToErrorPath("routingFormOrganization[" + i + "]");
            
            if (organization.getOrganization() == null) {
                //organization does not exist
                valid = false;
                errorMap.putError("organizationCode", KraKeyConstants.ERROR_ORG_NOT_FOUND, organization.getChartOfAccountsCode(), organization.getOrganizationCode());
            } else {
                if (!organizations.contains(organization.getOrganization())) {
                    organizations.add(organization.getOrganization());
                } else {
                    //organization already exists on RF
                    valid = false;
                    errorMap.putError("organizationCode", KraKeyConstants.ERROR_ORG_ALREADY_EXISTS_ON_RF, organization.getChartOfAccountsCode(), organization.getOrganizationCode());
                }
            }
            errorMap.removeFromErrorPath("routingFormOrganization[" + i++ + "]");
        }

        return valid;
    }
    
    /**
     * This method validates 'Subcontractors'.  It checks the following:
     * 1 - The Subcontractor must exist
     * 2 - The Subcontractor must appear in the list only once
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processRoutingFormSubcontractors(RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        List subcontractors = new ArrayList();

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        int i = 0;

        for (RoutingFormSubcontractor subcontractor : routingFormDocument.getRoutingFormSubcontractors()) {
            subcontractor.refresh();
            
            errorMap.addToErrorPath("routingFormSubcontractor[" + i + "]");
            
            if (subcontractor.getRoutingFormSubcontractorAmount() == null || !subcontractor.getRoutingFormSubcontractorAmount().isPositive()) {
                //Amount is zero or less
                valid = false;
                errorMap.putError("routingFormSubcontractorAmount", KraKeyConstants.ERROR_INVALID_AMOUNT_POSITIVE_ONLY);
            }

            if (subcontractor.getSubcontractor() == null) {
                //subcontractor doesn't exist
                valid = false;
                errorMap.putError("routingFormSubcontractorAmount", KraKeyConstants.ERROR_SUBCONTRACTOR_NOT_FOUND);
            } else {
                if (!subcontractors.contains(subcontractor.getSubcontractor())) {
                    subcontractors.add(subcontractor.getSubcontractor());
                } else {
                    //subcontractor already exists on RF
                    valid = false;
                    errorMap.putError("routingFormSubcontractorAmount", KraKeyConstants.ERROR_SUBCONTRACTOR_ALREADY_EXISTS_ON_RF);
                }
            }
            errorMap.removeFromErrorPath("routingFormSubcontractor[" + i++ + "]");
        }
        return valid;
    }
    
}
