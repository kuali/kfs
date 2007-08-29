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
package org.kuali.module.purap.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class holds methods for Purchasing and Accounts Payable documents to integrate with workflow
 * services and operations.
 */
@Transactional
public class PurApWorkflowIntegrationServiceImpl implements PurApWorkflowIntegrationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApWorkflowIntegrationServiceImpl.class);

    private KualiWorkflowInfo kualiWorkflowInfo;
    
    public void setKualiWorkflowInfo(KualiWorkflowInfo kualiWorkflowInfo) {
        this.kualiWorkflowInfo = kualiWorkflowInfo;
    }
    
    public KualiWorkflowInfo getKualiWorkflowInfo() {
        return kualiWorkflowInfo;
    }

    /**
     * @see org.kuali.module.purap.service.PurApWorkflowIntegrationService#isActionRequestedOfUserAtNodeName(java.lang.String, java.lang.String, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isActionRequestedOfUserAtNodeName(String documentNumber, String nodeName, UniversalUser userToCheck) {
        try {
            List<ActionRequestVO> actionRequests = getActiveActionRequestsForCriteria(documentNumber, nodeName, userToCheck);
            return !actionRequests.isEmpty();
        }
        catch (WorkflowException e) {
            String errorMessage = "Error trying to get test action requests of document id '" + documentNumber + "'";
            LOG.error("isActionRequestedOfUserAtNodeName() " + errorMessage,e);
            throw new RuntimeException(errorMessage,e);
        }
    }

    /**
     * @see org.kuali.module.purap.service.PurApWorkflowIntegrationService#takeAllActionsForGivenCriteria(org.kuali.core.document.Document, java.lang.String, org.kuali.core.bo.user.UniversalUser, java.lang.String)
     */
    public void takeAllActionsForGivenCriteria(Document document, String nodeName, UniversalUser userToCheck, String personUserIdToImpersonate) {
        try {
            if ( (ObjectUtils.isNull(userToCheck)) && (StringUtils.isBlank(nodeName)) ) {
                // if the user to check is null and node name is blank... we want to take all actions
                // super user approve document as personUserIdToImpersonate
            }
            else if ( (ObjectUtils.isNull(userToCheck)) && (StringUtils.isNotBlank(nodeName)) ) {
                // if user to check is null and node name is not blank... take all actions at given node
                // super user approve individual action requests as personUserIdToImpersonate
            }
            else if ( (ObjectUtils.isNotNull(userToCheck)) && (StringUtils.isBlank(nodeName)) ) {
                // if user to check is not null and node name is blank... take all actions as given user
                DocumentService docService = SpringContext.getBean(DocumentService.class);
                if (document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
                    docService.approveDocument(document, null, new ArrayList());
                }
                else if (document.getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()) {
                    docService.acknowledgeDocument(document, null, new ArrayList());
                }
                else if (document.getDocumentHeader().getWorkflowDocument().isFYIRequested()) {
                    docService.clearDocumentFyi(document, new ArrayList());
                }
            }
            else if ( (ObjectUtils.isNotNull(userToCheck)) && (StringUtils.isNotBlank(nodeName)) ) {
                /* if user to check is not null and node name is not blank... take all actions as given user at given node
                 * NOTE: This could potentially satisfy actions at other nodes
                 */
                DocumentService docService = SpringContext.getBean(DocumentService.class);
                if (document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
                    docService.approveDocument(document, null, new ArrayList());
                }
                else if (document.getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()) {
                    docService.acknowledgeDocument(document, null, new ArrayList());
                }
                else if (document.getDocumentHeader().getWorkflowDocument().isFYIRequested()) {
                    docService.clearDocumentFyi(document, new ArrayList());
                }
            }
        }
        catch (WorkflowException e) {
            String errorMessage = "Error trying to get test action requests of document id '" + document.getDocumentNumber() + "'";
            LOG.error("takeAllActionsForGivenCriteria() " + errorMessage,e);
            throw new RuntimeException(errorMessage,e);
        }
    }
    
    private List<ActionRequestVO> getActiveActionRequestsForCriteria(String documentNumber, String nodeName, UniversalUser user) throws WorkflowException {
        if (StringUtils.isBlank(documentNumber)) {
            // throw exception
        }
        List<ActionRequestVO> activeRequests = new ArrayList<ActionRequestVO>();
        String personUserIdentifier = (ObjectUtils.isNotNull(user)) ? user.getPersonUserIdentifier() : null;
///////////////////////////////
        // TODO delyea - KEW UPDATE - edit below once KEW is updated in KFS
//        ActionRequestVO[] actionRequests = getKualiWorkflowInfo().getActionRequests(Long.valueOf(documentNumber), nodeName, new NetworkIdVO(personUserIdentifier));
        ActionRequestVO[] nonNodeActionRequests = getKualiWorkflowInfo().getActionRequests(Long.valueOf(documentNumber));
        List<ActionRequestVO> actionRequests = new ArrayList<ActionRequestVO>();
        for (ActionRequestVO nonNodeActionRequest : nonNodeActionRequests) {
            if ( (StringUtils.isBlank(nodeName)) || (StringUtils.equals(nonNodeActionRequest.getNodeName(), nodeName)) ) {
                actionRequests.add(nonNodeActionRequest);
            }
        }
///////////////////////////////
        for (ActionRequestVO actionRequest : actionRequests) {
            // identify which requests for the given node name can be satisfied by an action by this user
            if (actionRequest.isActivated()) {
                activeRequests.add(actionRequest);
            }
        }
        return activeRequests;
    }
    
    /**
     * @see org.kuali.module.purap.service.PurApWorkflowIntegrationService#willDocumentStopAtGivenFutureRouteNode(org.kuali.module.purap.document.PurchasingAccountsPayableDocument, org.kuali.module.purap.PurapWorkflowConstants.NodeDetails)
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
                    ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
                    reportCriteriaVO.setXmlContent(document.getXmlForRouteReport());
                    reportCriteriaVO.setRoutingUser(new NetworkIdVO(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier()));
                    reportCriteriaVO.setTargetNodeName(givenNodeDetail.getName());
                    boolean value = getKualiWorkflowInfo().documentWillHaveAtLeastOneActionRequest(
                            reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ});
                     return value;
                }
                else {
                    /* Document has had at least one workflow action taken so we need to pass the doc id so the simulation will use the existing
                     * actions taken and action requests in determining if rules will fire or not.  We also need to call a save routing data so that
                     * the xml Workflow uses represents what is currently on the document
                     */ 
                    ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(Long.valueOf(document.getDocumentNumber()));
                    reportCriteriaVO.setXmlContent(document.getXmlForRouteReport());
                    reportCriteriaVO.setTargetNodeName(givenNodeDetail.getName());
                    boolean value = getKualiWorkflowInfo().documentWillHaveAtLeastOneActionRequest(
                            reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ});
                     return value;
                }
            }
            return false;
        }
        catch (WorkflowException e) {
            String errorMessage = "Error trying to test document id '" + document.getDocumentNumber() + "' for action requests at node name '" + givenNodeDetail.getName() + "'";
            LOG.error("isDocumentStoppingAtRouteLevel() " + errorMessage,e);
            throw new RuntimeException(errorMessage,e);
        }
    }

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
    
}
