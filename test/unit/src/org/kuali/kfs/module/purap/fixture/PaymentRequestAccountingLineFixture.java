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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;

public enum PaymentRequestAccountingLineFixture {
    BASIC_PREQ_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.LINE2 // AccountingLineFixture
    ),
    PREQ_APO_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE1 // AccountingLineFixture
    ),
    PREQ_ACCOUNT_FOR_PO_CLOSE_DOC(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.PURAP_LINE1 // AccountingLineFixture
    ),;

    private PurApAccountingLineFixture purApAccountingLineFixture;
    private AccountingLineFixture accountingLineFixture;

    private PaymentRequestAccountingLineFixture(PurApAccountingLineFixture purApAccountingLineFixture, AccountingLineFixture accountingLineFixture) {
        this.purApAccountingLineFixture = purApAccountingLineFixture;
        this.accountingLineFixture = accountingLineFixture;
    }

    public PurApAccountingLine createPurApAccountingLine(Class clazz, PurApAccountingLineFixture puralFixture, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;

        // TODO: what should this debit code really be
        line = (PurApAccountingLine) puralFixture.createPurApAccountingLine(PaymentRequestAccount.class, alFixture);

        return line;
    }

    public void addTo(PaymentRequestItem item) {
        PurApAccountingLine purApAccountingLine = createPurApAccountingLine(item.getAccountingLineClass(), purApAccountingLineFixture, accountingLineFixture);
        purApAccountingLine.setPurapItem(item);
        item.getSourceAccountingLines().add(purApAccountingLine);
    }

    /**
     * This method adds an account to an item
     * 
     * @param document
     * @param purApItemFixture
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void addTo(PurApItem item, PurApAccountingLineFixture purApaccountFixture, AccountingLineFixture alFixture) throws IllegalAccessException, InstantiationException {
        // purApaccountFixture.createPurApAccountingLine(PaymentRequestAccount.class, alFixture);
        if (0 == 0)
            ;
    }

}
