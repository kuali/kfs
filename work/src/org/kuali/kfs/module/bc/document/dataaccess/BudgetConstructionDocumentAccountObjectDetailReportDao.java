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

