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

import org.kuali.kfs.coa.document.validation.impl.AccountRule;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;

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
     * Sets the Labor Benefit Rate Category Code, otherwise leave
     * it read/wrtie.
     * 
     * @param readOnlyPropetyNames
     */
    protected void setLaborBenefitRateCategoryCodeEditable(Set<String> readOnlyPropertyNames){
        ParameterService service = SpringContext.getBean(ParameterService.class);
        
        //make sure the parameter exists
        if(service.parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY")){
          //check the system param to see if the labor benefit rate category should be editable
            String sysParam = SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY");
            LOG.debug("sysParam: " + sysParam);
            //if sysParam != Y then Labor Benefit Rate Category Code is not editable
            if (!sysParam.equalsIgnoreCase("Y")) {
                readOnlyPropertyNames.add("laborBenefitRateCategoryCode");
                
            }
        }else{
            LOG.debug("System paramter doesn't exist.  Making the Labor Benefit Rate Category Code not editable.");
            readOnlyPropertyNames.add("laborBenefitRateCategoryCode");
        }
    }
    
    /**
     * 
     * Hides the Labor Benefit Rate Category Code depending on the system parameter ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY
     * 
     * @param hiddenPropetyNames
     */
    protected void setLaborBenefitRateCategoryCodeHidden(Set<String> hiddenPropertyNames){
        ParameterService service = SpringContext.getBean(ParameterService.class);
        
        //make sure the parameter exists
        if(service.parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY")){
          //check the system param to see if the labor benefit rate category should be hidden
            String sysParam = SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY");
            LOG.debug("sysParam: " + sysParam);
            //if sysParam != Y then Labor Benefit Rate Category Code is hidden
            if (!sysParam.equalsIgnoreCase("Y")) {
                hiddenPropertyNames.add("laborBenefitRateCategoryCode");
                
            }
        }else{
            LOG.debug("System paramter doesn't exist.  Making the Labor Benefit Rate Category Code not editable.");
            hiddenPropertyNames.add("laborBenefitRateCategoryCode");
        }
    }
    
}
