/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.entity.Entity;

public interface TravelerService {

    /**
     * Creates a {@link TravelerDetail} from a {@link Person} instance
     *
     * @param person to create {@link TravelerDetail} instance from
     * @return a new {@link Traveler} detail instance
     */
    TravelerDetail convertToTraveler(final Person person);

    /**
     * Creates a {@link TravelerDetail} from a {@link Person} instance
     *
     * @param person to create {@link TravelerDetail} instance from
     * @return a new {@link Traveler} detail instance
     */
    TravelerDetail convertToTraveler(final AccountsReceivableCustomer person);

    /**
     * Determines if the given {@link TravelerDetail} instance is an employee by comparing
     * what we know about employee traveler types. This is really up to the implementation.
     *
     * @param traveler a {@link TravelerDetail} instance to check
     * @return whether our institution regards the {@link TravelerDetail} instance to be an
     * employee or not
     */
    boolean isEmployee(final TravelerDetail traveler);

    /**
     *
     * @param profile
     * @param docType
     * @param user
     * @param isProfileAdmin
     * @param isAssignedArranger
     * @param isOrgArranger
     * @param isArrangerDoc
     * @param isRiskManagement
     * @return
     */
    public boolean canIncludeProfileInSearch(TemProfile profile, String docType, Person user, boolean isProfileAdmin, boolean isAssignedArranger, boolean isOrgArranger, boolean isArrangerDoc, boolean isRiskManagement);

    /**
     *
     * @param profile
     * @param customer
     */
    public void copyTemProfileToCustomer(TemProfile profile, AccountsReceivableCustomer customer);

    /**
     *
     * @param profile
     * @param customer
     */
    public void copyCustomerToTemProfile(TemProfile profile, AccountsReceivableCustomer customer);

    /**
     *
     * @param profile
     * @param principal
     * @param kimEntityInfo
     */
    public void copyKimDataToTemProfile(TemProfile profile, Person principal, Entity kimEntityInfo);

    /**
     *
     * This method converts a KIM Person to a TemProfileFromKim object so that we can then use it to
     * actually create a TemProfile from it
     * @param personDetail
     * @return the TemProfileFromKimPerson object populated from the search
     */
    TemProfileFromKimPerson convertToTemProfileFromKim(Person personDetail);

    /**
     *
     * This method converts a Customer to a TemProfileFromCustomer object so that we can then use it to
     * actually create a TemProfile from it
     * @param person to create {@link TemProfileFromCustomer} instance from
     * @return a new {@link TemProfileFromCustomer} instance
     */
    TemProfileFromCustomer convertToTemProfileFromCustomer(AccountsReceivableCustomer person);


    public void convertTemProfileToTravelerDetail(TemProfile profile, TravelerDetail detail);

    /**
     *
     * This method copies the fromTraveler to a new TravelerDetail.
     *
     * Document Number is set in the copied emergency contacts and the TravelerDetail
     *
     * @param fromTraveler
     * @param documentNumber
     * @return
     */
    public TravelerDetail copyTravelerDetail(TravelerDetail fromTraveler, String documentNumber);

    /**
     * Copies traveler detail emergency contacts and sets new document number
     */
    public List<TravelerDetailEmergencyContact> copyTravelerDetailEmergencyContact(List<TravelerDetailEmergencyContact> emergencyContacts, String documentNumber);

    /**
     *
     * @param profile
     */
    public void populateTemProfile(TemProfile profile);

    /**
     *
     * @param person
     * @return
     */
    public boolean isCustomerEmployee(AccountsReceivableCustomer person);

    /**
     *
     * @param principal
     * @return
     */
    public boolean isKimPersonEmployee(Person principal);

    /**
     *
     * This method converts a CustomerAddress to a TemProfileAddress object so that we can then use it to
     * actually create a TemProfile from it
     * @param person to create {@link CustomerAddress} instance from
     * @return a new {@link TemProfileAddress} instance
     */
	TemProfileAddress convertToTemProfileAddressFromCustomer(AccountsReceivableCustomerAddress customerAddress);

	/**
     *
     * This method converts a KimEntityAddressImpl to a TemProfileAddress object so that we can then use it to
     * actually create a TemProfile from it
     * @param person to create {@link KimEntityAddressImpl} instance from
     * @return a new {@link TemProfileAddress} instance
     */
	TemProfileAddress convertToTemProfileAddressFromKimAddress(EntityAddressContract address);

	/**
	 *
	 * @param chartCode
	 * @param orgCode
	 * @param roleChartCode
	 * @param roleOrgCode
	 * @param descendHierarchy
	 * @return
	 */
	public boolean isParentOrg(String chartCode, String orgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy);
}
