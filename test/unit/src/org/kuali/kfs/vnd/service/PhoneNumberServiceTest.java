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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.web.format.FormatException;

@ConfigureContext(session = khuntley)
public class PhoneNumberServiceTest extends KualiTestBase {

    private PhoneNumberService phoneNumberService;

    private final String phoneGenericFormat1 = "888-888-8888";
    private final String phoneGenericFormat2 = "(888) 888-8888";
    private final String phoneGenericFormat3 = "888 888 8888";
    private final String phoneGenericFaxFormat1 = "888,888-8888";

    private final String phoneTooLong = "888-888-88888";
    private final String phoneTooShort = "888-888-888";

    private final String phoneNonNumeric = "812-3EX-PRES";
    private final String phoneAllTogether = "8888888888";

    protected void setUp() throws Exception {
        super.setUp();
        phoneNumberService = SpringContext.getBean(PhoneNumberService.class);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsValidPhoneNumber_GenericFormat1() {
        assertTrue(phoneNumberService.isValidPhoneNumber(phoneGenericFormat1));
    }

    public void testIsValidPhoneNumber_GenericFormat2() {
        assertTrue(phoneNumberService.isValidPhoneNumber(phoneGenericFormat2));
    }

    public void testIsValidPhoneNumber_GenericFormat3() {
        assertTrue(phoneNumberService.isValidPhoneNumber(phoneGenericFormat3));
    }

    public void testIsValidPhoneNumber_GenericFaxFormat1() {
        assertFalse(phoneNumberService.isValidPhoneNumber(phoneGenericFaxFormat1));
    }

    public void testIsValidPhoneNumber_TooLong() {
        assertFalse(phoneNumberService.isValidPhoneNumber(phoneTooLong));
    }

    public void testIsValidPhoneNumber_TooShort() {
        assertFalse(phoneNumberService.isValidPhoneNumber(phoneTooShort));
    }

    public void testIsValidPhoneNumber_NonNumeric() {
        assertFalse(phoneNumberService.isValidPhoneNumber(phoneNonNumeric));
    }

    public void testIsValidPhoneNumber_AllTogether() {
        assertFalse(phoneNumberService.isValidPhoneNumber(phoneAllTogether));
    }

    public void testIsDefaultFormatPhoneNumber_GenericFormat1() {
        assertTrue(phoneNumberService.isDefaultFormatPhoneNumber(phoneGenericFormat1));
    }

    public void testIsDefaultFormatPhoneNumber_GenericFormat3() {
        assertFalse(phoneNumberService.isDefaultFormatPhoneNumber(phoneGenericFormat3));
    }

    public void testFormatToDefaultGeneric_GenericFormat1() {
        assertEquals(phoneGenericFormat1, phoneNumberService.formatNumberIfPossible(phoneGenericFormat1));
    }

    public void testFormatToDefaultGeneric_GenericFormat2() {
        assertEquals(phoneGenericFormat1, phoneNumberService.formatNumberIfPossible(phoneGenericFormat2));
    }

    public void testFormatToDefaultGeneric_GenericFormat3() {
        assertEquals(phoneGenericFormat1, phoneNumberService.formatNumberIfPossible(phoneGenericFormat3));
    }

    public void testFormatToDefaultGeneric_GenericFaxFormat1() {
        assertEquals(phoneGenericFormat1, phoneNumberService.formatNumberIfPossible(phoneGenericFaxFormat1));
    }

    public void testFormatToDefaultGeneric_phoneTooShort() {
        boolean b = true;
        try {
            b = phoneGenericFormat1.equals(phoneNumberService.formatNumberIfPossible(phoneTooShort));
        }
        catch (FormatException fe) {
            if (fe.getMessage().indexOf("fewer than") > 0) {
                b = false;
            }
        }
        finally {
            assertEquals(b, false);
        }
    }

    public void testFormatToDefaultGeneric_phoneTooLong() {
        boolean b = true;
        try {
            b = phoneGenericFormat1.equals(phoneNumberService.formatNumberIfPossible(phoneTooLong));
        }
        catch (FormatException fe) {
            if (fe.getMessage().indexOf("more than") > 0) {
                b = false;
            }
        }
        finally {
            assertEquals(b, false);
        }
    }

    public void testFormatToDefaultGeneric_phoneNonNumeric() {
        boolean b = true;
        try {
            b = phoneNumberService.isDefaultFormatPhoneNumber(phoneNumberService.formatNumberIfPossible(phoneNonNumeric));
        }
        catch (FormatException fe) {
            if (fe.getMessage().indexOf("fewer than") > 0) {
                b = false;
            }
        }
        finally {
            assertEquals(b, false);
        }
    }
}

