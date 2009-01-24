/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.BudgetUser;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;

public class ResearchTransactionalDocumentDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    private static final String KRA_BUDGET_DOC_TYPE = "KualiBudgetDocument";
    private static final String KRA_ROUTING_FORM_DOC_TYPE = "KualiRoutingFormDocument";

    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KimAttributes.DOCUMENT_NUMBER);
    }

    /**
     *  This service takes the following attributes:
     *  Document Id
     *
     *  Requirements:
     *  - KFS-CG Routing Form Ad Hoc Acknowledger: list of people on the approvals tab of the routing form document -
     *  People in ad hoc acknowledge list built up on the routing form document
     *  - KFS-CG Pre-Award Project Director: document type will be RoutingFormDocument or BudgetDocument, 
     *  Project directors listed on the routing form and budget documents
     *
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);

        String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        List<String> principalIds = new ArrayList<String>();
        Document document = getDocument(documentNumber);
        if(CGConstants.CGKimConstants.ROUTING_FORM_ADHOC_ACKNOWLEDGER_KIM_ROLE_NAME.equals(roleName)){
            if (document.getAdHocRoutePersons() != null) {
                for (AdHocRoutePerson adHocRoutePerson : document.getAdHocRoutePersons()) {
                    principalIds.add(adHocRoutePerson.getId());
                }
            }
        } else if(CGConstants.CGKimConstants.PREAWARD_PROJECT_DIRECTOR_KIM_ROLE_NAME.equals(roleName)){
            String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
            if(KRA_ROUTING_FORM_DOC_TYPE.equals(documentType)){
                principalIds.addAll(getProjectDirectors((RoutingFormDocument)document));
            } else if(KRA_BUDGET_DOC_TYPE.equals(documentType)){
                principalIds.addAll(getProjectDirectors((BudgetDocument)document));
            }
        }
        return principalIds;
    }

    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);

        String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        boolean hasApplicationRole = false;
        Document document = getDocument(documentNumber);
        if(CGConstants.CGKimConstants.ROUTING_FORM_ADHOC_ACKNOWLEDGER_KIM_ROLE_NAME.equals(roleName)){
            hasApplicationRole = document.getAdHocRoutePersons()!=null && document.getAdHocRoutePersons().contains(principalId);
        } else if(CGConstants.CGKimConstants.PREAWARD_PROJECT_DIRECTOR_KIM_ROLE_NAME.equals(roleName)){
            String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
            List projectDirs;
            if(KRA_ROUTING_FORM_DOC_TYPE.equals(documentType)){
                projectDirs = getProjectDirectors((RoutingFormDocument)document);
                hasApplicationRole = projectDirs!=null && projectDirs.contains(principalId);
            } else if(KRA_BUDGET_DOC_TYPE.equals(documentType)){
                projectDirs = getProjectDirectors((BudgetDocument)document);
                hasApplicationRole = projectDirs!=null && projectDirs.contains(principalId);
            }
        }
        return hasApplicationRole;
    }

    protected Document getDocument(String documentNumber){
        try{
            return (Document)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
        } catch (WorkflowException e) {
            String errorMessage = "Workflow problem while trying to get document using doc id '" + documentNumber + "'";
            throw new RuntimeException(errorMessage, e);
        }
    }

    private List<String> getProjectDirectors(RoutingFormDocument document){
        List<String> principalIds = new ArrayList<String>();
        for(RoutingFormPersonnel user: document.getRoutingFormPersonnel()){
            if(ObjectUtils.isNotNull(user.getPersonRoleCode()) && user.isProjectDirector()){
                principalIds.add(user.getPrincipalId());
            }
        }
        return principalIds;
    }

    private List<String> getProjectDirectors(BudgetDocument document){
        List<String> principalIds = new ArrayList<String>();
        for(BudgetUser person: document.getBudget().getPersonnel()){
            if(person.isPersonProjectDirectorIndicator()){
                principalIds.add(person.getPrincipalId());
            }
        }
        return principalIds;
    }
    
}