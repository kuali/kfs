/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TmProfile;
import org.kuali.kfs.module.tem.businessobject.TmProfileArranger;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class TemProfileServiceImpl implements TemProfileService {

    private BusinessObjectService businessObjectService;
    private PersonService personService;

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileByPrincipalId(java.lang.String)
     */
    @Override
    public TmProfile findTemProfileByPrincipalId(String principalId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TEMProfileProperties.PRINCIPAL_ID, principalId);
        return findTemProfile(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileById(java.lang.Integer)
     */
    @Override
    public TmProfile findTemProfileById(Integer profileId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TEMProfileProperties.PROFILE_ID, String.valueOf(profileId));
        return findTemProfile(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfile(java.util.Map)
     */
    @Override
    public TmProfile findTemProfile(Map<String, String> criteria) {
        Collection<TmProfile> profiles = getBusinessObjectService().findMatching(TmProfile.class, criteria);
        if(ObjectUtils.isNotNull(profiles) && profiles.size() > 0) {
            return profiles.iterator().next();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#getAddressFromProfile(org.kuali.kfs.module.tem.businessobject.TmProfile, org.kuali.kfs.module.tem.businessobject.TemProfileAddress)
     */
    @Override
    public TemProfileAddress getAddressFromProfile(TmProfile profile, TemProfileAddress defaultAddress) {

        if(ObjectUtils.isNull(defaultAddress)) {
        	defaultAddress = new TemProfileAddress();
        }

        if (!StringUtils.isEmpty(profile.getPrincipalId())) {
            Person person = getPersonService().getPerson(profile.getPrincipalId());
            TemProfileAddress kimAddress = createTemProfileAddressFromPerson(person, profile.getProfileId(), defaultAddress);
            return kimAddress;
        }
        return defaultAddress;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#createTemProfileAddressFromPerson(org.kuali.rice.kim.bo.Person, java.lang.Integer, org.kuali.kfs.module.tem.businessobject.TemProfileAddress)
     */
    @Override
    public TemProfileAddress createTemProfileAddressFromPerson(Person person, Integer profileId, TemProfileAddress defaultAddress) {
        defaultAddress.setProfileId(profileId);
        defaultAddress.setStreetAddressLine1(StringUtils.upperCase(person.getAddressLine1()));
        defaultAddress.setStreetAddressLine2(StringUtils.upperCase(person.getAddressLine2()));
        defaultAddress.setCityName(StringUtils.upperCase(person.getAddressCity()));
        defaultAddress.setStateCode(StringUtils.upperCase(person.getAddressStateProvinceCode()));
        defaultAddress.setZipCode(person.getAddressPostalCode());
        defaultAddress.setCountryCode(StringUtils.upperCase(person.getAddressCountryCode()));
        return defaultAddress;
    }

	/**
	 * @see org.kuali.kfs.module.tem.service.TemProfileService#getAllActiveTemProfile()
	 */
	@Override
	public List<TmProfile> getAllActiveTemProfile() {
		Map<String,Object> criteria = new HashMap<String,Object>(3);
        criteria.put(KFSPropertyConstants.ACTIVE, true);
		List<TmProfile> profiles = (List<TmProfile>) getBusinessObjectService().findMatching(TmProfile.class, criteria);
		return profiles;
	}

	/**
	 * @see org.kuali.kfs.module.tem.service.TemProfileService#updateACHAccountInfo(org.kuali.kfs.module.tem.businessobject.TmProfile)
	 */
	@Override
    public void updateACHAccountInfo(TmProfile profile){

	    //set defaults
        profile.setAchSignUp("No");
        profile.setAchTransactionType("None");

        if (TemConstants.EMP_TRAVELER_TYP_CD.equals(profile.getTravelerTypeCode()) &&
                profile.getEmployeeId() != null) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSPropertyConstants.PAYEE_ID_NUMBER, profile.getEmployeeId());
            List<PayeeACHAccount> accounts = (List<PayeeACHAccount>) getBusinessObjectService().findMatching(PayeeACHAccount.class, fieldValues);

            //if there are any ACH accounts matching the employee Id lookup, use the first one for display
            if (!accounts.isEmpty()){
                profile.setAchSignUp("Yes");
                profile.setAchTransactionType(accounts.get(0).getAchTransactionType());
            }
        }
	}

	/**
	 * @see org.kuali.kfs.module.tem.service.TemProfileService#isProfileNonEmploye(org.kuali.kfs.module.tem.businessobject.TmProfile)
	 */
    @Override
    public boolean isProfileNonEmploye(TmProfile profile) {
        return !StringUtils.isBlank(profile.getTravelerTypeCode()) && profile.getTravelerTypeCode().equals(TemConstants.NONEMP_TRAVELER_TYP_CD);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#hasActiveArrangers(org.kuali.kfs.module.tem.businessobject.TmProfile)
     */
    @Override
    public boolean hasActiveArrangers(TmProfile profile) {
        for (TmProfileArranger arranger : profile.getArrangers()) {
            if (arranger.isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileByEmployeeId(java.lang.String)
     */
    @Override
    public TmProfile findTemProfileByEmployeeId(String employeeId) {
        final Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TEMProfileProperties.EMPLOYEE_ID, employeeId);
        return findTemProfile(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileByCustomerNumber(java.lang.String)
     */
    @Override
    public TmProfile findTemProfileByCustomerNumber(String customerNumber) {
        final Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TEMProfileProperties.CUSTOMER_NUMBER, customerNumber);
        return findTemProfile(criteria);
    }

    /**
     * Gets the personService attribute.
     * @return Returns the personService.
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Sets the personService attribute value.
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
