/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.authorization;
import java.util.Set;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborEnterpriseFeedStep;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.krad.bo.BusinessObject;

public class BenefitsCalculationMaintenanceDocumentPresentationController extends MaintenanceDocumentPresentationControllerBase implements InquiryPresentationController {

    private ParameterService parameterService;
    
    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenPropertyNames(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);
        
        String offsetParmValue = getParameterService().getParameterValueAsString(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);
        
        if(offsetParmValue.equalsIgnoreCase("n")) {
            fields.add(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME);
            fields.add(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME);
        } else {
            fields.remove(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME);
            fields.remove(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME);
        }
        
        return fields;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        if(parameterService == null){
            parameterService = (ParameterService)GlobalResourceLoader.getService( "parameterService" );
        }
        return parameterService;
    }

}
