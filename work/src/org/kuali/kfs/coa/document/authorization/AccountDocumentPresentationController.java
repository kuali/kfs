/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.document.authorization;

import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class AccountDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDocumentPresentationController.class);

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        setLaborBenefitRateCategoryCodeEditable(readOnlyPropertyNames);
        return readOnlyPropertyNames;
    }

//    /**
//     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenPropertyNames(org.kuali.rice.kns.bo.BusinessObject)
//     */
//    @Override
//    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
//        Set<String> hiddenPropertyNames = super.getConditionallyHiddenPropertyNames(businessObject);
//        setLaborBenefitRateCategoryCodeHidden(hiddenPropertyNames);
//        return hiddenPropertyNames;
//    }

    /**
     * 
     * @return
     */
    protected Boolean getFridgeBenefitCalculationEnableIndicator(){
        AccountService service = SpringContext.getBean(AccountService.class);
        return service.isFridgeBenefitCalculationEnable();
    }
    
    /**
     * 
     * Sets the Labor Benefit Rate Category Code, otherwise leave
     * it read/wrtie.
     * 
     * @param readOnlyPropetyNames
     */
    protected void setLaborBenefitRateCategoryCodeEditable(Set<String> readOnlyPropertyNames){
        Boolean isFridgeBenefitCalcEnable = getFridgeBenefitCalculationEnableIndicator();
        
        //default null to false, if FridgeBenefitCalculation is NOT enable - makes code not editable 
        if ( ! isFridgeBenefitCalcEnable ){
            readOnlyPropertyNames.add(KFSPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE);
        }
    }
    
    /**
     * 
     * Hides the Labor Benefit Rate Category Code depending on the system parameter ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND
     * 
     * @param hiddenPropetyNames
     */
    protected void setLaborBenefitRateCategoryCodeHidden(Set<String> hiddenPropertyNames){
        Boolean isFridgeBenefitCalcEnable = getFridgeBenefitCalculationEnableIndicator();
        
        //default null to false, if FridgeBenefitCalculation is NOT enable - makes code not editable 
        if ( ! isFridgeBenefitCalcEnable ){
            hiddenPropertyNames.add(KFSPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE);
        }
    }
    
}
