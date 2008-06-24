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

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocOrg;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.businessobject.AdhocWorkgroup;
import org.kuali.kfs.module.cg.businessobject.ResearchAdhocPermissionType;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class ResearchDocumentPermissionsServiceImpl implements ResearchDocumentPermissionsService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentPermissionsServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.kra.budget.service.BudgetPermissionsService#getAdHocPermission(String documentNumber, String
     *      personUniversalIdentifier)
     */
    public AdhocPerson getAdHocPerson(String documentNumber, String personUniversalIdentifier) {
        return (AdhocPerson) businessObjectService.retrieve(new AdhocPerson(documentNumber, personUniversalIdentifier));
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
        permissionTypeList.add(new ResearchAdhocPermissionType(KFSConstants.PERMISSION_READ_CODE, KFSConstants.PERMISSION_READ_DESCRIPTION));
        permissionTypeList.add(new ResearchAdhocPermissionType(KFSConstants.PERMISSION_MOD_CODE, KFSConstants.PERMISSION_MOD_DESCRIPTION));
        return permissionTypeList;
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetPermissionsService#getBudgetPermissionType(String orgXml, String documentType,
     *      String uuid)
     */
    public boolean isUserInOrgHierarchy(String orgXml, String documentType, String uuid) {
        ReportCriteriaVO criteria = new ReportCriteriaVO();
        criteria.setDocumentTypeName(documentType);
        criteria.setNodeNames(new String[] { CGConstants.ORG_REVIEW_NODE_NAME });
        criteria.setRuleTemplateNames(new String[] { CGConstants.ORG_REVIEW_TEMPLATE_NAME });
        criteria.setXmlContent(orgXml);
        WorkflowInfo info = new WorkflowInfo();
        try {
            DocumentDetailVO detail = info.routingReport(criteria);
            return isUserInRequests(detail.getActionRequests(), uuid);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Exception generating routing report: " + e);
        }
    }

    /**
     * Check whether given user is in the given action requests.
     * 
     * @param ActionRequestVO[] requests
     * @param String uuid
     * @return boolean
     */
    private boolean isUserInRequests(ActionRequestVO[] requests, String uuid) {
        for (int i = 0; i < requests.length; i++) {
            ActionRequestVO request = (ActionRequestVO) requests[i];
            if (request.getUserVO().getUuId().equals(uuid)) {
                return true;
            }
            if (isUserInRequests(request.getChildrenRequests(), uuid)) {
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
