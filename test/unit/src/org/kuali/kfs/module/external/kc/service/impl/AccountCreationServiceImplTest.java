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
package org.kuali.kfs.module.external.kc.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.dto.AccountParameters;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ObjectUtils;

@ConfigureContext(session = khuntley)
public class AccountCreationServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreationServiceImplTest.class);    
    
    private AccountCreationServiceImpl accountCreationServiceImpl;
    private DateTimeService dateTimeService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dateTimeService = SpringContext.getBean(DateTimeService.class);
        accountCreationServiceImpl = (AccountCreationServiceImpl) SpringContext.getService("kcMockAccountCreationService");
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
     * This method will create a CgDocument for the document type ACCT.  Successful if there are no error messages
     */
    public void testCreateCGAccountMaintenanceDocument() {
        List<String> errorMessages = new ArrayList();
        
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) accountCreationServiceImpl.createCGAccountMaintenanceDocument(errorMessages);
        assertTrue(ObjectUtils.isNotNull(maintenanceAccountDocument));
    }

    /**
     * This method will create AccountsParameters with test values...
     * @return accountParameters
     */
    public AccountParameters getAccountParameters() {
        
        AccountParameters accountParameters = new AccountParameters();
        accountParameters.setAccountName("Test Account Name");
        accountParameters.setAccountNumber("1031400");
        accountParameters.setCfdaNumber("123456");
        accountParameters.setExpenseGuidelineText("expense guidelines");
        accountParameters.setIncomeGuidelineText("income guidelines");
        accountParameters.setPurposeText("purpose text");
        accountParameters.setOffCampusIndicator(true);
        accountParameters.setEffectiveDate(dateTimeService.getCurrentDate());
        accountParameters.setExpirationDate(dateTimeService.getCurrentDate());

        return accountParameters;
    }

    /**
     * This method will construct AccountAutoCreateDefaults object with default values
     * @return accountAutoCreateDefaults
     */
    public AccountAutoCreateDefaults getAccountAutoCreateDefaults() {
        
        AccountAutoCreateDefaults defaults = new AccountAutoCreateDefaults();
        defaults.setKcUnit("testUnit");

        return defaults;
    }
    
    /**
     * This method will test the creation of a CgDocument and try to route it based on system parameter value...
     */
    public void NoRun_testCreateRouteAutomaticCGAccountDocument() {
        List<String> errorMessages = new ArrayList();
        
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) accountCreationServiceImpl.createCGAccountMaintenanceDocument(errorMessages);
        assertTrue(ObjectUtils.isNotNull(maintenanceAccountDocument));

        maintenanceAccountDocument.getDocumentHeader().setDocumentDescription("Automatic CG Account Document Creation");
        
        //create accountparameters and defaults and then use these two to create account object
        AccountParameters accountParameters = this.getAccountParameters();
        AccountAutoCreateDefaults defaults = this.getAccountAutoCreateDefaults();
        Account account = accountCreationServiceImpl.createAccountObject(accountParameters, defaults, errorMessages);
        
        maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
        
        String systemParameterRouteValue = KFSConstants.WORKFLOW_DOCUMENT_NO_SUBMIT;
        // the document should be saved....
        accountCreationServiceImpl.createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, systemParameterRouteValue, errorMessages);
        assertTrue(maintenanceAccountDocument.getDocumentHeader().getWorkflowDocument().stateIsSaved());
        
        systemParameterRouteValue = KFSConstants.WORKFLOW_DOCUMENT_SUBMIT;
        // the document should be submitted....
        accountCreationServiceImpl.createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, systemParameterRouteValue, errorMessages);
        assertTrue(maintenanceAccountDocument.getDocumentHeader().getWorkflowDocument().stateIsProcessed());
        
        systemParameterRouteValue = KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE;
        // the document should be blanket approved....
        accountCreationServiceImpl.createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, systemParameterRouteValue, errorMessages);
        assertTrue(maintenanceAccountDocument.getDocumentHeader().getWorkflowDocument().stateIsApproved());
    }
}
