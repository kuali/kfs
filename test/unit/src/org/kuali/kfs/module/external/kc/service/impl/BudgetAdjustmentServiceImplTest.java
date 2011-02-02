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

import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.module.external.kc.fixture.BudgetAdjustmentParameterDTOFixture;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentServiceTest;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class BudgetAdjustmentServiceImplTest extends BudgetAdjustmentServiceTest {
    private BudgetAdjustmentService budgetAdjustmentService;
    private DateTimeService dateTimeService;
    
    @Override
    protected void setUp() throws Exception 
    {
        super.setUp();
        // Initialize service objects.
        budgetAdjustmentService =  SpringContext.getBean(BudgetAdjustmentService.class);
        
        // Initialize objects.
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);
        System.out.println( "BA Allowed Years: " + fiscalYearFunctionControlService.getBudgetAdjustmentAllowedYears() );
        System.out.println( "Testing FY: " + TestUtils.getFiscalYearForTesting() );
        // ensure we have an active BA document for the given year
        FiscalYearFunctionControl fyfc = new FiscalYearFunctionControl();
        fyfc.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        fyfc.setFinancialSystemFunctionControlCode("BAACTV");
        FiscalYearFunctionControl existingFyfc = (FiscalYearFunctionControl) SpringContext.getBean(BusinessObjectService.class).retrieve(fyfc);
        if ( existingFyfc != null ) {
            fyfc = existingFyfc;
            fyfc.setFinancialSystemFunctionActiveIndicator(true);
        }
        SpringContext.getBean(BusinessObjectService.class).save(fyfc);
        System.out.println( "BA Allowed Years (after update): " + fiscalYearFunctionControlService.getBudgetAdjustmentAllowedYears() );
    }
    
    /**
     * This method will create AccountsParameters with test values...
     * @return accountParameters
     */
    public BudgetAdjustmentParametersDTO getBudgetAdjustmentParameters() {
        return BudgetAdjustmentParameterDTOFixture.CONTROL_1.createBudgetAdjustmentParameters();
 
    }
   
    
    /**
     * This method tests the service locally
     */
//    public void testBudgetAdjustmentServiceLocally() 
//    {  
//        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = getBudgetAdjustmentParameters();
//        GlobalVariables.setUserSession(new UserSession( KIMServiceLocator.getIdentityManagementService().getPrincipal( budgetAdjustmentParametersDTO.getPrincipalId() ).getPrincipalName() ));
//        org.kuali.rice.kew.web.session.UserSession.setAuthenticatedUser( new org.kuali.rice.kew.web.session.UserSession(budgetAdjustmentParametersDTO.getPrincipalId()));
//        //set the ACCOUNT_AUTO_CREATE_ROUTE as "save"
//        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class,  KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_SAVE);
//
//        BudgetAdjustmentCreationStatusDTO status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
//        assertTrue("Errors during service call - save only: " + status.getErrorMessages(), status.getErrorMessages().isEmpty());
//        
//        //set the ACCOUNT_AUTO_CREATE_ROUTE as "route"
//        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_ROUTE);
////      // the document should be submitted....
//         status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
//        assertTrue("Errors during service call - route: " + status.getErrorMessages(), status.getErrorMessages().isEmpty());
//
//      //  TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE);
//     // the document should be blanket approved.....
//      //   status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
//    //    assertTrue(status.getErrorMessages().isEmpty());
//
//        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, "I");
////      // the document should be submitted....
//         status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
////       //we want to test for failure of the routing by using routing value not defined for the system parameter...
//        assertFalse( "Service call should have failed.", status.getErrorMessages().isEmpty());
//    }

    
    /**
     * This method tests the service using KSB, but locally 
     */
//    public void testBudgetAdjustmentServiceWithKSB() {
//        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = getBudgetAdjustmentParameters();
//
//        try {
//            // BudgetAdjustmentService budgetAdjustService = (BudgetAdjustmentService) GlobalResourceLoader.getService(new QName(KFSConstants.Reserch.KC_NAMESPACE_URI, KFSConstants.Reserch.KC_UNIT_SERVICE));
//
//            
//            URL url = new URL("http://localhost:8080/remoting/budgetAdjustmentServiceSOAP");
//            QName qName = new QName("KFS", "budgetAdjustmentServiceSoap");
// 
//            Service service = Service.create(url, qName);
//            BudgetAdjustmentService budgetAdjustService = (BudgetAdjustmentService) service.getPort(BudgetAdjustmentService.class);
//            BudgetAdjustmentCreationStatusDTO creationStatus = budgetAdjustService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
//                    
//            System.out.println("doc number: " + creationStatus.getDocumentNumber());            
//            assertTrue(creationStatus.getStatus().equals("success"));
//            
//        } catch (Exception e) {
//            System.out.println("error: " + e.getMessage());
//        }
//
//    }

}
