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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.BudgetUser;
import org.kuali.kfs.module.cg.businessobject.ProposalProjectDirector;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class ResearchTransactionalDocumentDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchTransactionalDocumentDerivedRoleTypeServiceImpl.class);

    private static final String KRA_BUDGET_DOC_TYPE = "KualiBudgetDocument";
    private static final String KRA_ROUTING_FORM_DOC_TYPE = "KualiRoutingFormDocument";

    private transient DocumentService documentService;
    private transient ParameterService parameterService;

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
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
//        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();
        String documentNumber = null;
        if ( qualification != null ) {
            documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        }
        if ( StringUtils.isNotBlank(documentNumber) ) {
            Document document = getDocument(documentNumber);
            if ( document != null ) {
                if(CGConstants.CGKimConstants.ROUTING_FORM_ADHOC_ACKNOWLEDGER_KIM_ROLE_NAME.equals(roleName)){
                    if (document.getAdHocRoutePersons() != null) {
                        for (AdHocRoutePerson adHocRoutePerson : document.getAdHocRoutePersons()) {
                            members.add( new RoleMembershipInfo(null,null,adHocRoutePerson.getId(),KimRole.PRINCIPAL_MEMBER_TYPE,null) );
                        }
                    }
                } else if(CGConstants.CGKimConstants.PREAWARD_PROJECT_DIRECTOR_KIM_ROLE_NAME.equals(roleName)){
                    String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
                    if(KRA_ROUTING_FORM_DOC_TYPE.equals(documentType)){
                        for ( String principalId : getProjectDirectors((RoutingFormDocument)document) ) {
                            members.add( new RoleMembershipInfo(null,null,principalId,KimRole.PRINCIPAL_MEMBER_TYPE,null) );
                        }
                    } else if(KRA_BUDGET_DOC_TYPE.equals(documentType)){
                        for ( String principalId : getProjectDirectors((BudgetDocument)document) ) {
                            members.add( new RoleMembershipInfo(null,null,principalId,KimRole.PRINCIPAL_MEMBER_TYPE,null) );
                        }
                    }
                }
            } else {
                LOG.error( "Unable to find document: " + documentNumber, new Throwable() );
            }
        } else { // no qualification or missing document number - find all project directors
            for ( String principalId : getAllProjectDirectorPrincipalIds() ) {
                members.add( new RoleMembershipInfo(null,null,principalId,KimRole.PRINCIPAL_MEMBER_TYPE,null) );
            }
        }
        return members;
    }

    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
