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
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;

public class MockTemProfileServiceImpl implements TemProfileService {

    protected TemProfileService realTemProfileService;
    protected DateTimeService dateTimeService;

    public static final int VALID_TEM_PROFILE_ID = 37;

    @Override
    public TemProfile findTemProfileByEmployeeId(String employeeId) {
        if (StringUtils.equals(employeeId, "987654321") || StringUtils.equals(employeeId, "ABC1234")) { // skip the values used by ExpenseImportByTravelerServiceTest#testValidateTraveler
            return null;
        }
        return createTemProfileForEmployee(employeeId);
    }

    @Override
    public TemProfile findTemProfileByCustomerNumber(String customerNumber) {
        if (StringUtils.equals(customerNumber, "987654321")) {
            return null;
        }
        return createTemProfileForCustomer(customerNumber);
    }

    protected TemProfile createTemProfileForEmployee(String employeeId) {
        TemProfile profile = new TemProfile();
        profile.setProfileId(VALID_TEM_PROFILE_ID);
        profile.getTemProfileAddress().setProfileId(VALID_TEM_PROFILE_ID);
        profile.setEmployeeId(employeeId);
        profile.setDefaultChartCode("BL");
        profile.setDefaultAccount("1031400");
        profile.setDefaultSubAccount("ADV");
        profile.setDefaultProjectCode("KUL");
        profile.setDateOfBirth(dateTimeService.getCurrentSqlDate());
        profile.setGender("M");
        profile.setHomeDeptOrgCode("BL");
        profile.setHomeDeptChartOfAccountsCode("BL");
        return profile;
    }

    protected TemProfile createTemProfileForCustomer(String customerNumber) {
        TemProfile profile = new TemProfile();
        profile.setProfileId(VALID_TEM_PROFILE_ID);
        profile.getTemProfileAddress().setProfileId(VALID_TEM_PROFILE_ID);
        profile.setCustomerNumber(customerNumber);
        profile.setDefaultChartCode("BL");
        profile.setDefaultAccount("1031400");
        profile.setDefaultSubAccount("ADV");
        profile.setDefaultProjectCode("KUL");
        profile.setDateOfBirth(dateTimeService.getCurrentSqlDate());
        profile.setGender("M");
        profile.setHomeDeptOrgCode("BL");
        profile.setHomeDeptChartOfAccountsCode("BL");
        return profile;
    }


    public void setProfileService(TemProfileService temProfileService) {
        this.realTemProfileService = temProfileService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

/** methods which have not been changed for testing **/

    @Override
    public TemProfileAddress createTemProfileAddressFromPerson(Person person, Integer profileId, TemProfileAddress defaultAddress) {
        return realTemProfileService.createTemProfileAddressFromPerson(person, profileId, defaultAddress);
    }

    @Override
    public TemProfile findTemProfile(Map<String, String> criteria) {
        return realTemProfileService.findTemProfile(criteria);
    }

    @Override
    public TemProfile findTemProfileById(Integer profileId) {
        return realTemProfileService.findTemProfileById(profileId);
    }

    @Override
    public TemProfile findTemProfileByPrincipalId(String principalId) {
        return realTemProfileService.findTemProfileByPrincipalId(principalId);
    }

    @Override
    public TemProfileAddress getAddressFromProfile(TemProfile profile, TemProfileAddress defaultAddress) {
        return realTemProfileService.getAddressFromProfile(profile, defaultAddress);
    }

    @Override
    public List<TemProfile> getAllActiveTemProfile() {
        return realTemProfileService.getAllActiveTemProfile();
    }

    @Override
    public void updateACHAccountInfo(TemProfile profile) {
        realTemProfileService.updateACHAccountInfo(profile);
    }

    @Override
    public boolean hasActiveArrangers(TemProfile profile) {
        return realTemProfileService.hasActiveArrangers(profile);
    }

    @Override
    public boolean isProfileNonEmploye(TemProfile profile) {
        return realTemProfileService.isProfileNonEmploye(profile);
    }

    @Override
    public List<KeyValue> getGenderKeyValues() {
        return realTemProfileService.getGenderKeyValues();
    }

    @Override
    public boolean doesProfileAccountExist(TemProfileAccount account, TemProfile skipProfile) {
        return realTemProfileService.doesProfileAccountExist(account, skipProfile);
    }

}
