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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentServiceTest;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.service.DateTimeService;

@ConfigureContext(session = khuntley)
public class BudgetAdjustmentServiceImplTest extends BudgetAdjustmentServiceTest {
    private BudgetAdjustmentService budgetAdjustmentService;
    private DateTimeService dateTimeService;
    
    @Override
    protected void setUp() throws Exception 
    {
        // Initialize service objects.
        budgetAdjustmentService = 
            SpringContext.getBean(BudgetAdjustmentService.class);
        
        // Initialize objects.
        
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
     * This method will create AccountsParameters with test values...
     * @return accountParameters
     */
    public BudgetAdjustmentParametersDTO getBudgetAdjustmentParameters() {
        
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = new BudgetAdjustmentParametersDTO();
        budgetAdjustmentParametersDTO.setPrincipalId("khuntley");
        budgetAdjustmentParametersDTO.setPostingPeriodCode("10");
       // budgetAdjustmentParametersDTO.setPostingYear("2010");
        budgetAdjustmentParametersDTO.setAwardDocumentNumber("1234");
        budgetAdjustmentParametersDTO.setBudgetVersionNumber("1.0");
        budgetAdjustmentParametersDTO.setComment("Mock BudgetAdjustment test data");
  
        List<BudgetAdjustmentParametersDTO.Details> details = new ArrayList<BudgetAdjustmentParametersDTO.Details>();
        budgetAdjustmentParametersDTO.setDetails(details);
        
        BudgetAdjustmentParametersDTO.Details detail = new BudgetAdjustmentParametersDTO.Details();
        /*
        detail.setLineType("T");
        detail.setChart("BL");
        detail.setAccount("4631640");
        detail.setObjectCode("5000");
        detail.setAmount("0.00");
        detail.setCurrentBudgetAdjustAmount("100.00");
        detail.setBaseBudgetAdjustAmount("0");
        */
        details.add(detail);
 
        BudgetAdjustmentParametersDTO.Details detailF = new BudgetAdjustmentParametersDTO.Details();
        /*
        detailF.setLineType("F");
        detailF.setChart("BL");
        detailF.setAccount("4331300");
        detailF.setObjectCode("0701");
        detailF.setAmount("0.00");
        detailF.setCurrentBudgetAdjustAmount("100.00");
        detailF.setBaseBudgetAdjustAmount("0");
        */
        details.add(detailF);
        return budgetAdjustmentParametersDTO;
    }
   
    
    /**
     * This method tests the service locally
     */
    public void testBudgetAdjustmentServiceLocally() 
    {  
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = getBudgetAdjustmentParameters();
        //set the ACCOUNT_AUTO_CREATE_ROUTE as "save"
        TestUtils.setSystemParameter(Account.class,  KcConstants.BudgetAdjustmentService.PARAMETER_KC_BA_DOCUMENT_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_SAVE);

        BudgetAdjustmentCreationStatusDTO status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
        assertTrue(status.getErrorMessages().isEmpty());
        
        //set the ACCOUNT_AUTO_CREATE_ROUTE as "route"
        TestUtils.setSystemParameter(Account.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_BA_DOCUMENT_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_ROUTE);
//      // the document should be submitted....
         status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
        assertTrue(status.getErrorMessages().isEmpty());

        TestUtils.setSystemParameter(Account.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_BA_DOCUMENT_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE);
     // the document should be blanket approved.....
         status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
        assertTrue(status.getErrorMessages().isEmpty());

        TestUtils.setSystemParameter(Account.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_BA_DOCUMENT_ROUTE, "I");
//      // the document should be submitted....
         status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
//       //we want to test for failure of the routing by using routing value not defined for the system parameter...
        assertTrue(status.getErrorMessages().isEmpty());
    }

    
    /**
     * This method tests the service using KSB, but locally 
     */
    public void testBudgetAdjustmentServiceWithKSB() {
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = getBudgetAdjustmentParameters();
/*
        try {
            URL url = new URL("http://localhost:8080/wsdl");
            QName qName = new QName("KFS", "budgetAdjustmentServiceSoap");
            
            Service service = Service.create(url, qName);
            BudgetAdjustmentService budgetAdjustService = (BudgetAdjustmentService) service.getPort(BudgetAdjustmentService.class);
            BudgetAdjustmentCreationStatusDTO creationStatus = budgetAdjustService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
                    
            System.out.println("account number: " + creationStatus.getAccountNumber());            
            assertTrue(creationStatus.getStatus().equals("success"));
            
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
      */
    }

}
