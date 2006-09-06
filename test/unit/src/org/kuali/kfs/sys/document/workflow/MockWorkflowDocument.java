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
package org.kuali.workflow;

import java.sql.Timestamp;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is the base class for a MockWorkflowDocument. It can be extended by any other kind of mock document that needs to
 * override certain methods.
 * 
 * This class has absolutely no state or behavior. There is no public constructor, and no member variables. All void methods do
 * nothing. All methods with a return value return null.
 * 
 * All state and behavior needs to be added via a subclass.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public abstract class MockWorkflowDocument implements KualiWorkflowDocument {

    /**
     * Constructs a MockWorkflowDocument.java.
     */
    protected MockWorkflowDocument() {
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getApplicationContent()
     */
    public String getApplicationContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#setApplicationContent(java.lang.String)
     */
    public void setApplicationContent(String applicationContent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#clearAttributeContent()
     */
    public void clearAttributeContent() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getAttributeContent()
     */
    public String getAttributeContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#addAttributeDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#removeAttributeDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#clearAttributeDefinitions()
     */
    public void clearAttributeDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionVO[] getAttributeDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#addSearchableDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#removeSearchableDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#clearSearchableDefinitions()
     */
    public void clearSearchableDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionVO[] getSearchableDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteHeader()
     */
    public RouteHeaderVO getRouteHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteHeaderId()
     */
    public Long getRouteHeaderId() throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#setAppDocId(java.lang.String)
     */
    public void setAppDocId(String appDocId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getAppDocId()
     */
    public String getAppDocId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getInitiatorNetworkId()
     */
    public String getInitiatorNetworkId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getTitle()
     */
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#saveDocument(java.lang.String)
     */
    public void saveDocument(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#routeDocument(java.lang.String)
     */
    public void routeDocument(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#disapprove(java.lang.String)
     */
    public void disapprove(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#approve(java.lang.String)
     */
    public void approve(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#superUserApprove(java.lang.String)
     */
    public void superUserApprove(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#cancel(java.lang.String)
     */
    public void cancel(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String)
     */
    public void blanketApprove(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String, java.lang.Integer)
     */
    public void blanketApprove(String annotation, Integer routeLevel) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#saveRoutingData()
     */
    public void saveRoutingData() throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#acknowledge(java.lang.String)
     */
    public void acknowledge(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#fyi()
     */
    public void fyi() throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#delete()
     */
    public void delete() throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#refreshContent()
     */
    public void refreshContent() throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#appSpecificRouteDocumentToUser(java.lang.String, java.lang.String,
     *      int, java.lang.String, edu.iu.uis.eden.clientapp.vo.UserIdVO, java.lang.String, boolean)
     */
    public void appSpecificRouteDocumentToUser(String actionRequested, String routeTypeName, int priority, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#appSpecificRouteDocumentToWorkgroup(java.lang.String,
     *      java.lang.String, int, java.lang.String, edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO, java.lang.String, boolean)
     */
    public void appSpecificRouteDocumentToWorkgroup(String actionRequested, String routeTypeName, int priority, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#setTitle(java.lang.String)
     */
    public void setTitle(String title) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocumentType()
     */
    public String getDocumentType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocRouteLevel()
     */
    public Integer getDocRouteLevel() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocRouteLevelName()
     */
    public String getDocRouteLevelName() throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteTypeName()
     */
    public String getRouteTypeName() throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#complete(java.lang.String)
     */
    public void complete(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#returnToPreviousRouteLevel(java.lang.String, java.lang.Integer)
     */
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#logDocumentAction(java.lang.String)
     */
    public void logDocumentAction(String annotation) throws WorkflowException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsInitiated()
     */
    public boolean stateIsInitiated() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsSaved()
     */
    public boolean stateIsSaved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsEnroute()
     */
    public boolean stateIsEnroute() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsFinal()
     */
    public boolean stateIsFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsException()
     */
    public boolean stateIsException() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsCanceled()
     */
    public boolean stateIsCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsDisapproved()
     */
    public boolean stateIsDisapproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsApproved()
     */
    public boolean stateIsApproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsProcessed()
     */
    public boolean stateIsProcessed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getStatusDisplayValue()
     */
    public String getStatusDisplayValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getCreateDate()
     */
    public Timestamp getCreateDate() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#userIsInitiator(org.kuali.core.bo.user.KualiUser)
     */
    public boolean userIsInitiator(KualiUser user) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getNodeNames()
     */
    public String[] getNodeNames() throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

}
