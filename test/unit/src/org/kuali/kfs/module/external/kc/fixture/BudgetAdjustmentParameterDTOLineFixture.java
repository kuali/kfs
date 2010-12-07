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
 
public enum BudgetAdjustmentParameterDTOLineFixture {
    DETAIL_F_LINE1("F","BL","2231473","5197","0.00","100.00", "0"),
    DETAIL_T_LINE1("T","BL","0142900","5000","0.00","100.00","0");
    
    private String lineType;
    private String chart;
    private String account;
    private String objectCode;
    private String amount;
    private String currentBudgetAdjustAmount;
    private String baseBudgetAdjustAmount;

    
  private BudgetAdjustmentParameterDTOLineFixture(String lineType, String chart, 
          String account, String objectCode, String amount,
          String currentBudgetAdjustAmount, String baseBudgetAdjustAmount) {
      this.lineType = lineType;
      this.chart = chart;
      this.account = account;
      this.objectCode = objectCode;
      this.amount = amount;
      this.currentBudgetAdjustAmount = currentBudgetAdjustAmount;
      this.baseBudgetAdjustAmount = baseBudgetAdjustAmount;
  }
  
  public BudgetAdjustmentParametersDTO.Details createBudgetAdjustmentParameterDTO() {
        
        BudgetAdjustmentParametersDTO.Details detail = new BudgetAdjustmentParametersDTO.Details();
        detail.setLineType(this.lineType);
        detail.setChart(this.chart);
        detail.setAccount(this.account);
        detail.setObjectCode(this.objectCode);
        detail.setAmount(this.amount);
        detail.setCurrentBudgetAdjustAmount(this.currentBudgetAdjustAmount);
        detail.setBaseBudgetAdjustAmount(this.baseBudgetAdjustAmount);
        return detail;
     }

}
