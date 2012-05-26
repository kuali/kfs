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

import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.fixture.BudgetAdjustmentParameterDTOFixture;
import org.kuali.kfs.module.external.kc.fixture.BudgetAdjustmentParameterDTOLineFixture;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentServiceTest;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class BudgetAdjustmentServiceImplTest extends BudgetAdjustmentServiceTest {
    private BudgetAdjustmentService budgetAdjustmentService;
    private DateTimeService dateTimeService;
    private Integer fiscalYear;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Initialize service objects.       // budgetAdjustmentDocument.getDocumentHeader().setDocumentNumber(parameters.getDocumentNumber());

        budgetAdjustmentService = SpringContext.getBean(BudgetAdjustmentService.class);

        // Initialize objects.
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);
        System.out.println("BA Allowed Years: " + fiscalYearFunctionControlService.getBudgetAdjustmentAllowedYears());
        fiscalYear = TestUtils.getFiscalYearForTesting();
  
        System.out.println("Testing FY: " + fiscalYear);
        // ensure we have an active BA document for the given year
        FiscalYearFunctionControl fyfc = new FiscalYearFunctionControl();
        fyfc.setUniversityFiscalYear(fiscalYear);
        fyfc.setFinancialSystemFunctionControlCode("BAACTV");
        FiscalYearFunctionControl existingFyfc = (FiscalYearFunctionControl) SpringContext.getBean(BusinessObjectService.class).retrieve(fyfc);
        if (existingFyfc != null) {
            fyfc = existingFyfc;
            fyfc.setFinancialSystemFunctionActiveIndicator(true);
        }
         //

        SpringContext.getBean(BusinessObjectService.class).save(fyfc);
        System.out.println("BA Allowed Years (after update): " + fiscalYearFunctionControlService.getBudgetAdjustmentAllowedYears());
    }

    /**
     * This method will create AccountsParameters with test values...
     * 
     * @return accountParameters
     */
    public List<BudgetAdjustmentParametersDTO> getBudgetAdjustmentParameters() {
        List<BudgetAdjustmentParametersDTO> list = new ArrayList();
        /*
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO1 = BudgetAdjustmentParameterDTOFixture.CONTROL_1.createBudgetAdjustmentParameters();
        budgetAdjustmentParametersDTO1.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_F_LINE1.createBudgetAdjustmentParameterDTO());
        //budgetAdjustmentParametersDTO1.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_T_LINE1.createBudgetAdjustmentParameterDTO());
        list.add(budgetAdjustmentParametersDTO1);
        
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO2 = BudgetAdjustmentParameterDTOFixture.CONTROL_2.createBudgetAdjustmentParameters();
        budgetAdjustmentParametersDTO2.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_F_LINE2.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO2.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_T_LINE2.createBudgetAdjustmentParameterDTO());
        list.add(budgetAdjustmentParametersDTO2);
        */
        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_INCOME_OBJECT_CODES_BY_SPONSOR_TYPE, BudgetAdjustmentParameterDTOFixture.CONTROL_3.getSystemParameterResearchSponsorType());

        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = BudgetAdjustmentParameterDTOFixture.CONTROL_3.createBudgetAdjustmentParameters();
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE1.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE2.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE3.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE4.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE5.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE6.createBudgetAdjustmentParameterDTO());
        
 
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE7.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE8.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE9.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE10.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE11.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE12.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE13.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE14.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE15.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE16.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE17.createBudgetAdjustmentParameterDTO());
        budgetAdjustmentParametersDTO.getDetails().add(BudgetAdjustmentParameterDTOLineFixture.DETAIL_LINE18.createBudgetAdjustmentParameterDTO());

        list.add(budgetAdjustmentParametersDTO);
        return list;
    }


    /**
     * This method tests the service locally
     */
    public void testBudgetAdjustmentServiceSave() {
        List<BudgetAdjustmentParametersDTO> budgetAdjustmentParametersDTOs = getBudgetAdjustmentParameters();
        // set the ACCOUNT_AUTO_CREATE_ROUTE as "save"
        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_SAVE);
        //
        for (BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO : budgetAdjustmentParametersDTOs) {
            BudgetAdjustmentCreationStatusDTO status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
            assertTrue("Errors during service call - save only: " + status.getErrorMessages(), status.getErrorMessages().isEmpty());
        }
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = false)
    public void testBudgetAdjustmentServiceRoute() throws Exception {
        List<BudgetAdjustmentParametersDTO> budgetAdjustmentParametersDTOs = getBudgetAdjustmentParameters();

        // //set the ACCOUNT_AUTO_CREATE_ROUTE as "route"
        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_ROUTE);
        // the document should be submitted....
        for (BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO : budgetAdjustmentParametersDTOs) {
            BudgetAdjustmentCreationStatusDTO status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
            assertTrue("Errors during service call - route: " + status.getErrorMessages(), status.getErrorMessages().isEmpty());
        }
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = false)
    public void testBudgetAdjustmentServiceBlanket() {
        List<BudgetAdjustmentParametersDTO> budgetAdjustmentParametersDTOs = getBudgetAdjustmentParameters();
        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE);
        // // the document should be blanket approved.....
        for (BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO : budgetAdjustmentParametersDTOs) {
            BudgetAdjustmentCreationStatusDTO status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
            assertTrue("Errors during service  call -blanketroute: " + status.getErrorMessages(), status.getErrorMessages().isEmpty());
        }
    }


    public void testBudgetAdjustmentServiceFail() {
        List<BudgetAdjustmentParametersDTO> budgetAdjustmentParametersDTOs = getBudgetAdjustmentParameters();
        // GlobalVariables.setUserSession(new
        // UserSession(SpringContext.getBean(IdentityManagementService.class).getPrincipal(budgetAdjustmentParametersDTO.getPrincipalId()).getPrincipalName()));
        // org.kuali.rice.krad.UserSession.setAuthenticatedUser(new
        // org.kuali.rice.krad.UserSession(budgetAdjustmentParametersDTO.getPrincipalId()));

        //
        TestUtils.setSystemParameter(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE, "I");
        // // // the document should be submitted....
        for (BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO : budgetAdjustmentParametersDTOs) {
            BudgetAdjustmentCreationStatusDTO status = budgetAdjustmentService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
            // // //we want to test for failure of the routing by using routing value not defined for the system parameter...
            assertFalse("Service call should have failed.", status.getErrorMessages().isEmpty());
        }
    }


    /**
     * This method tests      budgetAdjustmentSourceAccountingLine.the service using KSB, but locally
     */
    // public void NORUN_testBudgetAdjustmentServiceWithKSB() {
    // BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = getBudgetAdjustmentParameters();
    //
    // try {
    // // BudgetAdjustmentService budgetAdjustService = (BudgetAdjustmentService) GlobalResourceLoader.getService(new
    // QName(KFSConstants.Reserch.KC_NAMESPACE_URI, KFSConstants.Reserch.KC_UNIT_SERVICE));
    //
    //            
    // URL url = new URL("http://localhost:8080/remoting/budgetAdjustmentServiceSOAP");
    // QName qName = new QName("KFS", "budgetAdjustmentServiceSoap");
    // 
    // Service service = Service.create(url, qName);
    // BudgetAdjustmentService budgetAdjustService = (BudgetAdjustmentService) service.getPort(BudgetAdjustmentService.class);
    // BudgetAdjustmentCreationStatusDTO creationStatus = budgetAdjustService.createBudgetAdjustment(budgetAdjustmentParametersDTO);
    //                    
    // System.out.println("doc number: " + creationStatus.getDocumentNumber());
    // assertTrue(creationStatus.getStatus().equals("success"));
    //            
    // } catch (Exception e) {
    // System.out.println("error: " + e.getMessage());
    // }
    //
    // }

}
