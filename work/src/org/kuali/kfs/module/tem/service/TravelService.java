/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
