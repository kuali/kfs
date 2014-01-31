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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;

/**
 * This class is the base class for a MockWorkflowDocument. It can be extended by any other kind of mock document that needs to
 * override certain methods. This class has absolutely no state or behavior. There is no public constructor, and no member
 * variables. All void methods do nothing. All methods with a return value return null. All state and behavior needs to be added via
 * a subclass.
 */
/**
 *
 */
public abstract class MockWorkflowDocument implements WorkflowDocument {

    /**
     * Constructs a MockWorkflowDocument.java.
     */
    protected MockWorkflowDocument() {
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#switchPrincipal(java.lang.String)
     */
    @Override
    public void switchPrincipal(String principalId) {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDocument()
     */
    @Override
    public Document getDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDocumentContent()
     */
    @Override
    public DocumentContent getDocumentContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getApplicationContent()
     */
    @Override
    public String getApplicationContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setApplicationDocumentId(java.lang.String)
     */
    @Override
    public void setApplicationDocumentId(String applicationDocumentId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setApplicationDocumentStatus(java.lang.String)
     */
    @Override
    public void setApplicationDocumentStatus(String applicationDocumentStatus) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setApplicationContent(java.lang.String)
     */
    @Override
    public void setApplicationContent(String applicationContent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setAttributeContent(java.lang.String)
     */
    @Override
    public void setAttributeContent(String attributeContent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearAttributeContent()
     */
    @Override
    public void clearAttributeContent() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getAttributeContent()
     */
    @Override
    public String getAttributeContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#addAttributeDefinition(org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition)
     */
    @Override
    public void addAttributeDefinition(WorkflowAttributeDefinition attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#removeAttributeDefinition(org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition)
     */
    @Override
    public void removeAttributeDefinition(WorkflowAttributeDefinition attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearAttributeDefinitions()
     */
    @Override
    public void clearAttributeDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getAttributeDefinitions()
     */
    @Override
    public List<WorkflowAttributeDefinition> getAttributeDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setSearchableContent(java.lang.String)
     */
    @Override
    public void setSearchableContent(String searchableContent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#addSearchableDefinition(org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition)
     */
    @Override
    public void addSearchableDefinition(WorkflowAttributeDefinition searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#removeSearchableDefinition(org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition)
     */
    @Override
    public void removeSearchableDefinition(WorkflowAttributeDefinition searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearSearchableDefinitions()
     */
    @Override
    public void clearSearchableDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#clearSearchableContent()
     */
    @Override
    public void clearSearchableContent() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getSearchableDefinitions()
     */
    @Override
    public List<WorkflowAttributeDefinition> getSearchableDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setVariable(java.lang.String, java.lang.String)
     */
    @Override
    public void setVariable(String name, String value) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getVariableValue(java.lang.String)
     */
    @Override
    public String getVariableValue(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setReceiveFutureRequests()
     */
    @Override
    public void setReceiveFutureRequests() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setDoNotReceiveFutureRequests()
     */
    @Override
    public void setDoNotReceiveFutureRequests() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#setClearFutureRequests()
     */
    @Override
    public void setClearFutureRequests() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getReceiveFutureRequestsValue()
     */
    @Override
    public String getReceiveFutureRequestsValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDoNotReceiveFutureRequestsValue()
     */
    @Override
    public String getDoNotReceiveFutureRequestsValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getClearFutureRequestsValue()
     */
    @Override
    public String getClearFutureRequestsValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#validateAttributeDefinition(org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition)
     */
    @Override
    public List<? extends RemotableAttributeError> validateAttributeDefinition(WorkflowAttributeDefinition attributeDefinition) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRootActionRequests()
     */
    @Override
    public List<ActionRequest> getRootActionRequests() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getActionsTaken()
     */
    @Override
    public List<ActionTaken> getActionsTaken() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getValidActions()
     */
    @Override
    public ValidActions getValidActions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRequestedActions()
     */
    @Override
    public RequestedActions getRequestedActions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#saveDocument(java.lang.String)
     */
    @Override
    public void saveDocument(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#route(java.lang.String)
     */
    @Override
    public void route(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#complete(java.lang.String)
     */
    @Override
    public void complete(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#disapprove(java.lang.String)
     */
    @Override
    public void disapprove(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#approve(java.lang.String)
     */
    @Override
    public void approve(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#cancel(java.lang.String)
     */
    @Override
    public void cancel(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#blanketApprove(java.lang.String)
     */
    @Override
    public void blanketApprove(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#blanketApprove(java.lang.String, java.lang.String[])
     */
    @Override
    public void blanketApprove(String annotation, String... nodeNames) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#saveDocumentData()
     */
    @Override
    public void saveDocumentData() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#acknowledge(java.lang.String)
     */
    @Override
    public void acknowledge(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#fyi(java.lang.String)
     */
    @Override
    public void fyi(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#fyi()
     */
    @Override
    public void fyi() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#delete()
     */
    @Override
    public void delete() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#refresh()
     */
    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToPrincipal(org.kuali.rice.kew.api.action.ActionRequestType, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void adHocToPrincipal(ActionRequestType actionRequested, String annotation, String targetPrincipalId, String responsibilityDescription, boolean forceAction) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToPrincipal(org.kuali.rice.kew.api.action.ActionRequestType, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void adHocToPrincipal(ActionRequestType actionRequested, String nodeName, String annotation, String targetPrincipalId, String responsibilityDescription, boolean forceAction) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToPrincipal(org.kuali.rice.kew.api.action.ActionRequestType, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    @Override
    public void adHocToPrincipal(ActionRequestType actionRequested, String nodeName, String annotation, String targetPrincipalId, String responsibilityDescription, boolean forceAction, String requestLabel) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToPrincipal(org.kuali.rice.kew.api.action.AdHocToPrincipal, java.lang.String)
     */
    @Override
    public void adHocToPrincipal(AdHocToPrincipal adHocToPrincipal, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToGroup(org.kuali.rice.kew.api.action.ActionRequestType, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void adHocToGroup(ActionRequestType actionRequested, String annotation, String targetGroupId, String responsibilityDescription, boolean forceAction) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToGroup(org.kuali.rice.kew.api.action.ActionRequestType, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void adHocToGroup(ActionRequestType actionRequested, String nodeName, String annotation, String targetGroupId, String responsibilityDescription, boolean forceAction) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToGroup(org.kuali.rice.kew.api.action.ActionRequestType, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    @Override
    public void adHocToGroup(ActionRequestType actionRequested, String nodeName, String annotation, String targetGroupId, String responsibilityDescription, boolean forceAction, String requestLabel) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#adHocToGroup(org.kuali.rice.kew.api.action.AdHocToGroup, java.lang.String)
     */
    @Override
    public void adHocToGroup(AdHocToGroup adHocToGroup, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#revokeAdHocRequestById(java.lang.String, java.lang.String)
     */
    @Override
    public void revokeAdHocRequestById(String actionRequestId, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#revokeAdHocRequests(org.kuali.rice.kew.api.action.AdHocRevoke, java.lang.String)
     */
    @Override
    public void revokeAdHocRequests(AdHocRevoke revoke, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#revokeAllAdHocRequests(java.lang.String)
     */
    @Override
    public void revokeAllAdHocRequests(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#returnToPreviousNode(java.lang.String, java.lang.String)
     */
    @Override
    public void returnToPreviousNode(String nodeName, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#returnToPreviousNode(org.kuali.rice.kew.api.action.ReturnPoint, java.lang.String)
     */
  //  @Override
  //  public void returnToPreviousNode(ReturnPoint returnPoint, String annotation) {
  //      // TODO Auto-generated method stub
//
  //  }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#move(org.kuali.rice.kew.api.action.MovePoint, java.lang.String)
     */
    @Override
    public void move(MovePoint movePoint, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#takeGroupAuthority(java.lang.String, java.lang.String)
     */
    @Override
    public void takeGroupAuthority(String annotation, String groupId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#releaseGroupAuthority(java.lang.String, java.lang.String)
     */
    @Override
    public void releaseGroupAuthority(String annotation, String groupId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#placeInExceptionRouting(java.lang.String)
     */
    @Override
    public void placeInExceptionRouting(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserBlanketApprove(java.lang.String)
     */
    @Override
    public void superUserBlanketApprove(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserNodeApprove(java.lang.String, java.lang.String)
     */
    @Override
    public void superUserNodeApprove(String nodeName, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserTakeRequestedAction(java.lang.String, java.lang.String)
     */
    @Override
    public void superUserTakeRequestedAction(String actionRequestId, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserDisapprove(java.lang.String)
     */
    @Override
    public void superUserDisapprove(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserCancel(java.lang.String)
     */
    @Override
    public void superUserCancel(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#superUserReturnToPreviousNode(org.kuali.rice.kew.api.action.ReturnPoint, java.lang.String)
     */
    @Override
    public void superUserReturnToPreviousNode(ReturnPoint returnPoint, String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#logAnnotation(java.lang.String)
     */
    @Override
    public void logAnnotation(String annotation) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isCompletionRequested()
     */
    @Override
    public boolean isCompletionRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isApprovalRequested()
     */
    @Override
    public boolean isApprovalRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isAcknowledgeRequested()
     */
    @Override
    public boolean isAcknowledgeRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isFYIRequested()
     */
    @Override
    public boolean isFYIRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isBlanketApproveCapable()
     */
    @Override
    public boolean isBlanketApproveCapable() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isRouteCapable()
     */
    @Override
    public boolean isRouteCapable() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isValidAction(org.kuali.rice.kew.api.action.ActionType)
     */
    @Override
    public boolean isValidAction(ActionType actionType) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#checkStatus(org.kuali.rice.kew.api.document.DocumentStatus)
     */
    @Override
    public boolean checkStatus(DocumentStatus status) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isInitiated()
     */
    @Override
    public boolean isInitiated() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isSaved()
     */
    @Override
    public boolean isSaved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isEnroute()
     */
    @Override
    public boolean isEnroute() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isException()
     */
    @Override
    public boolean isException() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isCanceled()
     */
    @Override
    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isDisapproved()
     */
    @Override
    public boolean isDisapproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isApproved()
     */
    @Override
    public boolean isApproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isProcessed()
     */
    @Override
    public boolean isProcessed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#isFinal()
     */
    @Override
    public boolean isFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getNodeNames()
     */
    @Override
    public Set<String> getNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getCurrentNodeNames()
     */
    @Override
    public Set<String> getCurrentNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getActiveRouteNodeInstances()
     */
    @Override
    public List<RouteNodeInstance> getActiveRouteNodeInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getCurrentRouteNodeInstances()
     */
    @Override
    public List<RouteNodeInstance> getCurrentRouteNodeInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getRouteNodeInstances()
     */
    @Override
    public List<RouteNodeInstance> getRouteNodeInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getPreviousNodeNames()
     */
    @Override
    public List<String> getPreviousNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#getDocumentDetail()
     */
    @Override
    public DocumentDetail getDocumentDetail() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.api.WorkflowDocument#updateDocumentContent(org.kuali.rice.kew.api.document.DocumentContentUpdate)
     */
    @Override
    public void updateDocumentContent(DocumentContentUpdate documentContentUpdate) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getApplicationDocumentId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getApplicationDocumentStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getApplicationDocumentStatusDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getDateApproved() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getDateCreated() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getDateFinalized() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getDateLastModified() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDocumentHandlerUrl() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getDocumentId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDocumentTypeId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDocumentTypeName() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String getInitiatorPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRoutedByPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public DocumentStatus getStatus() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Map<String, String> getVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void recall(String annotation, boolean cancel) {
        // TODO Auto-generated method stub
    }

    @Override
    public void returnToPreviousNode(String annotation, ReturnPoint returnPoint) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isRecalled() {
        // TODO Auto-generated method stub
        return false;
    }

}

