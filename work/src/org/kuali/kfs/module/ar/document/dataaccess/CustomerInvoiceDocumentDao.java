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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface CustomerInvoiceDocumentDao {
    
    /**
     * 
     * Retrieves all Invoice document numbers that meet the following criteria: 
     * 1) PrintIndicator = BY_USER
     * 2) PrintDate = null 
     * 3) DocHeader.Status = Approved
     * 
     * WARNING that all the returned documents lack any workflow wiring.
     * 
     * @param initiatorPrincipalName
     * @return
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersFromUserQueue();

    /**
     * 
     * Retrieves all Invoice document numbers in the system associated with the given 
     * Processing Chart and Org, that are approved and ready to print.
     * 
     * WARNING that all the returned documents lack any workflow wiring.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode);
    
    /**
     * 
     * Retrieves all Invoice document numbers in the system associated with the given 
     * Billing Chart and Org, that are approved and ready to print.
     * 
     * WARNING that all the returned documents lack any workflow wiring.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * 
     * Retrieves all Invoice document numbers in the system associated with the given 
     * Billing Chart and Org, that are approved but disregards ready to print and print date as this is for Billing Statement generation.
     * 
     * WARNING that all the returned documents lack any workflow wiring.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersForBillingStatementByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);
    
    /**
     * 
     * Retrieves all Invoice document numbers in the system associated with the given 
     * Processing Chart and Org.
     * 
     * WARNING that all the returned documents lack any workflow wiring.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<String> getCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode);
    
    /**
     * 
     * Retrieves all Invoice document numbers in the system associated with the given 
     * Billing Chart and Org.
     * 
     * WARNING that all the returned documents lack any workflow wiring.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<String> getCustomerInvoiceDocumentNumbersByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode);
    
    /**
     * 
     * Retrieves all Open invoices, with outstanding balances.
     * 
     * @return
     */
    public Collection getAllOpen();
    
    /**
     * 
     * Retrieves all Open invoices from the specified Customer Number.
     * @param customerNumber
     * @return
     */
    public Collection getOpenByCustomerNumber(String customerNumber);
    
    /**
     * 
     * Retrieves all Open invoices, by the specified Customer Name and Customer Type Code
     * 
     * Retrieves all Open invoices, by the specified Customer Name (a LIKE customerName* search) and Customer Type Code.
     * 
     * @param customerName
     * @param customerTypeCode
     * @return
     */
    public Collection getOpenByCustomerNameByCustomerType(String customerName, String customerTypeCode);
    
    /**
     * 
     * Retrieves all Open invoices, by the specified Customer Name.
     * 
     * NOTE - this search uses customerName as a leading substring search, 
     * so it will return anything matching a customerName that begins with the 
     * value passed in.  ie, a LIKE customerName* search.
     * 
     * @param customerName
     * @return
     */
    public Collection getOpenByCustomerName(String customerName);
    
    /**
     * 
     * Retrieves all Open invoices, by the specified Customer Type Code.
     * @param customerTypeCode
     * @return
     */
    public Collection getOpenByCustomerType(String customerTypeCode);
    
    /**
     * @param organizationInvoiceNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber);
    
    /**
     * @param documentNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String documentNumber);
}
