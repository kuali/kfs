/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.RoutingReportCriteria;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class holds methods for Purchasing and Accounts Payable documents to integrate with workflow services and operations.
 */
@Transactional
public class PurApWorkflowIntegrationServiceImpl implements PurApWorkflowIntegrationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApWorkflowIntegrationServiceImpl.class);


    private WorkflowDocumentService workflowDocumentService;
    private PersonService personService;


    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Performs a super user approval of all action requests.
     *
     * @param superUser
     * @param documentNumber
     * @param nodeName
     * @param user
     * @param annotation
     * @throws WorkflowException
     */
    protected void superUserApproveAllActionRequests(Person superUser, String documentNumber, String nodeName, Person user, String annotation) throws WorkflowException {
        WorkflowDocument workflowDoc = workflowDocumentService.loadWorkflowDocument(documentNumber, superUser);
        List<ActionRequest> actionRequests = getActiveActionRequestsForCriteria(documentNumber, nodeName, user);
        for (ActionRequest actionRequestDTO : actionRequests) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Active Action Request list size to process is " + actionRequests.size());
                LOG.debug("Attempting to super user approve action request with id " + actionRequestDTO.getId());
            }
            SpringContext.getBean(org.kuali.rice.kew.routeheader.service.WorkflowDocumentService.class).superUserActionRequestApproveAction(superUser.getPrincipalId(), documentNumber, actionRequestDTO.getId(), annotation, true );
            break;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService#takeAllActionsForGivenCriteria(org.kuali.rice.krad.document.Document,
     *      java.lang.String, java.lang.String, org.kuali.rice.kim.api.identity.Person, java.lang.String)
     */
    @Override
    public boolean takeAllActionsForGivenCriteria(Document document, String potentialAnnotation, String nodeName, Person userToCheck, String superUserNetworkId) {
        try {
            String documentNumber = document.getDocumentNumber();
            String networkIdString = (ObjectUtils.isNotNull(userToCheck)) ? userToCheck.getPrincipalName() : "none";
            List<ActionRequest> activeActionRequests = getActiveActionRequestsForCriteria(documentNumber, nodeName, userToCheck);

            // if no action requests are found... no actions required
            if (activeActionRequests.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No action requests found on document id " + documentNumber + " for given criteria:  principalName - " + networkIdString + "; nodeName - " + nodeName);
                }
                return false;
            }

            // if a super user network id was given... take all actions as super user
            if (StringUtils.isNotBlank(superUserNetworkId)) {
                // approve each action request as the super user
                Person superUser = getPersonService().getPersonByPrincipalName(superUserNetworkId);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Attempting to super user approve all action requests found on document id " + documentNumber + " for given criteria:  principalName - " + networkIdString + "; nodeName - " + nodeName);
                }
                superUserApproveAllActionRequests(superUser, documentNumber, nodeName, userToCheck, potentialAnnotation);
                return true;
            }
            else {
                // if a user was given... take the action as that user
                if (ObjectUtils.isNotNull(userToCheck)) {
                    WorkflowDocument workflowDocument = workflowDocumentService.loadWorkflowDocument(documentNumber, userToCheck);
                    boolean containsFyiRequest = false;
                    boolean containsAckRequest = false;
                    boolean containsApproveRequest = false;
                    boolean containsCompleteRequest = false;
                    if (StringUtils.isBlank(nodeName)) {
                        // requests are for a specific user but not at a specific level... take regular actions
                        containsCompleteRequest = workflowDocument.isCompletionRequested();
                        containsApproveRequest = workflowDocument.isApprovalRequested();
                        containsAckRequest = workflowDocument.isAcknowledgeRequested();
                        containsFyiRequest = workflowDocument.isFYIRequested();
                    }
                    else {
                        for (ActionRequest actionRequestDTO : activeActionRequests) {
                            containsFyiRequest |= (KewApiConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestDTO.getActionRequested().getCode()));
                            containsAckRequest |= (KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestDTO.getActionRequested().getCode()));
                            containsApproveRequest |= (KewApiConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestDTO.getActionRequested().getCode()));
                            containsCompleteRequest |= (KewApiConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestDTO.getActionRequested().getCode()));
                        }
                    }
                    if (containsCompleteRequest || containsApproveRequest) {
                        workflowDocumentService.approve(workflowDocument, potentialAnnotation, new ArrayList());
                        return true;
                    }
                    else if (containsAckRequest) {
                        workflowDocumentService.acknowledge(workflowDocument, potentialAnnotation, new ArrayList());
                        return true;
                    }
                    else if (containsFyiRequest) {
                        workflowDocumentService.clearFyi(workflowDocument, new ArrayList());
                        return true;
                    }
                }
                else {
                    // no user to check and no super user given... cannot take actions on document
                    String errorMessage = "No super user network id and no user to check given.  Need at least one or both";
                    LOG.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }
            return false;
        }
        catch (WorkflowException e) {
            String errorMessage = "Error trying to get action requests of document id '" + document.getDocumentNumber() + "'";
            LOG.error("takeAllActionsForGivenCriteria() " + errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        catch (Exception e) {
            String errorMessage = "Error trying to get user for network id '" + superUserNetworkId + "'";
            LOG.error("takeAllActionsForGivenCriteria() " + errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Retrieves the active action requests for the given criteria
     *
     * @param documentNumber
     * @param nodeName
     * @param user
     * @return List of action requests
     * @throws WorkflowException
     */
    protected List<ActionRequest> getActiveActionRequestsForCriteria(String documentNumber, String nodeName, Person user) throws WorkflowException {
        if ( StringUtils.isBlank(documentNumber) ) {
            // throw exception
        }
        org.kuali.rice.kew.api.document.WorkflowDocumentService workflowDocService = KewApiServiceLocator.getWorkflowDocumentService();
        List<ActionRequest> actionRequests = workflowDocService.getActionRequestsForPrincipalAtNode(documentNumber, nodeName, user.getPrincipalId());
        List<ActionRequest> activeRequests = new ArrayList<ActionRequest>();
        for (ActionRequest actionRequest : actionRequests) {
            // identify which requests for the given node name can be satisfied by an action by this user
            if (actionRequest.isActivated()) {
                activeRequests.add(actionRequest);
            }
        }
        return activeRequests;
    }

    /**
     * DON'T CALL THIS IF THE DOC HAS NOT BEEN SAVED
     *
     * @see org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService#willDocumentStopAtGivenFutureRouteNode(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument,
     *      org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails)
     */
    public boolean willDocumentStopAtGivenFutureRouteNode(PurchasingAccountsPayableDocument document, String givenNodeName) {
        if (givenNodeName == null) {
            throw new InvalidParameterException("Given Node Detail object was null");
        }
        try {
            String activeNode = null;
            String[] nodeNames = (String[]) document.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames().toArray(new String[0]);

             if (nodeNames.length == 1) {
                activeNode = nodeNames[0];
            }

            if (isGivenNodeAfterCurrentNode(document, activeNode, givenNodeName)) {
                if (document.getDocumentHeader().getWorkflowDocument().isInitiated()) {
                    // document is only initiated so we need to pass xml for workflow to simulate route properly
                    RoutingReportCriteria.Builder builder = RoutingReportCriteria.Builder.createByDocumentTypeName(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
                    builder.setXmlContent(document.getXmlForRouteReport());
                    builder.setRoutingPrincipalId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
                    builder.setTargetNodeName(givenNodeName);
                    RoutingReportCriteria reportCriteria = builder.build();
                    boolean value = SpringContext.getBean(WorkflowDocumentActionsService.class).documentWillHaveAtLeastOneActionRequest(reportCriteria, Arrays.asList( KewApiConstants.ACTION_REQUEST_APPROVE_REQ, KewApiConstants.ACTION_REQUEST_COMPLETE_REQ ), false);
                    return value;
                }else {                
                    /* Document has had at least one workflow action taken so we need to pass the doc id so the simulation will use
                     * the existing actions taken and action requests in determining if rules will fire or not. We also need to call
                     * a save routing data so that the xml Workflow uses represents what is currently on the document
                     */
                    RoutingReportCriteria.Builder builder = RoutingReportCriteria.Builder.createByDocumentId(document.getDocumentNumber());
                    builder.setXmlContent(document.getXmlForRouteReport());
                    builder.setTargetNodeName(givenNodeName);
                    RoutingReportCriteria reportCriteria = builder.build();
                    boolean value = SpringContext.getBean(WorkflowDocumentActionsService.class).documentWillHaveAtLeastOneActionRequest(reportCriteria, Arrays.asList( KewApiConstants.ACTION_REQUEST_APPROVE_REQ, KewApiConstants.ACTION_REQUEST_COMPLETE_REQ ), false);
                    return value;
                }
            }
            return false;
        }
        catch (Exception e) {
            String errorMessage = "Error trying to test document id '" + document.getDocumentNumber() + "' for action requests at node name '" + givenNodeName + "'";
            LOG.error("isDocumentStoppingAtRouteLevel() " + errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Evaluates if given node is after the current node
     *
     * @param currentNodeDetail
     * @param givenNodeDetail
     * @return boolean to indicate if given node is after the current node
     */
    protected boolean isGivenNodeAfterCurrentNode(Document document, String currentNodeName, String givenNodeName) {
        if (ObjectUtils.isNull(givenNodeName)) {
            // given node does not exist
            return false;
        }
        if (ObjectUtils.isNull(currentNodeName)) {
            // current node does not exist... assume we are pre-route
            return true;
        }

        //grab doctype, and get node list
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        DocumentType docType = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(docTypeName);        
        List<RouteNode>nodes = CompatUtils.getRouteLevelCompatibleNodeList(KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentNumber()).getDocumentType()); 
        
        int currentNodeIndex = 0;
        int givenNodeIndex = 0;
        RouteNode node = null;
        
        //find index of given and current node
        for(int i=0; i < nodes.size(); i++){
            node = nodes.get(i);

            if(node.getName().equals(currentNodeName)){
                currentNodeIndex = i;                
            }
            if(node.getName().equals(givenNodeName)){
                givenNodeIndex = i;                
            }
        }
        
        //compare
        return givenNodeIndex > currentNodeIndex;
        
    }


    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

}
