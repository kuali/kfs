/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.vnd.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.StatesZips;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.impl.country.CountryBo;
import org.kuali.rice.location.impl.state.StateBo;

public enum VendorRuleAddressStateZipFixture {

    BOTH_US_BOTH_STATES_BOTH_ZIPS(KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode, KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode)
//    , BOTH_US_WITHOUT_STATES_WITHOUT_ZIPS(KFSConstants.COUNTRY_CODE_UNITED_STATES, null, null, KFSConstants.COUNTRY_CODE_UNITED_STATES, null, null)
//    , BOTH_US_EMPTY_STATES_EMPTY_ZIPS(KFSConstants.COUNTRY_CODE_UNITED_STATES, "", "", KFSConstants.COUNTRY_CODE_UNITED_STATES, "", "")
    , BOTH_US_BOTH_STATES_ONE_ZIP_ONE_NULL(KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode, KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, null)
    , BOTH_US_BOTH_STATES_ONE_ZIP_ONE_EMPTY(KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode, KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, "")
//    , WITHOUT_US_BOTH_STATES_WITHOUT_ZIPS("", StatesZips.stateCd, null, "", StatesZips.stateCd, null)
//    , WITHOUT_US_BOTH_STATES_EMPTY_ZIPS("", StatesZips.stateCd, "", "", StatesZips.stateCd, "")
//    , WITHOUT_US_BOTH_STATES_BOTH_ZIPS("", StatesZips.stateCd, StatesZips.zipCode, "", StatesZips.stateCd, StatesZips.zipCode),
    ;

    // Country and State were moved from KFS, so any module cannot instantiate a country or state object directly
    private KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);

    private State state1;
    private State state2;
    private Country country1;
    private Country country2;

    private String countryCd1;
    private String stateCd1;
    private String zip1;
    private String countryCd2;
    private String stateCd2;
    private String zip2;

    private VendorRuleAddressStateZipFixture(String countryCd1, String stateCd1, String zip1, String countryCd2, String stateCd2, String zip2) {
        this.countryCd1 = countryCd1;
        this.stateCd1 = stateCd1;
        this.zip1 = zip1;
        this.countryCd2 = countryCd2;
        this.stateCd2 = stateCd2;
        this.zip2 = zip2;

        this.state1 = State.Builder.create(stateCd1, "Some State", countryCd1).build();
        this.state2 = State.Builder.create(stateCd2, "Some State", countryCd1).build();
        this.country1 = Country.Builder.create(countryCd1, "Some Country").build();
        this.country2 = Country.Builder.create(countryCd2, "Some Country").build();
    }

    /**
     * This method does the setup for the tests which examine the implementation of the requirement that, if a vendor address's
     * country is the United States, the address must have a state and a zip code.
     *
     * @param country1 Any String, really, but possibly a country value from KFSConstants.
     * @param zip1 Also any String, intended to be a Zip code.
     * @param country2 Country for the second address of the collection
     * @param zip2 Zip code for the second address of the collection
     * @return A List<VendorAddress>, appropriately populated with countries and zip codes.
     */
    @SuppressWarnings("deprecation")
    public List<VendorAddress> populateAddresses() {
        List<VendorAddress> addrList = new ArrayList<VendorAddress>();
        VendorAddress addr1 = new VendorAddress();
        VendorAddress addr2 = new VendorAddress();

        addr1.setVendorCountry(CountryBo.from(country1));
        addr1.setVendorState(StateBo.from(state1));
        addr1.setVendorCountryCode(countryCd1);
        addr1.setVendorStateCode(stateCd1);
        addr1.setVendorZipCode(zip1);
        addr2.setVendorCountry(CountryBo.from(country2));
        addr2.setVendorState(StateBo.from(state2));
        addr2.setVendorCountryCode(countryCd2);
        addr2.setVendorStateCode(stateCd2);
        addr2.setVendorZipCode(zip2);
        addrList.add(addr1);
        addrList.add(addr2);

        return addrList;
    }
}
