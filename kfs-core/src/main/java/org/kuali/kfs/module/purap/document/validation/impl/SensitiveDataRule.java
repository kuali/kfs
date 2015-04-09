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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Validates the SensitiveData maintenance document.
 */
public class SensitiveDataRule extends MaintenanceDocumentRuleBase {

    /**
     * This method performs custom route business rule checks on the document being routed.  The rules include confirming the 
     * validity of the work group.
     * 
     * @param document The document being routed.
     * @return True if all the business rules pass, false otherwise.
     * 
     * @see MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        valid &= validateInactivationBlocking();
        return valid;
    }

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        valid &= validateInactivationBlocking();
        return valid;
    }

    protected boolean validateInactivationBlocking() {
        SensitiveData oldSensitiveData = (SensitiveData)getOldBo();
        SensitiveData newSensitiveData = (SensitiveData)getNewBo();
        if (oldSensitiveData.isActive() && !newSensitiveData.isActive()) {
            if (hasABlockingRecord(newSensitiveData.getSensitiveDataCode())) {
                String documentLabel = "SensitiveData"; //SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(newSensitiveData.getClass());
                putGlobalError(KFSKeyConstants.ERROR_CANNOT_INACTIVATE_USED_BY_ACTIVE_RECORDS, documentLabel);
                return false;
            }
        }
        return true;
    }
    
    protected boolean hasABlockingRecord(String sensitiveDataCode) {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("sensitiveDataCode", sensitiveDataCode);

        //Check whether there are any PurchaseOrderSensitiveData whose sensitiveDataCode match with this SensitiveData's code
        boolean hasPurchaseOrderSensitiveDataBlockingRecord = SpringContext.getBean(BusinessObjectService.class).countMatching(PurchaseOrderSensitiveData.class, queryMap) > 0;
        
        queryMap.put("active", true);
        //Check whether there are any active CommodityCode whose sensitiveDataCode match with this SensitiveData's code
        boolean hasCommodityCodeBlockingRecord = SpringContext.getBean(BusinessObjectService.class).countMatching(CommodityCode.class, queryMap) > 0;
        
        return hasPurchaseOrderSensitiveDataBlockingRecord || hasCommodityCodeBlockingRecord;
    }

}
