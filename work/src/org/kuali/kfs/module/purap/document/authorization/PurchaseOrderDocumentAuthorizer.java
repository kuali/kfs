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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the PO document.
 */
public class PurchaseOrderDocumentAuthorizer extends AccountingDocumentAuthorizerBase {


    /**
     * This is essentially the same getEditMode as in DocumentAuthorizerBase.java. In AccountingDocumentAuthorizerBase.java, which
     * is currently the superclass of this class, this method is being overriden. Unfortunately it will return view only edit mode
     * if the initiator of the document is different than the current user. Currently the initiators of Purchase Order Document are
     * all "Kuali System User" which is different than the users that we use to log in. Therefore here we have to re-override the
     * getEditMode to prevent the problem where the fields appear as read-only. There has been an addition to this method, which at
     * this point I'm not sure whether there would be any cases where the Purchase Order Document would have status "RETR". If so,
     * then when the status code is "RETR" (retransmit), the edit mode should be set to displayRetransmitTab because we want to hide
     * the other tabs and display the retransmit tab when the user clicks on the Retransmit button (is that what we want ?)
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
//    @Override
//    public Map getEditMode(Document d, Person user) {
//        Map editModeMap = new HashMap();
//        return editModeMap;
//    }

    //FIXME hjs do we still need this code??
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        PurchaseOrderDocument po = (PurchaseOrderDocument) document;
        String statusCode = po.getStatusCode();

        if ((StringUtils.equals(statusCode, PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT)) || (StringUtils.equals(statusCode, PurchaseOrderStatuses.WAITING_FOR_VENDOR)) || StringUtils.equals(statusCode, PurchaseOrderStatuses.QUOTE)) {
            flags.setCanRoute(false);
        }
        else if (PurchaseOrderStatuses.STATUSES_BY_TRANSMISSION_TYPE.values().contains(statusCode)) {
            if (SpringContext.getBean(PurApWorkflowIntegrationService.class).isActionRequestedOfUserAtNodeName(po.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getPerson())) {
                /*
                 * code below for overriding workflow buttons has to do with hiding the workflow buttons but still allowing the
                 * actions... this is needed because document service calls this method (getDocumentActionFlags) before it will
                 * allow a workflow action to be performed
                 */
                if (ObjectUtils.isNotNull(po.getOverrideWorkflowButtons()) && (po.getOverrideWorkflowButtons())) {
                    /*
                     * if document is in pending transmission status and current user has document transmission action request then
                     * assume that the transmit button/action whatever it might be will take associated workflow action for user
                     * automatically
                     */
                    flags.setCanApprove(false);
                    flags.setCanDisapprove(false);
                    flags.setCanAcknowledge(false);
                    flags.setCanFYI(false);
                }
            }
        }
        if (po.isPendingSplit()) {
            flags.setCanRoute(false);
            flags.setCanSave(false);
            flags.setCanReload(false);
            flags.setCanClose(false);
            flags.setCanCancel(false);
        }
        if (po.isDocumentStoppedInRouteNode(NodeDetailEnum.INTERNAL_PURCHASING_REVIEW)) {
            flags.setCanSave(true);
            // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
            this.setAnnotateFlag(flags);
        }

        return flags;
    }
}

