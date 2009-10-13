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
package org.kuali.kfs.module.bc.document.dataaccess;
/**
 * 
 * provides the public method that provides data for a report of the contents of a single budget construction document, with object-level detail
 */
public interface BudgetConstructionDocumentAccountObjectDetailReportDao {

    /**
     * 
     * populates a data-holding table with the data from a single Budget Construction document, with the general ledger amounts, and sums of the FTE associated with them
     * @param principalName   the user requesting the report
     * @param documentNumber         the budget construction document for the data being reported
     * @param universityFiscalYear   the budget fiscal year 
     * @param chartOfAccountsCode    the chart of accounts for the document 
     * @param accountNumber          the account number for the document
     * @param subAccountNumber       the subaccount number for the document
     */
    public void updateDocumentAccountObjectDetailReportTable(String principalName, String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber);
    
}

