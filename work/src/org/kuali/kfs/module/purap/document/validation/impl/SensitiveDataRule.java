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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * This class validates the SensitiveData maintenance document.
 */
public class SensitiveDataRule extends MaintenanceDocumentRuleBase {

    /**
     * This method creates a new ServiceBillingControl instance and returns it.
     * 
     * @return The new business object that this maintenance doc is creating.
     */
    private SensitiveData getNewBusinessObject() {
        return (SensitiveData)getNewBo();
    }

    /**
     * This method performs custom business rule checks on the document being saved.  The rules include confirming the 
     * validity of the work group.  This method always returns true, because saves should always succeed, regardless 
     * of business rule failures.
     * 
     * @param document The document being saved.
     * @return This method always returns true.  Saves should always succeed, regardless of business rule errors encountered 
     * or reported in the data.
     * 
     * @see MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        //isValidWorkgroup(document.getDocumentBusinessObject().getClass());
        // Save always succeeds, even if there are business rule failures
        return true;
    }

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
        //valid &= isValidWorkgroup(document.getDocumentBusinessObject().getClass());
        return valid;
    }

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        valid &= validateInactivationBlocking();
        return valid;
    }
    /**
     * Adds a global error for the sensitiveDataWorkgroupName field if it doesn't exist or is inactive.
     * 
     * @return Whether it exists and is active
     *
    private boolean isValidWorkgroup(Class businessObjectClass) {
        String name = getNewBusinessObject().getSensitiveDataWorkgroupName();
        if (StringUtils.isNotBlank(name)) {
            if (!workgroupExistsAndIsActive(name)) {
                putFieldErrorWithShortLabel(PurapPropertyConstants.WORKGROUP_NAME, KFSKeyConstants.ERROR_EXISTENCE);
                return false;
            }
        }
        return true;
    }
    */

    /**
     * Checks whether the given workgroup exists and is active.
     * 
     * @param name The name of the workgroup to check.
     * @return Whether the given workgroup exists and is active.
     *
    private static boolean workgroupExistsAndIsActive(String name) {
        try {
            WorkgroupDTO workgroupVo = SpringContext.getBean(KualiWorkflowInfo.class).getWorkgroup(new WorkgroupNameIdDTO(name));
            return workgroupVo != null && workgroupVo.isActiveInd();
        }
        catch (WorkflowException e) {
            return false;
        }
    }
    */
    
    private boolean validateInactivationBlocking() {
        SensitiveData oldSensitiveData = (SensitiveData)getOldBo();
        SensitiveData newSensitiveData = (SensitiveData)getNewBo();
        if (oldSensitiveData.isActive() && !newSensitiveData.isActive()) {
            if (hasABlockingRecord(newSensitiveData.getSensitiveDataCode())) {
                String documentLabel = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(newSensitiveData.getClass());
                putGlobalError(PurapKeyConstants.ERROR_CANNOT_INACTIVATE_USED_BY_ACTIVE_RECORDS, documentLabel);
                return false;
            }
        }
        return true;
    }
    
    private boolean hasABlockingRecord(String sensitiveDataCode) {
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
