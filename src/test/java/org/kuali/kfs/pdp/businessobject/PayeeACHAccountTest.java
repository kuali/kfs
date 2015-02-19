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
package org.kuali.kfs.pdp.businessobject;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiInteger;

@ConfigureContext
public class PayeeACHAccountTest extends KualiTestBase {

    public void testToString() {
        PayeeACHAccount payeeACHAccount = new PayeeACHAccount();

        payeeACHAccount.setAchAccountGeneratedIdentifier(new KualiInteger(1010101010));
        payeeACHAccount.setBankRoutingNumber("999999999");
        payeeACHAccount.setBankAccountNumber("4444");
        payeeACHAccount.setPayeeIdNumber("50000");
        payeeACHAccount.setPayeeName("FOO");
        payeeACHAccount.setPayeeEmailAddress("foo@kuali.org");
        payeeACHAccount.setPayeeIdentifierTypeCode("T");
        payeeACHAccount.setAchTransactionType("BZ");
        payeeACHAccount.setBankAccountTypeCode("22");
        payeeACHAccount.setActive(true);

        String payeeACHAccountToString = payeeACHAccount.toString();

        assertTrue("achAccountGeneratedIdentifier should show on toString", payeeACHAccountToString.contains("achAccountGeneratedIdentifier"));

        assertFalse("bankAccountNumber should not show on toString", payeeACHAccountToString.contains("bankAccountNumber"));
    }

}
