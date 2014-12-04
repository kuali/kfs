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
