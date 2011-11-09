/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.workflow;

import java.sql.Timestamp;
import java.util.Set;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This class is the base class for a MockWorkflowDocument. It can be extended by any other kind of mock document that needs to
 * override certain methods. This class has absolutely no state or behavior. There is no public constructor, and no member
 * variables. All void methods do nothing. All methods with a return value return null. All state and behavior needs to be added via
 * a subclass.
 */
public abstract class MockWorkflowDocument implements WorkflowDocument {

    /**
     * Constructs a MockWorkflowDocument.java.
     */
    protected MockWorkflowDocument() {
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getApplicationContent()
     */
    public String getApplicationContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setApplicationContent(java.lang.String)
     */
    public void setApplicationContent(String applicationContent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearAttributeContent()
     */
    public void clearAttributeContent() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getAttributeContent()
     */
    public String getAttributeContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#addAttributeDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#removeAttributeDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearAttributeDefinitions()
     */
    public void clearAttributeDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getAttributeDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#addSearchableDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#removeSearchableDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearSearchableDefinitions()
     */
    public void clearSearchableDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getSearchableDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRouteHeader()
     */
    public RouteHeaderDTO getRouteHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRouteHeaderId()
     */
    public Long getRouteHeaderId()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setAppDocId(java.lang.String)
     */
    public void setAppDocId(String appDocId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getAppDocId()
     */
    public String getAppDocId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getInitiatorPrincipalId()
     */
    public String getInitiatorPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getInitiatorPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }
    
        /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRoutedByPrincipalId()
     */
    public String getRoutedByPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getTitle()
     */
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#saveDocument(java.lang.String)
     */
    public void saveDocument(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#routeDocument(java.lang.String)
     */
    public void routeDocument(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#disapprove(java.lang.String)
     */
    public void disapprove(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#approve(java.lang.String)
     */
    public void approve(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserApprove(java.lang.String)
     */
    public void superUserApprove(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#cancel(java.lang.String)
     */
    public void cancel(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#blanketApprove(java.lang.String)
     */
    public void blanketApprove(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#blanketApprove(java.lang.String, java.lang.Integer)
     */
    public void blanketApprove(String annotation, Integer routeLevel)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#saveRoutingData()
     */
    public void saveRoutingData()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#acknowledge(java.lang.String)
     */
    public void acknowledge(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#fyi()
     */
    public void fyi()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#delete()
     */
    public void delete()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#refreshContent()
     */
    public void refreshContent()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setTitle(java.lang.String)
     */
    public void setTitle(String title)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDocumentType()
     */
    public String getDocumentType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDocRouteLevel()
     */
    public Integer getDocRouteLevel() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDocRouteLevelName()
     */
    public String getDocRouteLevelName()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRouteTypeName()
     */
    public String getRouteTypeName()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#complete(java.lang.String)
     */
    public void complete(String annotation)  {
        // TODO Auto-generated method stub

    }

    public void returnToPreviousNode(String annotation, String nodeName)  {
    }
    public void returnToPreviousNode(String annotation, ReturnPointDTO returnPoint)  {
    }
    public void setReceiveFutureRequests()  {
    }
    public void setDoNotReceiveFutureRequests()  {
    }
    public void setClearFutureRequests()  {
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#returnToPreviousRouteLevel(java.lang.String, java.lang.Integer)
     */
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel)  {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#logDocumentAction(java.lang.String)
     */
    public void logDocumentAction(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isInitiated()
     */
    public boolean isInitiated() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isSaved()
     */
    public boolean isSaved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isEnroute()
     */
    public boolean isEnroute() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isFinal()
     */
    public boolean isFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isException()
     */
    public boolean isException() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isCanceled()
     */
    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isDisapproved()
     */
    public boolean isDisapproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isApproved()
     */
    public boolean isApproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isProcessed()
     */
    public boolean isProcessed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getStatusDisplayValue()
     */
    public String getStatusDisplayValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getCreateDate()
     */
    public Timestamp getCreateDate() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#userIsInitiator(org.kuali.rice.krad.bo.user.KualiUser)
     */
    public boolean userIsInitiator(Person user) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getNodeNames()
     */
    public String[] getNodeNames()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getAllPriorApprovers()
     */
    public Set<Person> getAllPriorApprovers()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getCurrentRouteNodeNames()
     */
    public String getCurrentRouteNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRoutedByUserNetworkId()
     */
    public String getRoutedByUserNetworkId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isStandardSaveAllowed()
     */
    public boolean isStandardSaveAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserActionRequestApprove(java.lang.Long, java.lang.String)
     */
    public void superUserActionRequestApprove(Long actionRequestId, String annotation)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserCancel(java.lang.String)
     */
    public void superUserCancel(String annotation)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserDisapprove(java.lang.String)
     */
    public void superUserDisapprove(String annotation)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#userIsRoutedByUser(org.kuali.rice.kim.api.identity.Person)
     */
    public boolean userIsRoutedByUser(Person user) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocRouteDocumentToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePreviousActions, String actionRequestLabel)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocRouteDocumentToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePreviousActions)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocRouteDocumentToPrincipal(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePreviousActions, String actionRequestLabel)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocRouteDocumentToPrincipal(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePreviousActions)  {
        // TODO Auto-generated method stub
        
    }

    
}

