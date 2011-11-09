/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.document.authorization;

import java.util.Set;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.BusinessObject;

public class ObjectCodeDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    
    /**
     * Determins whether to display the research admin attributes or not
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenSectionIds(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> hiddenSectionIds = super.getConditionallyHiddenSectionIds(businessObject);
        
        //  consult the system param on whether to hide the research admin attributes tab
        if (!"Y".equalsIgnoreCase(SpringContext.getBean(ParameterService.class).getParameterValueAsString(ObjectCode.class, KFSConstants.ObjectCodeConstants.PARAMETER_KC_ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND))) {
            hiddenSectionIds.add(KFSConstants.ObjectCodeConstants.SECTION_ID_RESEARCH_ADMIN_ATTRIBUTES);
        }

        return hiddenSectionIds;
    }

}
