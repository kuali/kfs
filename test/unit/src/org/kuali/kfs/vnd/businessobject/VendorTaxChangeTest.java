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
public class VendorTaxChangeTest extends KualiTestBase {

    public void testToString() {
        VendorTaxChange vendorTaxChange = new VendorTaxChange();

        vendorTaxChange.setVendorTaxChangeGeneratedIdentifier(new Integer(1010101010));
        vendorTaxChange.setVendorPreviousTaxNumber("999999999");
        vendorTaxChange.setVendorPreviousTaxTypeCode("XXX");
        vendorTaxChange.setVendorTaxChangePersonIdentifier("username");

        String vendorTaxChangeToString = vendorTaxChange.toString();
        assertFalse("vendorPreviousTaxNumber should not show on toString", vendorTaxChangeToString.contains("vendorPreviousTaxNumber"));
        assertFalse("vendorPreviousTaxTypeCode should not show on toString", vendorTaxChangeToString.contains("vendorPreviousTaxTypeCode"));

   }

}