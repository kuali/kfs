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

