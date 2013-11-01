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

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleMembership;

public interface TemRoleService {


    /**
     * Determine if the principal Id is the profile's arranger - do not check document type
     *
     * @param principalId
     * @param profileId
     * @return
     */
    public boolean isArrangerForProfile(String principalId, int profileId);

    /**
     * Determine if the principal Id is the profile's arranger
     * Also check if the arranger is allowed for specific doc type: TA(TAA,TAC) or TR/RELO/ENT
     *
     * @param documentType
     * @param principalId
     * @param profileId
     * @return
     */
    public boolean isTravelDocumentArrangerForProfile(String documentType, String principalId, Integer profileId);

    /**
     * Check if user is stored as profile arranger
     *
     * @param principalId
     * @return
     */
    public boolean isProfileArranger(String principalId);

    /**
     * Check if user is a profile administrator
     *
     * @param currentUser
     * @param homeDepartment
     * @return
     */
    public boolean isProfileAdmin(Person currentUser, String homeDepartment);

    /**
     * First check if the user is the initiator, if its not,
     *
     * check from the document profile that whether the user has the arranger access by checking the two roles:
     * 1) Assigned Profile Arranger and 2) Org Profile Arranger
     *
     * NOTE: This does NOT check on manager type role access
     *
     * @param travelDocument
     * @param currentUser
     * @return
     */
    public boolean canAccessTravelDocument(TravelDocument travelDocument, Person currentUser);

    /**
     * Check to see if the user is a travel arranger
     *
     * documentType & profileId are the qualifications required to check for arranger role:
     * Assigned Profile Arranger
     *
     * primary department code is to check for org role:
     * Organization Profile Arranger
     *
     * NOTE: When profileId is not specified, Assigned Profile Arranger role check if the user exists in the Profile
     * Arranger table
     *
     * @param user
     * @param primaryDepartmentCode
     * @param profileId
     * @return
     */
    public boolean isTravelArranger(final Person user, final String primaryDepartmentCode, String profileId, String documentType);

    /**
     * Check to see if the user has the travel arranger role assigned to them
     *
     * @param user
     * @return true if the user is a travel arranger, false otherwise
     */
    public boolean isTravelArranger(final Person user);

    /**
     * Check to see if the user has the travel manager role assigned to them
     *
     * @param user
     * @return true if the user is a travel manager, false otherwise
     */
    public boolean isTravelManager(final Person user);

    /**
     * Check TEM specific user role w/o qualification
     *
     * @param user
     * @param role
     * @param parameterNamespace
     * @return
     */
    public boolean checkUserTemRole(final Person user, String role);

    /**
     *
     * @param user
     * @param role
     * @param parameterNamespace
     * @param qualification
     * @return
     */
    public boolean checkUserRole(final Person user, String role, String parameterNamespace, Map<String,String> qualification);

    /**
     *
     * @param user
     * @param role
     * @param parameterNamespace
     * @param primaryDepartmentCode
     * @return
     */
    public boolean checkOrganizationRole(final Person user, String role, String parameterNamespace, String primaryDepartmentCode);

    public Collection<RoleMembership> getTravelArrangers(String chartCode, String orgCode);


}
