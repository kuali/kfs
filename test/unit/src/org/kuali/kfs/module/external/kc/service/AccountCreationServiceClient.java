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

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.TestCase;

import org.kuali.kfs.module.external.kc.dto.AccountCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.AccountParametersDTO;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.service.KSBServiceLocator;

public class AccountCreationServiceClient extends TestCase
{
    private AccountParametersDTO accountParameters;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception 
    {
        // Initialize objects.
        accountParameters = new AccountParametersDTO();
        accountParameters.setAccountNumber("123456");
        
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
     * This method tests the remote Service without using KSB
     */
    public void testCreateAccountServiceSoap() 
    {   
        try {
            URL url = new URL("http://localhost:8080/kfs-dev/remoting/accountCreationServiceSOAP?wsdl");
            QName qName = new QName("KFS", "accountCreationServiceSOAP");
            
            Service service = Service.create(url, qName);
            AccountCreationService accountService = (AccountCreationService)service.getPort(AccountCreationService.class);
                    
            AccountCreationStatusDTO creationStatus = accountService.createAccount(accountParameters);      
            System.out.println("account number: " + creationStatus.getAccountNumber());            
            assertTrue(creationStatus.isSuccess());
            
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
    
    /**
     * This method tests the remote Service using KSB
     * Works with the kuali environment (KSB should be up), but not here
     */
    public void testCreateAccountServiceNoSoap() {
        
        try {                        
            //AccountCreationService accountService = (AccountCreationService) GlobalResourceLoader.getService(new QName("KFS", "accountCreationServiceLocal"));
            AccountCreationService accountService = (AccountCreationService) GlobalResourceLoader.getService("{KFS}accountCreationServiceLocal");
            
            AccountCreationStatusDTO creationStatus = accountService.createAccount(accountParameters);        
            System.out.println("account number: " + creationStatus.getAccountNumber());        
            assertTrue(creationStatus.isSuccess());
            
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

}
