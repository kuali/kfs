/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

/**
 * This interface defines the methods that perform Pullup or Pushdown operations on Budget Construction Documents.
 */
public interface BudgetPushPullService {
    
    /**
     * Pulls up Budget Construction documents based on user selected Organizations and the current point of view Organization
     * on the Organization Selection screen running in Pullup mode. 
     * 
     * @param principalId
     * @param FiscalYear
     * @param pointOfViewCharOfAccountsCode
     * @param pointOfViewOrganizationCode
     */
    public void pullupSelectedOrganizationDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode);

    /**
     * Pushes down Budget Construction documents based on user selected Organizations and the current point of view Organization
     * on the Organization Selection screen running in Pushdown mode. 
     * 
     * @param principalId
     * @param FiscalYear
     * @param pointOfViewCharOfAccountsCode
     * @param pointOfViewOrganizationCode
     */
    public void pushdownSelectedOrganizationDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode);
    
    /**
     * Builds list of budget documents that are in the selected organizations and above the user's point of view. Called from the Org Pull Up screen to display the documents
     * that will be affected by the pull up.
     * 
     * @param principalId current user doing the pullup
     * @param FiscalYear budget fiscal year
     * @param pointOfViewCharOfAccountsCode user's point of view chart
     * @param pointOfViewOrganizationCode user's point of view org
     * @return int number of rows affected (number of documents)
     */
    public int buildPullUpBudgetedDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode);
    
    /**
     * Builds list of budget documents that are in the selected organizations and at the user's point of view. Called from the Org Push Down screen to display the documents
     * that will be affected by the push down.
     * 
     * @param principalId current user doing the pushdown
     * @param FiscalYear budget fiscal year
     * @param pointOfViewCharOfAccountsCode user's point of view chart
     * @param pointOfViewOrganizationCode user's point of view org
     * @return int number of rows affected (number of documents)
     */
    public int buildPushDownBudgetedDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode);

}

