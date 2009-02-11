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
package org.kuali.kfs.module.purap.document.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class holds methods for Purchasing and Accounts Payable documents to integrate with workflow services and operations.
 */
@Transactional
public class PurApWorkflowIntegrationServiceImpl implements PurApWorkflowIntegrationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApWorkflowIntegrationServiceImpl.class);

    private WorkflowInfo workflowInfo;
    private WorkflowDocumentService workflowDocumentService;
    private org.kuali.rice.kim.service.PersonService personService;

    
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
        this.personService = personService;
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
    private void superUserApproveAllActionRequests(Person superUser, Long documentNumber, String nodeName, Person user, String annotation) throws WorkflowException {
        KualiWorkflowDocument workflowDoc = workflowDocumentService.createWorkflowDocument(documentNumber, superUser);
        List<ActionRequestDTO> actionRequests = getActiveActionRequestsForCriteria(documentNumber, nodeName, user);
        for (ActionRequestDTO actionRequestDTO : actionRequests) {
            LOG.debug("Active Action Request list size to process is " + actionRequests.size());
            LOG.debug("Attempting to super user approve action request with id " + actionRequestDTO.getActionRequestId());
            workflowDoc.superUserActionRequestApprove(actionRequestDTO.getActionRequestId(), annotation);
            superUserApproveAllActionRequests(superUser, documentNumber, nodeName, user, annotation);
            break;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService#takeAllActionsForGivenCriteria(org.kuali.rice.kns.document.Document,
     *      java.lang.String, java.lang.String, org.kuali.rice.kim.bo.Person, java.lang.String)
     */
    public boolean takeAllActionsForGivenCriteria(Document document, String potentialAnnotation, String nodeName, Person userToCheck, String superUserNetworkId) {
        try {
            Long documentNumber = document.getDocumentHeader().getWorkflowDocument().getRouteHeaderId();
            String networkIdString = (ObjectUtils.isNotNull(userToCheck)) ? userToCheck.getPrincipalName() : "none";
            List<ActionRequestDTO> activeActionRequests = getActiveActionRequestsForCriteria(documentNumber, nodeName, userToCheck);

            // if no action requests are found... no actions required
            if (activeActionRequests.isEmpty()) {
                LOG.debug("No action requests found on document id " + documentNumber + " for given criteria:  principalName - " + networkIdString + "; nodeName - " + nodeName);
                return false;
            }

            // if a super user network id was given... take all actions as super user
            if (StringUtils.isNotBlank(superUserNetworkId)) {
                // approve each action request as the super user
                Person superUser = personService.getPersonByPrincipalName(superUserNetworkId);
                LOG.debug("Attempting to super user approve all action requests found on document id " + documentNumber + " for given criteria:  principalName - " + networkIdString + "; nodeName - " + nodeName);
                superUserApproveAllActionRequests(superUser, documentNumber, nodeName, userToCheck, potentialAnnotation);
                return true;
            }
            else {
                // if a user was given... take the action as that user
                if (ObjectUtils.isNotNull(userToCheck)) {
                    KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(documentNumber, userToCheck);
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
                        for (ActionRequestDTO actionRequestDTO : activeActionRequests) {
                            containsFyiRequest |= (KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestDTO.getActionRequested()));
                            containsAckRequest |= (KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestDTO.getActionRequested()));
                            containsApproveRequest |= (KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestDTO.getActionRequested()));
                            containsCompleteRequest |= (KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestDTO.getActionRequested()));
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
    private List<ActionRequestDTO> getActiveActionRequestsForCriteria(Long documentNumber, String nodeName, Person user) throws WorkflowException {
        if (ObjectUtils.isNull(documentNumber)) {
            // throw exception
        }
        List<ActionRequestDTO> activeRequests = new ArrayList<ActionRequestDTO>();
        ActionRequestDTO[] actionRequests = getWorkflowInfo().getActionRequests(documentNumber, nodeName, user.getPrincipalId());
        for (ActionRequestDTO actionRequest : actionRequests) {
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
    public boolean willDocumentStopAtGivenFutureRouteNode(PurchasingAccountsPayableDocument document, NodeDetails givenNodeDetail) {
        if (givenNodeDetail == null) {
            throw new InvalidParameterException("Given Node Detail object was null");
        }
        try {
            String activeNode = null;
            String[] nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
            if (nodeNames.length == 1) {
                activeNode = nodeNames[0];
            }
            if (isGivenNodeAfterCurrentNode(givenNodeDetail.getNodeDetailByName(activeNode), givenNodeDetail)) {
                if (document.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
                    // document is only initiated so we need to pass xml for workflow to simulate route properly
                    ReportCriteriaDTO reportCriteriaDTO = new ReportCriteriaDTO(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
                    reportCriteriaDTO.setXmlContent(document.getXmlForRouteReport());
                    reportCriteriaDTO.setRoutingPrincipalId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
                    reportCriteriaDTO.setTargetNodeName(givenNodeDetail.getName());
                    boolean value = getWorkflowInfo().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[] { KEWConstants.ACTION_REQUEST_APPROVE_REQ, KEWConstants.ACTION_REQUEST_COMPLETE_REQ }, false);
                    return value;
                }
                else {
                    /*
                     * Document has had at least one workflow action taken so we need to pass the doc id so the simulation will use
                     * the existing actions taken and action requests in determining if rules will fire or not. We also need to call
                     * a save routing data so that the xml Workflow uses represents what is currently on the document
                     */
                    ReportCriteriaDTO reportCriteriaDTO = new ReportCriteriaDTO(Long.valueOf(document.getDocumentNumber()));
                    reportCriteriaDTO.setXmlContent(document.getXmlForRouteReport());
                    reportCriteriaDTO.setTargetNodeName(givenNodeDetail.getName());
                    boolean value = getWorkflowInfo().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[] { KEWConstants.ACTION_REQUEST_APPROVE_REQ, KEWConstants.ACTION_REQUEST_COMPLETE_REQ }, false);
                    return value;
                }
            }
            return false;
        }
        catch (WorkflowException e) {
            String errorMessage = "Error trying to test document id '" + document.getDocumentNumber() + "' for action requests at node name '" + givenNodeDetail.getName() + "'";
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
    private boolean isGivenNodeAfterCurrentNode(NodeDetails currentNodeDetail, NodeDetails givenNodeDetail) {
        if (ObjectUtils.isNull(givenNodeDetail)) {
            // given node does not exist
            return false;
        }
        if (ObjectUtils.isNull(currentNodeDetail)) {
            // current node does not exist... assume we are pre-route
            return true;
        }
        return givenNodeDetail.getOrdinal() > currentNodeDetail.getOrdinal();
    }
    
    private WorkflowInfo getWorkflowInfo() {
        if (workflowInfo == null) {
            workflowInfo = new WorkflowInfo();
        }
        return workflowInfo;
    }
}

