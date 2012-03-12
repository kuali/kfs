/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.sys.fixture.UserNameFixture;

public enum BudgetAdjustmentParameterDTOFixture {
    
    CONTROL_1(UserNameFixture.khuntley,"Award ID 0"," Auto Mock BudgetAdjustment test data","TEST","odn"),
    CONTROL_2(UserNameFixture.khuntley,"Award ID 1","Auto Mock BudgetAdjustment test data","TEST","odn"),
    CONTROL_3(UserNameFixture.khuntley,"Award ID 1","Auto Mock BudgetAdjustment test data","TEST","odn");   
    
    private UserNameFixture initiator;
    private String description;
    private String explanation;
    private String sponsorTypeCode;
    private String orgDocNumber;
     
    private BudgetAdjustmentParameterDTOFixture(UserNameFixture initiator, String description, String explanation,String sponsorType, String odm) {
        this.initiator = initiator;
        this.description = description;
        this.explanation = explanation;
        this.sponsorTypeCode = sponsorType;
        this.orgDocNumber = odm;
     }

    public BudgetAdjustmentParametersDTO createBudgetAdjustmentParameters() {
        
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = new BudgetAdjustmentParametersDTO();
        budgetAdjustmentParametersDTO.setPrincipalId(initiator.getPerson().getPrincipalId());
        budgetAdjustmentParametersDTO.setDescription(description);
        budgetAdjustmentParametersDTO.setExplanation(explanation);
        budgetAdjustmentParametersDTO.setSponsorType(sponsorTypeCode);
        budgetAdjustmentParametersDTO.setOrgDocNumber(orgDocNumber);
        List<BudgetAdjustmentParametersDTO.Details> details = new ArrayList<BudgetAdjustmentParametersDTO.Details>();
        budgetAdjustmentParametersDTO.setDetails(details);
        return budgetAdjustmentParametersDTO;
    }
    
    public String getSystemParameterResearchSponsorType() {
        return "TEST=1133;1=1133;2=1133;";
        //return "0=0896;1=0896";
    }
}
