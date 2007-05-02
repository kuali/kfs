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
package org.kuali.module.kra.routingform.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.routingform.bo.DueDateType;
import org.kuali.module.kra.routingform.bo.PersonRole;
import org.kuali.module.kra.routingform.bo.ProjectType;
import org.kuali.module.kra.routingform.bo.Purpose;
import org.kuali.module.kra.routingform.bo.ResearchTypeCode;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormDueDateType;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonRole;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.bo.RoutingFormPurpose;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchTypeCode;
import org.kuali.module.kra.routingform.bo.RoutingFormSubmissionType;
import org.kuali.module.kra.routingform.bo.SubmissionType;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.PurposeService;
import org.kuali.module.kra.routingform.service.RoutingFormMainPageService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoutingFormMainPageServiceImpl implements RoutingFormMainPageService {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormMainPageServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private PurposeService purposeService;
    private KualiConfigurationService kualiConfigurationService;
    
    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#setupMainPageMaintainables(org.kuali.module.kra.routingform.document.RoutingFormDocument)
     */
    public void setupMainPageMaintainables(RoutingFormDocument routingFormDocument) {
        this.setupRoutingFormDueDateTypes(routingFormDocument);
        this.setupRoutingFormSubmissionTypes(routingFormDocument);
        this.setupRoutingFormProjectTypes(routingFormDocument);
        this.setupRoutingFormPurposes(routingFormDocument);
        this.setupRoutingFormResearchTypeCodes(routingFormDocument);
        this.setupRoutingFormPersonRoles(routingFormDocument);
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#getDueDateTypes()
     */
    public List<DueDateType> getDueDateTypes() {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, Constants.ACTIVE_INDICATOR);
        
        return new ArrayList(businessObjectService.findMatching(DueDateType.class, fieldValues));
    }
    
    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#getPersonRoles()
     */
    public List<PersonRole> getPersonRoles() {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, Constants.ACTIVE_INDICATOR);
        
        Collection col = businessObjectService.findMatchingOrderBy(PersonRole.class, fieldValues, PropertyConstants.PERSON_ROLE_SORT_NUMBER, true);
        
        return new ArrayList(col);
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#getProjectTypes()
     */
    public List<ProjectType> getProjectTypes() {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, Constants.ACTIVE_INDICATOR);
        
        Collection col = businessObjectService.findMatchingOrderBy(ProjectType.class, fieldValues, PropertyConstants.SORT_NUMBER, true);
        
        return new ArrayList(col);
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#getResearchTypeCodes()
     */
    public List<ResearchTypeCode> getResearchTypeCodes() {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, Constants.ACTIVE_INDICATOR);
        
        Collection col = businessObjectService.findMatching(ResearchTypeCode.class, fieldValues);
        
        return new ArrayList(col);
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#getSubmissionTypes()
     */
    public List<SubmissionType> getSubmissionTypes() {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, Constants.ACTIVE_INDICATOR);
        
        Collection col = businessObjectService.findMatchingOrderBy(SubmissionType.class, fieldValues, PropertyConstants.USER_SORT_NUMBER, true);
        
        return new ArrayList(col);
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#checkCoPdExistance(java.util.List)
     */
    public boolean checkCoPdExistance(List<RoutingFormPersonnel> routingFormPersonnel) {
        final String CO_PD_ROLE_CODE = kualiConfigurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, "KraRoutingFormPersonRoleCodeCoProjectDirector");
        
        for(RoutingFormPersonnel routingFormPerson : routingFormPersonnel) {
            if(routingFormPerson.getPersonRoleCode().equals(CO_PD_ROLE_CODE)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormMainPageService#getProjectDirector(java.util.List)
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
     * Setup routing form submission types.
     * @param routingFormDocument
     */
    private void setupRoutingFormSubmissionTypes(RoutingFormDocument routingFormDocument) {
        List<SubmissionType> submissionTypes = getSubmissionTypes();
        List<RoutingFormSubmissionType> routingFormSubmissionTypes = new ArrayList<RoutingFormSubmissionType>();
        for (SubmissionType submissionType : submissionTypes) {
            routingFormSubmissionTypes.add(new RoutingFormSubmissionType(routingFormDocument.getDocumentNumber(), submissionType));
        }

        routingFormDocument.setRoutingFormSubmissionTypes(routingFormSubmissionTypes);
    }
    
    /**
     * Setup routing form research type code.
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
     * @param routingFormDocument
     */
    private void setupRoutingFormPersonRoles(RoutingFormDocument routingFormDocument) {
        List<PersonRole> personRoles = getPersonRoles();
        List<RoutingFormPersonRole> routingFormPersonRoles = new ArrayList<RoutingFormPersonRole>();
        for (PersonRole personRole : personRoles) {
            routingFormPersonRoles.add(new RoutingFormPersonRole(routingFormDocument.getDocumentNumber(), personRole));
        }

        routingFormDocument.setRoutingFormPersonRoles(routingFormPersonRoles);
    }
    
    /**
     * Setup routing form due date types.
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
    
    /**
     * Setter for kualiConfigurationService property.
     * 
     * @param kualiConfigurationService kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
