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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.DueDateType;
import org.kuali.kfs.module.cg.businessobject.ContractsAndGrantsRoleCode;
import org.kuali.kfs.module.cg.businessobject.ProjectType;
import org.kuali.kfs.module.cg.businessobject.Purpose;
import org.kuali.kfs.module.cg.businessobject.ResearchTypeCode;
import org.kuali.kfs.module.cg.businessobject.RoutingFormAgency;
import org.kuali.kfs.module.cg.businessobject.RoutingFormDueDateType;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonRole;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.businessobject.RoutingFormProjectType;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPurpose;
import org.kuali.kfs.module.cg.businessobject.RoutingFormResearchTypeCode;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.service.PurposeService;
import org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;

public class RoutingFormMainPageServiceImpl implements RoutingFormMainPageService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormMainPageServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private PurposeService purposeService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#setupMainPageMaintainables(org.kuali.kfs.module.cg.document.RoutingFormDocument)
     */
    public void setupMainPageMaintainables(RoutingFormDocument routingFormDocument) {
        this.setupRoutingFormDueDateTypes(routingFormDocument);
        this.setupRoutingFormProjectTypes(routingFormDocument);
        this.setupRoutingFormPurposes(routingFormDocument);
        this.setupRoutingFormResearchTypeCodes(routingFormDocument);
        this.setupRoutingFormPersonRoles(routingFormDocument);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#getDueDateTypes()
     */
    public List<DueDateType> getDueDateTypes() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        return new ArrayList(businessObjectService.findMatching(DueDateType.class, fieldValues));
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#getPersonRoles()
     */
    public List<ContractsAndGrantsRoleCode> getPersonRoles() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        Collection col = businessObjectService.findMatchingOrderBy(ContractsAndGrantsRoleCode.class, fieldValues, KFSPropertyConstants.PERSON_ROLE_SORT_NUMBER, true);

        return new ArrayList(col);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#getProjectTypes()
     */
    public List<ProjectType> getProjectTypes() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        Collection col = businessObjectService.findMatchingOrderBy(ProjectType.class, fieldValues, KFSPropertyConstants.SORT_NUMBER, true);

        return new ArrayList(col);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#getResearchTypeCodes()
     */
    public List<ResearchTypeCode> getResearchTypeCodes() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        Collection col = businessObjectService.findMatching(ResearchTypeCode.class, fieldValues);

        return new ArrayList(col);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#checkCoPdExistance(java.util.List)
     */
    public boolean checkCoPdExistance(List<RoutingFormPersonnel> routingFormPersonnel) {
        final String CO_PD_ROLE_CODE = parameterService.getParameterValue(RoutingFormDocument.class, CGConstants.PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR);

        for (RoutingFormPersonnel routingFormPerson : routingFormPersonnel) {
            if (routingFormPerson.getPersonRoleCode().equals(CO_PD_ROLE_CODE)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#getProjectDirector(java.util.List)
     */
    public RoutingFormPersonnel getProjectDirector(List<RoutingFormPersonnel> routingFormPersonnel) {
        for (RoutingFormPersonnel person : routingFormPersonnel) {
            if (person.isProjectDirector()) {
                return person;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormMainPageService#getContactPerson(java.util.List)
     */
    public RoutingFormPersonnel getContactPerson(List<RoutingFormPersonnel> routingFormPersonnel) {
        for (RoutingFormPersonnel person : routingFormPersonnel) {
            if (person.isContactPerson()) {
                return person;
            }
        }

        return null;
    }

    /**
     * Setup routing form research type code.
     * 
     * @param routingFormDocument
     */
    private void setupRoutingFormResearchTypeCodes(RoutingFormDocument routingFormDocument) {
        List<ResearchTypeCode> researchTypeCodes = getResearchTypeCodes();
        List<RoutingFormResearchTypeCode> routingFormResearchTypeCodes = new ArrayList<RoutingFormResearchTypeCode>();
        for (ResearchTypeCode researchTypeCode : researchTypeCodes) {
            routingFormResearchTypeCodes.add(new RoutingFormResearchTypeCode(routingFormDocument.getDocumentNumber(), researchTypeCode));
        }

        routingFormDocument.setRoutingFormResearchTypeCodes(routingFormResearchTypeCodes);
    }

    /**
     * Setup routing form purpose.
     * 
     * @param routingFormDocument
     */
    private void setupRoutingFormPurposes(RoutingFormDocument routingFormDocument) {
        List<Purpose> purposes = purposeService.getPurposes();
        List<RoutingFormPurpose> routingFormPurposes = new ArrayList<RoutingFormPurpose>();
        for (Purpose purpose : purposes) {
            routingFormPurposes.add(new RoutingFormPurpose(routingFormDocument.getDocumentNumber(), purpose));
        }

        routingFormDocument.setRoutingFormPurposes(routingFormPurposes);
    }

    /**
     * Setup routing form project type.
     * 
     * @param routingFormDocument
     */
    private void setupRoutingFormProjectTypes(RoutingFormDocument routingFormDocument) {
        List<ProjectType> projectTypes = getProjectTypes();
        List<RoutingFormProjectType> routingFormProjectTypes = new ArrayList<RoutingFormProjectType>();
        for (ProjectType projectType : projectTypes) {
            routingFormProjectTypes.add(new RoutingFormProjectType(routingFormDocument.getDocumentNumber(), projectType));
        }

        routingFormDocument.setRoutingFormProjectTypes(routingFormProjectTypes);
    }

    /**
     * Setup routing form person role.
     * 
     * @param routingFormDocument
     */
    private void setupRoutingFormPersonRoles(RoutingFormDocument routingFormDocument) {
        List<ContractsAndGrantsRoleCode> personRoles = getPersonRoles();
        List<RoutingFormPersonRole> routingFormPersonRoles = new ArrayList<RoutingFormPersonRole>();
        for (ContractsAndGrantsRoleCode personRole : personRoles) {
            routingFormPersonRoles.add(new RoutingFormPersonRole(routingFormDocument.getDocumentNumber(), personRole));
        }

        routingFormDocument.setRoutingFormPersonRoles(routingFormPersonRoles);
    }

    /**
     * Setup routing form due date types.
     * 
     * @param routingFormDocument
     */
    private void setupRoutingFormDueDateTypes(RoutingFormDocument routingFormDocument) {
        List<DueDateType> dueDateTypes = getDueDateTypes();
        List<RoutingFormDueDateType> routingFormDueDateTypes = new ArrayList<RoutingFormDueDateType>();
        for (DueDateType dueDateType : dueDateTypes) {
            routingFormDueDateTypes.add(new RoutingFormDueDateType(routingFormDocument.getDocumentNumber(), dueDateType));
        }

        RoutingFormAgency routingFormAgency = new RoutingFormAgency();
        routingFormAgency.setRoutingFormDueDateTypes(routingFormDueDateTypes);
        routingFormDocument.setRoutingFormAgency(routingFormAgency);
    }

    /**
     * Setter for BusinessObjectService property.
     * 
     * @param businessObjectService businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Setter for PurposeService property.
     * 
     * @param purposeService purposeService
     */
    public void setPurposeService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
