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

import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentParametersDTO;
 
public enum BudgetAdjustmentParameterDTOLineFixture {
    /*
    DETAIL_F_LINE1("F","BL","2231473","5197","0.00","100.00", "0"),
    DETAIL_T_LINE1("T","BL","0142900","5000","0.00","100.00","0");
    */
    /* BA examples from Damon
    DETAIL_F_LINE1("F","BL","1031400","1800","0.00","100.00", "0"),
    DETAIL_T_LINE1("F","BL","1031400","5000","0.00","100.00","0"),
    DETAIL_F_LINE2("F","BA","6044900","4100","0.00","100.00", "0"),
    DETAIL_T_LINE2("T","BA","6044900","6000","0.00","100.00","0");
    */
    /*
    DETAIL_F_LINE1("BL","1031400","1800","-100.00",""),
    DETAIL_T_LINE1("BL","1031400","5000","-100.00",""),
    DETAIL_F_LINE2("BA","6044900","4100","-100.00",""),
    DETAIL_T_LINE2("BA","6044900","6000","100.00",""),
    */

    DETAIL_LINE1("BL","1031400","6200","20.00",""),
    DETAIL_LINE2("BL","1031400","5002","30.00",""),
    DETAIL_LINE3("BL","1031400","4620","100.00",""),
    DETAIL_LINE4("BL","1031400","4087","50.00",""),
    DETAIL_LINE5("BL","1031400","5500","4.00",""),
    DETAIL_LINE6("BL","1031400","5500","0.50",""),
    DETAIL_LINE7("BL","1031400","5500","4.00",""),
    DETAIL_LINE8("BL","1031400","5047","1.08",""),
    DETAIL_LINE9("BL","1031400","5600","0.32",""),
    DETAIL_LINE10("BL","1031400","2003","100.00",""),
    DETAIL_LINE11("BL","1031400","5500","135.50",""),
    DETAIL_LINE12("BL","1031400","5500","8.00",""),
    DETAIL_LINE13("BL","1031400","5500","11.14",""),
    DETAIL_LINE14("BL","1031400","5500","1.00",""),
    DETAIL_LINE15("BL","1031400","5500","8.00",""),
    DETAIL_LINE16("BL","1031400","5500","185.50",""),
    DETAIL_LINE17("BL","1031400","5500","25.00",""),
    DETAIL_LINE18("BL","1031400","5047","2.00","");
    /*
    DETAIL_LINE1("BL","9898989","4620","6538.44",""),
    DETAIL_LINE2("BL","9898989","6200","20.00",""),
    DETAIL_LINE3("BL","9898989","5002","200.00",""),
    DETAIL_LINE4("BL","9898989","0890","1722.56",""),
    DETAIL_LINE5("BL","9898989","5021","1000.00",""),
    DETAIL_LINE6("BL","9898989","0110","421.40","");
    */
    private String chart;
    private String account;
    private String objectCode;
    private String currentBudgetAdjustAmount;
    private String projectCode;

  private BudgetAdjustmentParameterDTOLineFixture( String chart, 
          String account, String objectCode, String currentBudgetAdjustAmount,String projCode) {
      this.chart = chart;
      this.account = account;
      this.objectCode = objectCode;
      this.currentBudgetAdjustAmount = currentBudgetAdjustAmount;
      this.projectCode = projCode;    
  }
  
  public BudgetAdjustmentParametersDTO.Details createBudgetAdjustmentParameterDTO() {
        
        BudgetAdjustmentParametersDTO.Details detail = new BudgetAdjustmentParametersDTO.Details();
        detail.setChart(this.chart);
        detail.setAccount(this.account);
        detail.setObjectCode(this.objectCode);
        detail.setCurrentAmount(this.currentBudgetAdjustAmount);
        detail.setProjectCode(projectCode);
        return detail;
     }

}
