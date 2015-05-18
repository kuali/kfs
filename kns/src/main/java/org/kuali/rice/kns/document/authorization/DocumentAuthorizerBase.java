/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.doctype.ProcessDefinition;
import org.kuali.rice.kew.api.doctype.RoutePath;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.bo.authorization.BusinessObjectAuthorizerBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization
 * code.
 */
public class DocumentAuthorizerBase extends BusinessObjectAuthorizerBase implements DocumentAuthorizer {
    protected static Log LOG = LogFactory.getLog(DocumentAuthorizerBase.class);

    public static final String PRE_ROUTING_ROUTE_NAME = "PreRoute";
    public static final String EDIT_MODE_DEFAULT_TRUE_VALUE = "TRUE";
    public static final String USER_SESSION_METHOD_TO_CALL_OBJECT_KEY = "METHOD_TO_CALL_KEYS_METHOD_OBJECT_KEY";
    public static final String USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY =
            "METHOD_TO_CALL_KEYS_COMPLETE_OBJECT_KEY";
    public static final String USER_SESSION_METHOD_TO_CALL_COMPLETE_MARKER = "_EXITING";

    /**
     * Individual document families will need to reimplement this according to
     * their own needs; this version should be good enough to be usable during
     * initial development.
     */
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActions) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '"
                    + document.getDocumentNumber()
                    + "'. user '"
                    + user.getPrincipalName()
                    + "'");
        }
        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && !canEdit(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_COPY) && !canCopy(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_CLOSE) && !canClose(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_CLOSE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_RELOAD) && !canReload(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_RELOAD);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE) && !canBlanketApprove(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_CANCEL) && !canCancel(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_RECALL) && !canRecall(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_RECALL);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_SAVE) && !canSave(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ROUTE) && !canRoute(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE) && !canAcknowledge(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_FYI) && !canFyi(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_APPROVE) && !canApprove(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE) && !canDisapprove(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
        }

        if (!canSendAnyTypeAdHocRequests(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI) && !canSendNoteFyi(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ANNOTATE) && !canAnnotate(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW) && !canEditDocumentOverview(
                document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT) && !canPerformRouteReport(document,
                user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
        }

        return documentActions;
    }

    public boolean canInitiate(String documentTypeName, Person user) {
        String nameSpaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), nameSpaceCode,
                KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails,
                Collections.<String, String>emptyMap());
    }

    public boolean canEdit(Document document, Person user) {
        // KULRICE-7864: document can be editable on adhoc route for completion 
        return document.getDocumentHeader().getWorkflowDocument().isCompletionRequested()
                || isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId());
    }

    public boolean canAnnotate(Document document, Person user) {
        return canEdit(document, user);
    }

    public boolean canReload(Document document, Person user) {
        return true;
    }

    public boolean canClose(Document document, Person user) {
        return true;
    }

    public boolean canSave(Document document, Person user) {
        return isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KimConstants.PermissionTemplateNames.SAVE_DOCUMENT, user.getPrincipalId());
    }

    public boolean canRoute(Document document, Person user) {
        return isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KimConstants.PermissionTemplateNames.ROUTE_DOCUMENT, user.getPrincipalId());
    }

    public boolean canCancel(Document document, Person user) {
        // KULRICE-8762: CANCEL button should be enabled for a person who is doing COMPLETE action 
        boolean isCompletionRequested = document.getDocumentHeader().getWorkflowDocument().isCompletionRequested();
        return isCompletionRequested || isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KimConstants.PermissionTemplateNames.CANCEL_DOCUMENT, user.getPrincipalId());
    }

    public boolean canRecall(Document document, Person user) {
        return KewApiServiceLocator.getWorkflowDocumentActionsService().determineValidActions(document.getDocumentNumber(), user.getPrincipalId()).getValidActions().contains(ActionType.RECALL);
    }

    public boolean canCopy(Document document, Person user) {
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.COPY_DOCUMENT, user.getPrincipalId());
    }

    public boolean canPerformRouteReport(Document document, Person user) {
        return true;
    }

    public boolean canBlanketApprove(Document document, Person user) {
        return isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KimConstants.PermissionTemplateNames.BLANKET_APPROVE_DOCUMENT, user.getPrincipalId());
    }

    public boolean canApprove(Document document, Person user) {
        return canTakeRequestedAction(document, KewApiConstants.ACTION_REQUEST_APPROVE_REQ, user);
    }

    public boolean canDisapprove(Document document, Person user) {
        return canApprove(document, user);
    }

    public boolean canSendNoteFyi(Document document, Person user) {
        return canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_FYI_REQ, user);
    }

    public boolean canFyi(Document document, Person user) {
        return canTakeRequestedAction(document, KewApiConstants.ACTION_REQUEST_FYI_REQ, user);
    }

    public boolean canAcknowledge(Document document, Person user) {
        return canTakeRequestedAction(document, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, user);
    }

    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_REQUEST_CD, actionRequestCode);
        return isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KimConstants.PermissionTemplateNames.AD_HOC_REVIEW_DOCUMENT, user.getPrincipalId(),
                additionalPermissionDetails, Collections.<String, String>emptyMap());
    }

    public boolean canOpen(Document document, Person user) {
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, user.getPrincipalId());
    }

    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        if (attachmentTypeCode != null) {
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ATTACHMENT_TYPE_CODE, attachmentTypeCode);
        }
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.ADD_NOTE_ATTACHMENT, user.getPrincipalId(),
                additionalPermissionDetails, Collections.<String, String>emptyMap());
    }

    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String createdBySelfOnly,
            Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        if (attachmentTypeCode != null) {
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ATTACHMENT_TYPE_CODE, attachmentTypeCode);
        }
        additionalPermissionDetails.put(KimConstants.AttributeConstants.CREATED_BY_SELF, createdBySelfOnly);
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.DELETE_NOTE_ATTACHMENT, user.getPrincipalId(),
                additionalPermissionDetails, Collections.<String, String>emptyMap());
    }

    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        if (attachmentTypeCode != null) {
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ATTACHMENT_TYPE_CODE, attachmentTypeCode);
        }
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.VIEW_NOTE_ATTACHMENT, user.getPrincipalId(),
                additionalPermissionDetails, Collections.<String, String>emptyMap());
    }

    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, String authorUniversalIdentifier,
            Person user) {
        return canViewNoteAttachment(document, attachmentTypeCode, user);
    }

    public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        if (actionRequestCd != null) {
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_REQUEST_CD, actionRequestCd);
        }
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.SEND_AD_HOC_REQUEST, user.getPrincipalId(),
                additionalPermissionDetails, Collections.<String, String>emptyMap());
    }

    public boolean canEditDocumentOverview(Document document, Person user) {
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId()) && this.isDocumentInitiator(
                document, user);
    }

    public boolean canSendAnyTypeAdHocRequests(Document document, Person user) {
        if (canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_FYI_REQ, user)) {
            RoutePath routePath = KewApiServiceLocator.getDocumentTypeService().getRoutePathForDocumentTypeName(
                    document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            ProcessDefinition processDefinition = routePath.getPrimaryProcess();
            if (processDefinition != null) {
                if (processDefinition.getInitialRouteNode() == null) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } else if (canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, user)) {
            return true;
        }
        return canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_APPROVE_REQ, user);
    }

    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_REQUEST_CD, actionRequestCode);
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.TAKE_REQUESTED_ACTION, user.getPrincipalId(),
                additionalPermissionDetails, Collections.<String, String>emptyMap());
    }

    @Override
    protected void addPermissionDetails(Object dataObject, Map<String, String> attributes) {
        super.addPermissionDetails(dataObject, attributes);
        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);
        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

    protected void addStandardAttributes(Document document, Map<String, String> attributes) {
        WorkflowDocument wd = document.getDocumentHeader().getWorkflowDocument();
        attributes.put(KimConstants.AttributeConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        attributes.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, wd.getDocumentTypeName());
        if (wd.isInitiated() || wd.isSaved()) {
            attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, PRE_ROUTING_ROUTE_NAME);
        } else {
            attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME,
                    KRADServiceLocatorWeb.getWorkflowDocumentService().getCurrentRouteNodeNames(wd));
        }
        attributes.put(KimConstants.AttributeConstants.ROUTE_STATUS_CODE, wd.getStatus().getCode());
    }

    protected boolean isDocumentInitiator(Document document, Person user) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return workflowDocument.getInitiatorPrincipalId().equalsIgnoreCase(user.getPrincipalId());
    }
}
