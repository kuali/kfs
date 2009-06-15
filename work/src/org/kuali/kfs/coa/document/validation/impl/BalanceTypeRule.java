/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class BalanceTypeRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processRouteDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        boolean result = super.processRouteDocument(document);
        result &= checkForBlockingOffsetDefinitions((BalanceType)(((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject()));
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processSaveDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        boolean result = super.processSaveDocument(document);
        result &= checkForBlockingOffsetDefinitions((BalanceType)(((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject()));
        return result;
    }
    
    /**
     * Determines if the given document is inactivating the balance type being maintained
     * @param document the maintenance document maintaining a balance type
     * @return true if the document is inactivating that balance type, false otherwise
     */
    protected boolean isInactivating(MaintenanceDocument document) {
        if (!document.isEdit() || document.getOldMaintainableObject() == null) return false;
        return ((Inactivateable)document.getOldMaintainableObject().getBusinessObject()).isActive() && !((Inactivateable)document.getNewMaintainableObject().getBusinessObject()).isActive();
    }

    /**
     * Determines if the inactivating balance type should be blocked by offset definitions
     * @param balanceType the balance type to check
     * @return true if the balance type shouldn't be blocked by any existing offset definitions, false otherwise
     */
    protected boolean checkForBlockingOffsetDefinitions(BalanceType balanceType) {
        final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        boolean result = true;
        
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("financialBalanceTypeCode", balanceType.getFinancialBalanceTypeCode());
        
        final int matchingCount = businessObjectService.countMatching(OffsetDefinition.class, keys);
        if (matchingCount > 0) {
            putFieldError("financialBalanceTypeCode",KFSKeyConstants.ERROR_DOCUMENT_BALANCETYPMAINT_INACTIVATION_BLOCKING,new String[] {balanceType.getFinancialBalanceTypeCode(), Integer.toString(matchingCount), OffsetDefinition.class.getName()});
            result = false;
        }
        return result;
    }
}
