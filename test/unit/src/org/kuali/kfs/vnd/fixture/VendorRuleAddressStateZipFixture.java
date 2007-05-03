/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.vendor.fixtures;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.fixtures.VendorTestConstants.StatesZips;

public enum VendorRuleAddressStateZipFixture {

    BOTH_US_BOTH_STATES_BOTH_ZIPS(KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode,
            KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode),
    BOTH_US_WITHOUT_STATES_WITHOUT_ZIPS(KFSConstants.COUNTRY_CODE_UNITED_STATES, null, null,
            KFSConstants.COUNTRY_CODE_UNITED_STATES, null, null),
    BOTH_US_EMPTY_STATES_EMPTY_ZIPS(KFSConstants.COUNTRY_CODE_UNITED_STATES, "", "",
            KFSConstants.COUNTRY_CODE_UNITED_STATES, "", ""),
    BOTH_US_BOTH_STATES_ONE_ZIP_ONE_NULL(KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode,
            KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, null),
    BOTH_US_BOTH_STATES_ONE_ZIP_ONE_EMPTY(KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, StatesZips.zipCode,
            KFSConstants.COUNTRY_CODE_UNITED_STATES, StatesZips.stateCd, ""),
    WITHOUT_US_BOTH_STATES_WITHOUT_ZIPS( "", StatesZips.stateCd, null,
            "", StatesZips.stateCd, null ),
    WITHOUT_US_BOTH_STATES_EMPTY_ZIPS( "", StatesZips.stateCd, "",
            "", StatesZips.stateCd, "" ),
    WITHOUT_US_BOTH_STATES_BOTH_ZIPS( "", StatesZips.stateCd, StatesZips.zipCode,
            "", StatesZips.stateCd, StatesZips.zipCode ),;
    
    private State state = new State();
    private Country country = new Country();
    private String country1;
    private String stateCd1;
    private String zip1;
    private String country2;
    private String stateCd2;
    private String zip2;
    
    private VendorRuleAddressStateZipFixture( String country1, String stateCd1, 
            String zip1, String country2, String stateCd2, String zip2 ) {
        this.country1 = country1;
        this.stateCd1 = stateCd1;
        this.zip1 = zip1;
        this.country2 = country2;
        this.stateCd2 = stateCd2;
        this.zip2 = zip2;
    }
    
    /**
     * This method does the setup for the tests which examine the implementation
     * of the requirement that, if a vendor address's country is the United States,
     * the address must have a state and a zip code.
     * 
     * @param country1  Any String, really, but possibly a country value from KFSConstants.
     * @param zip1      Also any String, intended to be a Zip code.
     * @param country2  Country for the second address of the collection
     * @param zip2      Zip code for the second address of the collection
     * @return  A List<VendorAddress>, appropriately populated with countries and zip codes.
     */
    @SuppressWarnings("deprecation")
    public List<VendorAddress> populateAddresses() {
        List<VendorAddress> addrList = new ArrayList();
        VendorAddress addr1 = new VendorAddress();
        VendorAddress addr2 = new VendorAddress();
        state.setPostalStateCode( stateCd1 );
        country.setPostalCountryCode( country1 );
        addr1.setVendorCountry( country );
        addr1.setVendorState( state );
        addr1.setVendorZipCode( zip1 );
        country.setPostalCountryCode( country2 );
        addr2.setVendorCountry( country );
        state.setPostalStateCode( stateCd2 );
        addr2.setVendorState( state );
        addr2.setVendorZipCode( zip2 );
        addrList.add( addr1 );
        addrList.add( addr2 );
        return addrList;
    }
}
