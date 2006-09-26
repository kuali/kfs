/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.ServiceBillingControl;

import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class validates the ServiceBillingControl maintenance document.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class ServiceBillingControlRule extends MaintenanceDocumentRuleBase {

    /**
     * @return the new BO that this maint doc is creating
     */
    private ServiceBillingControl getNewServiceBillingControl() {
        return (ServiceBillingControl) getNewBo();
    }

    /**
     * @see MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        isValidWorkgroup();
        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
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
     * @return whether it exists and is active
     */
    private boolean isValidWorkgroup() {
        String name = getNewServiceBillingControl().getWorkgroupName();
        if (StringUtils.isNotBlank(name)) {
            if (!workgroupExistsAndIsActive(name)) {
                putFieldErrorWithShortLabel(PropertyConstants.WORKGROUP_NAME, KeyConstants.ERROR_EXISTENCE);
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given workgroup exists and is active.
     * 
     * @param name the workgroup to check
     * @return whether the given workgroup exists and is active.
     */
    private static boolean workgroupExistsAndIsActive(String name) {
        try {
            WorkgroupVO workgroupVo = SpringServiceLocator.getWorkflowInfoService().getWorkgroup(new WorkgroupNameIdVO(name));
            return workgroupVo != null && workgroupVo.isActiveInd();
        }
        catch (WorkflowException e) {
            return false;
        }
    }
}
