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

import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.sys.fixture.UserNameFixture;

public enum BudgetAdjustmentParameterDTOFixture {
    
    CONTROL_1(UserNameFixture.khuntley,"10","2011","1234","1.0","Mock BudgetAdjustment test data");
    
    private UserNameFixture initiator;
    private String postingPeriodCode;
    private String postingYear;
    private String awardDocumentNumber;
    private String budgetVersionNumber;
    private String comment;
    
    private BudgetAdjustmentParameterDTOFixture(UserNameFixture initiator, String postingPeriodCode, String postingYear, String awardDocumentNumber, String budgetVersionNumber, String comment) {
        this.initiator = initiator;
        this.postingPeriodCode = postingPeriodCode;
        this.postingYear = postingYear;
        this.awardDocumentNumber = awardDocumentNumber;
        this.budgetVersionNumber = budgetVersionNumber;
        this.comment = comment;
    }

    public BudgetAdjustmentParametersDTO createBudgetAdjustmentParameters() {
        
        BudgetAdjustmentParametersDTO budgetAdjustmentParametersDTO = new BudgetAdjustmentParametersDTO();
        budgetAdjustmentParametersDTO.setPrincipalId(initiator.getPerson().getPrincipalId());
        budgetAdjustmentParametersDTO.setPostingPeriodCode(postingPeriodCode);
        budgetAdjustmentParametersDTO.setPostingYear(postingYear);
        budgetAdjustmentParametersDTO.setAwardDocumentNumber(awardDocumentNumber);
        budgetAdjustmentParametersDTO.setBudgetVersionNumber(budgetVersionNumber);
        budgetAdjustmentParametersDTO.setComment(comment);
        List<BudgetAdjustmentParametersDTO.Details> details = new ArrayList<BudgetAdjustmentParametersDTO.Details>();
        budgetAdjustmentParametersDTO.setDetails(details);
        details.add( BudgetAdjustmentParameterDTOLineFixture.DETAIL_F_LINE1.createBudgetAdjustmentParameterDTO());
        details.add( BudgetAdjustmentParameterDTOLineFixture.DETAIL_T_LINE1.createBudgetAdjustmentParameterDTO());
        return budgetAdjustmentParametersDTO;
    }
}
