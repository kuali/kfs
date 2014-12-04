/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
