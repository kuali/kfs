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
package org.kuali.kfs.vnd.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class VendorHeaderTest extends KualiTestBase {


    public void testToString() {
        VendorHeader vendorHeader = new VendorHeader();
        vendorHeader.setVendorHeaderGeneratedIdentifier(new Integer(1010101010));
        vendorHeader.setVendorTaxNumber("999999999");
        vendorHeader.setVendorTypeCode("XXXX");
        vendorHeader.setVendorOwnershipCategoryCode("XX");
        vendorHeader.setVendorDebarredIndicator(false);

        String vendorHeaderToString = vendorHeader.toString();
        assertFalse("vendorTaxNumber should not show on toString", vendorHeaderToString.contains("vendorTaxNumber"));
        assertFalse("vendorTaxTypeCode should not show on toString", vendorHeaderToString.contains("vendorTaxTypeCode"));

   }
}