//        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        boolean hasApplicationRole = false;
        String documentNumber = null;
        if ( qualification != null ) {
            documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
        }
        if ( StringUtils.isNotBlank(documentNumber) ) {
            Document document = getDocument(documentNumber);
            if ( document != null ) {
                if(CGConstants.CGKimConstants.ROUTING_FORM_ADHOC_ACKNOWLEDGER_KIM_ROLE_NAME.equals(roleName)){
                    hasApplicationRole = document.getAdHocRoutePersons()!=null && document.getAdHocRoutePersons().contains(principalId);
                } else if(CGConstants.CGKimConstants.PREAWARD_PROJECT_DIRECTOR_KIM_ROLE_NAME.equals(roleName)){
                    String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
                    List<String> projectDirs = null;
                    if(KRA_ROUTING_FORM_DOC_TYPE.equals(documentType)){
                        projectDirs = getProjectDirectors((RoutingFormDocument)document);
                        hasApplicationRole = projectDirs!=null && projectDirs.contains(principalId);
                    } else if(KRA_BUDGET_DOC_TYPE.equals(documentType)){
                        projectDirs = getProjectDirectors((BudgetDocument)document);
                        hasApplicationRole = projectDirs!=null && projectDirs.contains(principalId);
                    }
                }
            } else {
                LOG.error( "Unable to find document: " + documentNumber, new Throwable() );
            }
        } else { // no qualification or missing document number - find all project directors
            hasApplicationRole = isProjectDirector(principalId);
        }
        return hasApplicationRole;
    }

    protected Document getDocument(String documentNumber){
        try{
            return (Document)getDocumentService().getByDocumentHeaderId(documentNumber);
        } catch (WorkflowException e) {
            throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
        }
    }

    protected List<String> getAllProjectDirectorPrincipalIds() {
        String projectDirectorRoleCode = getParameterService().getParameterValue(RoutingFormDocument.class, CGConstants.PERSON_ROLE_CODE_PROJECT_DIRECTOR);
        Map<String,Object> criteria = new HashMap<String, Object>( 1 );
        criteria.put( KNSPropertyConstants.ACTIVE, true);
        List<ProposalProjectDirector> pds = (List<ProposalProjectDirector>)getBusinessObjectService().findMatching(ProposalProjectDirector.class, criteria);
        Set<String> principalIds = new HashSet<String>();
        for ( ProposalProjectDirector user : pds ) {
            principalIds.add( user.getPrincipalId() );
        }
//        criteria.put( CGPropertyConstants.PERSON_ROLE_CODE, projectDirectorRoleCode);
//        criteria.put( CGPropertyConstants.PERSON_TO_BE_NAMED_IND, false);
//        List<RoutingFormPersonnel> personnel = (List<RoutingFormPersonnel>)getBusinessObjectService().findMatching(RoutingFormPersonnel.class, criteria);
//        criteria.clear();
//        criteria.put( CGPropertyConstants.PERSON_PROJECT_DIRECTOR_IND, true);
//        List<BudgetUser> budgetUsers = (List<BudgetUser>)getBusinessObjectService().findMatching(BudgetUser.class, criteria);
//        Set<String> principalIds = new HashSet<String>();
//        for ( RoutingFormPersonnel user : personnel ) {
//            principalIds.add( user.getPrincipalId() );
//        }
//        for ( BudgetUser user : budgetUsers ) {
//            principalIds.add(user.getPrincipalId() );
//        }
        return new ArrayList<String>( principalIds );
    }
    
    protected boolean isProjectDirector( String principalId ) {
        String projectDirectorRoleCode = getParameterService().getParameterValue(RoutingFormDocument.class, CGConstants.PERSON_ROLE_CODE_PROJECT_DIRECTOR);
        Map<String,Object> criteria = new HashMap<String, Object>( 3 );
        criteria.put( KIMPropertyConstants.Person.PRINCIPAL_ID, principalId);
        criteria.put( KNSPropertyConstants.ACTIVE, true);
        int matchingRecords = getBusinessObjectService().countMatching(ProposalProjectDirector.class, criteria);
        return matchingRecords != 0;
//        criteria.clear();
//        criteria.put( CGPropertyConstants.PERSON_ROLE_CODE, projectDirectorRoleCode);
//        criteria.put( CGPropertyConstants.PERSON_TO_BE_NAMED_IND, false);
//        criteria.put( KIMPropertyConstants.Person.PRINCIPAL_ID, principalId);
//        List<RoutingFormPersonnel> personnel = (List<RoutingFormPersonnel>)getBusinessObjectService().findMatching(RoutingFormPersonnel.class, criteria);
//        if ( !personnel.isEmpty() ) {
//            return true;
//        }
//        criteria.clear();
//        criteria.put( CGPropertyConstants.PERSON_PROJECT_DIRECTOR_IND, true);
//        criteria.put( KIMPropertyConstants.Person.PRINCIPAL_ID, principalId);
//        List<BudgetUser> budgetUsers = (List<BudgetUser>)getBusinessObjectService().findMatching(BudgetUser.class, criteria);
//        return !budgetUsers.isEmpty();
    }
    
    protected List<String> getProjectDirectors(RoutingFormDocument document){
        List<String> principalIds = new ArrayList<String>();
        for(RoutingFormPersonnel user: document.getRoutingFormPersonnel()){
            if(user.isProjectDirector()){
                principalIds.add(user.getPrincipalId());
            }
        }
        return principalIds;
    }

    protected List<String> getProjectDirectors(BudgetDocument document){
        List<String> principalIds = new ArrayList<String>();
        for(BudgetUser person: document.getBudget().getPersonnel()){
            if(person.isPersonProjectDirectorIndicator()){
                principalIds.add(person.getPrincipalId());
            }
        }
        return principalIds;
    }
    
    protected DocumentService getDocumentService() {
        if ( documentService == null ) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }
    
    protected ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
    
}