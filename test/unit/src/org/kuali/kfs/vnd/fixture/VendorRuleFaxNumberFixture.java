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

import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.FaxNumbers;

public enum VendorRuleFaxNumberFixture {

    TWO_DEFAULT_FORMATS(FaxNumbers.defaultFormat, FaxNumbers.defaultFormat), TWO_SHORT_FAXES(FaxNumbers.shortFax, FaxNumbers.shortFax), ONE_DEFAULT_ONE_SHORT_FAX(FaxNumbers.defaultFormat, FaxNumbers.shortFax), ;

    public final String fax1;
    public final String fax2;

    VendorRuleFaxNumberFixture(String fax1, String fax2) {
        this.fax1 = fax1;
        this.fax2 = fax2;
    }

    /**
     * This method does the setup for the tests which examine the implementation of the requirement that the fax numbers in the
     * VendorAddress collection must be of a valid format
     * 
     * @param fax1 A fax number in String form
     * @param fax2 Another fax number in String form
     * @return A List<VendorAddress>, appropriately populated with fax numbers.
     */
    public List<VendorAddress> getAddresses() {
        List<VendorAddress> addrList = new ArrayList();
        VendorAddress addr1 = new VendorAddress();
        VendorAddress addr2 = new VendorAddress();
        addr1.setVendorFaxNumber(fax1);
        addr2.setVendorFaxNumber(fax2);
        addrList.add(addr1);
        addrList.add(addr2);
        return addrList;
    }

}
