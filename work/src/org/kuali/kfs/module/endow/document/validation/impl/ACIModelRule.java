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
