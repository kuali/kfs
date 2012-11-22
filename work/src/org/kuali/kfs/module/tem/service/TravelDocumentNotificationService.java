/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

/**
 * define the notification to the traveler based on the email preferences stored in the traveler profile
 */
public interface TravelDocumentNotificationService {

    /**
     * send notification when the status of the given travel document is changed
     *
     * @param travelDocument the given travel document
     * @param statusChangeDTO the given document status change
     */
    void sendNotificationOnChange(TravelDocument travelDocument, DocumentRouteStatusChange statusChangeDTO);
}
