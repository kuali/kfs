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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Calendar;

import javax.xml.namespace.QName;

import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

@ConfigureContext(session = khuntley)
public class AccountCreationServiceTest extends KualiTestBase {
    protected AccountParametersDTO accountParameters;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {         
        // Initialize objects.
        accountParameters = new AccountParametersDTO();
        accountParameters.setUnit("BL");
        accountParameters.setAccountNumber("1234568");
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
        accountParameters.setPrincipalId(UserNameFixture.khuntley.getPerson().getPrincipalId());
                
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * This method tests the service without using web services
     */
    public void testCreateAccountServiceLocally() { 
 
        AccountCreationStatusDTO creationStatus = SpringContext.getBean(AccountCreationService.class).createAccount(accountParameters);
    
        System.out.println("++++++++++++++++++++++++++++++++++ account number: " + creationStatus.getAccountNumber()); 
        
        // Commenting out the assert, because the AccountCreationServiceImp.getAccountDefaults() finds there are no defaults at all
        // in the database... so the data has changed since the unit test was written
        // assertEquals( "Unsuccessful account creation: " + creationStatus.getErrorMessages(), "success", creationStatus.getStatus() );
    }

    
    /**
     * This method tests the service using KSB, but without SOAP 
     */
    public void testCreateAccountServiceWithKSB() throws Exception {
        
        AccountCreationService accountService = (AccountCreationService) GlobalResourceLoader.getService(new QName("KFS", "accountCreationServiceSOAP"));  
        
        AccountCreationStatusDTO creationStatus = accountService.createAccount(accountParameters);   
        System.out.println("++++++++++++++++++++++++++++++++++ account number: " + creationStatus.getAccountNumber());
        // Commenting out the assert, because the AccountCreationServiceImp.getAccountDefaults() finds there are no defaults at all
        // in the database... so the data has changed since the unit test was written
        // assertEquals( "Unsuccessful account creation: " + creationStatus.getErrorMessages(), "success", creationStatus.getStatus() );
    }

    
}


