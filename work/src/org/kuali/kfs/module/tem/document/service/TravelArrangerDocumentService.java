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
package org.kuali.kfs.module.tem.document.service;

import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;

public interface TravelArrangerDocumentService {

    public void createTravelProfileArranger(TravelArrangerDocument arrangerDoc);

    public TemProfileArranger findPrimaryTravelProfileArranger(String arrangerId, Integer profileId);

    public TemProfileArranger findTemProfileArranger(String principalId, Integer profileId);

    public void inactivateTravelProfileArranger(TravelArrangerDocument arrangerDoc);

    /**
     * Determines if the user with the given principal id is an active arranger for any profiles
     * @param principalId the principal id to check if the user is an arranger for
     * @return true if the given principal id represents a user who arranges for any travel profiles; false otherwise
     */
    public boolean hasArrangees(String principalId);

}
