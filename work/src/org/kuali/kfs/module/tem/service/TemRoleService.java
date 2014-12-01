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

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleMembership;

/**
 * Ideally, this service would not exist - everything would be done via permissions
 */
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
