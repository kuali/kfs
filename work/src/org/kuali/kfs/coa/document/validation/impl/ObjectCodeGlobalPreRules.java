/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.Collection;

import org.kuali.kfs.coa.businessobject.ObjectCodeGlobal;
import org.kuali.kfs.coa.businessobject.ObjectCodeGlobalDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;

/**
 * PreRules checks for the {@link ObjectCodeGlobal} that needs to occur while still in the Struts processing. This includes defaults
 */
public class ObjectCodeGlobalPreRules extends MaintenancePreRulesBase {


    /**
     * Updates the university fiscal year when it is not already set.
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        ObjectCodeGlobal bo = (ObjectCodeGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        Collection<ObjectCodeGlobalDetail> details = bo.getObjectCodeGlobalDetails();

        for (ObjectCodeGlobalDetail detail : details) {
            if (detail.getUniversityFiscalYear() == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("setting fiscal year on ObjectCodeGlobalDetail: " + detail);
                }
                detail.setUniversityFiscalYear(new Integer(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getCollectionFieldDefaultValue(maintenanceDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), "objectCodeGlobalDetails", "universityFiscalYear")));
            }
        }

        return true;
    }
}
