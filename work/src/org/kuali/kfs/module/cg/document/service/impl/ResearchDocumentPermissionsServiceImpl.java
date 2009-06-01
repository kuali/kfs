/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.service.impl;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.service.BusinessObjectService;

public class ResearchDocumentPermissionsServiceImpl implements ResearchDocumentPermissionsService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentPermissionsServiceImpl.class);

    private BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.module.kra.budget.service.BudgetPermissionsService#getBudgetPermissionType(String orgXml, String documentType,
     *      String uuid)
     */
    public boolean isUserInOrgHierarchy(String orgXml, String documentType, String uuid) {
        ReportCriteriaDTO criteria = new ReportCriteriaDTO();
        criteria.setDocumentTypeName(documentType);
        criteria.setNodeNames(new String[] { CGConstants.ORG_REVIEW_NODE_NAME });
        criteria.setRuleTemplateNames(new String[] { CGConstants.ORG_REVIEW_TEMPLATE_NAME });
        criteria.setXmlContent(orgXml);
        WorkflowInfo info = new WorkflowInfo();
        try {
            DocumentDetailDTO detail = info.routingReport(criteria);
            return isUserInRequests(detail.getActionRequests(), uuid);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Exception generating routing report: " + e);
        }
    }

    /**
     * Check whether given user is in the given action requests.
     * 
     * @param ActionRequestDTO[] requests
     * @param String uuid
     * @return boolean
     */
    private boolean isUserInRequests(ActionRequestDTO[] requests, String principalId) {
        for (int i = 0; i < requests.length; i++) {
            ActionRequestDTO request = (ActionRequestDTO) requests[i];
            if (request.getPrincipalId().equals(principalId)) {
                return true;
            }
            if (isUserInRequests(request.getChildrenRequests(), principalId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Setter for BusinessObjectService property.
     * 
     * @param BusinessObjectService businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

