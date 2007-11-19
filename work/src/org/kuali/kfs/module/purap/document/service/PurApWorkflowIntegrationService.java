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
package org.kuali.module.purap.service;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

public interface PurApWorkflowIntegrationService {

    /**
     * Determine if action is requested of user at given node name
     * 
     * @param documentNumber
     * @param nodeName
     * @param user
     * @return boolean indicating if action is requested of user at given node name
     */
    public boolean isActionRequestedOfUserAtNodeName(String documentNumber, String nodeName, UniversalUser user);

    /**
     * Take all actions on the given document based on the given criteria
     * 
     * @param document
     * @param potentialAnnotation
     * @param nodeName
     * @param user
     * @param networkIdToImpersonate
     * @return
     */
    public boolean takeAllActionsForGivenCriteria(Document document, String potentialAnnotation, String nodeName, UniversalUser user, String superUserNetworkId);

    /**
     * Determine if the document will stop at the given node in the future routing process
     * 
     * @param document
     * @param givenNodeDetail
     * @return boolean indicating if document is going to stop at the given node
     */
    public boolean willDocumentStopAtGivenFutureRouteNode(PurchasingAccountsPayableDocument document, NodeDetails givenNodeDetail);

    public String getLastUserId(DocumentRouteHeaderValue routeHeader) throws EdenUserNotFoundException;
}
