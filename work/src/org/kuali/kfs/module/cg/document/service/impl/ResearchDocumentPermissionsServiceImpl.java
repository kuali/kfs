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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocOrg;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.businessobject.AdhocWorkgroup;
import org.kuali.kfs.module.cg.businessobject.ResearchAdhocPermissionType;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
     * @see org.kuali.module.kra.budget.service.BudgetPermissionsService#getAdHocPermission(String documentNumber, String
     *      principalId)
     */
    public AdhocPerson getAdHocPerson(String documentNumber, String principalId) {
        return (AdhocPerson) businessObjectService.retrieve(new AdhocPerson(documentNumber, principalId));
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetPermissionsService#getAdHocWorkgroup(String documentNumber, String
     *      workgroupName)
     */
    public AdhocWorkgroup getAdHocWorkgroup(String documentNumber, String workgroupName) {
        return (AdhocWorkgroup) businessObjectService.retrieve(new AdhocWorkgroup(documentNumber, workgroupName));
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetPermissionsService#getAssAdHocWorkgroups(String documentNumber)
     */
    public List<AdhocWorkgroup> getAllAdHocWorkgroups(String documentNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        return new ArrayList(businessObjectService.findMatching(AdhocWorkgroup.class, fieldValues));
    }

    /**
     * @see org.kuali.module.kra.budget.service.PermissionsService#getAdHocOrgs(String documentNumber, String budgetPermissionCode)
     */
    public List<AdhocOrg> getAdHocOrgs(String documentNumber, String permissionCode) {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        fieldValues.put("permissionCode", permissionCode);
        return new ArrayList(businessObjectService.findMatching(AdhocOrg.class, fieldValues));
    }

    /**
     * @see org.kuali.module.kra.budget.service.PermissionsService#getPermissionType()
     */
    public List<ResearchAdhocPermissionType> getPermissionTypes() {
        List<ResearchAdhocPermissionType> permissionTypeList = new ArrayList<ResearchAdhocPermissionType>();
        permissionTypeList.add(new ResearchAdhocPermissionType(CGConstants.RoutingFormPermissionTypes.PERMISSION_READ_CODE, CGConstants.RoutingFormPermissionTypes.PERMISSION_READ_DESCRIPTION));
        permissionTypeList.add(new ResearchAdhocPermissionType(CGConstants.RoutingFormPermissionTypes.PERMISSION_MOD_CODE, CGConstants.RoutingFormPermissionTypes.PERMISSION_MOD_DESCRIPTION));
        return permissionTypeList;
    }

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

