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

