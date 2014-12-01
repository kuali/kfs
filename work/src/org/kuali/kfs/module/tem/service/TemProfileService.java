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
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;

public interface TemProfileService {

    /**
     *
     * This method returns a TemProfile associated with the principalId.
     * @param principalId
     * @return
     */
    public TemProfile findTemProfileByPrincipalId(String principalId);

    /**
     *
     * This method retrieves a KIM user's address if it is a KIM user, or the default address if they are not a KIM user.
     * @param profile
     * @param defaultAddress
     * @return
     */
    public TemProfileAddress getAddressFromProfile(TemProfile profile, TemProfileAddress defaultAddress);

    /**
     *
     * This method creates a {@link TemProfileAddress} from the KIM Person
     * @param person
     * @param profileId
     * @param defaultAddress
     * @return
     */
    public TemProfileAddress createTemProfileAddressFromPerson(Person person, Integer profileId, TemProfileAddress defaultAddress);

    /**
     *
     * This method returns a TemProfile matching the criteria passed in.
     * @param criteria
     * @return
     */
    public TemProfile findTemProfile(Map<String,String> criteria);

    /**
     *
     * This method returns a TemProfile from the profileId
     * @param profileId
     * @return
     */
    public TemProfile findTemProfileById(Integer profileId);

    /**
     *
     * This method returns all active TemProfiles
     * @return
     */
    public List<TemProfile> getAllActiveTemProfile();

    /**
     * Lookup PayeeACHAccount with matching employee number in the payee Id field.
     * If PayeeACHAccount is found, update the two profile fields: AchSignUp & AchTransactionType
     *
     * @param profile
     */
    public void updateACHAccountInfo(TemProfile profile);

    /**
     * Determines if the given profile is a record for a non-employee
     * @param profile the profile to check
     * @return true if profile is a non-employee, false if the profile is an employee or if it could not be determined
     */
    public boolean isProfileNonEmploye(TemProfile profile);

    /**
     * Determines if the given profile has any active arrangers
     * @param profile the profile to check
     * @return true if there are active arrangers associated with the profile, false otherwise
     */
    public boolean hasActiveArrangers(TemProfile profile);

    /**
     * This method returns a TemProfile for the given employeeId
     *
     * @param employeeId
     * @return
     */
    public TemProfile findTemProfileByEmployeeId(String employeeId);

    /**
     * This method returns a TemProfile for the given customer number
     *
     * @param customerNumber
     * @return
     */
    public TemProfile findTemProfileByCustomerNumber(String customerNumber);

    /**
     * @return a List of KeyValue pairs which represent genders
     */
    public List<KeyValue> getGenderKeyValues();

    /**
     * Determines if the given profile account already exists in the persistence store
     * @param account the account to check for the existence of
     * @param skipProfile if not null, any profile accounts with the same id as the given skip profile will be skipped
     * @return true if the profile account does exist, false otherwise
     */
    public boolean doesProfileAccountExist(TemProfileAccount account, TemProfile skipProfile);
}
