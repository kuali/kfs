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

import java.util.Collection;

import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.rice.kew.exception.WorkflowException;

public interface TravelEntertainmentDocumentService {
    public Collection<TravelEntertainmentDocument> findByTravelId(String travelDocumentIdentifier);

    public TravelEntertainmentDocument find(final String documentNumber) throws WorkflowException;

    Coversheet generateCoversheetFor(final TravelEntertainmentDocument document) throws Exception;

    void addListenersTo(TravelEntertainmentDocument entertainment);

    void handleNewAttendee(Attendee newAttendeeLine);
 
}
