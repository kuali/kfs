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
package org.kuali.kfs.vnd.service;


import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@ConfigureContext
public class TaxNumberServiceTest extends KualiTestBase {

    private TaxNumberService taxNumberService;
    private final String nullString = null;
    private final String emptyString = "";
    private final String first3Zero = "000123456";
    private final String first3Six = "666123456";
    private final String notAllNumber = "000234a2f";
    private final String middle2Zero = "123004567";
    private final String last4Zero = "123450000";
    private final String validTaxNumber = "123456789";
    private final String validTaxNumberDashed = "123-45-6789";
    private final String allZero = "000000000";
    private final String tenDigits = "1234567890";
    private final String twoDigits = "12";

    protected void setUp() throws Exception {
        super.setUp();
        taxNumberService = SpringContext.getBean(TaxNumberService.class);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsValidTaxNumber_notAllowedTaxNumber() {
        String[] notAllowedTaxNumbers = getNotAllowedTaxNumbers();
        for (int i = 0; i < notAllowedTaxNumbers.length; i++) {
            assertFalse(taxNumberService.isValidTaxNumber(notAllowedTaxNumbers[i], VendorConstants.TAX_TYPE_SSN));
        }
    }

    private String[] getNotAllowedTaxNumbers() {
        String[] notAllowedTaxNumbers = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(VendorDetail.class, VendorParameterConstants.NOT_ALLOWED_TAX_NUMBERS).toArray(new String[] {});
        return notAllowedTaxNumbers;
    }
}
