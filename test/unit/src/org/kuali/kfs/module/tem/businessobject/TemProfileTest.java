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
