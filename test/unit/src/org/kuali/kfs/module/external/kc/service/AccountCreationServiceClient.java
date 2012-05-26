/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service;

import java.net.URL;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.TestCase;

import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.service.impl.AccountCreationServiceImpl;

public class AccountCreationServiceClient extends TestCase
{
    private AccountParametersDTO accountParameters;    
    private AccountCreationService accountCreationService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception 
    {
        // Initialize service objects.
        //accountCreationService = SpringContext.getBean(AccountCreationService.class);
        accountCreationService = new AccountCreationServiceImpl();
        
        // Initialize objects.
        accountParameters = new AccountParametersDTO();
        accountParameters.setUnit("BL");
        accountParameters.setAccountNumber("1234572");
        accountParameters.setAccountName("KC Award");
        accountParameters.setHigherEdFunctionCode("IPR");
        accountParameters.setIndirectCostTypeCode("");
        accountParameters.setIndirectCostRate("");
        accountParameters.setExpenseGuidelineText("expenseGuidelineText");
        accountParameters.setIncomeGuidelineText("incomeGuidelineText");
        accountParameters.setPurposeText("purposeText");
        accountParameters.setCfdaNumber("");
        
        accountParameters.setDefaultAddressStreetAddress("1000 Main St");
        accountParameters.setDefaultAddressCityName("Cold Spring");
        accountParameters.setDefaultAddressStateCode("MD");
        accountParameters.setDefaultAddressZipCode("20090");
        
        accountParameters.setAdminContactAddressStreetAddress("1010 Main St");
        accountParameters.setAdminContactAddressCityName("Silver Spring");
        accountParameters.setAdminContactAddressStateCode("MD");
        accountParameters.setAdminContactAddressZipCode("20090");
                
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, 2012 );
        cal.set( Calendar.MONTH, Calendar.DECEMBER);
        cal.set( Calendar.DATE, 1 );        
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );
        accountParameters.setExpirationDate(new java.sql.Date(cal.getTime().getTime()) );
        
        cal.set( Calendar.YEAR, 2010 );
        cal.set( Calendar.MONTH, Calendar.JANUARY );
        cal.set( Calendar.DATE, 1 );
        accountParameters.setEffectiveDate(new java.sql.Date(cal.getTime().getTime()) );
        
        accountParameters.setOffCampusIndicator(false);
        accountParameters.setPrincipalId("6162502038");  //khuntley
                
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception 
    {
        super.tearDown();
    }
    
    /**
     * This method tests the remote Service without using KSB so the server should be up before testing
     */
    public void testCreateAccountServiceSoap() 
    {   
        try {
            URL url = new URL("http://localhost:8080/kfs-dev/remoting/accountCreationServiceSOAP?wsdl");
            //URL url = new URL("https://test.kfs.kuali.org/kfs-cnv/remoting/accountCreationServiceSOAP?wsdl");
            QName qName = new QName("KFS", "accountCreationServiceSOAP");
            
            Service service = Service.create(url, qName);
            AccountCreationService accountService = (AccountCreationService)service.getPort(AccountCreationService.class);
                    
            AccountCreationStatusDTO creationStatus = accountService.createAccount(accountParameters);      
            System.out.println("account number: " + creationStatus.getAccountNumber());
            System.out.println("document number: " + creationStatus.getDocumentNumber());
            assertTrue(creationStatus.getStatus().equals("success"));
            
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
    
}
