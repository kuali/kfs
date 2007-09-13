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

public interface PurApWorkflowIntegrationService {

    /**
     * TODO delyea - documentation
     * @param documentNumber
     * @param nodeName
     * @param user
     * @return
     */
    public boolean isActionRequestedOfUserAtNodeName(String documentNumber, String nodeName, UniversalUser user);

    /**
     * TODO delyea - documentation
     * @param document
     * @param potentialAnnotation
     * @param nodeName
     * @param user
     * @param networkIdToImpersonate
     * @return
     */
    public boolean takeAllActionsForGivenCriteria(Document document, String potentialAnnotation, String nodeName, UniversalUser user, String superUserNetworkId);

    /**
     * TODO delyea - documentation
     * @param document
     * @param givenNodeDetail
     * @return
     */
    public boolean willDocumentStopAtGivenFutureRouteNode(PurchasingAccountsPayableDocument document, NodeDetails givenNodeDetail);
}
