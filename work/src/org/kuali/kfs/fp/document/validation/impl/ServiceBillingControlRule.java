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
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.ServiceBillingControl;

import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class validates the ServiceBillingControl maintenance document.
 */
public class ServiceBillingControlRule extends MaintenanceDocumentRuleBase {

    /**
     * This method creates a new ServiceBillingControl instance and returns it.
     * 
     * @return The new business object that this maintenance doc is creating.
     */
    private ServiceBillingControl getNewServiceBillingControl() {
        return (ServiceBillingControl) getNewBo();
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
     * @see MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        isValidWorkgroup();
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
     * @see MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        valid &= isValidWorkgroup();
        return valid;
    }

    /**
     * Adds a global error for the workgroupName field if it doesn't exist or is inactive.
     * 
     * @return Whether it exists and is active
     */
    private boolean isValidWorkgroup() {
        String name = getNewServiceBillingControl().getWorkgroupName();
        if (StringUtils.isNotBlank(name)) {
            if (!workgroupExistsAndIsActive(name)) {
                putFieldErrorWithShortLabel(KFSPropertyConstants.WORKGROUP_NAME, KFSKeyConstants.ERROR_EXISTENCE);
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given workgroup exists and is active.
     * 
     * @param name The name of the workgroup to check.
     * @return Whether the given workgroup exists and is active.
     */
    private static boolean workgroupExistsAndIsActive(String name) {
        try {
            WorkgroupVO workgroupVo = SpringContext.getBean(KualiWorkflowInfo.class).getWorkgroup(new WorkgroupNameIdVO(name));
            return workgroupVo != null && workgroupVo.isActiveInd();
        }
        catch (WorkflowException e) {
            return false;
        }
    }
}
