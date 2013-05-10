/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

public class ACIModelRule extends MaintenanceDocumentRuleBase{
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SecurityRule.class);
    private AutomatedCashInvestmentModel newACIModel;
    
    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        
        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        AutomatedCashInvestmentModel newACIModel = (AutomatedCashInvestmentModel) document.getNewMaintainableObject().getBusinessObject();
        isValid = checkTotalPercentageEqualOne(newACIModel);        
        return isValid;
    }

    protected boolean checkTotalPercentageEqualOne(AutomatedCashInvestmentModel newACIModel){
        boolean isTotalPercentageEqualOne = true; 
        
//      BigDecimal investment1Percent = newACIModel.getInvestment1Percent().bigDecimalValue();
        BigDecimal investment1Percent = newACIModel.getInvestment1Percent();
        BigDecimal investment2Percent = newACIModel.getInvestment2Percent();
        BigDecimal investment3Percent = newACIModel.getInvestment3Percent();
        BigDecimal investment4Percent = newACIModel.getInvestment4Percent();
     
        BigDecimal total= new BigDecimal(0.0000);
        if ((newACIModel.getInvestment1SecurityID()!= null) && (investment1Percent != null)){
            total = total.add(investment1Percent);
        }
        if ((newACIModel.getInvestment2SecurityID()!= null) && (investment2Percent) != null){
            total = total.add(investment2Percent);
        }
        if ((newACIModel.getInvestment3SecurityID()!= null) && (investment3Percent != null)){
            total = total.add(investment3Percent);
        }
        if ((newACIModel.getInvestment4SecurityID()!= null) && (investment4Percent !=null)){
            total = total.add(investment4Percent);  
        }
                
        if ((total.compareTo(new BigDecimal(1.00)))!=0) {
            putFieldError(EndowPropertyConstants.INVESTMENT_1_PERCENT, EndowKeyConstants.ACIModelConstants.ERROR_TOTAL_OF_ALL_PERCENTAGES_NOT_EQUAL_ONE);
            isTotalPercentageEqualOne = false;
        }
        
        return isTotalPercentageEqualOne;
    }
}
