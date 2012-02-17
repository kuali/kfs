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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

public interface PurApWorkflowIntegrationService {

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
    public boolean takeAllActionsForGivenCriteria(Document document, String potentialAnnotation, String nodeName, Person user, String superUserNetworkId);

    /**
     * Determine if the document will stop at the given node in the future routing process
     * 
     * @param document
     * @param givenNodeName
     * @return boolean indicating if document is going to stop at the given node
     */
    public boolean willDocumentStopAtGivenFutureRouteNode(PurchasingAccountsPayableDocument document, String givenNodeName);
}

