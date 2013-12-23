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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.SequenceAccessorService;

public class TemProfileTest extends KualiTestBase {

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private SequenceAccessorService sas;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        sas = SpringContext.getBean(SequenceAccessorService.class);
    }

    @SuppressWarnings("deprecation")
    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception {
        TemProfile profile = new TemProfile();
        Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
        profile.setProfileId(newProfileId);
        profile.getTemProfileAddress().setProfileId(newProfileId);
        profile.setCustomerNumber("555555555");
        profile.setPrincipalId("66666666");
        profile.setDateOfBirth(new Date(Date.parse("03/03/1975")));
        profile.setCitizenship("United States");
        profile.setDriversLicenseExpDate(new Date(Date.parse("03/03/2014")));
        profile.setDriversLicenseNumber("B43212345");
        profile.setUpdatedBy("jamey");
        profile.setLastUpdate(new Date(Date.parse("03/03/2011")));
        profile.setGender("M");
        profile.setNonResidentAlien(false);
        profile.setHomeDeptChartOfAccountsCode("UA");
        profile.setHomeDeptOrgCode("VPIT");

        List<TravelerType> travelerTypes = (List<TravelerType>) businessObjectService.findMatching(TravelerType.class, new HashMap<String, Object>());
        profile.setTravelerType(travelerTypes.get(0));
        profile.setTravelerTypeCode(profile.getTravelerType().getCode());

        businessObjectService.save(profile);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put(TemPropertyConstants.TemProfileProperties.PROFILE_ID, profile.getProfileId());

        List<TemProfile> profileList = (List<TemProfile>) businessObjectService.findMatching(TemProfile.class, values);
        try{
            assertTrue(profile.getCustomerNumber().equals(profileList.get(0).getCustomerNumber()));
            assertTrue(profile.getPrincipalId().equals(profileList.get(0).getPrincipalId()));
            assertTrue(profile.getDateOfBirth().equals(profileList.get(0).getDateOfBirth()));
            assertTrue(profile.getCitizenship().equals(profileList.get(0).getCitizenship()));
            assertTrue(profile.getDriversLicenseExpDate().equals(profileList.get(0).getDriversLicenseExpDate()));
            assertTrue(profile.getDriversLicenseNumber().equals(profileList.get(0).getDriversLicenseNumber()));
            assertTrue(profile.getUpdatedBy().equals(profileList.get(0).getUpdatedBy()));
            assertTrue(profile.getLastUpdate().equals(profileList.get(0).getLastUpdate()));
            assertTrue(profile.getGender().equals(profileList.get(0).getGender()));
            assertTrue(profile.getNonResidentAlien().equals(profileList.get(0).getNonResidentAlien()));
            assertTrue(profile.getHomeDepartment().equals(profileList.get(0).getHomeDepartment()));
        }
        catch(Exception e){
            assert(false);
        }

    }


}
