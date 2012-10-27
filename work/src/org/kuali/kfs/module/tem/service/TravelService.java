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

import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kim.bo.Person;

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
     * This method returns a TEMProfile associated with the principalId.
     * @param principalId
     * @return
     */
    public TEMProfile findTemProfileByPrincipalId(String principalId);
    
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
     * 
     * @param tripType
     * @return
     */
    public List<PrimaryDestination> findAllDistinctPrimaryDestinations(String tripType);
    
    /**
     * Find default Primary destination base on provided class
     * 
     * @param clazz
     * @param countryCode
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List findDefaultPrimaryDestinations(Class clazz, String countryCode);
}