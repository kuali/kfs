/*
 * Copyright 2010 The Kuali Foundation.
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
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;

public interface TravelAuthorizationService {
    
    public void createCustomerInvoice(TravelAuthorizationDocument travelAuthorizationDocument);

    public void createDVARDocument(TravelAuthorizationDocument travelAuthorizationDocument);
    
    public TravelAuthorizationDocument getTravelAuthorizationBy(String documentNumber); 

    /**
     * Locate all {@link TravelAuthorizationDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelAuthorizationDocument} instances
     * @return {@link Collection} of {@link TravelAuthorizationDocument} instances
     */
    public Collection<TravelAuthorizationDocument> find(String travelDocumentIdentifier);

    /**
     * Locate all {@link TravelAuthorizationAmendmentDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelAuthorizationAmendmentDocument} instances
     * @return {@link Collection} of {@link TravelAuthorizationAmendmentDocument} instances
     */
    public Collection<TravelAuthorizationAmendmentDocument> findAmendment(Integer travelDocumentIdentifier);

    void setTravelDocumentDao(final TravelDocumentDao travelDocumentDao);
    
    //TODO: remove when sure.
    //public void adjustEncumbranceForAmendment(TravelDocument taDocument);    
}
