/*
 * Copyright 2013 The Kuali Foundation
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
