/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

