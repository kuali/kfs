/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TmProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;

public class MockTemProfileServiceImpl implements TemProfileService {

    protected TemProfileService realTemProfileService;
    protected DateTimeService dateTimeService;

    public static final int VALID_TEM_PROFILE_ID = 37;

    @Override
    public TmProfile findTemProfileByEmployeeId(String employeeId) {
        return createTemProfileForEmployee(employeeId);
    }

    @Override
    public TmProfile findTemProfileByCustomerNumber(String customerNumber) {
        return createTemProfileForCustomer(customerNumber);
    }

    protected TmProfile createTemProfileForEmployee(String employeeId) {
        TmProfile profile = new TmProfile();
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

    protected TmProfile createTemProfileForCustomer(String customerNumber) {
        TmProfile profile = new TmProfile();
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
    public TmProfile findTemProfile(Map<String, String> criteria) {
        return realTemProfileService.findTemProfile(criteria);
    }

    @Override
    public TmProfile findTemProfileById(Integer profileId) {
        return realTemProfileService.findTemProfileById(profileId);
    }

    @Override
    public TmProfile findTemProfileByPrincipalId(String principalId) {
        return realTemProfileService.findTemProfileByPrincipalId(principalId);
    }

    @Override
    public TemProfileAddress getAddressFromProfile(TmProfile profile, TemProfileAddress defaultAddress) {
        return realTemProfileService.getAddressFromProfile(profile, defaultAddress);
    }

    @Override
    public List<TmProfile> getAllActiveTemProfile() {
        return realTemProfileService.getAllActiveTemProfile();
    }

    @Override
    public void updateACHAccountInfo(TmProfile profile) {
        realTemProfileService.updateACHAccountInfo(profile);
    }

    @Override
    public boolean hasActiveArrangers(TmProfile profile) {
        return realTemProfileService.hasActiveArrangers(profile);
    }

    @Override
    public boolean isProfileNonEmploye(TmProfile profile) {
        return realTemProfileService.isProfileNonEmploye(profile);
    }
}
