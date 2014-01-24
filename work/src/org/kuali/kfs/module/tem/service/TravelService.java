/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Travel Service
 */
public interface TravelService {

    /**
     * Validate a phone number
     *
     * @param phoneNumber to validate
     */
    String validatePhoneNumber(final String phoneNumber, String error);

    /**
     * Validate a phone number
     *
     * @param countryCode to consider for validation
     * @param phoneNumber to validate
     */
    String validatePhoneNumber(final String countryCode, final String phoneNumber, String error);

    /**
     *
     * This method returns a TemProfile associated with the principalId.
     * @param principalId
     * @return
     */
    public TemProfile findTemProfileByPrincipalId(String principalId);

    /**
     * Determine if the user is the initiator or it is the arranger for the document (base on specific doc
     * type)
     *
     * @param document
     * @param user
     * @return
     */
    public boolean isUserInitiatorOrArranger(TravelDocument document, Person user);

    /**
     * Get Travel Card Type code list
     *
     * @return
     */
    public List<String> getTravelCardTypes();

    /**
     * Retrieves the parent document type names - up to "TT" - for the document type
     * @param documentTypeName the document type to find the ancestry of
     * @return the document type names, including TT and the given document type
     */
    public Set<String> getParentDocumentTypeNames(String documentTypeName);
}