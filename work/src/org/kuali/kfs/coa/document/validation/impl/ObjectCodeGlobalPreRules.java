/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCodeGlobal;
import org.kuali.module.chart.bo.ObjectCodeGlobalDetail;


public class ObjectCodeGlobalPreRules extends MaintenancePreRulesBase {

    
    
    /**
     * Updates the university fiscal year when it is not already set.
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        ObjectCodeGlobal bo = (ObjectCodeGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        
        List<ObjectCodeGlobalDetail> details = bo.getObjectCodeGlobalDetails();
        
        for ( ObjectCodeGlobalDetail detail : details ) {
            if ( detail.getUniversityFiscalYear() == null ) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "setting fiscal year on ObjectCodeGlobalDetail: " + detail );
                }
                detail.setUniversityFiscalYear( new Integer( SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getCollectionFieldDefaultValue(
                        maintenanceDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(),
                        "objectCodeGlobalDetails", "universityFiscalYear" ) ) );
            }
        }
        
        return true;
    }    
}
