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

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;

public class TemProfileServiceImpl implements TemProfileService {
    
    private BusinessObjectService businessObjectService;
    private PersonService<Person> personService;

    @Override
    public TEMProfile findTemProfileByPrincipalId(String principalId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TEMProfileProperties.PRINCIPAL_ID, principalId);
        return findTemProfile(criteria);
    }

    @Override
    public TEMProfile findTemProfileById(Integer profileId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TEMProfileProperties.PROFILE_ID, String.valueOf(profileId));
        return findTemProfile(criteria);
    }

    @Override
    public TEMProfile findTemProfile(Map<String, String> criteria) {
        Collection<TEMProfile> profiles = getBusinessObjectService().findMatching(TEMProfile.class, criteria);
        if(ObjectUtils.isNotNull(profiles) && profiles.size() > 0) {
            return profiles.iterator().next();
        }
        return null;
    }

    @Override
    public TemProfileAddress getAddressFromProfile(TEMProfile profile, TemProfileAddress defaultAddress) {
        
        if(ObjectUtils.isNull(defaultAddress)) {
        	defaultAddress = new TemProfileAddress();
        }

        if (!StringUtils.isEmpty(profile.getPrincipalId())) {
            PersonImpl person = (PersonImpl) getPersonService().getPerson(profile.getPrincipalId());
            
            TemProfileAddress kimAddress = createTemProfileAddressFromPerson(person, profile.getProfileId(), defaultAddress);
            return kimAddress;
        }
        return defaultAddress;
    }

    @Override
    public TemProfileAddress createTemProfileAddressFromPerson(Person person, Integer profileId, TemProfileAddress defaultAddress) {
        defaultAddress.setProfileId(profileId);
        defaultAddress.setStreetAddressLine1(person.getAddressLine1().toUpperCase());
        defaultAddress.setStreetAddressLine2(person.getAddressLine2().toUpperCase());
        defaultAddress.setCityName(person.getAddressCityName().toUpperCase());
        defaultAddress.setStateCode(person.getAddressStateCode().toUpperCase());
        defaultAddress.setZipCode(person.getAddressPostalCode());
        defaultAddress.setCountryCode(person.getAddressCountryCode().toUpperCase());
        return defaultAddress;
    }
    
	@Override
	public List<TEMProfile> getAllActiveTemProfile() {
		Map<String,Object> criteria = new HashMap<String,Object>(1);
        criteria.put(KNSPropertyConstants.ACTIVE, true);
		List<TEMProfile> profiles = (List<TEMProfile>) getBusinessObjectService().findMatching(TEMProfile.class, criteria);
		return profiles;
	}

    /**
     * Gets the personService attribute. 
     * @return Returns the personService.
     */
    public PersonService<Person> getPersonService() {
        return personService;
    }

    /**
     * Sets the personService attribute value.
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService<Person> personService) {
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
