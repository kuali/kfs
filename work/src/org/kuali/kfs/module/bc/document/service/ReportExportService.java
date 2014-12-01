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
 * Provides build methods and file dump services for report dumps.
 */
public interface ReportExportService {
    
    /**
     * Rebuilds the account dump maintenance table.
     * 
     * @param principalId - current user who is running the dump
     */
    public void updateAccountDump(String principalId);
    
    /**
     * Retrieves records for organization account dump and outputs file based on user preferences
     * 
     * @param principalId - current user who is running the dump
     * @param fieldSeperator - string to seperate fields 
     * @param textDelimiter - string for text delimiter
     */
    public StringBuilder buildOrganizationAccountDumpFile(String principalId, String fieldSeperator, String textDelimiter);
    
    /**
     * Retrieves records for organization monthly dump and outputs file based on user preferences
     * 
     * @param principalId - current user who is running the dump
     * @param fieldSeperator - string to seperate fields 
     * @param textDelimiter - string for text delimiter
     */
    public StringBuilder buildOrganizationMonthlyDumpFile(String principalId, String fieldSeperator, String textDelimiter);
    
    /**
     * Retrieves records for organization funding dump and outputs file based on user preferences
     * 
     * @param principalId - current user who is running the dump
     * @param fieldSeperator - string to seperate fields 
     * @param textDelimiter - string for text delimiter
     */
    public StringBuilder buildOrganizationFundingDumpFile(String principalId, String fieldSeperator, String textDelimiter);
    
    /**
     * Retrieves records for account dump and outputs file based on user preferences
     * 
     * @param principalId
     * @param fieldSeperator
     * @param textDelimiter
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @return
     */
    public StringBuilder buildAccountDumpFile(String principalId, String fieldSeperator, String textDelimiter, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber);
    
    /**
     * Retrieves records for account monthly dump and outputs file based on user preferences
     * 
     * @param principalId
     * @param fieldSeperator
     * @param textDelimiter
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @return
     */
    public StringBuilder buildAccountMonthlyDumpFile(String principalId, String fieldSeperator, String textDelimiter, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber);
    
    /**
     * Retrieves records for organization funding dump and outputs file based on user preferences
     * 
     * @param principalId
     * @param fieldSeperator
     * @param textDelimiter
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @return
     */
    public StringBuilder buildAccountFundingDumpFile(String principalId, String fieldSeperator, String textDelimiter, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber);
}

